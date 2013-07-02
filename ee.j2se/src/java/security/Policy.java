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

package java.security;
public abstract class Policy {
	public interface Parameters {
	}
	public final static java.security.PermissionCollection UNSUPPORTED_EMPTY_COLLECTION; static { UNSUPPORTED_EMPTY_COLLECTION = null; }
	public Policy() { } 
	public static java.security.Policy getInstance(java.lang.String var0, java.security.Policy.Parameters var1) throws java.security.NoSuchAlgorithmException { return null; }
	public static java.security.Policy getInstance(java.lang.String var0, java.security.Policy.Parameters var1, java.lang.String var2) throws java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException { return null; }
	public static java.security.Policy getInstance(java.lang.String var0, java.security.Policy.Parameters var1, java.security.Provider var2) throws java.security.NoSuchAlgorithmException { return null; }
	public java.security.Policy.Parameters getParameters() { return null; }
	public java.security.PermissionCollection getPermissions(java.security.CodeSource var0) { return null; }
	public java.security.PermissionCollection getPermissions(java.security.ProtectionDomain var0) { return null; }
	public static java.security.Policy getPolicy() { return null; }
	public java.security.Provider getProvider() { return null; }
	public java.lang.String getType() { return null; }
	public boolean implies(java.security.ProtectionDomain var0, java.security.Permission var1) { return false; }
	public void refresh() { }
	public static void setPolicy(java.security.Policy var0) { }
}

