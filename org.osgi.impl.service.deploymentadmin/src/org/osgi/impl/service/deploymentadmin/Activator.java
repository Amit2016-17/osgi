/*
 * ============================================================================
 * (c) Copyright 2004 Nokia
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Nokia and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */
package org.osgi.impl.service.deploymentadmin;

import org.osgi.framework.*;

public class Activator implements BundleActivator {

    DeploymentAdminImpl			deploymentAdmin = new DeploymentAdminImpl();
    
	public void start(BundleContext context) throws Exception {
		deploymentAdmin.start(context);
	}
    
	public void stop(BundleContext context) throws Exception {
        deploymentAdmin.stop(context);
	}
	
}
