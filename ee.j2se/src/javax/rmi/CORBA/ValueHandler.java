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

package javax.rmi.CORBA;
public interface ValueHandler {
	java.lang.String getRMIRepositoryID(java.lang.Class var0);
	org.omg.SendingContext.RunTime getRunTimeCodeBase();
	boolean isCustomMarshaled(java.lang.Class var0);
	java.io.Serializable readValue(org.omg.CORBA.portable.InputStream var0, int var1, java.lang.Class var2, java.lang.String var3, org.omg.SendingContext.RunTime var4);
	java.io.Serializable writeReplace(java.io.Serializable var0);
	void writeValue(org.omg.CORBA.portable.OutputStream var0, java.io.Serializable var1);
}

