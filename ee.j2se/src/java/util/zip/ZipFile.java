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

package java.util.zip;
public class ZipFile implements java.io.Closeable, java.util.zip.ZipConstants {
	public final static int OPEN_DELETE = 4;
	public final static int OPEN_READ = 1;
	public ZipFile(java.io.File var0) throws java.io.IOException { } 
	public ZipFile(java.io.File var0, int var1) throws java.io.IOException { } 
	public ZipFile(java.io.File var0, int var1, java.nio.charset.Charset var2) throws java.io.IOException { } 
	public ZipFile(java.io.File var0, java.nio.charset.Charset var1) throws java.io.IOException { } 
	public ZipFile(java.lang.String var0) throws java.io.IOException { } 
	public ZipFile(java.lang.String var0, java.nio.charset.Charset var1) throws java.io.IOException { } 
	public void close() throws java.io.IOException { }
	public java.util.Enumeration<? extends java.util.zip.ZipEntry> entries() { return null; }
	protected void finalize() throws java.io.IOException { }
	public java.lang.String getComment() { return null; }
	public java.util.zip.ZipEntry getEntry(java.lang.String var0) { return null; }
	public java.io.InputStream getInputStream(java.util.zip.ZipEntry var0) throws java.io.IOException { return null; }
	public java.lang.String getName() { return null; }
	public int size() { return 0; }
}

