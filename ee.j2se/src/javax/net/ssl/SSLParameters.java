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

package javax.net.ssl;
public class SSLParameters {
	public SSLParameters() { } 
	public SSLParameters(java.lang.String[] var0) { } 
	public SSLParameters(java.lang.String[] var0, java.lang.String[] var1) { } 
	public java.security.AlgorithmConstraints getAlgorithmConstraints() { return null; }
	public java.lang.String[] getCipherSuites() { return null; }
	public java.lang.String getEndpointIdentificationAlgorithm() { return null; }
	public boolean getNeedClientAuth() { return false; }
	public java.lang.String[] getProtocols() { return null; }
	public boolean getWantClientAuth() { return false; }
	public void setAlgorithmConstraints(java.security.AlgorithmConstraints var0) { }
	public void setCipherSuites(java.lang.String[] var0) { }
	public void setEndpointIdentificationAlgorithm(java.lang.String var0) { }
	public void setNeedClientAuth(boolean var0) { }
	public void setProtocols(java.lang.String[] var0) { }
	public void setWantClientAuth(boolean var0) { }
}

