/*
 * Copyright (c) OSGi Alliance (2015). All Rights Reserved.
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
package org.osgi.impl.bundle.metatype.annotations;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;

/**
 *
 *
 */
@Component(name = "testDesignateFactoryComponent")
@Designate(ocd = DesignateFactoryConfig.class, factory = true)
public class DesignateFactoryComponent {

	/**
	 */
	@Activate
	private void activate(DesignateFactoryConfig c) {
		System.out.println("Hello World!");
	}

	/**
	 */
	@Modified
	private void modified() {
		System.out.println("Modified World!");
	}

	/**
	 */
	@Deactivate
	private void deactivate() {
		System.out.println("Goodbye World!");
	}

}
