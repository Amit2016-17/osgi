/*
 * Copyright (c) 2014 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
 */

package org.osgi.impl.service.dal.functions;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import org.osgi.framework.BundleContext;
import org.osgi.impl.service.dal.PropertyMetadataImpl;
import org.osgi.impl.service.dal.SimulatedFunction;
import org.osgi.service.dal.Function;
import org.osgi.service.dal.PropertyMetadata;
import org.osgi.service.dal.functions.BooleanControl;
import org.osgi.service.dal.functions.data.BooleanData;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Simulated {@code BooleanControl}.
 */
public final class SimulatedBooleanControl extends SimulatedFunction implements BooleanControl {

	private static final Map	PROPERTY_METADATA;
	private static final Map	OPERATION_METADATA	= null;

	private BooleanData			data;

	static {
		Map metadata = new HashMap();
		metadata.put(
				PropertyMetadata.ACCESS,
				new Integer(
						PropertyMetadata.ACCESS_READABLE |
								PropertyMetadata.ACCESS_WRITABLE |
								PropertyMetadata.ACCESS_EVENTABLE));
		PropertyMetadata propMetadata = new PropertyMetadataImpl(
				metadata, // metadata
				null,     // resolution
				null,     // enumValues
				null,     // minValue
				null);    // maxValue
		PROPERTY_METADATA = new HashMap();
		PROPERTY_METADATA.put(PROPERTY_DATA, propMetadata);
	}

	/**
	 * Constructs a new instance with the specified arguments.
	 * 
	 * @param functionProps The service properties.
	 * @param bc The bundle context to register the service.
	 * @param eventAdminTracker The event admin tracker to post events.
	 */
	public SimulatedBooleanControl(Dictionary functionProps, BundleContext bc, ServiceTracker eventAdminTracker) {
		super(PROPERTY_METADATA, OPERATION_METADATA, eventAdminTracker);
		this.data = new BooleanData(System.currentTimeMillis(), null, false);
		super.register(
				new String[] {BooleanControl.class.getName(), Function.class.getName()},
				addPropertyAndOperationNames(functionProps), bc);
	}

	public BooleanData getData() {
		return this.data;
	}

	public void setData(boolean data) {
		if (this.data.getValue() == data) {
			return; // nothing to do
		}
		BooleanData newData = new BooleanData(System.currentTimeMillis(), null, data);
		this.data = newData;
		super.postEvent(PROPERTY_DATA, newData);
	}

	public void reverse() {
		setData(this.data.getValue() ? false : true);
	}

	public void setTrue() {
		setData(true);
	}

	public void setFalse() {
		setData(false);
	}

	public void publishEvent(String propName) {
		if (!PROPERTY_DATA.equals(propName)) {
			throw new IllegalArgumentException("The property is not supported: " + propName);
		}
		reverse();
	}

	private static Dictionary addPropertyAndOperationNames(Dictionary functionProps) {
		functionProps.put(
				SERVICE_PROPERTY_NAMES,
				new String[] {PROPERTY_DATA});
		functionProps.put(
				SERVICE_OPERATION_NAMES,
				new String[] {
						OPERATION_REVERSE,
						OPERATION_SET_FALSE,
						OPERATION_SET_TRUE});
		return functionProps;
	}
}
