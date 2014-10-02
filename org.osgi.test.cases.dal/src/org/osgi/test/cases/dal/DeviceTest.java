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
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.service.dal.Device;
import org.osgi.service.dal.DeviceException;
import org.osgi.test.support.step.TestStep;

/**
 * Test class validates the device operations and properties.
 */
public class DeviceTest extends AbstractDeviceTest {

	/**
	 * Tests device remove operation. The test method requires at least one
	 * device with remove support.
	 * 
	 * @throws DeviceException An error while removing the device.
	 */
	public void testRemoveDevice() throws DeviceException {
		super.testStepProxy.execute(
				DeviceTestSteps.STEP_ID_AVAILABLE_DEVICE,
				DeviceTestSteps.STEP_MESSAGE_AVAILABLE_DEVICE);
		ServiceReference[] deviceSRefs = getDeviceSRefs();
		boolean isRemoved = false;
		for (int i = 0; i < deviceSRefs.length; i++) {
			Device device = (Device) super.getContext().getService(deviceSRefs[i]);
			if (null == device) {
				continue;
			}
			try {
				device.remove();
				assertNull("The device service is not unregistered.", super.getContext().getService(deviceSRefs[i]));
				isRemoved = true;
				break;
			} catch (UnsupportedOperationException uoe) {
				// expected
			}
		}
		assertTrue("No device with remove support.", isRemoved);
	}

	/**
	 * Tests the device registration through {@link TestStep} service.
	 * 
	 * @throws InvalidSyntaxException The registered device UID can break LDAP
	 *         filter.
	 * @throws DeviceException An error while removing the device.
	 */
	public void testAddDevice() throws InvalidSyntaxException, DeviceException {
		super.deviceServiceListener.clear();
		super.testStepProxy.execute(
				DeviceTestSteps.STEP_ID_REGISTER_DEVICE,
				DeviceTestSteps.STEP_MESSAGE_REGISTER_DEVICE);
		ServiceEvent[] serviceEvents = super.deviceServiceListener.getEvents();
		assertTrue(
				"At least one device should be registered, but there are no events.",
				serviceEvents.length > 0);
		for (int i = 0; i < serviceEvents.length; i++) {
			assertEquals(
					"The event type must be registered.",
					ServiceEvent.REGISTERED, serviceEvents[i].getType());
			String deviceUID = (String) serviceEvents[i].getServiceReference().getProperty(Device.SERVICE_UID);
			assertNotNull("The device unique identifier is missing.", deviceUID);
		}
	}

	/**
	 * Tests the device properties with device service properties.
	 */
	public void testDeviceProperties() {
		super.testStepProxy.execute(
				DeviceTestSteps.STEP_ID_AVAILABLE_DEVICE,
				DeviceTestSteps.STEP_MESSAGE_AVAILABLE_DEVICE);
		ServiceReference[] deviceSRefs = getDeviceSRefs();
		boolean compared = false;
		for (int i = 0; i < deviceSRefs.length; i++) {
			Device device = (Device) super.getContext().getService(deviceSRefs[i]);
			if (null == device) {
				continue;
			}
			try {
				String[] refKeys = deviceSRefs[i].getPropertyKeys();
				for (int ii = 0; ii < refKeys.length; ii++) {
					assertTrue(
							"The device property and service property values are different.",
							TestUtil.areEqual(deviceSRefs[i].getProperty(refKeys[ii]), device.getServiceProperty(refKeys[ii])));
				}
				compared = true;
			} catch (UnsupportedOperationException uoe) {
				// expected
			}
		}
		assertTrue("No device with property access.", compared);
	}

	/**
	 * Tests the types of the device properties.
	 */
	public void testDevicePropertyTypes() {
		super.testStepProxy.execute(
				DeviceTestSteps.STEP_ID_DEVICES_ALL_PROPS,
				DeviceTestSteps.STEP_MESSAGE_DEVICES_ALL_PROPS);
		checkDevicePropertyType(Device.SERVICE_DESCRIPTION, new Class[] {String.class});
		checkDevicePropertyType(Device.SERVICE_DRIVER, new Class[] {String.class});
		checkDevicePropertyType(Device.SERVICE_FIRMWARE_VENDOR, new Class[] {String.class});
		checkDevicePropertyType(Device.SERVICE_FIRMWARE_VERSION, new Class[] {String.class});
		checkDevicePropertyType(Device.SERVICE_HARDWARE_VENDOR, new Class[] {String.class});
		checkDevicePropertyType(Device.SERVICE_HARDWARE_VERSION, new Class[] {String.class});
		checkDevicePropertyType(Device.SERVICE_MODEL, new Class[] {String.class});
		checkDevicePropertyType(Device.SERVICE_NAME, new Class[] {String.class});
		checkDevicePropertyType(Device.SERVICE_REFERENCE_UIDS, new Class[] {String[].class});
		checkDevicePropertyType(Device.SERVICE_SERIAL_NUMBER, new Class[] {String.class});
		checkDevicePropertyType(Device.SERVICE_STATUS, new Class[] {Integer.class});
		checkDevicePropertyType(Device.SERVICE_STATUS_DETAIL, new Class[] {Integer.class});
		checkDevicePropertyType(Device.SERVICE_TYPES, new Class[] {String[].class});
		checkDevicePropertyType(Device.SERVICE_UID, new Class[] {String.class});
	}

	/**
	 * Tests the availability of the required device properties.
	 */
	public void testRequiredDeviceProperties() {
		super.testStepProxy.execute(
				DeviceTestSteps.STEP_ID_AVAILABLE_DEVICE,
				DeviceTestSteps.STEP_MESSAGE_AVAILABLE_DEVICE);
		final ServiceReference[] deviceSRefs = getDeviceSRefs();
		for (int i = 0; i < deviceSRefs.length; i++) {
			super.checkRequiredProperties(
					deviceSRefs[i],
					new String[] {
							Device.SERVICE_UID,
							Device.SERVICE_DRIVER,
							Device.SERVICE_STATUS});
		}
	}

	private void checkDevicePropertyType(String propertyName, Class[] expectedTypes) {
		Device[] devices = null;
		try {
			devices = getDevices(propertyName);
		} catch (InvalidSyntaxException e) {
			fail(null, e); // not possible
		}
		for (int i = 0; i < devices.length; i++) {
			Class propertyType = devices[i].getServiceProperty(propertyName).getClass();
			assertTrue("The device proeprty type is not correct: " + propertyName + ", type: " + propertyType,
					TestUtil.contains(expectedTypes, propertyType));
		}
	}

	private ServiceReference[] getDeviceSRefs() {
		try {
			ServiceReference[] deviceSRefs = super.getContext().getServiceReferences(Device.class.getName(), null);
			assertNotNull("Device validation needs at least one device service.", deviceSRefs);
			return deviceSRefs;
		} catch (InvalidSyntaxException e) {
			// null is a valid filter
			return null;
		}
	}

	private Device[] getDevices(final String devicePropName) throws InvalidSyntaxException {
		BundleContext bc = super.getContext();
		ServiceReference[] deviceSRefs = bc.getServiceReferences(
				Device.class.getName(), '(' + devicePropName + "=*)");
		assertNotNull("There is no device with property: " + devicePropName, deviceSRefs);
		Device[] devices = new Device[deviceSRefs.length];
		for (int i = 0; i < devices.length; i++) {
			devices[i] = (Device) bc.getService(deviceSRefs[i]);
			assertNotNull(
					"The device service is missing with UID: " + deviceSRefs[i].getProperty(Device.SERVICE_UID),
					devices[i]);
		}
		return devices;
	}

}
