/*
 * Copyright (c) OSGi Alliance (2016). All Rights Reserved.
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

package org.osgi.test.cases.zigbee.mock;

import org.osgi.service.zigbee.ZCLAttribute;
import org.osgi.service.zigbee.descriptions.ZCLClusterDescription;
import org.osgi.test.cases.zigbee.config.file.ConfigurationFileReader;

/**
 * 
 *
 * Class used by the configuration file reader. see
 * {@link ConfigurationFileReader}
 * 
 * @author $Id: 1d84453d85ed9879dbad614548e2eb4fe5bc0c2b $
 */
public class ZCLClusterConf extends ZCLClusterImpl {

	private int[] commandIds;

	public ZCLClusterConf(int[] commandIds, ZCLAttribute[] attributes, ZCLClusterDescription desc) {
		super(commandIds, attributes, desc);
		this.commandIds = commandIds;
	}

	public int[] getCommandIds() {
		return commandIds;
	}

	public ZCLAttribute[] getAttributes() {
		return attributes;
	}
}