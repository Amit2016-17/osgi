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

package javax.naming.ldap;
public class InitialLdapContext extends javax.naming.directory.InitialDirContext implements javax.naming.ldap.LdapContext {
	public InitialLdapContext() throws javax.naming.NamingException { } 
	public InitialLdapContext(java.util.Hashtable<?,?> var0, javax.naming.ldap.Control[] var1) throws javax.naming.NamingException { } 
	public javax.naming.ldap.ExtendedResponse extendedOperation(javax.naming.ldap.ExtendedRequest var0) throws javax.naming.NamingException { return null; }
	public javax.naming.ldap.Control[] getConnectControls() throws javax.naming.NamingException { return null; }
	public javax.naming.ldap.Control[] getRequestControls() throws javax.naming.NamingException { return null; }
	public javax.naming.ldap.Control[] getResponseControls() throws javax.naming.NamingException { return null; }
	public javax.naming.ldap.LdapContext newInstance(javax.naming.ldap.Control[] var0) throws javax.naming.NamingException { return null; }
	public void reconnect(javax.naming.ldap.Control[] var0) throws javax.naming.NamingException { }
	public void setRequestControls(javax.naming.ldap.Control[] var0) throws javax.naming.NamingException { }
}

