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

package javax.management;
public class Notification extends java.util.EventObject {
	protected java.lang.Object source;
	public Notification(java.lang.String var0, java.lang.Object var1, long var2)  { super((java.lang.Object) null); } 
	public Notification(java.lang.String var0, java.lang.Object var1, long var2, long var3)  { super((java.lang.Object) null); } 
	public Notification(java.lang.String var0, java.lang.Object var1, long var2, long var3, java.lang.String var4)  { super((java.lang.Object) null); } 
	public Notification(java.lang.String var0, java.lang.Object var1, long var2, java.lang.String var3)  { super((java.lang.Object) null); } 
	public java.lang.String getMessage() { return null; }
	public long getSequenceNumber() { return 0l; }
	public long getTimeStamp() { return 0l; }
	public java.lang.String getType() { return null; }
	public java.lang.Object getUserData() { return null; }
	public void setSequenceNumber(long var0) { }
	public void setSource(java.lang.Object var0) { }
	public void setTimeStamp(long var0) { }
	public void setUserData(java.lang.Object var0) { }
}

