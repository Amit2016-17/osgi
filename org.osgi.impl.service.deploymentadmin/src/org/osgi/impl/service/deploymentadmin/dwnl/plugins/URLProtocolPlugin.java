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
package org.osgi.impl.service.deploymentadmin.dwnl.plugins;

import java.io.InputStream;
import java.net.*;
import java.util.*;
import org.osgi.framework.*;
import org.osgi.impl.service.deploymentadmin.api.*;

public class URLProtocolPlugin implements ProtocolPlugin, BundleActivator {
    
    private ServiceRegistration reg;

    public InputStream download(Map attr) throws Exception {
		URL url = new URL((String) attr.get("url"));
		if (null == url)
			throw new Exception("URL is missing");
		URLConnection connection = url.openConnection();
		return connection.getInputStream();
	}

	public void start(BundleContext context) throws Exception {
		Dictionary dict = new Hashtable();
		dict.put(ProtocolPlugin.PROTOCOL, "url");
		reg = context.registerService(ProtocolPlugin.class.getName(), this, dict);
	}

	public void stop(BundleContext context) throws Exception {
	    reg.unregister();
	}
	
}
