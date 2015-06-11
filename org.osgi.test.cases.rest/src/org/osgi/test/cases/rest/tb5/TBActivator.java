/*
 * Copyright (c) 2015 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
 */
package org.osgi.test.cases.rest.tb5;

import java.util.Dictionary;
import java.util.Hashtable;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.rest.RestApiExtension;
import org.restlet.resource.ServerResource;

/**
 * Test bundle registering REST API Extension with relative URI.
 *
 * @author Petia Sotirova
 */
public class TBActivator extends ServerResource implements BundleActivator, RestApiExtension {

  private ServiceRegistration sReg;

  public void start(BundleContext context) throws Exception {
    Dictionary<String, Object> properties = new Hashtable<String, Object>();
    properties.put(RestApiExtension.URI_PATH, "contributions/extension");
    properties.put(RestApiExtension.NAME, "REST CT Extension");
    properties.put("restlet", TBActivator.class);

    sReg = context.registerService(RestApiExtension.class.getName(), this, properties);
  }

  public void stop(BundleContext context) throws Exception {
    if (sReg != null) {
      sReg.unregister();
      sReg = null;
    }
  }

}
