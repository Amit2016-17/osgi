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

package javax.swing;
public class DefaultComboBoxModel<E> extends javax.swing.AbstractListModel<E> implements java.io.Serializable, javax.swing.MutableComboBoxModel<E> {
	public DefaultComboBoxModel() { } 
	public DefaultComboBoxModel(java.util.Vector<E> var0) { } 
	public DefaultComboBoxModel(E[] var0) { } 
	public void addElement(E var0) { }
	public E getElementAt(int var0) { return null; }
	public int getIndexOf(java.lang.Object var0) { return 0; }
	public java.lang.Object getSelectedItem() { return null; }
	public int getSize() { return 0; }
	public void insertElementAt(E var0, int var1) { }
	public void removeAllElements() { }
	public void removeElement(java.lang.Object var0) { }
	public void removeElementAt(int var0) { }
	public void setSelectedItem(java.lang.Object var0) { }
}
