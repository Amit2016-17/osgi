/*
 * Copyright (c) OSGi Alliance (2004, 2010). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.test.cases.event.junit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.osgi.framework.Bundle;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServicePermission;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.TopicPermission;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.event.service.TBCService;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The <code>EventTestControl</code> is the bundle initially installed and
 * started by the EventTestCase when it is started. It performs the various
 * generic event mechanism tests and reports back to the EventTestCase.
 * 
 * @version $Id$
 */
public class EventAdminTests extends DefaultTestBundleControl {

	EventAdmin eventAdmin;
	private Bundle tb1;
	private Bundle tb2;

	/**
	 * Prepare for each run. It is important that a test run is properly
	 * initialized and that each case can run standalone.
	 * 
	 * @throws Exception
	 */
	protected void setUp() throws Exception {
		log("#before each run");
		tb1 = installBundle("tb1.jar");
		tb1.start();
		tb2 = installBundle("tb2.jar");
		tb2.start();
		eventAdmin = (EventAdmin) getService(EventAdmin.class);
	}

	/**
	 * Clean up after a run. Notice that during debugging many times the
	 * tearDown is never reached.
	 * 
	 * @throws Exception
	 */
	protected void tearDown() throws Exception {
		log("#after each run");
		tb1.stop();
		uninstallBundle(tb1);
		tb2.stop();
		uninstallBundle(tb2);
		PermissionAdmin permissionAdmin = (PermissionAdmin) getService(PermissionAdmin.class);
		permissionAdmin.setPermissions(tb1.getLocation(), null);
		permissionAdmin.setPermissions(tb2.getLocation(), null);
		ungetAllServices();
	}

	/**
	 * Tests if org.osgi.test.cases.event.tb1 and org.osgi.test.cases.event.tb2
	 * are succesfully installed and if their TBCService serivices are avilable.
	 * It is checked if there is exactly one EventAdmin registered service.
	 * 
	 * Verify that the System bundle exists and exports the system services:
	 * PackageAdmin, PermissionAdmin.
	 * 
	 * @specification org.osgi.framework
	 * @specificationSection system.bundle
	 */
	public void testInstallation() throws Exception {
		ServiceTracker trackerProvider1 = new ServiceTracker(getContext(),
				"org.osgi.test.cases.event.tb1.Activator", null);
		trackerProvider1.open();
		TBCService tbcService1 = (TBCService) trackerProvider1.getService();
		assertNotNull("TBCService service in tb1 should be registered",
				tbcService1);
		trackerProvider1.close();

		ServiceTracker trackerProvider2 = new ServiceTracker(getContext(),
				"org.osgi.test.cases.event.tb2.Activator", null);
		trackerProvider2.open();
		TBCService tbcService2 = (TBCService) trackerProvider2.getService();
		assertNotNull("TBCService service in tb2 should be registered",
				tbcService2);
		trackerProvider2.close();

		Bundle system = getContext().getBundle(0);
		assertBundle(PermissionAdmin.class.getName(), system);

		ServiceReference[] eventAdminSRs = getContext().getServiceReferences(
				EventAdmin.class.getName(), null);
		if (eventAdminSRs != null) {
			assertEquals(
					"There must be exactly one EventAdmin registered service ["
							+ EventAdmin.class.getName() + "]", 1,
					eventAdminSRs.length);
		}
	}

	/**
	 * Tests if the permissions are set correctly and the exceptions that are
	 * thrown if they are not.
	 */
	public void testSetPermissions() {// TB4
		PermissionAdmin permissionAdmin = (PermissionAdmin) getService(PermissionAdmin.class);

		PermissionInfo regInfo = new PermissionInfo(ServicePermission.class
				.getName(), "org.osgi.service.event.EventHandler",
				ServicePermission.REGISTER);
		// set permissions to tb1
		PermissionInfo topInfo1 = new PermissionInfo(TopicPermission.class
				.getName(), "org/*", TopicPermission.SUBSCRIBE);
		PermissionInfo topInfo3 = new PermissionInfo(TopicPermission.class
				.getName(), "test/*", TopicPermission.SUBSCRIBE);
		addPermissions(permissionAdmin, tb1, new PermissionInfo[] { regInfo,
				topInfo1 });
		addPermissions(permissionAdmin, tb1, new PermissionInfo[] { regInfo,
				topInfo3 });

		// set permissions to tb2
		PermissionInfo topInfo2 = new PermissionInfo(TopicPermission.class
				.getName(), "org/osgi/*", TopicPermission.SUBSCRIBE);
		PermissionInfo topInfo4 = new PermissionInfo(TopicPermission.class
				.getName(), "org/osgi2/*", TopicPermission.SUBSCRIBE);

		addPermissions(permissionAdmin, tb2, new PermissionInfo[] { regInfo,
				topInfo2 });
		addPermissions(permissionAdmin, tb2, new PermissionInfo[] { regInfo,
				topInfo3 });
		addPermissions(permissionAdmin, tb2, new PermissionInfo[] { regInfo,
				topInfo4 });

		// try to send event and PUBLISH TopicPermission
		Hashtable properties = new Hashtable();
		Hashtable ht = new Hashtable();
		ht.put("topic", "org/osgi/test/cases/event");
		Event event1 = new Event("org/osgi/test/cases/event/ACTION1",
				(Dictionary) properties);
		checkTestingPermissions(event1);

		PermissionInfo[] perm1 = permissionAdmin.getPermissions(tb1
				.getLocation());
		assertNotNull("Permissions of [" + tb1.getLocation() + "]", perm1);
		for (int i = 0; i < perm1.length; i++) {
			pass("permission [" + i + "]: " + perm1[i]);
		}

		PermissionInfo[] perm2 = permissionAdmin.getPermissions(tb2
				.getLocation());
		assertNotNull("Permissions of [" + tb2.getLocation() + "]", perm2);
		for (int i = 0; i < perm2.length; i++) {
			pass("permission [" + i + "]: " + perm2[i]);
		}

	}

	/**
	 * Checks if the SecurityException is got if the caller bundle does not
	 * right <tt>publish</tt> TopicPermision.
	 * 
	 * @param event
	 *            the event used for testing
	 */
	private void checkTestingPermissions(Event event) {// TB5
		boolean hasTopicPermission = hasTopicPermissionForEvent(event);
		String message;
		if (hasTopicPermission) {
			message = "The caller has TopicPermission[topic,PUBLISH] for the topic: [";
		} else {
			message = "The caller does not have TopicPermission[topic,PUBLISH] for the topic: [";
		}
		try {
			eventAdmin.sendEvent(event);
			if (hasTopicPermission) {
				pass(message + event.getTopic() + "] and got no "
						+ SecurityException.class.getName());
			} else {
				failException(message + event.getTopic() + "]",
						SecurityException.class);
			}
		} catch (Throwable e) {
			if (hasTopicPermission) {
				fail(message + event.getTopic() + "] but got exception ["
						+ e.getClass().getName() + " ]");
			} else {
				failException(message + event.getTopic() + "]",
						SecurityException.class);
			}
		}
	}

	/**
	 * Checks if the caller bundle has right <tt>publish</tt> TopicPermision.
	 * 
	 * @param event
	 *            the event to be checked
	 * @return <tt>true</tt> if the caller has the right permission,
	 *         <tt>false</tt> otherwise.
	 */
	private boolean hasTopicPermissionForEvent(Event event) {
		String topic = event.getTopic();
		if (topic == null)
			return false;
		SecurityManager sMngr = System.getSecurityManager();
		if (sMngr != null) {
			TopicPermission topicPermission = new TopicPermission(topic,
					TopicPermission.PUBLISH);
			try {
				sMngr.checkPermission(topicPermission);
			} catch (SecurityException e) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Tests the notification for events after sending (if they match of the
	 * listeners).
	 */
	public void testSendEvent() { // TC4
		ServiceTracker trackerProvider1 = new ServiceTracker(getContext(),
				"org.osgi.test.cases.event.tb1.Activator", null);
		trackerProvider1.open();
		TBCService tbcService1 = (TBCService) trackerProvider1.getService();

		ServiceTracker trackerProvider2 = new ServiceTracker(getContext(),
				"org.osgi.test.cases.event.tb2.Activator", null);
		trackerProvider2.open();
		TBCService tbcService2 = (TBCService) trackerProvider2.getService();

		String[] topics;
		topics = new String[] { "org/osgi/test/*", "org/osgi/newtest1/*",
				"org/osgi1/*", "org/Event1" };
		tbcService1.setProperties(topics, null);

		topics = new String[] { "org/osgi/test/*",
				"org/osgi/newtest1/newtest2/*", "org/osgi2/*" };
		tbcService2.setProperties(topics, null);

		String[] events = new String[] { "org/osgi/test/Event0", "org/Event1",
				"org/osgi1/Event2", "org/osgi1/test/Event3",
				"org/osgi/newtest1/Event4", "org/osgi/newtest2/Event5",
				"org/osgi2/test/Event6" };
		Boolean[] eventsMap1 = new Boolean[] { Boolean.TRUE, Boolean.TRUE,
				Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.FALSE,
				Boolean.FALSE };
		Boolean[] eventsMap2 = new Boolean[] { Boolean.TRUE, Boolean.FALSE,
				Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE,
				Boolean.TRUE };

		Event event;
		for (int i = 0; i < events.length; i++) {
			event = new Event(events[i], (Dictionary) new Hashtable());
			eventAdmin.sendEvent(event);
			pass("before 1");
			assertEvent(event, tb1, tbcService1, eventsMap1[i].booleanValue());
			pass("before 2");
			assertEvent(event, tb2, tbcService2, eventsMap2[i].booleanValue());
		}
		trackerProvider1.close();
		trackerProvider2.close();
	}

	/**
	 * Tests the notification for events after posting (if they match of the
	 * listeners).
	 */
	public void testPostEvent() { // TC5
		ServiceTracker trackerProvider1 = new ServiceTracker(getContext(),
				"org.osgi.test.cases.event.tb1.Activator", null);
		trackerProvider1.open();
		TBCService tbcService1 = (TBCService) trackerProvider1.getService();

		ServiceTracker trackerProvider2 = new ServiceTracker(getContext(),
				"org.osgi.test.cases.event.tb2.Activator", null);
		trackerProvider2.open();
		TBCService tbcService2 = (TBCService) trackerProvider2.getService();

		String[] topics = new String[] { "test/*" };
		String[] delivery = new String[] {EventConstants.DELIVERY_ASYNC_ORDERED};
		tbcService1.setProperties(topics, null);
		tbcService2.setProperties(topics, delivery);

		Event[] events = new Event[10];
		for (int i = 0; i < events.length; i++) {
			events[i] = new Event("test/Event" + i,
					(Dictionary) new Hashtable());
		}

		for (int i = 0; i < events.length; i++) {
			eventAdmin.postEvent(events[i]);
		}
		// wait to ensure that events are received asynchronous
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
		}

		Vector tbc1Events = tbcService1.getLastReceivedEvents();
		Vector tbc2Events = tbcService2.getLastReceivedEvents();
		String message = "Events should be recieved in the same order as they are post ";

		if (tbc1Events == null || tbc1Events.size() == 0) {
			fail("tbc1: No events recived");
		}
		if (tbc2Events == null || tbc2Events.size() == 0) {
			fail("tbc2: No events recived");
		}

		Event event;
		for (int i = 0; i < tbc1Events.size(); i++) {
			event = (Event) tbc1Events.elementAt(i);
			if (event == null) {
				fail("tbc1: Event with topic [test/Event" + i
						+ "] not recieved");
			}
			assertEquals(message, "test/Event" + i, event.getTopic());
		}
		for (int i = 0; i < tbc2Events.size(); i++) {
			event = (Event) tbc2Events.elementAt(i);
			if (event == null) {
				fail("tbc2: Event with topic [test/Event" + i
						+ "] not recieved");
			}
			assertEquals(message, "test/Event" + i, event.getTopic());
		}
		trackerProvider1.close();
		trackerProvider2.close();
	}

	/**
	 * Tests the notification for events after posting (if they match of the
	 * listeners).
	 */
	public void testPostEventUnordered() { // TC5
		ServiceTracker trackerProvider1 = new ServiceTracker(getContext(),
				"org.osgi.test.cases.event.tb1.Activator", null);
		trackerProvider1.open();
		TBCService tbcService1 = (TBCService) trackerProvider1.getService();

		ServiceTracker trackerProvider2 = new ServiceTracker(getContext(),
				"org.osgi.test.cases.event.tb2.Activator", null);
		trackerProvider2.open();
		TBCService tbcService2 = (TBCService) trackerProvider2.getService();

		String[] topics = new String[] {"test/*"};
		String[] delivery = new String[] {EventConstants.DELIVERY_ASYNC_UNORDERED};
		tbcService1.setProperties(topics, delivery);
		tbcService2.setProperties(topics, delivery);

		Event[] events = new Event[10];
		for (int i = 0; i < events.length; i++) {
			events[i] = new Event("test/Event" + i,
					(Dictionary) new Hashtable());
		}

		for (int i = 0; i < events.length; i++) {
			eventAdmin.postEvent(events[i]);
		}
		// wait to ensure that events are received asynchronous
		try {
			Thread.sleep(5000);
		}
		catch (InterruptedException e) {
			// ignored
		}

		Vector tbc1Events = tbcService1.getLastReceivedEvents();
		Vector tbc2Events = tbcService2.getLastReceivedEvents();

		if (tbc1Events == null || tbc1Events.size() == 0) {
			fail("tbc1: No events recived");
		}
		if (tbc2Events == null || tbc2Events.size() == 0) {
			fail("tbc2: No events recived");
		}

		for (int i = 0; i < events.length; i++) {
			if (tbc1Events.contains(events[i])) {
				pass("tbc1: Event with topic [test/Event" + i + "] recieved");
			}
			else {
				fail("tbc1: Event with topic [test/Event" + i
						+ "] not recieved");
			}
		}
		for (int i = 0; i < events.length; i++) {
			if (tbc2Events.contains(events[i])) {
				pass("tbc2: Event with topic [test/Event" + i + "] recieved");
			}
			else {
				fail("tbc2: Event with topic [test/Event" + i
						+ "] not recieved");
			}
		}
		trackerProvider1.close();
		trackerProvider2.close();
	}

	/**
	 * Tests the notification for events after posting simultaneously in 10
	 * threads (if they match of the listeners).
	 */
	public void testMultiThreadsPostEvent() { // TC7
		testMultiThreads(10, "postEvent");
	}

	/**
	 * This is used to start posting or sending simultaneously.
	 */
	class MultiThread extends Thread {
		private final Event		event;
		private final Object	lock;
		private final String	method;

		MultiThread(Event event, Object lock, String name, String method) {
			super(name);
			this.lock = lock;
			this.event = event;
			this.method = method;
		}

		public void run() {
			trace("MultiThread started on event: " + event);
			synchronized (lock) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					// ignored
				}
			}
			if ("postEvent".equals(method)) {
				eventAdmin.postEvent(event);
			} else {
				if ("sendEvent".equals(method)) {
					eventAdmin.sendEvent(event);
				}
				else {
					// unrecognized method
				}
			}
			trace("MultiThread " + method
					+ " event: " + event);
		}
	}

	/**
	 * Tests the notification for events after sending simultaneously in 10
	 * threads (if they match of the listeners).
	 */
	public void testMultiThreadsSendEvent() { // TC6
		testMultiThreads(10, "sendEvent");
	}

	/**
	 * Tests the notification for events after firing simultaneously in count
	 * threads (if they match of the listeners).
	 */
	private void testMultiThreads(final int count, final String method) {
		ServiceTracker trackerProvider1 = new ServiceTracker(getContext(),
				"org.osgi.test.cases.event.tb1.Activator", null);
		trackerProvider1.open();
		TBCService tbcService1 = (TBCService) trackerProvider1.getService();

		ServiceTracker trackerProvider2 = new ServiceTracker(getContext(),
				"org.osgi.test.cases.event.tb2.Activator", null);
		trackerProvider2.open();
		TBCService tbcService2 = (TBCService) trackerProvider2.getService();

		String[] topics = new String[] {"test/*"};
		tbcService1.setProperties(topics,
				new String[] {EventConstants.DELIVERY_ASYNC_ORDERED});
		tbcService2.setProperties(topics,
				new String[] {EventConstants.DELIVERY_ASYNC_UNORDERED});

		Event[] events = new Event[count];
		for (int i = 0; i < events.length; i++) {
			events[i] = new Event("test/Event" + i,
					(Dictionary) new Hashtable());
		}

		MultiThread[] mpts = new MultiThread[events.length];

		Object lock = new Object();
		for (int i = 0; i < events.length; i++) {
			mpts[i] = new MultiThread(events[i], lock,
 "[MultiThread] - " + i,
					method);
			mpts[i].start();
		}

		// wait to ensure that all threads are started
		boolean allAlive;
		do {
			allAlive = true;
			for (int i = 0; i < mpts.length; i++) {
				if (!mpts[i].isAlive()) {
					allAlive = false;
					try {
						Thread.sleep(100);
					}
					catch (InterruptedException e) {
						// ignored
					}
					break;
				}
			}
		} while (!allAlive);
		// add small sleep to ensure all threads go to wait on lock
		try {
			Thread.sleep(3000);
		}
		catch (InterruptedException e) {
			// ignored
		}
		trace("All MultiThread started, notify all...");
		// here notify all threads to start posting events simultaneously
		synchronized (lock) {
			lock.notifyAll();
		}
		trace("Wait all MultiThread to deliver events");
		// wait to ensure that events are received asynchronous
		try {
			Thread.sleep(5000);
		}
		catch (InterruptedException e) {
			// ignored
		}

		Vector tbc1Events = tbcService1.getLastReceivedEvents();
		Vector tbc2Events = tbcService2.getLastReceivedEvents();

		if (tbc1Events == null || tbc1Events.size() == 0) {
			fail("tbc1: No events received");
		}
		if (tbc2Events == null || tbc2Events.size() == 0) {
			fail("tbc2: No events received");
		}

		for (int i = 0; i < events.length; i++) {
			if (tbc1Events.contains(events[i])) {
				pass("tbc1: Event with topic [test/Event" + i + "] received");
			}
			else {
				fail("tbc1: Event with topic [test/Event" + i
						+ "] not received");
			}
		}
		for (int i = 0; i < events.length; i++) {
			if (tbc2Events.contains(events[i])) {
				pass("tbc2: Event with topic [test/Event" + i + "] received");
			}
			else {
				fail("tbc2: Event with topic [test/Event" + i
						+ "] not received");
			}
		}
		trackerProvider1.close();
		trackerProvider2.close();
	}

	/**
	 * Verify that the service with name is exported by the bundle b.
	 * 
	 * @param name fqn of the service, e.g. com.acme.foo.Foo
	 * @param b the bundle to be asserted
	 */
	private void assertBundle(String name, Bundle b) {
		ServiceReference[] ref = null;
		try {
			ref = getContext().getServiceReferences(name, null);
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
		assertNotNull(name + "  service must be registered ", ref);
		for (int i = 0; i < ref.length; i++) {
			ServiceReference reference = ref[i];
			assertEquals("Invalid exporter for " + name, b, reference
					.getBundle());
		}
	}

	private void assertEvent(Event eventPassed, Bundle bundle,
			TBCService tbcService, boolean recieved) {
		pass(">>>Passed event: " + eventPassed);
		// pass("Bundle's event handler topic: " +
		// arrayToString(tbcService.getTopics()));
		Event eventReceived = tbcService.getLastReceivedEvent();
		assertEquals("In [" + bundle.getSymbolicName() + "] received event ["
				+ eventReceived + "]", recieved, eventReceived != null);
		if (eventReceived != null) {
			String[] properties = eventReceived.getPropertyNames();
			String property;
			for (int i = 0; i < properties.length; i++) {
				property = properties[i];
				eventReceived.getProperty(property);
			}
		}
	}

	private void addPermissions(PermissionAdmin permissionAdmin, Bundle bundle,
			PermissionInfo[] toAdd) {
		PermissionInfo[] oldPerm = permissionAdmin.getPermissions(bundle
				.getLocation());
		PermissionInfo[] defPerm = permissionAdmin.getDefaultPermissions();
		int oldLen = 0;
		int defLen = 0;
		if (oldPerm != null)
			oldLen = oldPerm.length;
		if (defPerm != null)
			defLen = defPerm.length;
		PermissionInfo[] newPerm = new PermissionInfo[oldLen + defLen
				+ toAdd.length];
		int i = 0;
		for (; i < oldLen; i++) {
			newPerm[i] = oldPerm[i];
		}
		for (i = 0; i < defLen; ++i) {
			newPerm[oldLen + i] = defPerm[i];
		}
		for (int j = 0; j < toAdd.length; j++) {
			newPerm[oldLen + defLen + j] = toAdd[j];
		}
		permissionAdmin.setPermissions(bundle.getLocation(), newPerm);
	}
	
	/**
	 * Tests support for type String as EventConstants.EVENT_TOPIC property value.
	 * An implementation passes this test if an event is received on the topic.
	 */
	public void testEventTopicsPropertyString() {
		ServiceTracker trackerProvider1 = new ServiceTracker(getContext(),
				"org.osgi.test.cases.event.tb1.Activator", null);
		trackerProvider1.open();
		TBCService tbcService1 = (TBCService) trackerProvider1.getService();
		String topic = "org/osgi/test/Event";
		Dictionary properties = new Hashtable();
		properties.put(EventConstants.EVENT_TOPIC, topic);
		tbcService1.setProperties(properties);
		Event event = new Event(topic, (Dictionary)null);
		eventAdmin.sendEvent(event);
		assertEvent(event, tb1, tbcService1, true);
		trackerProvider1.close();
	}
	
	/**
	 * Tests support for type String[] as EventConstants.EVENT_TOPIC property value.
	 * An implementation passes this test if an event is received on each topic.
	 */
	public void testEventTopicsPropertyStringArray() {
		ServiceTracker trackerProvider1 = new ServiceTracker(getContext(),
				"org.osgi.test.cases.event.tb1.Activator", null);
		trackerProvider1.open();
		TBCService tbcService1 = (TBCService) trackerProvider1.getService();
		String[] topics = new String[] {
				"org/osgi/test/Event1",
				"org/osgi/test/Event2"
		};
		Dictionary properties = new Hashtable();
		properties.put(EventConstants.EVENT_TOPIC, topics);
		tbcService1.setProperties(properties);
		for (int i = 0; i < topics.length; i++) {
			Event event = new Event(topics[i], (Dictionary)null);
			eventAdmin.sendEvent(event);
			assertEvent(event, tb1, tbcService1, true);
		}
		trackerProvider1.close();
	}
	
	/**
	 * Tests support for type Collection<String> as EventConstants.EVENT_TOPIC property value.
	 * An implementation passes this test if an event is received on each topic.
	 */
	public void testEventTopicsPropertyStringCollection() {
		ServiceTracker trackerProvider1 = new ServiceTracker(getContext(),
				"org.osgi.test.cases.event.tb1.Activator", null);
		trackerProvider1.open();
		TBCService tbcService1 = (TBCService) trackerProvider1.getService();
		Collection topics = new ArrayList(3);
		topics.add("org/osgi/test/Event1");
		topics.add("org/osgi/test/Event2");
		topics.add("org/osgi/test/Event3");
		Dictionary properties = new Hashtable();
		properties.put(EventConstants.EVENT_TOPIC, topics);
		tbcService1.setProperties(properties);
		for (Iterator i = topics.iterator(); i.hasNext();) {
			Event event = new Event((String)i.next(), (Dictionary)null);
			eventAdmin.sendEvent(event);
			assertEvent(event, tb1, tbcService1, true);
		}
		trackerProvider1.close();
	}
	
	/**
	 * Ensures EventAdmin does not deliver an event published on topic "a/b/c" 
	 * to an EventHandler listening to topic a/b/c/*.
	 */
	public void testEventDeliveryForWildcardTopic1() {
		ServiceTracker trackerProvider1 = new ServiceTracker(getContext(),
				"org.osgi.test.cases.event.tb1.Activator", null);
		trackerProvider1.open();
		TBCService tbcService1 = (TBCService) trackerProvider1.getService();
		Dictionary properties = new Hashtable();
		properties.put(EventConstants.EVENT_TOPIC, "a/b/c/*");
		tbcService1.setProperties(properties);
		Event event = new Event("a/b/c", (Dictionary) null);
		eventAdmin.sendEvent(event);
		assertEvent(event, tb1, tbcService1, false);
		trackerProvider1.close();
	}
	
	/**
	 * Ensures EventAdmin delivers an event published to topic "a/b/c" or 
	 * "a/b/c/d" to an EventHandler listening to topics "a/b/c" and "a/b/c/*".
	 */
	public void testEventDeliveryForWildcardTopic2() {
		ServiceTracker trackerProvider1 = new ServiceTracker(getContext(),
				"org.osgi.test.cases.event.tb1.Activator", null);
		trackerProvider1.open();
		TBCService tbcService1 = (TBCService) trackerProvider1.getService();
		Dictionary properties = new Hashtable();
		properties.put(EventConstants.EVENT_TOPIC, new String[] {"a/b/c", "a/b/c/*"});
		tbcService1.setProperties(properties);
		Event event = new Event("a/b/c", (Dictionary) null);
		eventAdmin.sendEvent(event);
		assertEvent(event, tb1, tbcService1, true);
		event = new Event("a/b/c/d", (Dictionary) null);
		eventAdmin.sendEvent(event);
		assertEvent(event, tb1, tbcService1, true);
		trackerProvider1.close();
	}
}
