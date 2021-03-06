/*
 * Copyright (c) OSGi Alliance (2010, 2012). All Rights Reserved.
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
package org.osgi.test.cases.resolver.resolver.tb2;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.test.cases.resolver.resolver.tb1.Test;

public class Activator implements BundleActivator {

	public void start(BundleContext context) throws Exception {
		String testValue = new Test().getValue();
		ServiceReference< ? >[] refs = context.getServiceReferences(
				(String) null, "(" + Test.TEST_KEY + "=*)");
		if (refs == null)
			return; // not testing package package version
		String expectedValue = (String) refs[0].getProperty(Test.TEST_KEY);
		if (expectedValue == null)
			return; // not testing package package version
		if (!testValue.equals(expectedValue))
			throw new RuntimeException("Wrong test value: expected: " + expectedValue + " but was: " + testValue);
	}

	public void stop(BundleContext context) throws Exception {
	}

}
