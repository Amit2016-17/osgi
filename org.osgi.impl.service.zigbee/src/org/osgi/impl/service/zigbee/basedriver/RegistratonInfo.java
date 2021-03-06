/*
 * Copyright (c) OSGi Alliance (2016, 2020). All Rights Reserved.
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

package org.osgi.impl.service.zigbee.basedriver;

import java.util.Map;

import org.osgi.impl.service.zigbee.util.teststep.ZigBeeEventSourceImpl;
import org.osgi.service.zigbee.ZCLEventListener;

/**
 * Collect the registration infos related to a ZCLEventListener.
 * 
 * @author $id$
 *
 */
public class RegistratonInfo {
	public ZCLEventListener			eventListener;
	public Map<String,Object>		properties;
	public ZigBeeEventSourceImpl	eventSource;
}
