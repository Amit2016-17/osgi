/*
 * Copyright (c) 2014 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
 */

package org.osgi.impl.service.dal;

import java.util.Map;
import org.osgi.service.dal.Function;
import org.osgi.service.dal.FunctionData;
import org.osgi.service.dal.FunctionEvent;
import org.osgi.service.dal.OperationMetadata;
import org.osgi.service.dal.PropertyMetadata;
import org.osgi.service.event.EventAdmin;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Common implementation of the simulated function.
 */
public abstract class SimulatedFunction extends SimulatedService implements Function {

	/** The property metadata. */
	protected final Map				propertyMetadata;

	/** The operation metadata. */
	protected final Map				operationMetadata;

	private final ServiceTracker	eventAdminTracker;

	/**
	 * Constructs a new simulated function with the specified arguments.
	 * 
	 * @param propertyMetadata The property metadata.
	 * @param operationMetadata The operation metadata.
	 * @param eventAdminTracker The event admin tracker.
	 */
	public SimulatedFunction(
			Map propertyMetadata,
			Map operationMetadata,
			ServiceTracker eventAdminTracker) {
		this.propertyMetadata = propertyMetadata;
		this.operationMetadata = operationMetadata;
		this.eventAdminTracker = eventAdminTracker;
	}

	public PropertyMetadata getPropertyMetadata(String propertyName) {
		return (null == this.propertyMetadata) ? null :
				(PropertyMetadata) this.propertyMetadata.get(propertyName);
	}

	public OperationMetadata getOperationMetadata(String operationName) {
		return (null == this.operationMetadata) ? null :
				(OperationMetadata) this.operationMetadata.get(operationName);
	}

	public Object getServiceProperty(String propName) {
		Object value = super.serviceRef.getProperty(propName);
		if (null == value) {
			throw new IllegalArgumentException("The property name is missing: " + propName);
		}
		return value;
	}

	public String[] getServicePropertyKeys() {
		return super.serviceRef.getPropertyKeys();
	}

	/**
	 * Unregisters the function from the service registry.
	 */
	public void remove() {
		super.serviceReg.unregister();
	}

	/**
	 * The child is responsible to publish an event with the current data.
	 * 
	 * @param propName The function property name.
	 * @throws IllegalArgumentException If the property is not supported.
	 */
	public abstract void publishEvent(String propName);

	/**
	 * Posts a new function property event through Event Admin service.
	 * 
	 * @param propName The function property name.
	 * @param propValue The function property value
	 */
	public void postEvent(String propName, FunctionData propValue) {
		final EventAdmin eventAdmin = (EventAdmin) this.eventAdminTracker.getService();
		if (null == eventAdmin) {
			throw new UnsupportedOperationException("The operation is not suported without Event Admin.");
		}
		FunctionEvent event = new FunctionEvent(
				FunctionEvent.TOPIC_PROPERTY_CHANGED,
				(String) this.getServiceProperty(Function.SERVICE_UID),
				propName,
				propValue);
		eventAdmin.postEvent(event);
	}
}
