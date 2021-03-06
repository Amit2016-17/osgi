/*
* Copyright (c) OSGi Alliance (2010, 2014). All Rights Reserved.
* 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/ 
package org.osgi.test.cases.metatype.junit;

import org.osgi.framework.Bundle;

/**
 * Run base class tests using metatype xml with R6 spelling for "Character" type
 * and that varies element ordering in line with R6 relaxing OCD and designate
 * element order.
 */
public class AdMaxOnlyR6Test extends AdMaxOnlyTest {
	
	protected Bundle getTestBundle() throws Exception {
		return install("tb3-r6.jar");
	}
}
