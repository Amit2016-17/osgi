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

package java.util;
public abstract class AbstractMap<K,V> implements java.util.Map<K,V> {
	public static class SimpleEntry<K,V> implements java.io.Serializable, java.util.Map.Entry<K,V> {
		public SimpleEntry(K var0, V var1) { } 
		public SimpleEntry(java.util.Map.Entry<? extends K,? extends V> var0) { } 
		public K getKey() { return null; }
		public V getValue() { return null; }
		public V setValue(V var0) { return null; }
	}
	public static class SimpleImmutableEntry<K,V> implements java.io.Serializable, java.util.Map.Entry<K,V> {
		public SimpleImmutableEntry(K var0, V var1) { } 
		public SimpleImmutableEntry(java.util.Map.Entry<? extends K,? extends V> var0) { } 
		public K getKey() { return null; }
		public V getValue() { return null; }
		public V setValue(V var0) { return null; }
	}
	protected AbstractMap() { } 
	public void clear() { }
	public boolean containsKey(java.lang.Object var0) { return false; }
	public boolean containsValue(java.lang.Object var0) { return false; }
	public V get(java.lang.Object var0) { return null; }
	public boolean isEmpty() { return false; }
	public java.util.Set<K> keySet() { return null; }
	public V put(K var0, V var1) { return null; }
	public void putAll(java.util.Map<? extends K,? extends V> var0) { }
	public V remove(java.lang.Object var0) { return null; }
	public int size() { return 0; }
	public java.util.Collection<V> values() { return null; }
}

