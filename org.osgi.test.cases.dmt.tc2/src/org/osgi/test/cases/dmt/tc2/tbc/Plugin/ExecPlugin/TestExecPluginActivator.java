/*
 * Copyright (c) OSGi Alliance (2004, 2020). All Rights Reserved.
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

package org.osgi.test.cases.dmt.tc2.tbc.Plugin.ExecPlugin;


import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.service.dmt.spi.ExecPlugin;
import org.osgi.service.dmt.spi.MountPlugin;
import org.osgi.test.cases.dmt.tc2.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc2.tbc.DmtTestControl;

/**
 * @author Andre Assad
 * 
 */
public class TestExecPluginActivator implements BundleActivator {

	private ServiceRegistration< ? >	servReg;
	
	private DmtTestControl tbc;

	public static final String ROOT = DmtConstants.OSGi_ROOT +  "/data_plugin";
	
	public static final String INEXISTENT_NODE_NAME = "inexistent";
	
	public static final String INEXISTENT_NODE = ROOT +"/" +INEXISTENT_NODE_NAME ;
	
	public static final String INTERIOR_NODE_NAME = "Interior";
	
	public static final String INTERIOR_NODE = ROOT +"/" + INTERIOR_NODE_NAME;
	
	public static final String INTERIOR_NODE_COPY = ROOT +"/copy";
	
	public static final String INTERIOR_NODE2_COPY = ROOT +"/copy2";
	
	public static final String INTERIOR_NODE2 = ROOT +"/interior2";
	
	public static final String INTERIOR_NODE_WITH_NULL_VALUES = ROOT + "/nullValues";
	
	public static final String INTERIOR_NODE_WITH_TWO_CHILDREN = ROOT + "/two_children";
	
	public static final String CHILD_INTERIOR_NODE = INTERIOR_NODE +"/child";
	
	public static final String INEXISTENT_LEAF_NODE_NAME = INTERIOR_NODE_NAME + "/inexistent_leaf";
	
	public static final String INEXISTENT_LEAF_NODE = INTERIOR_NODE +"/inexistent_leaf";
	
    public static final String LEAF_NAME = "leaf" ;
    
	public static final String LEAF_RELATIVE = INTERIOR_NODE_NAME + "/" + LEAF_NAME ;
	
	public static final String LEAF_NODE = INTERIOR_NODE + "/" + LEAF_NAME ;
	
	public static final String RENAMED_NODE_NAME = "NewNode";
	
	public static final String RENAMED_NODE = ROOT + "/" + RENAMED_NODE_NAME;
	
    public static final String INEXISTENT_INTERIOR_AND_LEAF_NODES = ROOT + "/testA/testB/testC";
    
    public static final String INEXISTENT_INTERIOR_NODES = ROOT + "/testD/testE";
    
	private TestExecPlugin testExecPlugin;
	
	public TestExecPluginActivator(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	@Override
	public void start(BundleContext bc) throws Exception {
		// creating the service
		testExecPlugin = new TestExecPlugin(tbc);
		Dictionary<String,Object> props = new Hashtable<>();
		props.put("dataRootURIs", new String[] { ROOT });
		props.put("execRootURIs", new String[] { ROOT });
		String[] ifs = new String[] { DataPlugin.class.getName(),ExecPlugin.class.getName(), MountPlugin.class.getName() };
		servReg = bc.registerService(ifs, testExecPlugin, props);
		System.out.println("TestDataPlugin activated.");
	}

	@Override
	public void stop(BundleContext bc) throws Exception {
		// unregistering the service
		servReg.unregister();
	}
}
