/*
 * Copyright (c) OSGi Alliance (2001, 2013). All Rights Reserved.
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

package javax.xml.ws.soap;
public final class AddressingFeature extends javax.xml.ws.WebServiceFeature {
	public enum Responses {
		ALL,
		ANONYMOUS,
		NON_ANONYMOUS;
	}
	public final static java.lang.String ID = "http://www.w3.org/2005/08/addressing/module";
	protected boolean required;
	public AddressingFeature() { } 
	public AddressingFeature(boolean var0) { } 
	public AddressingFeature(boolean var0, boolean var1) { } 
	public AddressingFeature(boolean var0, boolean var1, javax.xml.ws.soap.AddressingFeature.Responses var2) { } 
	public java.lang.String getID() { return null; }
	public javax.xml.ws.soap.AddressingFeature.Responses getResponses() { return null; }
	public boolean isRequired() { return false; }
}

