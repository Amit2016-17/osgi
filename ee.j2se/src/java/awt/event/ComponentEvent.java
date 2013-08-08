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

package java.awt.event;
public class ComponentEvent extends java.awt.AWTEvent {
	public final static int COMPONENT_FIRST = 100;
	public final static int COMPONENT_HIDDEN = 103;
	public final static int COMPONENT_LAST = 103;
	public final static int COMPONENT_MOVED = 100;
	public final static int COMPONENT_RESIZED = 101;
	public final static int COMPONENT_SHOWN = 102;
	public ComponentEvent(java.awt.Component var0, int var1)  { super((java.lang.Object) null, 0); } 
	public java.awt.Component getComponent() { return null; }
}

