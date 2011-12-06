package org.osgi.impl.service.dmt;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.cert.X509Certificate;
import java.util.Dictionary;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.osgi.framework.Bundle;
import org.osgi.service.dmt.Acl;
import org.osgi.service.dmt.DmtEvent;
import org.osgi.service.dmt.Uri;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.log.LogService;

//Multi-purpose event store class:
//- stores sets of node URIs for the different types of changes within an
//atomic session
//- contains a static event queue for the asynchronous local event delivery;
//this queue is emptied by DmtAdminFactory, which forwards the events to all
//locally registered DmtEventListeners
public class EventDispatcher {
	private static LinkedList localEventQueue = new LinkedList();

	private static void postLocalEvent(DmtEventCore event) {
		synchronized (localEventQueue) {
			localEventQueue.addLast(event);
			localEventQueue.notifyAll();
		}
	}

	// Retrieve the next event from the queue. If there are no events, block
	// until one is added, or until the given timeout time (in milliseconds) has
	// elapsed. A timeout of zero blocks indefinitely. In case of timeout, or
	// if the wait has been interrupted, the method returns "null".
	static DmtEventCore getNextLocalEvent(int timeout) {
		synchronized (localEventQueue) {
			if (localEventQueue.size() == 0) {
				try {
					localEventQueue.wait(timeout);
				} catch (InterruptedException e) {
					// do nothing
				}

				if (localEventQueue.size() == 0)
					return null;
			}

			return (DmtEventCore) localEventQueue.removeFirst();
		}
	}
	
	private final int sessionId;
	private final Context context;
	private final Bundle initiatingBundle;
	private String[] signers;
	
	private LinkedList events;

	public EventDispatcher(Context context, int sessionId, Bundle initiatingBundle) {
		this.sessionId = sessionId;
		this.context = context;
		this.initiatingBundle = initiatingBundle;
		// create signer array for events only once
		Map<X509Certificate, List<X509Certificate>> certs = initiatingBundle.getSignerCertificates(Bundle.SIGNERS_ALL);
		signers = new String[certs.size()];
		int i = 0;
		for (X509Certificate cert : certs.keySet() )
			signers[i] = cert.getIssuerDN().getName();

		events = new LinkedList();
	}

	synchronized void clear() {
		events.clear();
	}

	synchronized void excludeRoot(Node root) {
		Iterator iterator = events.iterator();
		while (iterator.hasNext())
			((DmtEventCore) iterator.next()).excludeRoot(root);
	}

	synchronized void add(int type, Node node, Node newNode, Acl acl,
			boolean isAtomic) {
		if (isAtomic) { // add event to event store, for delivery at commit
			Integer typeInteger = new Integer(type);
			DmtEventCore event = events.size() > 0 ? (DmtEventCore) events
					.getLast() : null;
			// create new DmtEventCore, if it is the first event at all or
			// the type differs from previous event
			if (event == null || event.getType() != type) {
				event = new DmtEventCore(typeInteger, sessionId);
				events.add(event);
			}
			// only add this node, if it differs from last node in this event
			List eventNodes = event.getNodes();
			boolean sameNode = eventNodes != null && eventNodes.size() > 0
					&& node.equals(eventNodes.get(eventNodes.size() - 1));
			if (!sameNode)
				event.addNode(node, newNode, acl);
		} else
			// dispatch to local and OSGi event listeners immediately
			dispatchEvent(new DmtEventCore(type, sessionId, node, newNode, acl));
	}

	synchronized void dispatchEvents() {
		// send all events in the list in chronological order
		Iterator iterator = events.iterator();
		while (iterator.hasNext())
			dispatchEvent((DmtEventCore) iterator.next());
		clear();
	}


	synchronized void dispatchSessionLifecycleEvent(int type,
			String sessionRoot, String principal, int locktype,
			boolean timeout, Exception fatalException) {
		if (type != DmtEvent.SESSION_OPENED && type != DmtEvent.SESSION_CLOSED)
			throw new IllegalArgumentException("Invalid event type, only "
					+ "session lifecycle events can be dispatched directly.");
		
		// adding mandatory session-lifecycle properties
		DmtEventCore dmtEvent = new DmtEventCore(type, sessionId);
		dmtEvent.addProperty("session.rooturi", sessionRoot);
		if ( principal != null )
			dmtEvent.addProperty("session.principal", principal);
		dmtEvent.addProperty("session.locktype", new Integer(locktype));
		if (timeout)
			dmtEvent.addProperty("session.timeout", new Boolean(true));
		if (fatalException != null ) {
			dmtEvent.addProperty("exception", fatalException);
			dmtEvent.addProperty("exception.message", fatalException.getMessage());
			dmtEvent.addProperty("exception.class", fatalException.getClass().getName());
		}
		dispatchEvent(dmtEvent);
	}

	private void dispatchEvent(DmtEventCore dmtEvent) {
		// ensure that mandatory properties are there see spec v2.0 117.11
		// mandatory life-cycle events are assumed already present in the event
		dmtEvent.addProperty("session.id", new Integer(dmtEvent.getSessionId()));
		dmtEvent.addProperty(EventConstants.EVENT_TOPIC, dmtEvent.getTopic());
		dmtEvent.addProperty("timestamp", new Long(System.currentTimeMillis()));
		
		// add bundle properties (see also Bug 2106)
		dmtEvent.addProperty("bundle", initiatingBundle );
		dmtEvent.addProperty("bundle.signer", signers );
		dmtEvent.addProperty("bundle.symbolicname", initiatingBundle.getSymbolicName());
		dmtEvent.addProperty("bundle.version", initiatingBundle.getVersion());
		dmtEvent.addProperty("bundle.id", initiatingBundle.getBundleId());

		// add the nodes and newnodes properties
		List<Node> nodes = dmtEvent.getNodes();
		if (nodes != null)
			dmtEvent.addProperty("nodes",
					Node.getUriArray(nodes.toArray(new Node[nodes.size()])));

		List<Node> newNodes = dmtEvent.getNewNodes();
		if (newNodes != null)
			dmtEvent.addProperty("newnodes",
					Node.getUriArray(newNodes.toArray(new Node[nodes.size()])));

		// send event to listeners directly registered with DmtAdmin
		postLocalEvent(dmtEvent);

		// send event to listeners registered through EventAdmin
		postOSGiEvent(dmtEvent);

	}

	private void postOSGiEvent(DmtEventCore dmtEvent) {
		final EventAdmin eventChannel = (EventAdmin) context.getTracker(
				EventAdmin.class).getService();

		if (eventChannel == null) {// logging a warning if Event Admin is
									// missing
			context.log(
					LogService.LOG_WARNING,
					"Event Admin not found, only "
							+ "delivering events to listeners directly registered with "
							+ "DmtAdmin.", null);
			return;
		}

		final Event event = new Event(dmtEvent.getTopic(),
				(Dictionary)dmtEvent.getProperties());

		AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				eventChannel.postEvent(event);
				return null;
			}
		});
	}

	
	/**
	 * dispatch events that come from plugin internal changes
	 * @param topic .. the event topic
	 * @param nodes 
	 * @param newNodes 
	 */
	public void dispatchPluginInternalEvent(String topic, String[] nodes, String[] newNodes) {
		DmtEventCore dmtEventCore = new DmtEventCore(topic, -1);
		for (String nodeUri : nodes )
			dmtEventCore.getNodes().add(new Node(Uri.toPath(nodeUri)));
		for (String nodeUri : newNodes )
			dmtEventCore.getNewNodes().add(new Node(Uri.toPath(nodeUri)));
			
		dispatchEvent(dmtEventCore);		
	}

}