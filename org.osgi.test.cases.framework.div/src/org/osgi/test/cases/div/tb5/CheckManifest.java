/** 
 OSGi Test Suite Implementation. OSGi Confidential.
 (C) Copyright Ericsson Telecom AB. 2001.
 This source code is owned by Ericsson Telecom AB, and is being distributed to OSGi 
 MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */
package org.osgi.test.cases.div.tb5;

import org.osgi.framework.*;

/**
 * Bundle for the CheckManifest test.
 * 
 * @author Ericsson Telecom AB
 */
public class CheckManifest implements BundleActivator {
	/**
	 * Starts the bundle.
	 */
	public void start(BundleContext bc) {
		System.out.println("TB5 start");
	}

	/**
	 * Stops the bundle.
	 */
	public void stop(BundleContext bc) {
		System.out.println("TB5 start");
	}
}
