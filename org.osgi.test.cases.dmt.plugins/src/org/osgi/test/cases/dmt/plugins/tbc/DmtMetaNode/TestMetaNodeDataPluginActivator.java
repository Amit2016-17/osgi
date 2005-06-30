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
 * Feb 22, 2005  Andre Assad
 * 11            Implement TCK Use Cases
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.plugins.tbc.DmtMetaNode;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.dmt.DmtDataPlugin;
import org.osgi.test.cases.dmt.plugins.tbc.DmtTestControl;

/**
 * @author Andre Assad
 * 
 */
public class TestMetaNodeDataPluginActivator implements BundleActivator {

	private ServiceRegistration servReg;
	
	private DmtTestControl tbc;

	public static final String TEST_METANODE_PLUGIN_ROOT = "./OSGi/test/meta_node";
	
	public static final String INEXISTENT_NODE = TEST_METANODE_PLUGIN_ROOT +"/inexistent";
	

	private TestMetaNodeDataPlugin testMetaNodeDataPlugin;
	
	public TestMetaNodeDataPluginActivator(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void start(BundleContext bc) throws Exception {
		// creating the service
		testMetaNodeDataPlugin = new TestMetaNodeDataPlugin(tbc);
		Hashtable props = new Hashtable();
		props.put("dataRootURIs", new String[] { TEST_METANODE_PLUGIN_ROOT });
		String[] ifs = new String[] { DmtDataPlugin.class.getName() };
		servReg = bc.registerService(ifs, testMetaNodeDataPlugin, props);
		System.out.println("TestMetaNodeDataPlugin activated.");
	}

	public void stop(BundleContext bc) throws Exception {
		// unregistering the service
		servReg.unregister();
	}
}
