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
import java.security.PermissionCollection;
import java.security.ProtectionDomain;
import java.util.Enumeration;
import org.eclipse.osgi.framework.adaptor.BundleData;
import org.eclipse.osgi.framework.debug.Debug;
import org.osgi.framework.*;

public class BundleFragment extends AbstractBundle {

	/** The resolved host that this fragment is attached to */
	protected BundleLoaderProxy[] hosts;

	/**
	 * @param bundledata
	 * @param framework
	 * @throws BundleException
	 */
	public BundleFragment(BundleData bundledata, Framework framework) throws BundleException {
		super(bundledata, framework);
		hosts = null;
	}

	/**
	 * Load the bundle.
	 * @exception org.osgi.framework.BundleException
	 */
	protected void load() throws BundleException {
		if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
			if ((state & (INSTALLED)) == 0) {
				Debug.println("Bundle.load called when state != INSTALLED: " + this); //$NON-NLS-1$
				Debug.printStackTrace(new Exception("Stack trace")); //$NON-NLS-1$
			}
		}

		if (framework.isActive()) {
			SecurityManager sm = System.getSecurityManager();

			if (sm != null) {
				PermissionCollection collection = framework.permissionAdmin.createPermissionCollection(this);

				domain = new ProtectionDomain(null, collection);
			}

			try {
				bundledata.open(); /* make sure the BundleData is open */
			} catch (IOException e) {
				throw new BundleException(Msg.formatter.getString("BUNDLE_READ_EXCEPTION"), e); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Reload from a new bundle.
	 * This method must be called while holding the bundles lock.
	 *
	 * @param newBundle Dummy Bundle which contains new data.
	 * @return  true if an exported package is "in use". i.e. it has been imported by a bundle
	 * @exception org.osgi.framework.BundleException
	 */
	protected boolean reload(AbstractBundle newBundle) throws BundleException {
		if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
			if ((state & (INSTALLED | RESOLVED)) == 0) {
				Debug.println("Bundle.reload called when state != INSTALLED | RESOLVED: " + this); //$NON-NLS-1$
				Debug.printStackTrace(new Exception("Stack trace")); //$NON-NLS-1$
			}
		}

		boolean exporting = false;
		if (framework.isActive()) {
			if (hosts != null) {
				if (state == RESOLVED) {
					for (int i = 0; i < hosts.length; i++) {
						exporting = hosts[i].inUse();
						if (exporting)
							break;
					}
					hosts = null;
					// publish UNRESOLVED event here.
					state = INSTALLED;
					framework.publishBundleEvent(BundleEvent.UNRESOLVED, this);
				}
			}
		} else {
			/* close the outgoing jarfile */
			try {
				this.bundledata.close();
			} catch (IOException e) {
				// Do Nothing
			}
		}
		if (!exporting) {
			/* close the outgoing jarfile */
			try {
				this.bundledata.close();
			} catch (IOException e) {
				// Do Nothing
			}
		}

		this.bundledata = newBundle.bundledata;
		this.bundledata.setBundle(this);
		return (exporting);
	}

	/**
	 * Refresh the bundle. This is called by Framework.refreshPackages.
	 * This method must be called while holding the bundles lock.
	 * this.loader.unimportPackages must have already been called before calling
	 * this method!
	 *
	 * @exception org.osgi.framework.BundleException if an exported package is "in use". i.e. it has been imported by a bundle
	 */
	protected void refresh() throws BundleException {
		if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
			if ((state & (UNINSTALLED | INSTALLED | RESOLVED)) == 0) {
				Debug.println("Bundle.refresh called when state != UNINSTALLED | INSTALLED | RESOLVED: " + this); //$NON-NLS-1$
				Debug.printStackTrace(new Exception("Stack trace")); //$NON-NLS-1$
			}
		}

		if (state == RESOLVED) {
			hosts = null;
			state = INSTALLED;
			// Do not publish UNRESOLVED event here.  This is done by caller 
			// to resolve if appropriate.
		}
		manifestLocalization = null;
	}

	/**
	 * Unload the bundle.
	 * This method must be called while holding the bundles lock.
	 *
	 * @return  true if an exported package is "in use". i.e. it has been imported by a bundle
	 */
	protected boolean unload() {
		if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
			if ((state & (UNINSTALLED | INSTALLED | RESOLVED)) == 0) {
				Debug.println("Bundle.unload called when state != UNINSTALLED | INSTALLED | RESOLVED: " + this); //$NON-NLS-1$
				Debug.printStackTrace(new Exception("Stack trace")); //$NON-NLS-1$
			}
		}

		boolean exporting = false;
		if (framework.isActive()) {
			if (hosts != null) {
				if (state == RESOLVED) {
					for (int i = 0; i < hosts.length; i++) {
						exporting = hosts[i].inUse();
						if (exporting)
							break;
					}
					hosts = null;
					// publish UNRESOLVED event here.
					state = INSTALLED;
					framework.publishBundleEvent(BundleEvent.UNRESOLVED, this);
				}
				domain = null;
			}
		}
		if (!exporting){
			try {
				this.bundledata.close();
			} catch (IOException e) { // Do Nothing.
			}
		}

		return (exporting);
	}

	/**
	 * This method loads a class from the bundle.
	 *
	 * @param      name     the name of the desired Class.
	 * @param      checkPermission indicates whether a permission check should be done.
	 * @return     the resulting Class
	 * @exception  java.lang.ClassNotFoundException  if the class definition was not found.
	 */
	protected Class loadClass(String name, boolean checkPermission) throws ClassNotFoundException {
		if (checkPermission) {
			framework.checkAdminPermission();
			checkValid();
		}
		// cannot load a class from a fragment because there is no classloader
		// associated with fragments.
		throw new ClassNotFoundException(Msg.formatter.getString("BUNDLE_FRAGMENT_CNFE", name)); //$NON-NLS-1$
	}

	/**
	 * Find the specified resource in this bundle.
	 *
	 * This bundle's class loader is called to search for the named resource.
	 * If this bundle's state is <tt>INSTALLED</tt>, then only this bundle will
	 * be searched for the specified resource. Imported packages cannot be searched
	 * when a bundle has not been resolved.
	 *
	 * @param name The name of the resource.
	 * See <tt>java.lang.ClassLoader.getResource</tt> for a description of
	 * the format of a resource name.
	 * @return a URL to the named resource, or <tt>null</tt> if the resource could
	 * not be found or if the caller does not have
	 * the <tt>AdminPermission</tt>, and the Java Runtime Environment supports permissions.
	 * 
	 * @exception java.lang.IllegalStateException If this bundle has been uninstalled.
	 */
	public URL getResource(String name) {
		checkValid();
		// cannot get a resource for a fragment because there is no classloader
		// associated with fragments.
		return (null);

	}

	public Enumeration getResources(String name) {
		checkValid();
		// cannot get a resource for a fragment because there is no classloader
		// associated with fragments.
		return null;
	}

	/**
	 * Internal worker to start a bundle.
	 *
	 * @param persistent if true persistently record the bundle was started.
	 */
	protected void startWorker(boolean persistent) throws BundleException {
		if (framework.active) {
			if ((state & (STARTING | ACTIVE)) != 0) {
				return;
			}

			if (state == INSTALLED) {
				if (!framework.packageAdmin.resolveBundles(new Bundle[] {this})) {
					throw new BundleException(getResolutionFailureMessage());
				}
			}

			if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
				Debug.println("Bundle: Active sl = " + framework.startLevelManager.getStartLevel() + "; Bundle " + getBundleId() + " sl = " + getStartLevel()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}

			if (getStartLevel() <= framework.startLevelManager.getStartLevel()) {
				if (state == UNINSTALLED) {
					throw new BundleException(Msg.formatter.getString("BUNDLE_UNINSTALLED_EXCEPTION")); //$NON-NLS-1$
				}
				if (framework.active) {
					state = ACTIVE;

					if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
						Debug.println("->started " + this); //$NON-NLS-1$
					}

					framework.publishBundleEvent(BundleEvent.STARTED, this);
				}
			}
		}

		if (persistent) {
			setStatus(Constants.BUNDLE_STARTED, true);
		}
	}

	/**
	 * Internal worker to stop a bundle.
	 *
	 * @param persistent if true persistently record the bundle was stopped.
	 */
	protected void stopWorker(boolean persistent) throws BundleException {
		if (persistent) {
			setStatus(Constants.BUNDLE_STARTED, false);
		}

		if (framework.active) {
			if ((state & (STOPPING | RESOLVED | INSTALLED)) != 0) {
				return;
			}

			state = RESOLVED;

			if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
				Debug.println("->stopped " + this); //$NON-NLS-1$
			}

			framework.publishBundleEvent(BundleEvent.STOPPED, this);
		}
	}

	/**
	 * Provides a list of {@link ServiceReference}s for the services
	 * registered by this bundle
	 * or <code>null</code> if the bundle has no registered
	 * services.
	 *
	 * <p>The list is valid at the time
	 * of the call to this method, but the framework is a very dynamic
	 * environment and services can be modified or unregistered at anytime.
	 *
	 * @return An array of {@link ServiceReference} or <code>null</code>.
	 * @exception java.lang.IllegalStateException If the
	 * bundle has been uninstalled.
	 * @see ServiceRegistrationImpl
	 * @see ServiceReference
	 */
	public ServiceReference[] getRegisteredServices() {
		checkValid();
		// Fragments cannot have a BundleContext and therefore
		// cannot have any services registered.
		return null;
	}

	/**
	 * Provides a list of {@link ServiceReference}s for the
	 * services this bundle is using,
	 * or <code>null</code> if the bundle is not using any services.
	 * A bundle is considered to be using a service if the bundle's
	 * use count for the service is greater than zero.
	 *
	 * <p>The list is valid at the time
	 * of the call to this method, but the framework is a very dynamic
	 * environment and services can be modified or unregistered at anytime.
	 *
	 * @return An array of {@link ServiceReference} or <code>null</code>.
	 * @exception java.lang.IllegalStateException If the
	 * bundle has been uninstalled.
	 * @see ServiceReference
	 */
	public ServiceReference[] getServicesInUse() {
		checkValid();
		// Fragments cannot have a BundleContext and therefore
		// cannot have any services in use.
		return null;
	}

	public BundleLoaderProxy[] getHosts() {
		return hosts;
	}

	public boolean isFragment() {
		return true;
	}

	/**
	 * Adds a host bundle for this fragment.
	 * @param value the BundleHost to add to the set of host bundles
	 */
	protected boolean addHost(BundleLoaderProxy host) {
		if (host != null) {
			try {
				((BundleHost)host.getBundleHost()).attachFragment(this);
			} catch (BundleException be) {
				framework.publishFrameworkEvent(FrameworkEvent.ERROR, host.getBundleHost(), be);
				return false;
			}
		}
		if (hosts == null) {
			hosts = new BundleLoaderProxy[] {host};
			return true;
		}
		for (int i = 0; i < hosts.length; i++) {
			if (host.getBundleHost() == hosts[i].getBundleHost())
				return true; // already a host
		}
		BundleLoaderProxy[] newHosts = new BundleLoaderProxy[hosts.length + 1];
		System.arraycopy(hosts,0,newHosts,0,hosts.length);
		newHosts[newHosts.length - 1] = host; 
		return true;
	}

	public BundleLoader getBundleLoader() {
		// Fragments cannot have a BundleLoader.
		return null;
	}

	/**
	 * Return the current context for this bundle.
	 *
	 * @return BundleContext for this bundle.
	 */
	protected BundleContextImpl getContext() {
		// Fragments cannot have a BundleContext.
		return null;
	}
}