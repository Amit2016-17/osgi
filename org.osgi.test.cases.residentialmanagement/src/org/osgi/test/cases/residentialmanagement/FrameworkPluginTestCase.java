/*
 * Copyright (c) OSGi Alliance (2000, 2010). All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.test.cases.residentialmanagement;

import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;
import junit.framework.TestCase;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.startlevel.StartLevel;
import info.dmtree.DmtAdmin;
import info.dmtree.DmtSession;
import info.dmtree.DmtData;
import info.dmtree.DmtException;
import info.dmtree.DmtIllegalStateException;
/**
 * 
 * @author Koya MORI, Shigekuni KONDO, Ikuo YAMASAKI, NTT Corporation
 */
public class FrameworkPluginTestCase extends TestCase {
	static final String INSTANCE_ID = "1";
	static final String PLUGIN_ROOT_URI = "./OSGi/1/Framework";

	protected static final String ID = "id";
	protected static final String STARTLEVEL = "StartLevel";
	protected static final String INSTALLBUNDLE = "InstallBundle";
	protected static final String FRAMEWORKLIFECYCLE = "FrameworkLifecycle";
	protected static final String BUNDLECONTROL = "BundleControl";
	protected static final String FRAMEWORKEVENT = "FrameworkEvent";
	protected static final String REFRESHPACKAGES = "RefreshPackages";
	
	protected static final String EVENT = "Event";
	protected static final String CATCHEVENTS = "CatchEvents";
	protected static final String THROWABLE = "Throwable";
	protected static final String TYPE = "Type";
	protected static final String BUNDLEID = "BundleId";

	protected static final String REQUESTEDSTARTLEVEL = "RequestedStartLevel";
	protected static final String ACTIVESTARTLEVEL = "ActiveStartLevel";
	protected static final String INITIALBUNDLESTARTLEVEL = "InitialBundleStartLevel";
	protected static final String BEGINNINGSTARTLEVEL = "BeginningStartLevel";
	
	protected static final String LOCATION = "Location";
	protected static final String URL = "URL";
	protected static final String INSTALLBUNDLEOPTION = "Option";
	protected static final String INSTALLBUNDLEOPERATIONRESULT = "OperationResult";
	
	protected static final String RESTART = "Restart";
	protected static final String SHUTDOWN = "Shutdown";
	protected static final String UPDATE = "Update";
	
	protected static final String LIFECYCLE = "Lifecycle";
	protected static final String BUNDLESTARTLEVEL = "BundleStartLevl";
	protected static final String BUNDLECONTROLREFRESHPACKAGES = "RefreshPackages";
	
	protected static final String OPERATION = "Operation";
	protected static final String BUNDLECONTROLOPTION = "Option";
	protected static final String BUNDLECONTROLOPERATIONRESULT = "OperationResult";
	
	protected static final String NODE_TYPE = "org.osgi/1.0/FrameworkManagementObject";

	private static String TESTBUNDLE1 = "org.osgi.test.cases.residentialmanagement.tb1.jar";
	private static String TESTBUNDLE2 = "org.osgi.test.cases.residentialmanagement.tb2.jar";

	private BundleContext context;
	private ServiceReference ref;
	private DmtAdmin dmtadmin;

	public void setBundleContext(BundleContext context) {
		this.context = context;
	}

	protected void setUp() {
		ref = context.getServiceReference(DmtAdmin.class.getName());
		dmtadmin = (DmtAdmin) context.getService(ref);
	}

	protected void tearDown() throws DmtException, BundleException {
		DmtSession session = dmtadmin.getSession(PLUGIN_ROOT_URI,
				DmtSession.LOCK_TYPE_ATOMIC);

		String[] children = session.getChildNodeNames(PLUGIN_ROOT_URI + "/"
				+ INSTALLBUNDLE);
		for (int i = 0; i < children.length; i++)
			session.deleteNode(PLUGIN_ROOT_URI + "/" + INSTALLBUNDLE + "/"
					+ children[i]);

		session.commit();

		session.close();

		Bundle[] bundles = context.getBundles();
		for (int i = 0; i < bundles.length; i++) {
			if (bundles[i].getSymbolicName().equals(TESTBUNDLE1)
					|| bundles[i].getSymbolicName().equals(TESTBUNDLE2))
				bundles[i].uninstall();
		}
	}

	public void testNodeArchitecuture() throws DmtException {
		String[] children;
		Hashtable expected;

		DmtSession session = dmtadmin.getSession(PLUGIN_ROOT_URI,
				DmtSession.LOCK_TYPE_SHARED);

		// 1st descendants
		children = session.getChildNodeNames(PLUGIN_ROOT_URI);
		expected = new Hashtable();
		expected.put(STARTLEVEL, "");
		expected.put(INSTALLBUNDLE, "");
		expected.put(FRAMEWORKLIFECYCLE, "");
		expected.put(BUNDLECONTROL, "");
		expected.put(FRAMEWORKEVENT, "");
		expected.put(REFRESHPACKAGES, "");
		for (int i = 0; i < children.length; i++) {
			expected.remove(children[i]);
		}
		assertEquals("There are undefined nodes in the Framework Plugin.", 0,
				expected.size());

		// StartLevel subtree
		children = session
				.getChildNodeNames(PLUGIN_ROOT_URI + "/" + STARTLEVEL);
		expected = new Hashtable();
		expected.put(REQUESTEDSTARTLEVEL, "");
		expected.put(ACTIVESTARTLEVEL, "");
		expected.put(INITIALBUNDLESTARTLEVEL, "");
		expected.put(BEGINNINGSTARTLEVEL, "");
		for (int i = 0; i < children.length; i++) {
			expected.remove(children[i]);
		}
		assertEquals("There are undefined nodes in the Framework Plugin.", 0,
				expected.size());

		// FrameworkLifecycle subtree
		children = session.getChildNodeNames(PLUGIN_ROOT_URI + "/" + FRAMEWORKLIFECYCLE);
		expected = new Hashtable();
		expected.put(RESTART, "");
		expected.put(SHUTDOWN, "");
		expected.put(UPDATE, "");
		for (int i = 0; i < children.length; i++) {
			expected.remove(children[i]);
		}
		assertEquals("There are undefined nodes in the Framework Plugin.", 0,
				expected.size());
		
		// FrameworkEvent subtree
		children = session.getChildNodeNames(PLUGIN_ROOT_URI + "/" + FRAMEWORKEVENT);
		expected = new Hashtable();
		expected.put(EVENT, "");
		expected.put(CATCHEVENTS, "");
		for (int i = 0; i < children.length; i++) {
			expected.remove(children[i]);
		}
		assertEquals("There are undefined nodes in the Framework Plugin.", 0,
				expected.size());
		
		//BundleContorol subtree
		Hashtable idTable = new Hashtable();
		Bundle[] bundles = context.getBundles();
		assertNotNull("This object should not be null.", bundles);
		for (int i = 0; i < bundles.length; i++) {
			long bundleId = bundles[i].getBundleId() + 1;
			String bundleIdStr = Long.toString(bundleId);
			idTable.put(bundleIdStr, "");
		}
		String[] ids = session.getChildNodeNames(PLUGIN_ROOT_URI + "/" + BUNDLECONTROL);
		assertFalse("These objects must exist.", ids.length==0);
		for (int i = 0; i < ids.length; i++) {
			idTable.remove(ids[i]);
		}
		assertEquals("There are undefined nodes in the Framework Plugin.", 0,
				idTable.size());
		idTable = null;
		
		// ../BundleContorol/<id>
		for (int i = 0; i < ids.length; i++) {
			expected.put(LIFECYCLE, "");
			expected.put(BUNDLESTARTLEVEL, "");
			expected.put(BUNDLECONTROLREFRESHPACKAGES, "");
			String[] childrenId = session.getChildNodeNames(PLUGIN_ROOT_URI + "/" + BUNDLECONTROL + "/" + ids[i]);
			for (int j = 0; j < childrenId.length; j++) {
				expected.remove(childrenId[j]);
			}
			assertEquals(
					"There are undefined nodes in the Framework Plugin.", 0, expected.size());
		}
		
		// ../BundleContorol/<id>/Lifecycle
		for (int i = 0; i < ids.length; i++) {
			expected.put(OPERATION, "");
			expected.put(BUNDLECONTROLOPTION, "");
			expected.put(BUNDLECONTROLOPERATIONRESULT, "");
			String[] childrenId = session.getChildNodeNames(PLUGIN_ROOT_URI + "/" + BUNDLECONTROL 
					+ "/" + ids[i]+ "/" + LIFECYCLE);
			for (int j = 0; j < childrenId.length; j++) {
				expected.remove(childrenId[j]);
			}
			assertEquals(
					"There are undefined nodes in the Framework Plugin.", 0, expected.size());
		}
		session.close();
	}

	public void testNodeCreation() throws DmtException {
		DmtSession session = dmtadmin.getSession(PLUGIN_ROOT_URI,
				DmtSession.LOCK_TYPE_ATOMIC);

		session.createInteriorNode(PLUGIN_ROOT_URI + "/" + INSTALLBUNDLE + "/1");
		String[] children = session.getChildNodeNames(PLUGIN_ROOT_URI + "/"
				+ INSTALLBUNDLE + "/1");

		Hashtable expected = new Hashtable();
		expected.put(LOCATION, "");
		expected.put(URL, "");
		expected.put(INSTALLBUNDLEOPTION, "");
		expected.put(INSTALLBUNDLEOPERATIONRESULT, "");

		for (int i = 0; i < children.length; i++) {
			expected.remove(children[i]);
		}
		assertEquals("There are undefined nodes in the Framework Plugin.", 0,
				expected.size());

		session.rollback();
		session.close();
	}

	public void testReadOnlyNode() throws DmtException {

		// Read Operation
		DmtSession session = dmtadmin.getSession(PLUGIN_ROOT_URI,
				DmtSession.LOCK_TYPE_SHARED);

		try {
			DmtData data = session.getNodeValue(PLUGIN_ROOT_URI + "/"
					+ REFRESHPACKAGES);
			data.getBoolean();
			
			data = session.getNodeValue(PLUGIN_ROOT_URI + "/"
					+ STARTLEVEL + "/" + REQUESTEDSTARTLEVEL);
			data.getInt();

			data = session.getNodeValue(PLUGIN_ROOT_URI + "/" + STARTLEVEL
					+ "/" + ACTIVESTARTLEVEL);
			data.getInt();

			data = session.getNodeValue(PLUGIN_ROOT_URI + "/" + STARTLEVEL
					+ "/" + INITIALBUNDLESTARTLEVEL);
			data.getInt();
			
			data = session.getNodeValue(PLUGIN_ROOT_URI + "/" + STARTLEVEL
					+ "/" + BEGINNINGSTARTLEVEL);
			data.getInt();

			data = session.getNodeValue(PLUGIN_ROOT_URI + "/" + FRAMEWORKLIFECYCLE + "/"
					+ RESTART);
			data.getBoolean();

			data = session.getNodeValue(PLUGIN_ROOT_URI + "/" + FRAMEWORKLIFECYCLE + "/"
					+ SHUTDOWN);
			data.getBoolean();

			data = session.getNodeValue(PLUGIN_ROOT_URI + "/" + FRAMEWORKLIFECYCLE + "/"
					+ UPDATE);
			data.getBoolean();
			
			data = session.getNodeValue(PLUGIN_ROOT_URI + "/" + FRAMEWORKEVENT + "/"
					+ CATCHEVENTS);
			data.getBoolean();
			
			data = session.getNodeValue(PLUGIN_ROOT_URI + "/" + BUNDLECONTROL + "/"
					+ "1" + "/" + BUNDLESTARTLEVEL);
			data.getInt();
			
			data = session.getNodeValue(PLUGIN_ROOT_URI + "/" + BUNDLECONTROL + "/"
					+ "1" + "/" + BUNDLECONTROLREFRESHPACKAGES);
			data.getBoolean();
			
			data = session.getNodeValue(PLUGIN_ROOT_URI + "/" + BUNDLECONTROL + "/"
					+ "1" + "/" + LIFECYCLE + "/" + OPERATION);
			data.getString();
			
			data = session.getNodeValue(PLUGIN_ROOT_URI + "/" + BUNDLECONTROL + "/"
					+ "1" + "/" + LIFECYCLE + "/" + BUNDLECONTROLOPTION);
			data.getString();
			
			data = session.getNodeValue(PLUGIN_ROOT_URI + "/" + BUNDLECONTROL + "/"
					+ "1" + "/" + LIFECYCLE + "/" + BUNDLECONTROLOPERATIONRESULT);
			data.getString();
			
		} catch (DmtIllegalStateException e) {
			fail("Leaf node contains illegal format values.");
		} catch (DmtException e) {
			e.printStackTrace();
			fail("Can not get a leaf node's value.");
		}

		session.close();

		// Write Operation
		session = dmtadmin.getSession(PLUGIN_ROOT_URI, DmtSession.LOCK_TYPE_ATOMIC);

		int error = 0;
		try {
			session.setNodeValue(PLUGIN_ROOT_URI + "/" + STARTLEVEL + "/"
					+ ACTIVESTARTLEVEL, new DmtData(1));
		} catch (DmtException e) {
			error = 1;
		}

		assertEquals("The leaf node must be read-only: " + PLUGIN_ROOT_URI
				+ "/" + STARTLEVEL + "/" + ACTIVESTARTLEVEL, 1, error);

		session.rollback();
		session.close();

		// InstallBundle Subtree
		session = dmtadmin.getSession(PLUGIN_ROOT_URI, DmtSession.LOCK_TYPE_ATOMIC);
		session.createInteriorNode(PLUGIN_ROOT_URI + "/" + INSTALLBUNDLE + "/1");
		try {
			DmtData data = session.getNodeValue(PLUGIN_ROOT_URI + "/"
					+ INSTALLBUNDLE + "/1/" + LOCATION);
			if (!data.getString().equals(""))
				fail("The default value of " + LOCATION
						+ " must be empty string.");
			data = session.getNodeValue(PLUGIN_ROOT_URI + "/" + INSTALLBUNDLE
					+ "/1/" + URL);
			if (!data.getString().equals(""))
				fail("The default value of " + URL + " must be empty string.");
			data = session.getNodeValue(PLUGIN_ROOT_URI + "/" + INSTALLBUNDLE
					+ "/1/" + INSTALLBUNDLEOPTION);
			if (!data.getString().equals("NO START"))
				fail("The default value of " + INSTALLBUNDLEOPTION + " must be NO START.");
			data = session.getNodeValue(PLUGIN_ROOT_URI + "/" + INSTALLBUNDLE
					+ "/1/" + INSTALLBUNDLEOPERATIONRESULT);
			if (!data.getString().equals(""))
				fail("The default value of " + INSTALLBUNDLEOPERATIONRESULT + " must be empty string.");
		} catch (DmtIllegalStateException e) {
			fail("Leaf node contains illegal format values.");
		} catch (DmtException e) {
			e.printStackTrace();
			fail("Can not get a leaf node's value.");
		}
		session.rollback();
		session.close();
	}

	public void testConfirmStartLevel() throws DmtException {
		DmtSession session = dmtadmin.getSession(PLUGIN_ROOT_URI, DmtSession.LOCK_TYPE_SHARED);

		DmtData data = session.getNodeValue(PLUGIN_ROOT_URI + "/" + STARTLEVEL
				+ "/" + ACTIVESTARTLEVEL);
		session.close();

		ServiceReference ref = context
				.getServiceReference(org.osgi.service.startlevel.StartLevel.class
						.getName());
		StartLevel sl = (StartLevel) context.getService(ref);

		assertEquals("Start level mismatch.", sl.getStartLevel(), data.getInt());

		context.ungetService(ref);
	}

	public void testChangeStartLevel() throws DmtException,
			InterruptedException {
		DmtSession session = dmtadmin.getSession(PLUGIN_ROOT_URI, DmtSession.LOCK_TYPE_ATOMIC);

		DmtData data = session.getNodeValue(PLUGIN_ROOT_URI + "/" + STARTLEVEL
				+ "/" + ACTIVESTARTLEVEL);

		session.setNodeValue(PLUGIN_ROOT_URI + "/" + STARTLEVEL + "/"
				+ REQUESTEDSTARTLEVEL, new DmtData(data.getInt() + 1));

		session.commit();
		session.close();

		Thread.sleep(500);

		ServiceReference ref = context
				.getServiceReference(org.osgi.service.startlevel.StartLevel.class
						.getName());
		StartLevel sl = (StartLevel) context.getService(ref);

		assertEquals("Start level mismatch.", data.getInt() + 1, sl
				.getStartLevel());

		context.ungetService(ref);
	}

	public void testConfirmInitialBundleStartLevel() throws DmtException {
		DmtSession session = dmtadmin.getSession(PLUGIN_ROOT_URI,
				DmtSession.LOCK_TYPE_SHARED);

		DmtData data = session.getNodeValue(PLUGIN_ROOT_URI + "/" + STARTLEVEL
				+ "/" + INITIALBUNDLESTARTLEVEL);
		
		session.close();

		ServiceReference ref = context
				.getServiceReference(org.osgi.service.startlevel.StartLevel.class
						.getName());
		StartLevel sl = (StartLevel) context.getService(ref);

		assertEquals("Start level mismatch.", sl.getInitialBundleStartLevel(),
				data.getInt());

		context.ungetService(ref);
	}

	public void testChangeInitialBundleStartLevel() throws DmtException {
		DmtSession session = dmtadmin.getSession(PLUGIN_ROOT_URI,
				DmtSession.LOCK_TYPE_ATOMIC);
		
		DmtData data = session.getNodeValue(PLUGIN_ROOT_URI + "/" + STARTLEVEL
				+ "/" + INITIALBUNDLESTARTLEVEL);
		
		session.setNodeValue(PLUGIN_ROOT_URI + "/" + STARTLEVEL + "/"
				+ INITIALBUNDLESTARTLEVEL, new DmtData(data.getInt() + 1));

		session.commit();
		session.close();

		ServiceReference ref = context
				.getServiceReference(org.osgi.service.startlevel.StartLevel.class
						.getName());
		StartLevel sl = (StartLevel) context.getService(ref);

		assertEquals("Start level mismatch.", data.getInt() + 1, sl
				.getInitialBundleStartLevel());

		context.ungetService(ref);
	}

	public void testCheckDataBeforeCommit() throws DmtException {
			
		DmtSession session = dmtadmin.getSession(PLUGIN_ROOT_URI,
				DmtSession.LOCK_TYPE_ATOMIC);

		// Set data
		session.createInteriorNode(PLUGIN_ROOT_URI + "/" + INSTALLBUNDLE + "/1");

		DmtData data = session.getNodeValue(PLUGIN_ROOT_URI + "/" + STARTLEVEL
				+ "/" + REQUESTEDSTARTLEVEL);
		int rsl = data.getInt() + 1;
		session.setNodeValue(PLUGIN_ROOT_URI + "/" + STARTLEVEL + "/"
				+ REQUESTEDSTARTLEVEL, new DmtData(rsl));
		session.setNodeValue(PLUGIN_ROOT_URI + "/" + INSTALLBUNDLE + "/1/"
				+ LOCATION, new DmtData("testLocation"));
		session.setNodeValue(PLUGIN_ROOT_URI + "/" + INSTALLBUNDLE + "/1/"
				+ URL, new DmtData("testURL"));
		session.setNodeValue(PLUGIN_ROOT_URI + "/" + INSTALLBUNDLE + "/1/"
				+ INSTALLBUNDLEOPTION, new DmtData("START TRANSIENT"));
		
		// Check data before commit
		data = session.getNodeValue(PLUGIN_ROOT_URI + "/" + STARTLEVEL + "/"
				+ REQUESTEDSTARTLEVEL);
		assertEquals("The value of " + REQUESTEDSTARTLEVEL
				+ " must change before commit.", rsl, data.getInt());

		data = session.getNodeValue(PLUGIN_ROOT_URI + "/" + INSTALLBUNDLE
				+ "/1/" + LOCATION);
		if (!data.getString().equals("testLocation"))
			fail("The value of " + LOCATION + " must change before commit.");

		data = session.getNodeValue(PLUGIN_ROOT_URI + "/" + INSTALLBUNDLE
				+ "/1/" + URL);
		if (!data.getString().equals("testURL"))
			fail("The value of " + URL + " must change before commit.");
		
		data = session.getNodeValue(PLUGIN_ROOT_URI + "/" + INSTALLBUNDLE
				+ "/1/" + INSTALLBUNDLEOPTION);
		if (!data.getString().equals("START TRANSIENT"))
			fail("The value of " + INSTALLBUNDLEOPTION + " must change before commit.");

		session.rollback();
		session.close();
	}
	
	public void testCheckDataAfterCommit() throws DmtException {
		DmtSession session = dmtadmin.getSession(PLUGIN_ROOT_URI,
				DmtSession.LOCK_TYPE_ATOMIC);

		// Set data
		DmtData data = session.getNodeValue(PLUGIN_ROOT_URI + "/" + STARTLEVEL
				+ "/" + REQUESTEDSTARTLEVEL);
		int rsl = data.getInt() + 1;
		session.setNodeValue(PLUGIN_ROOT_URI + "/" + STARTLEVEL + "/"
				+ REQUESTEDSTARTLEVEL, new DmtData(rsl));

		data = session.getNodeValue(PLUGIN_ROOT_URI + "/" + STARTLEVEL + "/"
				+ INITIALBUNDLESTARTLEVEL);
		int bsl = data.getInt() + 1;
		session.setNodeValue(PLUGIN_ROOT_URI + "/" + STARTLEVEL + "/"
				+ INITIALBUNDLESTARTLEVEL, new DmtData(bsl));

		session.commit();

		// Check data before commit
		data = session.getNodeValue(PLUGIN_ROOT_URI + "/" + STARTLEVEL + "/"
				+ REQUESTEDSTARTLEVEL);
		assertEquals("The value of " + REQUESTEDSTARTLEVEL
				+ " must change before commit.", rsl, data.getInt());

		data = session.getNodeValue(PLUGIN_ROOT_URI + "/" + STARTLEVEL + "/"
				+ INITIALBUNDLESTARTLEVEL);
		assertEquals("The value of " + INITIALBUNDLESTARTLEVEL
				+ " must change before commit.", bsl, data.getInt());

		session.close();
	}

	public void testCheckDataAfterRoleback() throws DmtException {
		DmtSession session = dmtadmin.getSession(PLUGIN_ROOT_URI,
				DmtSession.LOCK_TYPE_ATOMIC);

		// Set data
		session.createInteriorNode(PLUGIN_ROOT_URI + "/" + INSTALLBUNDLE + "/1");

		DmtData data = session.getNodeValue(PLUGIN_ROOT_URI + "/" + STARTLEVEL
				+ "/" + REQUESTEDSTARTLEVEL);
		int rsl = data.getInt() + 1;
		session.setNodeValue(PLUGIN_ROOT_URI + "/" + STARTLEVEL + "/"
				+ REQUESTEDSTARTLEVEL, new DmtData(rsl));

		data = session.getNodeValue(PLUGIN_ROOT_URI + "/" + STARTLEVEL + "/"
				+ INITIALBUNDLESTARTLEVEL);
		int bsl = data.getInt() + 1;
		session.setNodeValue(PLUGIN_ROOT_URI + "/" + STARTLEVEL + "/"
				+ INITIALBUNDLESTARTLEVEL, new DmtData(bsl));

		session.setNodeValue(PLUGIN_ROOT_URI + "/" + INSTALLBUNDLE + "/1/"
				+ LOCATION, new DmtData("testLocation"));
		session.setNodeValue(PLUGIN_ROOT_URI + "/" + INSTALLBUNDLE + "/1/"
				+ URL, new DmtData("testURL"));
		session.setNodeValue(PLUGIN_ROOT_URI + "/" + INSTALLBUNDLE + "/1/"
				+ INSTALLBUNDLEOPTION, new DmtData("START TRANSIENT"));

		session.rollback();

		// Check data before commit
		data = session.getNodeValue(PLUGIN_ROOT_URI + "/" + STARTLEVEL + "/"
				+ REQUESTEDSTARTLEVEL);
		assertEquals("The value of " + REQUESTEDSTARTLEVEL
				+ " must be rolebacked.", rsl - 1, data.getInt());

		data = session.getNodeValue(PLUGIN_ROOT_URI + "/" + STARTLEVEL + "/"
				+ INITIALBUNDLESTARTLEVEL);
		assertEquals("The value of " + INITIALBUNDLESTARTLEVEL
				+ " must be rolebacked.", bsl - 1, data.getInt());

		assertEquals("The child node of " + INSTALLBUNDLE
				+ " must be rolebacked.", false, session
				.isNodeUri(PLUGIN_ROOT_URI + "/" + INSTALLBUNDLE + "/1"));

		session.close();
	}

	public void testInstallBundleByLocation() throws DmtException, IOException {
		DmtSession session = dmtadmin.getSession(PLUGIN_ROOT_URI,
				DmtSession.LOCK_TYPE_ATOMIC);

		// Set data
		session.createInteriorNode(PLUGIN_ROOT_URI + "/" + INSTALLBUNDLE + "/1");
		URL url1 = context.getBundle().getResource(TESTBUNDLE1);
		System.out.println(url1.toExternalForm());
		session.setNodeValue(PLUGIN_ROOT_URI + "/" + INSTALLBUNDLE + "/1/"
				+ LOCATION, new DmtData(url1.toExternalForm()));

		session.createInteriorNode(PLUGIN_ROOT_URI + "/" + INSTALLBUNDLE + "/2");
		URL url2 = context.getBundle().getResource(TESTBUNDLE2);
		System.out.println(url2.toExternalForm());
		session.setNodeValue(PLUGIN_ROOT_URI + "/" + INSTALLBUNDLE + "/2/"
				+ LOCATION, new DmtData(url2.toExternalForm()));

		// Check bundle installation before commit
		Bundle[] bundles = context.getBundles();
		boolean frag1 = false;
		boolean frag2 = false;
		for (int i = 0; i < bundles.length; i++) {
			if (bundles[i].getLocation().equals(url1.toExternalForm()))
				frag1 = true;
			if (bundles[i].getLocation().equals(url2.toExternalForm()))
				frag2 = true;
		}

		assertEquals("TestBundle1 has been already installed before commit.",
				false, frag1);
		assertEquals("TestBundle2 has been already installed before commit.",
				false, frag2);

		// Execute bundle installation
		session.commit();

		// Check InstallBundle sub-tree
		String[] children = session.getChildNodeNames(PLUGIN_ROOT_URI + "/"
				+ INSTALLBUNDLE);
		assertEquals("There are remaining sub-tree under InstallBundle node.",
				0, children.length);

		session.close();

		// Check bundle installation after commit
		bundles = context.getBundles();
		frag1 = false;
		frag2 = false;
		for (int i = 0; i < bundles.length; i++) {
			if (bundles[i].getLocation().equals(url1.toExternalForm()))
				frag1 = true;
			if (bundles[i].getLocation().equals(url2.toExternalForm()))
				frag2 = true;
		}

		assertEquals("Failed to install TestBundle1.", true, frag1);
		assertEquals("Failed to install TestBundle2.", true, frag2);
	}

	public void testInstallBundleByLocationURL() throws DmtException,
			IOException {
		DmtSession session = dmtadmin.getSession(PLUGIN_ROOT_URI,
				DmtSession.LOCK_TYPE_ATOMIC);

		// Set data
		session.createInteriorNode(PLUGIN_ROOT_URI + "/" + INSTALLBUNDLE
						+ "/1");
		URL url1 = context.getBundle().getResource(TESTBUNDLE1);
		System.out.println(url1.toExternalForm());
		session.setNodeValue(PLUGIN_ROOT_URI + "/" + INSTALLBUNDLE + "/1/"
				+ LOCATION, new DmtData(TESTBUNDLE1));
		session.setNodeValue(PLUGIN_ROOT_URI + "/" + INSTALLBUNDLE + "/1/"
				+ URL, new DmtData(url1.toExternalForm()));

		session.createInteriorNode(PLUGIN_ROOT_URI + "/" + INSTALLBUNDLE
						+ "/2");
		URL url2 = context.getBundle().getResource(TESTBUNDLE2);
		System.out.println(url2.toExternalForm());
		session.setNodeValue(PLUGIN_ROOT_URI + "/" + INSTALLBUNDLE + "/2/"
				+ LOCATION, new DmtData(TESTBUNDLE2));
		session.setNodeValue(PLUGIN_ROOT_URI + "/" + INSTALLBUNDLE + "/2/"
				+ URL, new DmtData(url2.toExternalForm()));

		// Check bundle installation before commit
		Bundle[] bundles = context.getBundles();
		boolean frag1 = false;
		boolean frag2 = false;
		for (int i = 0; i < bundles.length; i++) {
			if (bundles[i].getLocation().equals(TESTBUNDLE1))
				frag1 = true;
			if (bundles[i].getLocation().equals(TESTBUNDLE2))
				frag2 = true;
		}

		assertEquals("TestBundle1 has been already installed before commit.",
				false, frag1);
		assertEquals("TestBundle2 has been already installed before commit.",
				false, frag2);

		// Execute bundle installation
		session.commit();

		// Check InstallBundle sub-tree
		String[] children = session.getChildNodeNames(PLUGIN_ROOT_URI + "/"
				+ INSTALLBUNDLE);
		assertEquals("There are remaining sub-tree under InstallBundle node.",
				0, children.length);

		session.close();

		// Check bundle installation after commit
		bundles = context.getBundles();
		frag1 = false;
		frag2 = false;
		for (int i = 0; i < bundles.length; i++) {
			if (bundles[i].getLocation().equals(TESTBUNDLE1))
				frag1 = true;
			if (bundles[i].getLocation().equals(TESTBUNDLE2))
				frag2 = true;
		}

		assertEquals("Failed to install TestBundle1.", true, frag1);
		assertEquals("Failed to install TestBundle2.", true, frag2);
	}
	
	public void testInstallBundleErrorByLocation() throws DmtException {
		DmtSession session = dmtadmin.getSession(PLUGIN_ROOT_URI,
				DmtSession.LOCK_TYPE_ATOMIC);

		// Set data
		session
				.createInteriorNode(PLUGIN_ROOT_URI + "/" + INSTALLBUNDLE
						+ "/1");
		URL url1 = context.getBundle().getResource(TESTBUNDLE1);
		session.setNodeValue(PLUGIN_ROOT_URI + "/" + INSTALLBUNDLE + "/1/"
				+ LOCATION, new DmtData(url1.toExternalForm() + ".Error"));

		// Check bundle installation before commit

		Bundle[] bundles = context.getBundles();
		boolean frag1 = false;
		for (int i = 0; i < bundles.length; i++) {
			if (bundles[i].getLocation().equals(url1.toExternalForm() + ".Error"))
				frag1 = true;
		}

		assertEquals("TestBundle1 has been already installed before commit.",
				false, frag1);

		// Execute bundle installation
		session.commit();

		// Check InstallBundle sub-tree
		String[] children = session.getChildNodeNames(PLUGIN_ROOT_URI + "/"
				+ INSTALLBUNDLE);
		assertEquals(
				"There is no Error node in spite of wrong bundle installation.",
				1, children.length);

		DmtData error = session.getNodeValue(PLUGIN_ROOT_URI + "/"
				+ INSTALLBUNDLE + "/1/" + INSTALLBUNDLEOPERATIONRESULT);
		System.out.println(error.getString());

		session.close();

		// Check bundle installation after commit
		bundles = context.getBundles();
		frag1 = false;
		for (int i = 0; i < bundles.length; i++) {
			if (bundles[i].getLocation().equals(url1.toExternalForm() + ".Error"))
				frag1 = true;
		}

		assertEquals(
				"TestBundle1 has been installed in spite of wrong bundle installation.",
				false, frag1);
	}

	public void testInstallBundleErrorByLocationURL() throws DmtException {
		DmtSession session = dmtadmin.getSession(PLUGIN_ROOT_URI,
				DmtSession.LOCK_TYPE_ATOMIC);

		// Set data
		session.createInteriorNode(PLUGIN_ROOT_URI + "/" + INSTALLBUNDLE
						+ "/1");
		URL url1 = context.getBundle().getResource(TESTBUNDLE1);
		session.setNodeValue(PLUGIN_ROOT_URI + "/" + INSTALLBUNDLE + "/1/"
				+ LOCATION, new DmtData(TESTBUNDLE1 + ".Error"));
		session.setNodeValue(PLUGIN_ROOT_URI + "/" + INSTALLBUNDLE + "/1/"
				+ URL, new DmtData(url1.toExternalForm() + ".Error"));

		// Check bundle installation before commit

		Bundle[] bundles = context.getBundles();
		boolean frag1 = false;
		for (int i = 0; i < bundles.length; i++) {
			if (bundles[i].getLocation().equals(TESTBUNDLE1 + ".Error"))
				frag1 = true;
		}

		assertEquals("TestBundle1 has been already installed before commit.",
				false, frag1);

		// Execute bundle installation
		session.commit();

		// Check InstallBundle sub-tree
		String[] children = session.getChildNodeNames(PLUGIN_ROOT_URI + "/"
				+ INSTALLBUNDLE);
		assertEquals(
				"There is no Error node in spite of wrong bundle installation.",
				1, children.length);

		DmtData error = session.getNodeValue(PLUGIN_ROOT_URI + "/"
				+ INSTALLBUNDLE + "/1/" + INSTALLBUNDLEOPERATIONRESULT);
		System.out.println(error.getString());

		session.close();

		// Check bundle installation after commit
		bundles = context.getBundles();
		frag1 = false;
		for (int i = 0; i < bundles.length; i++) {
			if (bundles[i].getLocation().equals(TESTBUNDLE1 + ".Error"))
				frag1 = true;
		}

		assertEquals(
				"TestBundle1 has been installed in spite of wrong bundle installation.",
				false, frag1);
	}
}
