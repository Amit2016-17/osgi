/*
 * Copyright (c) OSGi Alliance (2014). All Rights Reserved.
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

package org.osgi.test.cases.enocean;

import org.osgi.framework.ServiceReference;
import org.osgi.service.enocean.EnOceanDevice;
import org.osgi.test.cases.enocean.utils.Fixtures;
import org.osgi.test.cases.enocean.utils.Logger;

/**
 * This class contains:
 * 
 * - testAutoDeviceRegistration, tests initial, and automatic device
 * registration from a raw Radio teach-in packet that is triggered through the
 * step service.
 * 
 * @author $Id$
 */
public class RegistrationTestCase extends AbstractEnOceanTestCase {

	/**
	 * Tests initial device registration from a raw Radio teach-in packet.
	 * 
	 * @throws Exception
	 */
	public void testAutoDeviceRegistration() throws Exception {

		/* Insert a device */
		super.testStepProxy.execute("MessageExample1_A", "Insert an a5_02_01 device.");

		// Device added
		String lastServiceEvent = devices.waitForService();
		Logger.d("Device added, lastServiceEvent: " + lastServiceEvent);
		assertNotNull("Timeout reached.", lastServiceEvent);

		// Device modified (profile)
		lastServiceEvent = devices.waitForService();
		Logger.d("Device modified (profile), lastServiceEvent: " + lastServiceEvent);
		assertNotNull("Timeout reached.", lastServiceEvent);

		/*
		 * NOTE: The service should have been modified AFTER insertion,
		 * nevertheless it seems that when registration and modification happen
		 * almost in the same time, OSGi only generates a single SERVICE_ADDED
		 * event.
		 */
		ServiceReference ref = devices.getServiceReference();

		/*
		 * Verify that the device has been registered with the correct service
		 * properties
		 */
		assertEquals("category mismatch", EnOceanDevice.DEVICE_CATEGORY, ref.getProperty(org.osgi.service.device.Constants.DEVICE_CATEGORY));

		// TODO AAA: Replace the 5 following lines, and just check the existency
		// of these properties, and that they are String, and defined.
		assertEquals("RORG mismatch", Fixtures.STR_RORG, ref.getProperty(EnOceanDevice.RORG));
		assertEquals("FUNC mismatch", Fixtures.STR_FUNC, ref.getProperty(EnOceanDevice.FUNC));
		assertEquals("TYPE mismatch", Fixtures.STR_TYPE_1, ref.getProperty(EnOceanDevice.TYPE));
		assertEquals("CHIP_ID mismatch", Fixtures.STR_HOST_ID, ref.getProperty(EnOceanDevice.CHIP_ID));
		assertEquals("MANUFACTURER mismatch", Fixtures.STR_MANUFACTURER, ref.getProperty(EnOceanDevice.MANUFACTURER));

		log("Unget service with service reference: " + ref);
		getContext().ungetService(ref);
	}
}
