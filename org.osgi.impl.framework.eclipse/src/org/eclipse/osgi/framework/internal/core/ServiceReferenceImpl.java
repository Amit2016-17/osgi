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

import org.osgi.framework.ServiceReference;

/**
 * A reference to a service.
 *
 * The framework returns ServiceReference objects from the
 * {@link BundleContextImpl#getServiceReference BundleContext.getServiceReference} and
 * {@link BundleContextImpl#getServiceReferences BundleContext.getServiceReferences} methods.
 * <p>A ServiceReference may be shared between bundles and
 * can be used to examine the properties of the service and to
 * get the service object
 * (See {@link BundleContextImpl#getService BundleContext.getService}).
 * <p>A registered service <i>may</i> have multiple, distinct ServiceReference
 * objects which refer to it.
 * However these ServiceReference objects will have
 * the same <code>hashCode</code> and the <code>equals</code> method
 * will return <code>true</code> when compared.
 */

public class ServiceReferenceImpl implements ServiceReference, Comparable {
	/** Registered Service object. */
	protected ServiceRegistrationImpl registration;

	/**
	 * Construct a reference.
	 *
	 */
	protected ServiceReferenceImpl(ServiceRegistrationImpl registration) {
		this.registration = registration;
	}

	/**
	 * Get the value of a service's property.
	 *
	 * <p>This method will continue to return property values after the
	 * service has been unregistered. This is so that references to
	 * unregistered service can be interrogated.
	 * (For example: ServiceReference objects stored in the log.)
	 *
	 * @param key Name of the property.
	 * @return Value of the property or <code>null</code> if there is
	 * no property by that name.
	 */
	public Object getProperty(String key) {
		return (registration.getProperty(key));
	}

	/**
	 * Get the list of key names for the service's properties.
	 *
	 * <p>This method will continue to return the keys after the
	 * service has been unregistered. This is so that references to
	 * unregistered service can be interrogated.
	 * (For example: ServiceReference objects stored in the log.)
	 *
	 * @return The list of property key names.
	 */
	public String[] getPropertyKeys() {
		return (registration.getPropertyKeys());
	}

	/**
	 * Return the bundle which registered the service.
	 *
	 * <p>This method will always return <code>null</code> when the
	 * service has been unregistered. This can be used to
	 * determine if the service has been unregistered.
	 *
	 * @return The bundle which registered the service.
	 */
	public org.osgi.framework.Bundle getBundle() {
		return (registration.getBundle());
	}

	/**
	 * Return the list of bundles which are using the service.
	 *
	 * <p>This method will always return <code>null</code> when the
	 * service has been unregistered.
	 *
	 * @return The array of bundles using the service or null if
	 * no bundles are using the service.
	 * @see BundleContextImpl#getService
	 */
	public org.osgi.framework.Bundle[] getUsingBundles() {
		return (registration.getUsingBundles());
	}

	/**
	 * Return the classes under which the referenced service was registered.
	 *
	 * @return array of class names.
	 */
	protected String[] getClasses() {
		return (registration.clazzes);
	}

	/**
	 * Return the serviceid of the ServiceRegistration.
	 *
	 * @return service.id of the service
	 */
	protected long getId() {
		return (registration.serviceid);
	}

	/**
	 * Return the serviceranking of the ServiceRegistration.
	 *
	 * @return service.ranking of the service
	 */
	protected int getRanking() {
		return (registration.serviceranking);
	}

	/**
	 * Returns a hash code value for the object.
	 *
	 * @return  a hash code value for this object.
	 */
	public int hashCode() {
		return (registration.hashCode());
	}

	/**
	 * Indicates whether some other object is "equal to" this one.
	 *
	 * @param   obj   the reference object with which to compare.
	 * @return  <code>true</code> if this object is the same as the obj
	 *          argument; <code>false</code> otherwise.
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return (true);
		}

		if (!(obj instanceof ServiceReferenceImpl)) {
			return (false);
		}

		ServiceReferenceImpl other = (ServiceReferenceImpl) obj;

		return (registration == other.registration);
	}

	/**
	 * Return a string representation of this reference.
	 *
	 * @return String
	 */
	public String toString() {
		return (registration.toString());
	}

	/**
	 *
	 *  Compares two service objects and returns an indication as to which is higher ranked
	 *  based on service ranking and service_ID.
	 *
	 * The service with the higher service ranking (as specified in its service.ranking property)
	 * is defined to be higher ranked.
	 *
	 * If there is a tie in ranking, the service with the lowest
	 * service ID (as specified in its service.id property); that is,
	 * the service that was registered first is returned.
	 *
	 * This is the same algorithm used by BundleContext.getServiceReference.
	 *
	 * @return		int
	 *                                  int = 0 if this object's ranking and
	 *                                  service id are equivalent
	 *
	 *                                  int < 0 if this object's ranking is lower
	 *                                  than the argument's ranking or if the
	 *                                  rankings are equal, this object's service
	 *                                  id is greater than the argument's service
	 *                                  id.
	 *
	 *                                  int > 0 if this object's ranking is higher
	 *                                  than than the argument's ranking or if the
	 *                                  rankings are equal, this object's service
	 *                                  id is less than the argument's service id.
	 *
	 *
	 * @param		object
	 *					an object to compare the receiver to
	 * @exception	ClassCastException
	 *					if the argument can not be converted
	 *					into something comparable with the
	 *					receiver.
	 */
	public int compareTo(Object object) {
		ServiceReferenceImpl other = (ServiceReferenceImpl) object;

		int compare = this.getRanking() - other.getRanking();

		if (compare == 0) /* rankings are equal */{ /* check service id */
			return (int) (other.getId() - this.getId());
		}

		return compare;
	}
}