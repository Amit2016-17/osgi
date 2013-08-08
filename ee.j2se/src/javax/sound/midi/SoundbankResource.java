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

package javax.sound.midi;
public abstract class SoundbankResource {
	protected SoundbankResource(javax.sound.midi.Soundbank var0, java.lang.String var1, java.lang.Class<?> var2) { } 
	public abstract java.lang.Object getData();
	public java.lang.Class<?> getDataClass() { return null; }
	public java.lang.String getName() { return null; }
	public javax.sound.midi.Soundbank getSoundbank() { return null; }
}

