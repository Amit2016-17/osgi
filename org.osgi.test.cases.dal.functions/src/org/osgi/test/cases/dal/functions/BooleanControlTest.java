/*
 * Copyright (c) 2014 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
 */

package org.osgi.test.cases.dal.functions;

import java.util.Map;
import org.osgi.service.dal.DeviceException;
import org.osgi.service.dal.Function;
import org.osgi.service.dal.FunctionEvent;
import org.osgi.service.dal.OperationMetadata;
import org.osgi.service.dal.PropertyMetadata;
import org.osgi.service.dal.functions.BooleanControl;
import org.osgi.service.dal.functions.data.BooleanData;

/**
 * Validates the {@code BooleanControl} functions.
 */
public final class BooleanControlTest extends AbstractFunctionTest {

	/**
	 * Checks {@link BooleanControl#setTrue()} operation functionality.
	 * 
	 * @throws DeviceException If operation error is available.
	 */
	public void testSetTrue() throws DeviceException {
		super.testStepProxy.execute(
				FunctionsTestSteps.STEP_ID_AVAILABLE_BC,
				FunctionsTestSteps.STEP_MESSAGE_AVAILABLE_BC);
		Function[] booleanControls = super.getFunctions(BooleanControl.class.getName());
		for (int i = 0; i < booleanControls.length; i++) {
			BooleanControl currentBooleanControl = (BooleanControl) booleanControls[i];
			currentBooleanControl.setTrue();
			super.assertEquals(true, currentBooleanControl.getData());
			checkMetadata(currentBooleanControl.getOperationMetadata(BooleanControl.OPERATION_SET_TRUE));
		}
	}

	/**
	 * Checks {@link BooleanControl#setFalse()} operation functionality.
	 * 
	 * @throws DeviceException If operation error is available.
	 */
	public void testSetFalse() throws DeviceException {
		super.testStepProxy.execute(
				FunctionsTestSteps.STEP_ID_AVAILABLE_BC,
				FunctionsTestSteps.STEP_MESSAGE_AVAILABLE_BC);
		Function[] booleanControls = super.getFunctions(BooleanControl.class.getName());
		for (int i = 0; i < booleanControls.length; i++) {
			BooleanControl currentBooleanControl = (BooleanControl) booleanControls[i];
			currentBooleanControl.setFalse();
			super.assertEquals(false, currentBooleanControl.getData());
			checkMetadata(currentBooleanControl.getOperationMetadata(BooleanControl.OPERATION_SET_FALSE));
		}
	}

	/**
	 * Checks {@link BooleanControl#inverse()} operation functionality.
	 * 
	 * @throws DeviceException If operation error is available.
	 */
	public void testReverse() throws DeviceException {
		super.testStepProxy.execute(
				FunctionsTestSteps.STEP_ID_AVAILABLE_BC,
				FunctionsTestSteps.STEP_MESSAGE_AVAILABLE_BC);
		Function[] booleanControls = super.getFunctions(BooleanControl.class.getName());
		for (int i = 0; i < booleanControls.length; i++) {
			BooleanControl currentBooleanControl = (BooleanControl) booleanControls[i];
			BooleanData currentData = currentBooleanControl.getData();
			currentBooleanControl.inverse();
			super.assertEquals(currentData.getValue() ? false : true, currentBooleanControl.getData());
			checkMetadata(currentBooleanControl.getOperationMetadata(BooleanControl.OPERATION_INVERSE));
		}
	}

	/**
	 * Checks {@code BooleanControl} function events.
	 * 
	 * @throws DeviceException If an error is available while executing the
	 *         operation.
	 */
	public void testPropertyEvent() throws DeviceException {
		super.testStepProxy.execute(
				FunctionsTestSteps.STEP_ID_AVAILABLE_BC,
				FunctionsTestSteps.STEP_MESSAGE_AVAILABLE_BC);
		Function[] functions = getFunctions(
				BooleanControl.class.getName(), PropertyMetadata.ACCESS_EVENTABLE);
		BooleanControl booleanControl = (BooleanControl) functions[0];
		String functionUID = (String) booleanControl.getServiceProperty(Function.SERVICE_UID);
		FunctionEventHandler eventHandler = new FunctionEventHandler(super.getContext());
		eventHandler.register(functionUID, BooleanControl.PROPERTY_DATA);
		FunctionEvent functionEvent;
		BooleanData currentData = booleanControl.getData();
		try {
			booleanControl.inverse();
			functionEvent = eventHandler.getEvents(1)[0];
		} finally {
			eventHandler.unregister();
		}
		BooleanData propertyData = (BooleanData) functionEvent.getFunctionPropertyValue();
		super.assertEquals(!currentData.getValue(), propertyData);
		assertEquals(
				"The event function identifier is not correct!",
				functionUID,
				functionEvent.getFunctionUID());
		assertEquals(
				"The property name is not correct!",
				BooleanControl.PROPERTY_DATA,
				functionEvent.getFunctionPropertyName());
	}

	private void checkMetadata(OperationMetadata opMetadata) {
		if (null == opMetadata) {
			return;
		}
		assertNull("BooleanControl operation doesn't have parameters.",
				opMetadata.getParametersMetadata());
		assertNull("BooleanControl operation doesn't have return value.",
				opMetadata.getReturnValueMetadata());
		Map metadata = opMetadata.getMetadata();
		if (null == metadata) {
			return;
		}
		Object description = metadata.get(OperationMetadata.DESCRIPTION);
		if (null != description) {
			assertTrue("The operation description must be a string.", description instanceof String);
		}
	}
}
