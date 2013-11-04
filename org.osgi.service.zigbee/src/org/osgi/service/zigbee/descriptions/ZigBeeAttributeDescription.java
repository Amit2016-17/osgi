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
 * This interface represents a ZigBee Attribute description
 * 
 * @version 1.0
 */
public interface ZigBeeAttributeDescription {

	/**
	 * @return the attribute identifier
	 */
	public int getId();

	/**
	 * @return The attribute name
	 */
	public String getName();

	/**
	 * @return The Attribute functional description
	 */
	public String getShortDescription();

	/**
	 * @return The attribute default value
	 */
	public Object getDefaultValue();

	/**
	 * @return true, if and only if the attribute is mandatory
	 */
	public boolean isMandatory();

	/**
	 * @return the true if and only if the attribute support subscription
	 */
	public boolean isReportable();

	/**
	 * @return true if the attribute is read only, false otherwise (i.e. if the
	 *         attribute is read/write or optionnaly writable (R*W))
	 */
	public boolean isReadOnly();

	/**
	 * @return A {@link ZigBeeDataTypeDescription} representing the attribute
	 *         data type
	 */
	public ZigBeeDataTypeDescription getDataTypeDescription();

	/**
	 * checks whether the value object is conform to the attribute data type
	 * description
	 * 
	 * @param value The value to check
	 * @return true if value is conform otherwise returns false
	 */
	public boolean checkValue(Object value);

	/**
	 * @return true if the attribute is part of a scene (cluster), false
	 *         otherwise
	 */
	public boolean isPartOfAScene();

}
