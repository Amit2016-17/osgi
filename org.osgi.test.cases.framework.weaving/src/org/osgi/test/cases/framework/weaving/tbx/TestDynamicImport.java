/*
 * Copyright (c) OSGi Alliance (2010). All Rights Reserved.
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
package org.osgi.test.cases.framework.weaving.tbx;

/**
*
*/
public class TestDynamicImport {

	public String toString() {
		return doDynamicImport();
	}

	private String doDynamicImport() {
		String dynamicTestName = System.getProperty("weaving.dynamic.name");
		try {
			return Class.forName(dynamicTestName).newInstance().toString();
		} catch (Throwable t) {
			throw new RuntimeException("Unexpected error", t);
		}
	}
}
