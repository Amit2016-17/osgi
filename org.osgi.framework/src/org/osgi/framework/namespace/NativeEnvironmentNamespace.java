/*
 * Copyright (c) OSGi Alliance (2012). All Rights Reserved.
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

package org.osgi.framework.namespace;

import org.osgi.framework.Constants;
import org.osgi.resource.Namespace;

/**
 * Native Environment Capability and Requirement Namespace.
 * 
 * <p>
 * This class defines the names for the attributes and directives for this
 * namespace. All unspecified capability attributes are of type {@code String}
 * and are used as arbitrary matching attributes for the capability. The values
 * associated with the specified directive and attribute keys are of type
 * {@code String}, unless otherwise indicated.
 * 
 * @Immutable
 * @version $Id$
 */
public final class NativeEnvironmentNamespace extends Namespace {

	/**
	 * Namespace name for native environment capabilities and requirements.
	 */
	public static final String	NATIVE_ENVIRONMENT_NAMESPACE		= "osgi.native.environment";

	/**
	 * The capability attribute contains alias values of the
	 * {@link Constants#FRAMEWORK_OS_NAME org.osgi.framework.os.name} launching
	 * property value according to the <a
	 * href="http://www.osgi.org/Specifications/Reference">OSGi Specification
	 * References</a>. The value of this attribute must be of type
	 * {@code List<String>}.
	 */
	public final static String	CAPABILITY_OSNAME_ATTRIBUTE			= "osgi.native.environment.osname";

	/**
	 * The capability attribute contains a {@code Version} parsed from the
	 * {@link Constants#FRAMEWORK_OS_VERSION org.osgi.framework.os.version}
	 * launching property value. The value of this attribute must be of type
	 * {@code Version}.
	 */
	public final static String	CAPABILITY_OSVERSION_ATTRIBUTE		= "osgi.native.environment.osversion";

	/**
	 * The capability attribute contains alias values of the
	 * {@link Constants#FRAMEWORK_PROCESSOR org.osgi.framework.processor}
	 * launching property value according to the <a
	 * href="http://www.osgi.org/Specifications/Reference">OSGi Specification
	 * References</a>. The value of this attribute must be of type
	 * {@code List<String>}.
	 */
	public final static String	CAPABILITY_PROCESSOR_ATTRIBUTE		= "osgi.native.environment.processor";

	/**
	 * The capability attribute contains the
	 * {@link Constants#FRAMEWORK_LANGUAGE org.osgi.framework.language}
	 * launching property value. The value of this attribute must be of type
	 * {@code String}.
	 */
	public final static String	CAPABILITY_LANGUAGE_ATTRIBUTE		= "osgi.native.environment.language";

	/**
	 * An optional attribute which frameworks may use to specify the native code
	 * paths associated with a native environment requirement. If specified, the
	 * value of this attribute must be of type {@code List<String>}
	 */
	public final static String	REQUIREMENT_NATIVE_PATHS_ATTRIBUTE	= "native.paths";

	private NativeEnvironmentNamespace() {
		// empty
	}
}
