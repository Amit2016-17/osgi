/*
 * Copyright (c) OSGi Alliance (2018). All Rights Reserved.
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
package org.osgi.impl.bundle.component.annotations;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;

@interface Config {
	String prop() default "default.prop";
}

/**
 *
 *
 */
@Component(name = "testActivationFields")
public class ActivationFields {
	@Activate
	private ComponentContext	cc;

	@Activate
	private BundleContext		bc;

	@Activate
	private Map<String,Object>	props;

	@Activate
	private Config				configNames;

	@Activate
	private void activate() {
		System.out.println("Hello World!");
	}

	@Modified
	private void modified() {
		System.out.println("Modified World!");
	}

	@Deactivate
	private void deactivate() {
		System.out.println("Goodbye World!");
	}
}


