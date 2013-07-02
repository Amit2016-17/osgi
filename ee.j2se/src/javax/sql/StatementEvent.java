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

package javax.sql;
public class StatementEvent extends java.util.EventObject {
	public StatementEvent(javax.sql.PooledConnection var0, java.sql.PreparedStatement var1)  { super((java.lang.Object) null); } 
	public StatementEvent(javax.sql.PooledConnection var0, java.sql.PreparedStatement var1, java.sql.SQLException var2)  { super((java.lang.Object) null); } 
	public java.sql.SQLException getSQLException() { return null; }
	public java.sql.PreparedStatement getStatement() { return null; }
}

