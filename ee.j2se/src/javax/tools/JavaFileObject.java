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

package javax.tools;
public interface JavaFileObject extends javax.tools.FileObject {
	public enum Kind {
		CLASS,
		HTML,
		OTHER,
		SOURCE;
		public final java.lang.String extension; { extension = null; }
	}
	javax.lang.model.element.Modifier getAccessLevel();
	javax.tools.JavaFileObject.Kind getKind();
	javax.lang.model.element.NestingKind getNestingKind();
	boolean isNameCompatible(java.lang.String var0, javax.tools.JavaFileObject.Kind var1);
}

