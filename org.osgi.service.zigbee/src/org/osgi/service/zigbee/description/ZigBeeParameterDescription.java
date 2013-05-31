/*
 * Copyright (c) OSGi Alliance (${year}). All Rights Reserved.
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


package org.osgi.service.zigbee.description;

import org.osgi.service.zigbee.datatype.ZigBeeDataTypeDescription;

/**
 * This interface represents a ZigBee parameter description
 * 
 * @version 1.0
 */
public interface ZigBeeParameterDescription {
	/**
	 * @return the parameter data type
	 */
	public ZigBeeDataTypeDescription getDataTypeDescription();

	/**
	 * checks whether the value object is conform to the parameter data type
	 * description
	 * 
	 * @param value The value to check
	 * @return true if value is conform otherwise returns false
	 */
	public boolean checkValue(Object value);
}
