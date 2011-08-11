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
package org.osgi.impl.service.residentialmanagement.plugins;

import org.osgi.service.dmt.spi.DataPlugin;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
/**
 * 
 * @author Koya MORI NTT Corporation, Shigekuni KONDO
 */
public class FiltersPluginActivator implements BundleActivator {
	static final String INSTANCE_ID = "1";
	static final String[] PLUGIN_ROOT_PATH = 
        new String[] { ".", "OSGi", INSTANCE_ID, "Filters" };
    static final String PLUGIN_ROOT_URI = "./OSGi/" + INSTANCE_ID + "/Filters";
    
    private ServiceRegistration servReg;
    private FiltersPlugin     filtersPlugin;
    
	public void start(BundleContext bc) throws Exception {
 		filtersPlugin = new FiltersPlugin(bc);
		Hashtable props = new Hashtable();
		props.put("dataRootURIs", new String[] { PLUGIN_ROOT_URI });
		String[] ifs = new String[] {DataPlugin.class.getName()};
		servReg = bc.registerService(ifs, filtersPlugin, props);
		Util.log("Filters plugin activated successfully.");
	}

	public void stop(BundleContext bc) throws Exception {
		servReg.unregister();
		Util.log("Filters plugin stopped successfully.");
	}

}
