/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.osgi.framework.internal.core;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

public abstract class PackageSource implements KeyedElement {
	protected String id;

	public PackageSource(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public abstract SingleSourcePackage[] getSuppliers();

	public boolean compare(KeyedElement other) {
		return id.equals(((PackageSource) other).getId());
	}

	public int getKeyHashCode() {
		return id.hashCode();
	}

	public Object getKey() {
		return id;
	}

	public boolean isNullSource() {
		return false;
	}

	public abstract Class loadClass(String name, String pkgName, boolean providePkg);
	public abstract URL getResource(String name, String pkgName, boolean providePkg);
	public abstract Enumeration getResources(String name, String pkgName, boolean providePkg) throws IOException;
	public abstract Object getObject(String name, String pkgName, boolean providePkg);
}