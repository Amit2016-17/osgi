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
package org.eclipse.osgi.internal.resolver;

import java.util.*;

import org.eclipse.osgi.framework.debug.Debug;
import org.eclipse.osgi.framework.debug.DebugOptions;
import org.eclipse.osgi.framework.internal.core.KeyedElement;
import org.eclipse.osgi.framework.internal.core.KeyedHashSet;
import org.eclipse.osgi.service.resolver.*;
import org.osgi.framework.Version;

public abstract class StateImpl implements State {
	transient private Resolver resolver;
	transient private StateDeltaImpl changes;
	transient private boolean resolving = false;
	transient private HashSet removalPendings = new HashSet();
	private boolean resolved = true;
	private long timeStamp = System.currentTimeMillis();
	private KeyedHashSet bundleDescriptions = new KeyedHashSet(false);
	private StateObjectFactory factory;
	private KeyedHashSet resolvedBundles = new KeyedHashSet();
	boolean fullyLoaded = false;

	// only used for lazy loading of BundleDescriptions
	private StateReader reader;

	private static long cumulativeTime;

	protected StateImpl() {
		// to prevent extra-package instantiation 
	}

	public boolean addBundle(BundleDescription description) {
		if (!basicAddBundle(description))
			return false;
		resolved = false;
		getDelta().recordBundleAdded((BundleDescriptionImpl) description);
		if (resolver != null)
			resolver.bundleAdded(description);
		return true;
	}

	public boolean updateBundle(BundleDescription newDescription) {
		BundleDescriptionImpl existing = (BundleDescriptionImpl) bundleDescriptions.get((BundleDescriptionImpl) newDescription);
		if (existing == null)
			return false;
		if (!bundleDescriptions.remove(existing))
			return false;
		resolvedBundles.remove(existing);
		if (!basicAddBundle(newDescription))
			return false;
		resolved = false;
		getDelta().recordBundleUpdated((BundleDescriptionImpl) newDescription);
		if (resolver != null) {
			boolean pending = existing.getDependents().length > 0;
			resolver.bundleUpdated(newDescription, existing, pending);
			if (pending){
				getDelta().recordBundleRemovalPending(existing);
				removalPendings.add(existing);
			}
			else {
				// an existing bundle has been updated with no dependents it can safely be unresolved now
				synchronized (this) {
					try {
						resolving = true;
						resolveBundle(existing, false, null, null, null, null);
					} finally {
						resolving = false;
					}
				}
			}
		}
		return true;
	}

	public BundleDescription removeBundle(long bundleId) {
		BundleDescription toRemove = getBundle(bundleId);
		if (toRemove == null || !removeBundle(toRemove))
			return null;
		return toRemove;
	}

	public boolean removeBundle(BundleDescription toRemove) {
		if (!bundleDescriptions.remove((KeyedElement) toRemove))
			return false;
		resolvedBundles.remove((KeyedElement) toRemove);
		resolved = false;
		getDelta().recordBundleRemoved((BundleDescriptionImpl) toRemove);
		if (resolver != null) {
			boolean pending = toRemove.getDependents().length > 0;
			resolver.bundleRemoved(toRemove, pending);
			if (pending) {
				getDelta().recordBundleRemovalPending((BundleDescriptionImpl) toRemove);
				removalPendings.add(toRemove);
			}
			else {
				// a bundle has been removed with no dependents it can safely be unresolved now
				synchronized (this) {
					try {
						resolving = true;
						resolveBundle(toRemove, false, null, null, null, null);
					}
					finally {
						resolving = false;
					}
				}
			}
		}
		return true;
	}

	public StateDelta getChanges() {
		return getDelta();
	}

	private StateDeltaImpl getDelta() {
		if (changes == null)
			changes = new StateDeltaImpl(this);
		return changes;
	}

	public BundleDescription[] getBundles(final String symbolicName) {
		final List bundles = new ArrayList();
		for (Iterator iter = bundleDescriptions.iterator(); iter.hasNext();) {
			BundleDescription bundle = (BundleDescription) iter.next();
			if (symbolicName.equals(bundle.getSymbolicName()))
				bundles.add(bundle);
		}
		return (BundleDescription[]) bundles.toArray(new BundleDescription[bundles.size()]);
	}

	public BundleDescription[] getBundles() {
		return (BundleDescription[]) bundleDescriptions.elements(new BundleDescription[bundleDescriptions.size()]);
	}

	public BundleDescription getBundle(long id) {
		BundleDescription result = (BundleDescription) bundleDescriptions.getByKey(new Long(id));
		if (result != null)
			return result;
		// need to look in removal pending bundles;
		for (Iterator iter = removalPendings.iterator(); iter.hasNext();) {
			BundleDescription removedBundle = (BundleDescription) iter.next();
			if (removedBundle.getBundleId() == id) // just return the first matching id
				return removedBundle;
		}
		return null;
	}

	// TODO: this does not comply with the spec	
	public BundleDescription getBundle(String name, Version version) {
		for (Iterator i = bundleDescriptions.iterator(); i.hasNext();) {
			BundleDescription current = (BundleDescription) i.next();
			if (name.equals(current.getSymbolicName()) && current.getVersion().equals(version))
				return current;
		}
		return null;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public boolean isResolved() {
		return resolved || isEmpty();
	}

	public void resolveConstraint(VersionConstraint constraint, BaseDescription supplier) {
		((VersionConstraintImpl)constraint).setSupplier(supplier);
	}

	public void resolveBundle(BundleDescription bundle, boolean status, BundleDescription[] hosts, ExportPackageDescription[] selectedExports, BundleDescription[] resolvedRequires, ExportPackageDescription[] resolvedImports) {
		if (!resolving)
			throw new IllegalStateException(); // TODO need error message here!
		BundleDescriptionImpl modifiable = (BundleDescriptionImpl) bundle;
		// must record the change before setting the resolve state to 
		// accurately record if a change has happened.
		getDelta().recordBundleResolved(modifiable, status);
		modifiable.setResolved(status);
		if (status) {
			resolveConstraints(modifiable, hosts, selectedExports, resolvedRequires, resolvedImports);
			resolvedBundles.add(modifiable);
		}
		else {
			// ensures no links are left 
			unresolveConstraints(modifiable);
			// remove the bundle from the resolved pool
			resolvedBundles.remove(modifiable);
		}
	}

	public void removeBundleComplete(BundleDescription bundle) {
		if (!resolving)
			throw new IllegalStateException(); // TODO need error message here!
		getDelta().recordBundleRemovalComplete((BundleDescriptionImpl) bundle);
		removalPendings.remove(bundle);
	}

	private void resolveConstraints(BundleDescriptionImpl bundle, BundleDescription[] hosts, ExportPackageDescription[] selectedExports, BundleDescription[] resolvedRequires, ExportPackageDescription[] resolvedImports) {
		HostSpecificationImpl hostSpec = (HostSpecificationImpl) bundle.getHost();
		if (hostSpec != null) {
			if (hosts != null) {
				hostSpec.setHosts(hosts);
				for (int i = 0; i < hosts.length; i++)
					((BundleDescriptionImpl)hosts[i]).addDependency(bundle);
			}
		}

		bundle.setSelectedExports(selectedExports);
		bundle.setResolvedRequires(resolvedRequires);
		bundle.setResolvedImports(resolvedImports);
		
		bundle.addDependencies(hosts);
		bundle.addDependencies(resolvedRequires);
		bundle.addDependencies(resolvedImports);
	}

	private void unresolveConstraints(BundleDescriptionImpl bundle) {
		HostSpecificationImpl host = (HostSpecificationImpl) bundle.getHost();
		if (host != null)
			host.setHosts(null);


		bundle.setSelectedExports(null);
		bundle.setResolvedImports(null);
		bundle.setResolvedRequires(null);

		bundle.removeDependencies();
	}

	private synchronized StateDelta resolve(boolean incremental, BundleDescription[] reResolve) {
		try {
			resolving = true;
			if (resolver == null)
				throw new IllegalStateException("no resolver set"); //$NON-NLS-1$
			fullyLoad();
			long start = 0;
			if (StateManager.DEBUG_PLATFORM_ADMIN_RESOLVER)
				start = System.currentTimeMillis();
			if (!incremental)
				flush();
			if (resolved && reResolve == null)
				return new StateDeltaImpl(this);
			if (removalPendings.size() > 0) {
				BundleDescription[] removed = (BundleDescription[]) removalPendings.toArray(new BundleDescription[removalPendings.size()]);
				reResolve = mergeBundles(reResolve, removed);
			}
			if (reResolve != null)
				resolver.resolve(reResolve);
			else
				resolver.resolve();
			resolved = true;

			StateDelta savedChanges = changes == null ? new StateDeltaImpl(this) : changes;
			changes = new StateDeltaImpl(this);

			if (StateManager.DEBUG_PLATFORM_ADMIN_RESOLVER) {
				long time = System.currentTimeMillis() - start;
				Debug.println("Time spent resolving: " + time); //$NON-NLS-1$
				cumulativeTime = cumulativeTime + time;
				DebugOptions.getDefault().setOption("org.eclipse.core.runtime.adaptor/resolver/timing/value", Long.toString(cumulativeTime)); //$NON-NLS-1$
			}

			return savedChanges;
		} finally {
			resolving = false;
		}
	}

	private BundleDescription[] mergeBundles(BundleDescription[] reResolve, BundleDescription[] removed) {
		if (reResolve == null)
			return removed; // just return all the removed bundles
		if (reResolve.length == 0)
			return reResolve; // if reResolve length==0 then we want to prevent pending removal
		// merge in all removal pending bundles that are not already in the list
		ArrayList result = new ArrayList(reResolve.length + removed.length);
		for (int i = 0; i < reResolve.length; i++)
			result.add(reResolve[i]);
		for (int i = 0; i < removed.length; i++) {
			boolean found = false;
			for (int j = 0; j < reResolve.length; j++) {
				if (removed[i] == reResolve[j]) {
					found = true;
					break;
				}
			}
			if (!found)
				result.add(removed[i]);
		}
		return (BundleDescription[]) result.toArray(new BundleDescription[result.size()]);
	}

	private void flush() {
		resolver.flush();
		resolved = false;
		if (resolvedBundles.isEmpty())
			return;
		BundleDescription[] bundles = getResolvedBundles();
		for (int i = 0; i < bundles.length; i++) {
			resolveBundle(bundles[i], false, null, null, null, null);
		}
		resolvedBundles.clear();
	}

	public StateDelta resolve() {
		return resolve(true, null);
	}

	public StateDelta resolve(boolean incremental) {
		return resolve(incremental, null);
	}

	public StateDelta resolve(BundleDescription[] reResolve) {
		return resolve(true, reResolve);
	}

	public void setOverrides(Object value) {
		throw new UnsupportedOperationException();
	}

	public BundleDescription[] getResolvedBundles() {
		return (BundleDescription[]) resolvedBundles.elements(new BundleDescription[resolvedBundles.size()]);
	}

	public boolean isEmpty() {
		return bundleDescriptions.isEmpty();
	}

	void setResolved(boolean resolved) {
		this.resolved = resolved;
	}

	boolean basicAddBundle(BundleDescription description) {
		((BundleDescriptionImpl) description).setContainingState(this);
		return bundleDescriptions.add((BundleDescriptionImpl) description);
	}

	void addResolvedBundle(BundleDescriptionImpl resolved) {
		resolvedBundles.add(resolved);
	}

	public ExportPackageDescription[] getExportedPackages() {
		fullyLoad();
		final List allExportedPackages = new ArrayList();
		for (Iterator iter = resolvedBundles.iterator(); iter.hasNext();) {
			BundleDescription bundle = (BundleDescription) iter.next();
			ExportPackageDescription[] bundlePackages = bundle.getSelectedExports();
			if (bundlePackages == null)
				continue;
			for (int i = 0; i < bundlePackages.length; i++)
				allExportedPackages.add(bundlePackages[i]);
		}
		for (Iterator iter = removalPendings.iterator(); iter.hasNext();) {
			BundleDescription bundle = (BundleDescription) iter.next();
			ExportPackageDescription[] bundlePackages = bundle.getSelectedExports();
			if (bundlePackages == null)
				continue;
			for (int i = 0; i < bundlePackages.length; i++)
				allExportedPackages.add(bundlePackages[i]);
		}
		return (ExportPackageDescription[]) allExportedPackages.toArray(new ExportPackageDescription[allExportedPackages.size()]);
	}

	BundleDescription[] getFragments(final BundleDescription host) {
		final List fragments = new ArrayList();
		for (Iterator iter = bundleDescriptions.iterator(); iter.hasNext();) {
			BundleDescription bundle = (BundleDescription) iter.next();
			HostSpecification hostSpec = bundle.getHost();
			
			if (hostSpec != null) {
				BundleDescription[] hosts = hostSpec.getHosts();
				if (hosts != null)
					for(int i = 0; i < hosts.length; i++)
						if (hosts[i] == host) {
							fragments.add(bundle);
							break;
						}
			}
		}
		return (BundleDescription[]) fragments.toArray(new BundleDescription[fragments.size()]);
	}

	public void setTimeStamp(long newTimeStamp) {
		timeStamp = newTimeStamp;
	}

	public StateObjectFactory getFactory() {
		return factory;
	}

	void setFactory(StateObjectFactory factory) {
		this.factory = factory;
	}
	public BundleDescription getBundleByLocation(String location) {
		for (Iterator i = bundleDescriptions.iterator(); i.hasNext();) {
			BundleDescription current = (BundleDescription) i.next();
			if (location.equals(current.getLocation()))
				return current;
		}
		return null;
	}

	public Resolver getResolver() {
		return resolver;
	}

	public void setResolver(Resolver newResolver) {
		if (resolver == newResolver)
			return;
		if (resolver != null) {
			Resolver oldResolver = resolver;
			resolver = null;
			oldResolver.setState(null);
		}
		resolver = newResolver;
		if (resolver == null)
			return;
		resolver.setState(this);
	}

	BundleDescription[] getRemovalPendings() {
		return (BundleDescription[]) removalPendings.toArray(new BundleDescription[removalPendings.size()]);
	}

	public synchronized ExportPackageDescription linkDynamicImport(BundleDescription importingBundle, String requestedPackage) {
		if (resolver == null)
			throw new IllegalStateException("no resolver set"); //$NON-NLS-1$
		fullyLoad();
		// ask the resolver to resolve our dynamic import
		ExportPackageDescriptionImpl result = (ExportPackageDescriptionImpl) resolver.resolveDynamicImport(importingBundle, requestedPackage);
		if (result == null)
			return null;
		// need to add the result to the list of resolved imports
		((BundleDescriptionImpl)importingBundle).addDynamicResolvedImport(result);
		return result;
	}

	void setReader(StateReader reader) {
		this.reader = reader;
	}
	StateReader getReader() {
		return reader;
	}
	synchronized void fullyLoad() {
		if (fullyLoaded == true)
			return;
		if (reader != null && reader.isLazyLoaded())
			reader.fullyLoad();
		fullyLoaded = true;
	}
}