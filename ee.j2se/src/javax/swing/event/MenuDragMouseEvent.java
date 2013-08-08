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

package javax.swing.event;
public class MenuDragMouseEvent extends java.awt.event.MouseEvent {
	public MenuDragMouseEvent(java.awt.Component var0, int var1, long var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, javax.swing.MenuElement[] var10, javax.swing.MenuSelectionManager var11)  { super((java.awt.Component) null, 0, 0l, 0, 0, 0, 0, false, 0); } 
	public MenuDragMouseEvent(java.awt.Component var0, int var1, long var2, int var3, int var4, int var5, int var6, boolean var7, javax.swing.MenuElement[] var8, javax.swing.MenuSelectionManager var9)  { super((java.awt.Component) null, 0, 0l, 0, 0, 0, 0, false, 0); } 
	public javax.swing.MenuSelectionManager getMenuSelectionManager() { return null; }
	public javax.swing.MenuElement[] getPath() { return null; }
}

