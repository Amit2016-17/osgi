package org.osgi.test.cases.remoteserviceadmin.tb7;

import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import junit.framework.Assert;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.remoteserviceadmin.EndpointDescription;
import org.osgi.service.remoteserviceadmin.EndpointEvent;
import org.osgi.service.remoteserviceadmin.EndpointEventListener;
import org.osgi.service.remoteserviceadmin.RemoteConstants;
import org.osgi.test.cases.remoteserviceadmin.common.B;
import org.osgi.test.cases.remoteserviceadmin.common.ModifiableService;
import org.osgi.test.cases.remoteserviceadmin.common.RemoteServiceConstants;
import org.osgi.test.cases.remoteserviceadmin.common.Utils;

/*
 * Copyright (c) OSGi Alliance (2008, 2014). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * 
 * Test bundle that registers a service to be exported and manually notifies any
 * discovery implementation that it should publish an endpoint for this service.
 * The bundle is very similar to tb1 but uses the RSA 1.1 EndpointEventListener
 * instead of the deprecated EndpointListener.
 * 
 * The bundle further provides an implementation of the ModifiableService
 * allowing to remotely change the service registration to cause a service
 * modified event.
 * 
 * @author <a href="mailto:marc@marc-schaaf.de">Marc Schaaf</a>
 * 
 */
public class Activator implements BundleActivator, ModifiableService, B {

	private ServiceRegistration<ModifiableService> registration;
	private EndpointDescription endpoint;
	private BundleContext bctx;
	private Map<String, Object> endpointProperties;

	public void start(BundleContext context) throws Exception {
		this.bctx = context;

		Hashtable<String, String> dictionary = createBasicServiceProperties();

		registration = context.registerService(ModifiableService.class, this,
				dictionary);

		test();

	}

	private Hashtable<String, String> createBasicServiceProperties() {
		Hashtable<String, String> dictionary = new Hashtable<String, String>();
		dictionary.put("mykey", "will be overridden");
		dictionary.put("myprop", "myvalue");
		dictionary.put(RemoteServiceConstants.SERVICE_EXPORTED_INTERFACES,
				ModifiableService.class.getName());
		return dictionary;
	}

	private void test() throws InvalidSyntaxException {
		//
		// create an EndpointDescription
		//
		Long endpointID = new Long(12345);
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("mykey", "has been overridden");
		// properties.put(Constants.OBJECTCLASS, new String []
		// {A.class.getName()}); // needed? no, already in servicereference
		properties.put(RemoteConstants.SERVICE_IMPORTED,
				ModifiableService.class.getName());
		properties.put(RemoteConstants.ENDPOINT_SERVICE_ID, endpointID);
		properties.put(RemoteConstants.ENDPOINT_FRAMEWORK_UUID,
				bctx.getProperty("org.osgi.framework.uuid"));
		properties.put(RemoteConstants.ENDPOINT_ID, "someURI"); // mandatory
		properties.put(RemoteConstants.SERVICE_IMPORTED_CONFIGS, "A"); // mandatory
		endpoint = new EndpointDescription(registration.getReference(),
				properties);

		endpointProperties = properties;

		//
		// find the EndpointListeners and call them with the endpoint
		// description
		//
		EndpointEvent endpointEvent = new EndpointEvent(EndpointEvent.ADDED,
				endpoint);
		notifyEndpointEventListeners(bctx, endpointEvent);

	}

	private void stoptest() throws Exception {
		//
		// find the EndpointListeners and call them with the endpoint
		// description
		//
		EndpointEvent endpointEvent = new EndpointEvent(EndpointEvent.REMOVED,
				endpoint);
		notifyEndpointEventListeners(bctx, endpointEvent);
	}

	public void stop(BundleContext context) throws Exception {
		registration.unregister();
		stoptest();
	}

	public String getB() {
		return "this is B";
	}

	public void addServiceProperty() {
		System.out
				.println("TestBundle7: ------------------------- ADD SERVICE PROPERTY AND SEND MODIFIED EVENT ---------------- ");
		// update the service registration
		Hashtable<String, String> properties_reg = createBasicServiceProperties();
		properties_reg.put("someNewProp", "SomeValue");
		registration.setProperties(properties_reg);

		// notify all endpointEventListeners about the modification
		Map<String, Object> properties = new HashMap<String, Object>(
				endpoint.getProperties());
		properties.put("someNewProp", "SomeValue");
		endpoint= new EndpointDescription(registration.getReference(),
				properties);

		EndpointEvent endpointEvent = new EndpointEvent(EndpointEvent.MODIFIED,
				endpoint);
		try {
			notifyEndpointEventListeners(bctx, endpointEvent);
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
	}

	public void changeRequiredServiceProperty() {

		// update the service registration
		Hashtable<String, String> properties_reg = createBasicServiceProperties();
		properties_reg.put("mykey", "set to a non matching value");
		registration.setProperties(properties_reg);

		// notify all endpointEventListeners about the modification
		Map<String, Object> properties = endpointProperties;
		properties.put("mykey", "set to a non matching value");
		endpoint = new EndpointDescription(registration.getReference(),
				properties);
		EndpointEvent endpointEvent = new EndpointEvent(
				EndpointEvent.MODIFIED_ENDMATCH, endpoint);
		try {
			notifyEndpointEventListeners(bctx, endpointEvent);
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
	}

	private void notifyEndpointEventListeners(BundleContext context,
			EndpointEvent endpointEvent) throws InvalidSyntaxException {
		// FIXME: should I also filter for framework UUID == my UUID as
		// suggested in 122.6.1?
		String filter = "(" + EndpointEventListener.ENDPOINT_LISTENER_SCOPE
				+ "=*)";
		Collection<ServiceReference<EndpointEventListener>> listeners = context
				.getServiceReferences(
				EndpointEventListener.class, filter);
		Assert.assertNotNull("no EndpointEventListeners found", listeners);

		boolean foundListener = false;
		for (ServiceReference<EndpointEventListener> sr : listeners) {
			EndpointEventListener listener = context.getService(sr);
			Object scope = sr
					.getProperty(EndpointEventListener.ENDPOINT_LISTENER_SCOPE);

			String matchedFilter = Utils.isInterested(scope, endpoint);

			if (matchedFilter != null) {
				foundListener = true;
				listener.endpointChanged(endpointEvent, matchedFilter);
				System.out
						.println("TestBundle7: ******************** Propagated EndpointChanged Event to endpointEventListener: "
								+ listener + " <<  " + endpointEvent.getType());
			}
		}
		Assert.assertTrue("no interested EndpointEventListener found",
				foundListener);
	}

}
