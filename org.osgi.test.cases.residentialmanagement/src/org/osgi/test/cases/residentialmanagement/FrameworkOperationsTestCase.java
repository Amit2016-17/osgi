/*
 * Copyright (c) OSGi Alliance (2000, 2011). All Rights Reserved.
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

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.startlevel.FrameworkStartLevel;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtSession;

/**
 * This test case checks the possible operations on the Framework subtree
 * This includes 
 * - get and set the Framework-Startlevel
 * - install and uninstall a bundle
 * - change the state of an installed bundle
 * 
 * @author Steffen Druesedow (Deutsche Telekom Laboratories)
 */
public class FrameworkOperationsTestCase extends RMTTestBase {

	
	/**
	 * asserts that StartLevels can be set via the RMT
	 * @throws Exception 
	 */
	public void testSetFrameworkStartLevels() throws Exception {

		FrameworkStartLevel fwStartLevel = getContext().getBundle(0).adapt(FrameworkStartLevel.class);
		int initialStartLevel = fwStartLevel.getStartLevel();
		int initialBundleStartLevel = fwStartLevel.getInitialBundleStartLevel();
		
		// read value and perform changes in an atomic session
		session = dmtAdmin.getSession(FRAMEWORK_ROOT, DmtSession.LOCK_TYPE_ATOMIC);
		assertNotNull(session);
		int rmtStartLevel = session.getNodeValue(FRAMEWORK_ROOT + "/" + STARTLEVEL ).getInt();
		int rmtInitialBundleStartLevel = session.getNodeValue(FRAMEWORK_ROOT + "/" + INITIAL_BUNDLE_STARTLEVEL ).getInt();
		
		assertEquals("The value of the StartLevel is incorrect.", initialStartLevel, rmtStartLevel);
		assertEquals("The value of the InitialBundleStartLevel is incorrect.", initialBundleStartLevel, rmtInitialBundleStartLevel);

		try {
			// set a new value for the startlevel
			session.setNodeValue(FRAMEWORK_ROOT + "/" + STARTLEVEL , new DmtData(initialStartLevel + 1));
			session.setNodeValue(FRAMEWORK_ROOT + "/" + INITIAL_BUNDLE_STARTLEVEL, new DmtData(initialBundleStartLevel + 1));
			
			session.commit();
			
			// check the new values in a new shared session
			rmtStartLevel = session.getNodeValue(FRAMEWORK_ROOT + "/" + STARTLEVEL ).getInt();
			rmtInitialBundleStartLevel = session.getNodeValue(FRAMEWORK_ROOT + "/" + INITIAL_BUNDLE_STARTLEVEL ).getInt();
			
			assertEquals("The value of the Framework StartLevel was not set correctly.", fwStartLevel.getStartLevel(), initialStartLevel + 1);
			assertEquals("The value of the Framework initialBundleStartLevel was not set correctly.", fwStartLevel.getStartLevel(), initialStartLevel + 1);
		}
		finally {
			fwStartLevel.setStartLevel(initialStartLevel, (FrameworkListener[]) null);
			fwStartLevel.setInitialBundleStartLevel(initialBundleStartLevel);
		}
	}
	
	
	/**
	 * tests installation and uninstallation of a bundle via RMT
	 * @throws Exception 
	 */
	public void testBundleInstallUninstall() throws Exception {
		final String BUNDLE_KEY = "TestBundle";
		final String BUNDLE_URL = getContext().getBundle().getEntry(TESTBUNDLE_EXPORTPACKAGE).toString();

		
		session = dmtAdmin.getSession(FRAMEWORK_ROOT, DmtSession.LOCK_TYPE_ATOMIC);
		assertNotNull(session);

		String bundleUri = BUNDLE + "/" + BUNDLE_KEY;
		// --------- Install a bundle -----------
		// ensure that no such bundle exists already in the RMT
		assertFalse( "TestBundle must not be in the bundle list initially!", session.isNodeUri(bundleUri));
		session.createInteriorNode(bundleUri);
		session.setNodeValue(bundleUri + "/" + URL_STRING, new DmtData(BUNDLE_URL));
		session.setNodeValue(bundleUri + "/" + AUTOSTART, new DmtData(true));
		session.setNodeValue(bundleUri + "/" + REQUESTED_STATE, new DmtData("ACTIVE"));

		session.commit();
		testBundle1 = getContext().getBundle("TestBundle");
		assertNotNull("No bundle with this location is installed.", testBundle1);
		assertEquals( "The testBundle should be in 'ACTIVE' state now.", Bundle.ACTIVE, testBundle1.getState());
		
		// check that the bundle has been installed, there are no faults and the state is active
		// TODO: check FaultType/FaultMessage after discussion about cardinality is finished
//		assertFalse( "There must be no FaultType after successfull operation.", session.isNodeUri(bundleUri + "/" + FAULT_TYPE));
//		assertFalse( "There must be no FaultMessage after successfull operation.", session.isNodeUri(bundleUri + "/" + FAULT_MESSAGE));
		assertTrue( "There must be a State node after successfull installation.", session.isNodeUri(bundleUri + "/" + STATE));
		assertEquals( "ACTIVE", session.getNodeValue(bundleUri + "/" + STATE).getString());
		assertTrue( "TestBundle must have a bundle id after successfull installation!", session.isNodeUri(bundleUri + "/" + BUNDLEID));
		assertTrue( "TestBundle must have a bundle-version after successfull installation!", session.isNodeUri(bundleUri + "/" + VERSION));
		assertTrue( "TestBundle must have a SymbolicName after successfull installation!", session.isNodeUri(bundleUri + "/" + SYMBOLIC_NAME));
		
		long id = session.getNodeValue(bundleUri + "/" + BUNDLEID).getLong();
		
		
		// --------- Uninstall a bundle -----------
		// uninstall bundle via RMT
		session.setNodeValue(bundleUri + "/" + REQUESTED_STATE, new DmtData("UNINSTALLED"));
		session.commit();
		
		// check that the bundle has been uninstalled and there are no faults 
		// TODO: check FaultType/FaultMessage after discussion about cardinality is finished
//		assertFalse( "There must be no FaultType after successfull operation.", session.isNodeUri(bundleUri + "/" + FAULT_TYPE));
//		assertFalse( "There must be no FaultMessage after successfull operation.", session.isNodeUri(bundleUri + "/" + FAULT_MESSAGE));
		assertFalse( "There must be no State node after un-installation.", session.isNodeUri(bundleUri + "/" + STATE));
		
		assertNull("The testBundle was not un-installed successfully!", getContext().getBundle(id) );
		
	}
	
	
	/**
	 * tests state-changes of a bundle via the RMT (starting/stopping)
	 * @throws Exception 
	 */
	public void testBundleStateChanges() throws Exception {
		testBundle1 = installBundle(TESTBUNDLE_EXPORTPACKAGE, false);
		
		// ensure that the bundle is in INSTALLED state
		assertFalse( "testbundle should not be in 'ACTIVE' state initially", Bundle.ACTIVE == testBundle1.getState() );
		
		session = dmtAdmin.getSession(FRAMEWORK_ROOT, DmtSession.LOCK_TYPE_ATOMIC);
		assertNotNull(session);
		String uri = FRAMEWORK_ROOT + "/" + BUNDLE;
		
		// get the corresponding bundle entry in the RMT 
		String bundleBaseUri = null;
		String[] bundles = session.getChildNodeNames(uri);
		for (String bundle : bundles ) {
			long id = session.getNodeValue(uri + "/" + bundle + "/" + BUNDLEID).getLong();
			if ( id == testBundle1.getBundleId() )
				bundleBaseUri = uri + "/" + bundle; 
		}
		assertNotNull("Can't find the testBundle in the RMT bundle map!", bundleBaseUri );

		// check initial state of testBundle in the RMT
		assertEquals( getBundleStateString(testBundle1.getState()), session.getNodeValue(bundleBaseUri + "/" + STATE).getString());
		// attempt bundle resolving
		session.setNodeValue(bundleBaseUri + "/" + REQUESTED_STATE, new DmtData("RESOLVED"));
		session.commit();

		// check real bundle state
		assertEquals( "testBundle should be in 'RESOLVED' state now.", Bundle.RESOLVED, testBundle1.getState());

		// check for Faults in previous commit
		assertEquals( session.getNodeValue(bundleBaseUri + "/" + FAULT_TYPE).getInt(), -1);
		assertEquals( session.getNodeValue(bundleBaseUri + "/" + FAULT_MESSAGE).getString(), "");
		// check state of testBundle in the RMT
		assertEquals( "RESOLVED", session.getNodeValue(bundleBaseUri + "/" + STATE).getString());
		// attempt bundle start
		session.setNodeValue(bundleBaseUri + "/" + REQUESTED_STATE, new DmtData("ACTIVE"));
		session.commit();
		
		// check real bundle state
		assertEquals( "testBundle should be in 'ACTIVE' state now.", Bundle.ACTIVE, testBundle1.getState());
		
		// check bundle state in RMT now and stop it again via RMT
		// check for Faults in previous commit
		assertEquals( session.getNodeValue(bundleBaseUri + "/" + FAULT_TYPE).getInt(), -1);
		assertEquals( session.getNodeValue(bundleBaseUri + "/" + FAULT_MESSAGE).getString(), "");
		assertEquals( "ACTIVE", session.getNodeValue(bundleBaseUri + "/" + STATE).getString());
		session.setNodeValue(bundleBaseUri + "/" + REQUESTED_STATE, new DmtData("RESOLVED"));
		session.commit();
		
		// check real bundle state
		assertEquals( "testBundle should be in 'RESOLVED' state now.", Bundle.RESOLVED, testBundle1.getState());

		// check bundle state in RMT again
		assertEquals( "RESOLVED", session.getNodeValue(bundleBaseUri + "/" + STATE).getString());
	}


}
