/*
 * Copyright (c) OSGi Alliance (2005, 2011). All Rights Reserved.
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

package org.osgi.test.cases.jmx.provisioning.tb2;

import java.util.Dictionary;
import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.test.cases.jmx.provisioning.tb2.api.HelloSayer;
import org.osgi.test.cases.jmx.provisioning.tb2.impl.ConfiguratorImpl;
import org.osgi.test.cases.jmx.provisioning.tb2.impl.HelloSayerImpl;


public class Activator implements BundleActivator {
	private ServiceRegistration helloRegistration;
	private ServiceRegistration configRegistration;
	private ServiceRegistration configFactoryRegistration;
	
	public void start(BundleContext context) throws Exception {
		System.out.println("Hello moon, I am started");
		
		//create configuration service
		ConfiguratorImpl cfg = new ConfiguratorImpl();

		//register as Managed Service
		Properties props = new Properties();
		props.setProperty("test_key", "test_value");
		props.setProperty(Constants.SERVICE_PID, context.getBundle().getBundleId() + ".1");
		
		configRegistration = context.registerService(
				ManagedService.class.getName(), cfg, (Dictionary) props);
		
		//register as Managed Service Factory		
		Properties propsFactory = new Properties();
		propsFactory.setProperty("test_key", "test_value");
		propsFactory.setProperty(Constants.SERVICE_PID, context.getBundle().getBundleId() + ".factory");

		configFactoryRegistration = context.registerService(
				ManagedServiceFactory.class.getName(), cfg,
				(Dictionary) propsFactory);
		
		helloRegistration = context.registerService(HelloSayer.class.getCanonicalName(), new HelloSayerImpl(), null);
	}

	public void stop(BundleContext context) throws Exception {
		System.out.println("stopped");
		helloRegistration.unregister();
		configRegistration.unregister();
		configFactoryRegistration.unregister();
	}
}
