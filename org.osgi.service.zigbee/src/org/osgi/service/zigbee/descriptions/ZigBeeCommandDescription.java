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
 * This interface represents a ZigBee Cluster description
 * 
 * @version 1.0
 */
public interface ZigBeeCommandDescription {
	/**
	 * @return the command identifier
	 */
	public int getId();

	/**
	 * @return the command name
	 */
	public String getName();

	/**
	 * @return the command functional description
	 */
	public String getShortDescription();

	/**
	 * @return true, if and only if the command is mandatory
	 */
	public boolean isMandatory();

	/**
	 * @return an array of command's parameters description
	 */
	public ZigBeeParameterDescription[] getParameterDescriptions();

	/**
	 * Serialize javaValues to byte[]. This byte[] can them be used in
	 * org.osgi.service.zigbee.ZigBeeCommand.invoke(byte[] bytes,
	 * ZigBeeCommandHandler handler) throws ZigBeeException.
	 * 
	 * @param javaValues ordered java values
	 * @return serialized javaValues as a byte[]
	 */
	public byte[] serialize(Object[] javaValues);

	/**
	 * Deserialize byte[] to javaValues. This byte[] is expected to be a result
	 * of the invocation of org.osgi.service.zigbee.ZigBeeCommand.invoke(byte[]
	 * bytes, ZigBeeCommandHandler handler) throws ZigBeeException.
	 * 
	 * @param bytes ordered javaValues' as a byte[]
	 * @return deserialized byte[] as javaValues
	 */
	public Object[] deserialize(byte[] bytes);
}
