/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi 
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */

package org.osgi.test.cases.classpath.tb2;

import org.osgi.framework.*;

/**
 */
public class StaticExport implements BundleActivator
{
	/**
	   Starts the bundle.
	*/
	public void start(BundleContext bc)
	{
		org.osgi.test.cases.classpath.tbc.exp.Exported e;
		e = new org.osgi.test.cases.classpath.tbc.exp.Exported();
	}

	/**
	   Stops the bundle.
	*/
	public void stop(BundleContext bc) { }
}
