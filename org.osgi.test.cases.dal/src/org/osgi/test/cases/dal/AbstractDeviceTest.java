/*
 * Copyright (c) 2014 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
 */

package org.osgi.test.cases.dal;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.dal.Device;
import org.osgi.service.dal.Function;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.test.support.step.TestStepProxy;

/**
 * Common class for all Device Abstraction Layer TCs. It contains some helper
 * methods.
 */
public abstract class AbstractDeviceTest extends DefaultTestBundleControl {

	/**
	 * A service listener, which can track {@code Device} services.
	 */
	protected TestServiceListener	deviceServiceListener;

	/**
	 * The manual test steps are sent to the test step proxy.
	 */
	protected TestStepProxy			testStepProxy;

	/**
	 * initializes the listeners and the test step proxy.
	 * 
	 * @see org.osgi.test.support.compatibility.DefaultTestBundleControl#setUp()
	 */
	protected void setUp() throws Exception {
		this.testStepProxy = new TestStepProxy(super.getContext());
		this.deviceServiceListener = new TestServiceListener(
				super.getContext(), TestServiceListener.DEVICE_FILTER);
	}

	/**
	 * Unregisters the listeners and closes the test step proxy.
	 * 
	 * @see org.osgi.test.support.compatibility.DefaultTestBundleControl#tearDown()
	 */
	protected void tearDown() throws Exception {
		this.testStepProxy.close();
		this.deviceServiceListener.unregister();
	}

	/**
	 * Returns the device with the given identifier.
	 * 
	 * @param deviceUID The device identifier.
	 * 
	 * @return The device with the given identifier.
	 * 
	 * @throws InvalidSyntaxException If the device identifier cannot build
	 *         valid LDAP filter.
	 */
	protected Device getDevice(final String deviceUID) throws InvalidSyntaxException {
		BundleContext bc = super.getContext();
		ServiceReference[] deviceSRefs = bc.getServiceReferences(
				Device.class.getName(), '(' + Device.SERVICE_UID + '=' + deviceUID + ')');
		assertEquals("One device with the given UID is expected.", 1, deviceSRefs.length);
		Device device = (Device) bc.getService(deviceSRefs[0]);
		assertNotNull("The device service is missing.", device);
		return device;
	}

	/**
	 * Returns the function with the specified property name and property value.
	 * Each argument can be {@code null}.
	 * 
	 * @param propName Specifies the property name, can be {@code null}.
	 * @param propValue Specifies the property value, can be {@code null}. That
	 *        means any value.
	 * 
	 * @return The functions according to the specified arguments.
	 * 
	 * @throws InvalidSyntaxException If invalid filter is built with the
	 *         specified arguments.
	 */
	protected Function[] getFunctions(String propName, String propValue) throws InvalidSyntaxException {
		String filter = null;
		if (null != propName) {
			if (null == propValue) {
				propValue = "*";
			}
			filter = '(' + propName + '=' + propValue + ')';
		}
		BundleContext bc = super.getContext();
		ServiceReference[] functionSRefs = bc.getServiceReferences(null, filter);
		assertNotNull("There is no function with property: " + propName, functionSRefs);
		Function[] functions = new Function[functionSRefs.length];
		for (int i = 0; i < functions.length; i++) {
			functions[i] = (Function) bc.getService(functionSRefs[i]);
			assertNotNull(
					"The function service is missing with UID: " + functionSRefs[i].getProperty(Device.SERVICE_UID),
					functions[i]);
		}
		return functions;
	}

	/**
	 * Checks that the given set of properties are available in the service
	 * reference.
	 * 
	 * @param serviceRef The reference to check.
	 * @param propNames The property names to check.
	 */
	protected void checkRequiredProperties(ServiceReference serviceRef, String[] propNames) {
		for (int i = 0; i < propNames.length; i++) {
			assertNotNull(propNames[i] + " property is missing",
					serviceRef.getProperty(propNames[i]));
		}
	}

}
