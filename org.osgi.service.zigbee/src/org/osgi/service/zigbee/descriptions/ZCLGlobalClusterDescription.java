/*
 * Copyright (c) OSGi Alliance (2013). All Rights Reserved.
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

package org.osgi.service.zigbee.descriptions;

/**
 * This interface represents Cluster global description
 * 
 * @version 1.0
 */
public interface ZCLGlobalClusterDescription {

	/**
	 * @return the cluster identifier
	 */
	public int getClusterId();

	/**
	 * @return the cluster name
	 */
	public String getClusterName();

	/**
	 * @return the cluster functional description
	 */
	public String getClusterDescription();

	/**
	 * @return the cluster functional domain
	 */
	public String getClusterFunctionalDomain();

	/**
	 * @return a ZCLClusterDescription representing the client cluster
	 *         description
	 */
	public ZCLClusterDescription getClientClusterDescription();

	/**
	 * @return a ZCLClusterDescription representing the server cluster
	 *         description
	 */
	public ZCLClusterDescription getServerClusterDescription();

}
