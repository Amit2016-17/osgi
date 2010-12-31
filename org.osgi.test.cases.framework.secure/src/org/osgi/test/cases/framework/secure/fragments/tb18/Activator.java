/*
 * $Id$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
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

package org.osgi.test.cases.framework.secure.fragments.tb18;

import java.io.IOException;
import java.io.InputStream;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 * 
 * Bundle for Extension Bundles tests. Invoker with
 * AdminPermission[<bundle>, EXTENSIONLIFECYCLE] should be able to
 * install extension bundles.
 * 
 * @author jorge.mascena@cesar.org.br
 * 
 * @version $Id$
 */
public class Activator implements BundleActivator{

	/**
	 * Starts Bundle. Tries to install an extension bundle.
	 * @param context
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */

	public void start(BundleContext context) {
		ServiceReference[] bundleRefs;
		try {
			bundleRefs = context.getServiceReferences(Bundle.class.getName(), "(bundle=fragments.tests)");
		}
		catch (InvalidSyntaxException e) {
			throw new RuntimeException(e);
		}
		if (bundleRefs == null)
			throw new RuntimeException("No fragment.tests bundle available");
		Bundle fragmentTests = (Bundle) context.getService(bundleRefs[0]);
		InputStream in = null;
		Bundle tb16b = null;
		try {
			// Install extension bundle
			in = fragmentTests.getEntry("fragments.tb16b.jar").openStream();
			tb16b = context.installBundle("fragments.tb16b.jar", in);
		}
		catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		if(tb16b != null) {
			try {
				tb16b.uninstall();
			}
			catch (BundleException e1) {
				throw new RuntimeException(e1.getMessage());
			}
		}
		if (in != null) {
			try {
				in.close();
			}
			catch (IOException e1) {
				throw new RuntimeException(e1.getMessage());
			}
		}
	}

	/**
	 * Stops Bundle. Nothing to be done here.
	 * 
	 * @param context
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) {

	}

}
