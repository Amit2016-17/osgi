/*
 * Copyright (c) The OSGi Alliance (2004). All Rights Reserved.
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

/* 
 * REVISION HISTORY:
 *
 * Date          Author(s)
 * CR            Headline
 * ============  ==============================================================
 * Mar 04, 2005  Luiz Felipe Guimaraes
 * 11            Implement TCK Use Cases
 * ============  ==============================================================

 */

package org.osgi.test.cases.dmt.plugins.tbc.ExecPlugin;

import java.util.Hashtable;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.dmt.spi.DataPluginFactory;
import org.osgi.service.dmt.spi.ExecPlugin;
import org.osgi.test.cases.dmt.plugins.tbc.DmtTestControl;

/**
 * @author Luiz Felipe Guimaraes
 * 
 */
public class TestExecPluginActivator implements BundleActivator {

	private ServiceRegistration servReg;
	
	private DmtTestControl tbc;

	public static final String ROOT = "./OSGi/exec_plugin";
	
	public static final String INTERIOR_NODE_EXCEPTION = ROOT + "/exception";

	private TestExecPlugin testExecPlugin;
	
	public TestExecPluginActivator(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void start(BundleContext bc) throws Exception {
		// creating the service
		testExecPlugin = new TestExecPlugin(tbc);
		Hashtable props = new Hashtable();
		props.put(ExecPlugin.EXEC_ROOT_URIS, new String[] { ROOT });
		props.put(DataPluginFactory.DATA_ROOT_URIS, new String[] { ROOT });
		String[] ifs = new String[] { DataPluginFactory.class.getName(),ExecPlugin.class.getName() };
		servReg = bc.registerService(ifs, testExecPlugin, props);
		System.out.println("TestExecPlugin activated.");
	}

	public void stop(BundleContext bc) throws Exception {
		// unregistering the service
		servReg.unregister();
	}
}
