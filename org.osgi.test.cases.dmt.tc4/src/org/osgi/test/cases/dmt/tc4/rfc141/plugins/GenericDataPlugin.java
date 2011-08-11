package org.osgi.test.cases.dmt.tc4.rfc141.plugins;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.MetaNode;
import org.osgi.service.dmt.Uri;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.service.dmt.spi.ExecPlugin;
import org.osgi.service.dmt.spi.MountPlugin;
import org.osgi.service.dmt.spi.MountPoint;
import org.osgi.service.dmt.spi.ReadWriteDataSession;
import org.osgi.service.dmt.spi.ReadableDataSession;
import org.osgi.service.dmt.spi.TransactionalDataSession;

import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

/**
 * this is a simplified plugin that also implements all 3 types of Sessions. It
 * is simplified because not all methods are fully implemented and furthermore
 * it assumes that is just and only mounted at 1 place in the DMT.
 */
public class GenericDataPlugin implements DataPlugin, TransactionalDataSession, MountPlugin, EventHandler, ExecPlugin {

	public static final int ACTION_SET_NODE_VALUE = 1;
	public static final int ACTION_IS_NODE_URI = 2;

	public int lastAction;
	public String lastOpenedSession;
	public String lastUri;
	public Object lastValue;
	public MountPoint[] lastAddedMountPoints;
	public MountPoint[] lastRemovedMountPoints;
	public Event lastReceivedEvent;
	public String[] lastExecPath;
	public String lastExecData;

	private String pluginID;
	private String rootUri;
	private String rootPrefix;
	private Node pluginRoot;

	/**
	 * a simple Plugin that knows
	 * 
	 * @param pluginID
	 *            an identifier that can be used for identity checks
	 * @param rootUri
	 *            the intended uri that the plugin should be mounted on
	 * @param root
	 *            the root <code>Node</code>
	 */
	public GenericDataPlugin(String pluginID, String rootUri, Node root) {
		this.pluginID = pluginID;
		this.pluginRoot = root;
		this.rootUri = rootUri;
		this.rootPrefix = rootUri.substring(0, rootUri.lastIndexOf("/") + 1);
	}

	/*
	 * non optimized version of a find node mechanims
	 */
	private Node findNode(Node start, String uri) {
		if (uri.equals(rootPrefix + start.getURI()))
			return start;
		Iterator iterator = start.getChildren().iterator();
		while (iterator.hasNext()) {
			Node found = findNode((Node) iterator.next(), uri);
			if (found != null)
				return found;
		}
		return null;
	}

	private Node findNode(Node start, String[] nodePath) {
		return findNode(start, Uri.toUri(nodePath));
	}

	public ReadableDataSession openReadOnlySession(String[] sessionRoot,
			DmtSession session) throws DmtException {
		lastOpenedSession = Uri.toUri(sessionRoot);
		return this;
	}

	public ReadWriteDataSession openReadWriteSession(String[] sessionRoot,
			DmtSession session) throws DmtException {
		lastOpenedSession = Uri.toUri(sessionRoot);
		return this;
	}

	public TransactionalDataSession openAtomicSession(String[] sessionRoot,
			DmtSession session) throws DmtException {
		lastOpenedSession = Uri.toUri(sessionRoot);
		return this;
	}

	public void copy(String[] nodePath, String[] newNodePath, boolean recursive)
			throws DmtException {
//		Node node = findNode(pluginRoot, nodePath);
		throw new DmtException(Uri.toUri(nodePath), DmtException.FEATURE_NOT_SUPPORTED, "This plugin does not support copy operations.");
	}

	public void createInteriorNode(String[] nodePath, String type)
			throws DmtException {
		// find parent node
		String[] parentUri = new String[ nodePath.length - 1 ];
		for (int i = 0; i < parentUri.length; i++)
			parentUri[i] = nodePath[i];
		Node parent = findNode(pluginRoot, parentUri);
		if ( parent != null )
			new Node(parent, nodePath[nodePath.length - 1], null );
	}

	public void createLeafNode(String[] nodePath, DmtData value, String mimeType)
			throws DmtException {
		// find parent node
		String[] parentUri = new String[ nodePath.length - 1 ];
		for (int i = 0; i < parentUri.length; i++)
			parentUri[i] = nodePath[i];
		Node parent = findNode(pluginRoot, parentUri);
		if ( parent != null )
			new Node(parent, nodePath[nodePath.length - 1], "" + value );
	}

	public void deleteNode(String[] nodePath) throws DmtException {
		Node n = findNode(pluginRoot, nodePath );
		n.getParent().removeChild(n);
	}

	public void renameNode(String[] nodePath, String newName)
			throws DmtException {
		Node n = findNode(pluginRoot, nodePath );
		n.setName(newName);
	}

	public void setNodeTitle(String[] nodePath, String title)
			throws DmtException {
		Node n = findNode(pluginRoot, nodePath );
		n.setTitle(title);
	}

	public void setNodeType(String[] nodePath, String type) throws DmtException {
		// TODO Auto-generated method stub

	}

	public void setNodeValue(String[] nodePath, DmtData data)
			throws DmtException {
		this.lastAction = ACTION_SET_NODE_VALUE;
		this.lastUri = Uri.toUri(nodePath);
		this.lastValue = data;
	}

	public void nodeChanged(String[] nodePath) throws DmtException {
		// TODO Auto-generated method stub

	}

	public void close() throws DmtException {
		// TODO Auto-generated method stub

	}

	public String[] getChildNodeNames(String[] nodePath) throws DmtException {
		Node n = findNode(pluginRoot, nodePath);
		Vector children = new Vector();
		if ( n != null ) {
			Iterator i = n.getChildren().iterator();
			while (i.hasNext())
				children.add( ((Node)i.next()).getName());
		}
		return (String[]) children.toArray( new String[n.getChildren().size()] );
	}

	public MetaNode getMetaNode(String[] nodePath) throws DmtException {
		Node n = findNode(pluginRoot, nodePath);
		return n != null ? n.getMetaNode() : null;
	}

	public int getNodeSize(String[] nodePath) throws DmtException {
		// TODO Auto-generated method stub
		return 0;
	}

	public Date getNodeTimestamp(String[] nodePath) throws DmtException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNodeTitle(String[] nodePath) throws DmtException {
		Node n = findNode(pluginRoot, nodePath);
		return n != null ? n.getTitle() : null;
	}

	public String getNodeType(String[] nodePath) throws DmtException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isNodeUri(String[] nodePath) {
		String uri = Uri.toUri(nodePath);
		this.lastAction = ACTION_IS_NODE_URI;
		this.lastUri = uri;
		return findNode(pluginRoot, uri) != null;
	}

	// it is a leaf node, if it has a value and no children
	public boolean isLeafNode(String[] nodePath) throws DmtException {
		Node n = findNode(pluginRoot, nodePath);
		return n != null && n.getValue() != null && n.getChildren().size() == 0;
	}

	public DmtData getNodeValue(String[] nodePath) throws DmtException {
		Node n = findNode(pluginRoot, nodePath);
		return (n != null ? new DmtData(n.getValue()) : null);
	}

	public int getNodeVersion(String[] nodePath) throws DmtException {
		// TODO Auto-generated method stub
		return 0;
	}

	public void commit() throws DmtException {
		// TODO Auto-generated method stub

	}

	public void rollback() throws DmtException {
		// TODO Auto-generated method stub

	}
	
	public String getRootUri() {
		return rootUri;
	}
	
	public void resetStatus() {
		this.lastAction = 0;
		this.lastUri = null;
		this.lastOpenedSession = null;
		this.lastValue = null;
		this.lastReceivedEvent = null;
		this.lastAddedMountPoints = null;
		this.lastRemovedMountPoints = null;
		this.lastExecPath = null;
		this.lastExecData = null;
	}

	public static void main(String[] args) {
		Node n2 = new Node(null, "A", "node A");
		Node n3 = new Node(n2, "B", "node B");
		GenericDataPlugin gdp = new GenericDataPlugin("P1", "./A", n2);
		System.out.println(gdp.findNode(n2, "./A/B"));
	}

	
	public void mountPointsAdded(MountPoint[] mountPoints) {
		this.lastAddedMountPoints = mountPoints;
	}

	public void mountPointsRemoved(MountPoint[] mountPoints) {
		this.lastRemovedMountPoints = mountPoints;
	}

	public void handleEvent(Event event) {
		this.lastReceivedEvent = event;
		
	}

	public void execute(DmtSession session, String[] nodePath,
			String correlator, String data) throws DmtException {
		this.lastExecPath = nodePath;
	}

}
