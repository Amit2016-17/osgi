/*
 * Copyright (c) 2014 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
 */

package org.osgi.test.cases.dal.functions.data;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.osgi.service.dal.FunctionData;
import org.osgi.service.dal.functions.data.LevelData;
import org.osgi.test.cases.dal.functions.AbstractFunctionTest;

/**
 * Validates the {@code LevelData} data structure.
 */
public final class LevelDataTest extends AbstractFunctionTest {

	private static final BigDecimal	TEST_VALUE	= new BigDecimal("1.00001");
	private static final String		TEST_UNIT	= "test-unit";

	/**
	 * Checks {@link LevelData#equals(Object)} method.
	 */
	public void testEquals() {
		// check without metadata
		LevelData data = new LevelData(Long.MIN_VALUE, null, TEST_VALUE, null);
		assertEquals("The level data comparison is wrong!",
				data,
				data);
		assertEquals("The level data comparison is wrong!",
				data,
				new LevelData(Long.MIN_VALUE, null, TEST_VALUE, null));

		// check with metadata
		Map metadata = new HashMap();
		metadata.put(FunctionData.DESCRIPTION, "test-description");
		data = new LevelData(Long.MIN_VALUE, metadata, TEST_VALUE, TEST_UNIT);
		assertEquals("The level data comparison is wrong!",
				data,
				data);
		assertEquals("The level data comparison is wrong!",
				data,
				new LevelData(Long.MIN_VALUE, metadata, TEST_VALUE, TEST_UNIT));

		// check with fields map
		Map fields = new HashMap();
		fields.put(LevelData.FIELD_LEVEL, TEST_VALUE);
		fields.put(FunctionData.FIELD_TIMESTAMP, new Long(Long.MIN_VALUE));
		fields.put(FunctionData.FIELD_METADATA, metadata);
		fields.put(LevelData.FIELD_UNIT, TEST_UNIT);
		data = new LevelData(fields);
		assertEquals("The level data comparison is wrong!",
				data,
				data);
		assertEquals("The level data comparison is wrong!",
				data,
				new LevelData(Long.MIN_VALUE, metadata, TEST_VALUE, TEST_UNIT));
	}

	/**
	 * Checks {@link LevelData#compareTo(Object)} with {@code LevelData}.
	 */
	public void testComparison() {
		// check without metadata
		LevelData data = new LevelData(Long.MIN_VALUE, null, TEST_VALUE, null);
		assertEquals(
				"The level data comparison is wrong!",
				0, data.compareTo(data));
		assertEquals(
				"The level data comparison is wrong!",
				0, data.compareTo(new LevelData(Long.MIN_VALUE, null, TEST_VALUE, null)));

		// check with metadata
		Map metadata = new HashMap();
		metadata.put(FunctionData.DESCRIPTION, "test-description");
		data = new LevelData(Long.MIN_VALUE, metadata, TEST_VALUE, TEST_UNIT);
		assertEquals(
				"The level data comparison is wrong!",
				0, data.compareTo(data));
		assertEquals(
				"The level data comparison is wrong!",
				0, data.compareTo(new LevelData(Long.MIN_VALUE, metadata, TEST_VALUE, TEST_UNIT)));

		// check with fields map
		Map fields = new HashMap();
		fields.put(LevelData.FIELD_LEVEL, TEST_VALUE);
		fields.put(FunctionData.FIELD_TIMESTAMP, new Long(Long.MIN_VALUE));
		fields.put(FunctionData.FIELD_METADATA, metadata);
		fields.put(LevelData.FIELD_UNIT, TEST_UNIT);
		data = new LevelData(fields);
		assertEquals(
				"The level data comparison is wrong!",
				0, data.compareTo(data));
		assertEquals("The level data comparison is wrong!",
				0, data.compareTo(new LevelData(Long.MIN_VALUE, metadata, TEST_VALUE, TEST_UNIT)));
	}

	/**
	 * Checks {@link LevelData#hashCode()}.
	 */
	public void testHashCode() {
		// check without metadata
		LevelData data = new LevelData(Long.MIN_VALUE, null, TEST_VALUE, null);
		assertEquals("The level data hash code is wrong!",
				data.hashCode(),
				data.hashCode());
		assertEquals("The level data hash code is wrong!",
				data.hashCode(),
				(new LevelData(Long.MIN_VALUE, null, TEST_VALUE, null)).hashCode());

		// check with metadata
		Map metadata = new HashMap();
		metadata.put(FunctionData.DESCRIPTION, "test-description");
		data = new LevelData(Long.MIN_VALUE, metadata, TEST_VALUE, TEST_UNIT);
		assertEquals("The level data hash code is wrong!",
				data.hashCode(),
				data.hashCode());
		assertEquals("The level data hash code is wrong!",
				data.hashCode(),
				(new LevelData(Long.MIN_VALUE, metadata, TEST_VALUE, TEST_UNIT)).hashCode());

		// check with fields map
		Map fields = new HashMap();
		fields.put(LevelData.FIELD_LEVEL, TEST_VALUE);
		fields.put(FunctionData.FIELD_TIMESTAMP, new Long(Long.MIN_VALUE));
		fields.put(FunctionData.FIELD_METADATA, metadata);
		fields.put(LevelData.FIELD_UNIT, TEST_UNIT);
		data = new LevelData(fields);
		assertEquals("The level data hash code is wrong!",
				data.hashCode(),
				data.hashCode());
		assertEquals("The level data hash code is wrong!",
				data.hashCode(),
				(new LevelData(Long.MIN_VALUE, metadata, TEST_VALUE, TEST_UNIT)).hashCode());
	}

	/**
	 * Checks {@code LevelData} field values.
	 */
	public void testFields() {
		// check without metadata
		LevelData data = new LevelData(Long.MIN_VALUE, null, TEST_VALUE, null);
		checkLevelDataFields(Long.MIN_VALUE, null, TEST_VALUE, null, data);

		// check with metadata
		Map metadata = new HashMap();
		metadata.put(FunctionData.DESCRIPTION, "test-description");
		data = new LevelData(Long.MIN_VALUE, metadata, TEST_VALUE, TEST_UNIT);
		checkLevelDataFields(Long.MIN_VALUE, metadata, TEST_VALUE, TEST_UNIT, data);

		// check with fields map
		Map fields = new HashMap();
		fields.put(LevelData.FIELD_LEVEL, TEST_VALUE);
		fields.put(FunctionData.FIELD_TIMESTAMP, new Long(Long.MIN_VALUE));
		fields.put(FunctionData.FIELD_METADATA, metadata);
		fields.put(LevelData.FIELD_UNIT, TEST_UNIT);
		data = new LevelData(fields);
		checkLevelDataFields(Long.MIN_VALUE, metadata, TEST_VALUE, TEST_UNIT, data);
	}

	/**
	 * Checks the {@code LevelData} construction with an invalid fields.
	 */
	public void testInvalidFields() {
		Map fields = new HashMap();
		fields.put(LevelData.FIELD_LEVEL, "wrong-type");
		checkInvalidFieldType(fields);

		BigDecimal testValue = new BigDecimal("1.0001");
		fields.clear();
		fields.put(LevelData.FIELD_LEVEL, testValue);
		fields.put(LevelData.FIELD_UNIT, Boolean.TRUE);
		checkInvalidFieldType(fields);

		fields.clear();
		fields.put(LevelData.FIELD_LEVEL, testValue);
		fields.put(LevelData.FIELD_METADATA, Boolean.TRUE);
		checkInvalidFieldType(fields);

		fields.clear();
		fields.put(LevelData.FIELD_LEVEL, testValue);
		fields.put(LevelData.FIELD_TIMESTAMP, Boolean.TRUE);
		checkInvalidFieldType(fields);

		fields.clear();
		try {
			new LevelData(fields);
			fail("The level data is built with empty fields.");
		} catch (IllegalArgumentException iae) {
			// go ahead, it's expected
		}

		try {
			new LevelData(null);
			fail("The level data is built with null fields");
		} catch (NullPointerException npe) {
			// go ahead, it's expected
		}
	}

	private void checkInvalidFieldType(Map fields) {
		try {
			new LevelData(fields);
			fail("The level data is built with invalid fields: " + fields);
		} catch (ClassCastException cce) {
			// go ahead, it's expected
		}
	}

	private void checkLevelDataFields(long timestamp, Map metadata, BigDecimal level, String unit, LevelData actualData) {
		super.assertFunctionDataFields(timestamp, metadata, actualData);
		// unit
		assertEquals(
				"The metadata is not correct!",
				unit,
				actualData.getUnit());

		// value
		assertEquals(
				"The level is not correct!",
				level,
				actualData.getLevel());
	}
}
