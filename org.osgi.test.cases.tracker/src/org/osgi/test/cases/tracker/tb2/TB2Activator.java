/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Telecom AB. 2001.
 * This source code is owned by Ericsson Telecom AB, and is being distributed to OSGi
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 */

package org.osgi.test.cases.tracker.tb2;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.test.cases.tracker.service.TestService2;

/**
 * Bundle for exporting packages
 * 
 * @author Ericsson Telecom AB
 */
public class TB2Activator implements BundleActivator {

	/**
	 * Starts the bundle. Installs several services later filtered by the tbc
	 */
	public void start(BundleContext bc) {
		Hashtable<String, Object> ts2Props = new Hashtable<String, Object>();
		ts2Props.put("name", "TestService2");
		ts2Props.put("version", Float.valueOf(1.0f));
		ts2Props.put("compatible", Float.valueOf(1.0f));
		ts2Props.put("description", "TestService 2");

		bc.registerService(TestService2.class.getName(), new TestService2() {
			// empty
		}, ts2Props);

	}

	/**
	 * Stops the bundle.
	 */
	public void stop(BundleContext bc) {
		// empty
	}
}
