/*
 * Copyright (c) OSGi Alliance (2016, 2017). All Rights Reserved.
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

package org.osgi.service.cdi;

import org.osgi.framework.Bundle;

/**
 * CdiEvents are sent by the CDI extender and received by registered CdiListener
 * services.
 *
 * @author $Id$
 */
public final class CdiEvent {

	/**
	 * @param type
	 * @param bundle
	 * @param extenderBundle
	 */
	public CdiEvent(CdiContainerState type, Bundle bundle, Bundle extenderBundle) {
		this(type, bundle, extenderBundle, null, null);
	}

	/**
	 * @param type
	 * @param bundle
	 * @param extenderBundle
	 * @param dependencies
	 */
	public CdiEvent(CdiContainerState type, Bundle bundle, Bundle extenderBundle, String dependencies) {
		this(type, bundle, extenderBundle, dependencies, null);
	}

	/**
	 * @param type
	 * @param bundle
	 * @param extenderBundle
	 * @param dependencies
	 * @param cause
	 */
	public CdiEvent(CdiContainerState type, Bundle bundle, Bundle extenderBundle, String dependencies, Throwable cause) {
		this.type = type;
		this.bundleId = bundle.getBundleId();
		this.extenderBundleId = extenderBundle.getBundleId();
		this.dependencies = dependencies;
		this.cause = cause.getMessage();
		this.timestamp = System.currentTimeMillis();
	}

	/**
	 * @return the bundle who's CDI container triggered this event
	 */
	public long getBundleId() {
		return bundleId;
	}

	/**
	 * @return the cause of the event if there was one
	 */
	public String getCause() {
		return cause;
	}

	/**
	 * @return the bundle of the CDI extender
	 */
	public long getExtenderBundleId() {
		return extenderBundleId;
	}

	/**
	 * @return the payload associated with this event
	 */
	public String getDependencies() {
		return dependencies;
	}

	/**
	 * @return the timestamp of the event
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @return the state of this event
	 */
	public CdiContainerState getType() {
		return type;
	}

	@Override
	public String toString() {
		if (string == null) {
			StringBuilder sb = new StringBuilder();

			sb.append("{type:'");
			sb.append(this.type);
			sb.append("', timestamp:");
			sb.append(this.timestamp);
			sb.append(", bundle:");
			sb.append(this.bundleId);
			sb.append(", extenderBundle:");
			sb.append(this.extenderBundleId);
			if (this.dependencies != null) {
				sb.append(", payload:'");
				sb.append(this.dependencies);
			}
			if (this.cause != null) {
				sb.append("', cause:'");
				sb.append(this.cause);
			}
			sb.append("'}");

			string = sb.toString();
		}
		return string;
	}

	private final long				bundleId;
	private final String			cause;
	private final long				extenderBundleId;
	private final String dependencies;
	private final long timestamp;
	private final CdiContainerState type;
	private volatile String			string;

}
