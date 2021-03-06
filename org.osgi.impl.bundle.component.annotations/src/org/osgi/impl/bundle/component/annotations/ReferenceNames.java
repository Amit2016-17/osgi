/*
 * Copyright (c) OSGi Alliance (2012, 2013). All Rights Reserved.
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

import java.util.EventListener;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 *
 *
 */
@Component(name = "testReferenceNames")
public class ReferenceNames {
	/**
	 */
	@Activate
	private void activate() {
		System.out.println("Hello World!");
	}

	/**
	 */
	@Deactivate
	private void deactivate() {
		System.out.println("Goodbye World!");
	}

	@Reference
	private void bindNameBind(EventListener l) {
		System.out.println("Bind " + l);
	}

	@SuppressWarnings("unused")
	private void unbindNameBind(EventListener l) {
		System.out.println("Unind " + l);
	}

	@Reference
	private void addNameAdd(EventListener l) {
		System.out.println("Bind " + l);
	}

	@SuppressWarnings("unused")
	private void removeNameAdd(EventListener l) {
		System.out.println("Unind " + l);
	}

	@Reference
	private void setNameSet(EventListener l) {
		System.out.println("Bind " + l);
	}

	@SuppressWarnings("unused")
	private void unsetNameSet(EventListener l) {
		System.out.println("Unind " + l);
	}

	@Reference
	private void name(EventListener l) {
		System.out.println("Bind " + l);
	}

	@SuppressWarnings("unused")
	private void unname(EventListener l) {
		System.out.println("Unind " + l);
	}

}
