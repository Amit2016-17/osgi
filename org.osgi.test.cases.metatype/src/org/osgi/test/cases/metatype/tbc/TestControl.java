/*
 * $Header$
 * 
 * Copyright (c) The OSGi Alliance (2004). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.test.cases.metatype.tbc;

import java.util.Arrays;
import java.util.Collection;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.MetaTypeInformation;
import org.osgi.service.metatype.MetaTypeService;
import org.osgi.service.metatype.ObjectClassDefinition;
import org.osgi.test.cases.util.DefaultTestBundleControl;

/**
 * Get a Meta Type Service and do diverse tests.
 * 
 * @author left
 * @version $Revision$
 */
public class TestControl extends DefaultTestBundleControl {

	private Bundle			bundle;
	private MetaTypeService	mts;

	/**
	 * Creates a new instance of TestControl
	 */
	public TestControl() {

	}

	/**
	 * Check if the meta type service is present.
	 * 
	 * @return
	 * @see org.osgi.test.cases.util.DefaultTestBundleControl#checkPrerequisites()
	 */
	public boolean checkPrerequisites() {
		boolean result;
		ServiceReference ref;

		ref = getContext().getServiceReference(MetaTypeService.class.getName());
		if (ref == null) {
			log("Service MetaTypeService is required to run this test. Install the service and try again.");
			result = false;
		}
		else {
			mts = (MetaTypeService) getContext().getService(ref);
			result = true;
		}

		return result;
	}

	/**
	 * Install the test bundle
	 * 
	 * @see org.osgi.test.cases.util.DefaultTestBundleControl#setState()
	 */
	public void setState() throws Exception {
		bundle = installBundle("tb1.jar");
	}

	/**
	 * Test MetaTypeService implementation
	 */
	public void testMetaTypeService() {
		MetaTypeInformation mti;

		mti = mts.getMetaTypeInformation(bundle);
		assertNotNull(
				"MetaTypeService.getMetaTypeInformation() return an object (as expected)",
				mti);
	}

	/**
	 * Test MetaTypeInformation implementation
	 */
	public void testMetaTypeInformation() {
		MetaTypeInformation mti;

		mti = mts.getMetaTypeInformation(bundle);

		// Test the method getBundle()
		assertSame("MetaTypeInformation.getBundle()", mti.getBundle(), bundle);

		// Test the method getPids()
		assertEquals("MetaTypeInformation.getPids()", mti.getPids().length, 1);
		assertNotNull(
				"MetaTypeInformation.getPids()[0] is not null (as expected)",
				mti.getPids()[0]);
	}

	/**
	 * Test MetaTypeProvider implementation
	 */
	public void testMetaTypeProvider() {
		MetaTypeInformation mti;
		ObjectClassDefinition ocd;
		String[] expectedLocales;
		String[] locales;

		mti = mts.getMetaTypeInformation(bundle);

		// Test the method getObjectClassDefinition() with invalid id
		try {
			ocd = mti.getObjectClassDefinition("", "du");
			assertNull(
					"MetaTypeProvider.getObjectClassDefinition() returns null (as expected)",
					ocd);
		}
		catch (IllegalArgumentException ex) {
			// Ignore this exception
		}

		// Test the method getObjectClassDefinition() with invalid locale
		try {
			ocd = mti.getObjectClassDefinition("com.acme.23456789", "abc");
			assertNull(
					"MetaTypeProvider.getObjectClassDefinition() returns null (as expected)",
					ocd);
		}
		catch (IllegalArgumentException ex) {
			// Ignore this exception
		}

		// Test the method getObjectClassDefinition() with valid id and locale
		ocd = mti.getObjectClassDefinition("com.acme.ocd1", "du");
		assertNotNull(
				"MetaTypeProvider.getObjectClassDefinition() cannot return null (as expected)",
				ocd);

		// Test the method getLocales()
		locales = mti.getLocales();
		assertEquals(".getLocales()", 2, locales.length);

		expectedLocales = new String[] {"du", "du_NL"};
		assertTrue("MetaTypeProvider.getLocales()", isArrayEquals(
				expectedLocales, locales));
	}

	/**
	 * Test ObjectClassDefinition implementation
	 */
	public void testObjectClassDefinition() throws Exception {
		MetaTypeInformation mti;
		ObjectClassDefinition ocd;

		mti = mts.getMetaTypeInformation(bundle);

		// Test the methods with default locale
		ocd = mti.getObjectClassDefinition("com.acme.ocd1", null);
		assertEquals("ObjectClassDefinition.getID()", ocd.getID(),
				"com.acme.ocd1");
		assertEquals("ObjectClassDefinition.getName()", "Person(default)", ocd
				.getName());
		assertEquals("ObjectClassDefinition.getDescription()",
				"Description(default)", ocd.getDescription());
		assertEquals("ObjectClassDefinition.getAttributeDefinitions()", 2, ocd
				.getAttributeDefinitions(ObjectClassDefinition.OPTIONAL).length);
		assertEquals("ObjectClassDefinition.getAttributeDefinitions()", 2, ocd
				.getAttributeDefinitions(ObjectClassDefinition.REQUIRED).length);
		assertEquals("ObjectClassDefinition.getAttributeDefinitions()", 4, ocd
				.getAttributeDefinitions(ObjectClassDefinition.ALL).length);
		assertNull("ObjectClassDefinition.getIcon()", ocd.getIcon(32));
		assertNotNull("ObjectClassDefinition.getIcon()", ocd.getIcon(16));

		// Test the methods with Dutch locale
		ocd = mti.getObjectClassDefinition("com.acme.ocd1", "du");
		assertEquals("ObjectClassDefinition.getID()", ocd.getID(),
				"com.acme.ocd1");
		assertEquals("ObjectClassDefinition.getName()", "Persoon(du)", ocd
				.getName());
		assertEquals("ObjectClassDefinition.getDescription()",
				"De beschrijving", ocd.getDescription());
		assertEquals("ObjectClassDefinition.getAttributeDefinitions()", 2, ocd
				.getAttributeDefinitions(ObjectClassDefinition.OPTIONAL).length);
		assertEquals("ObjectClassDefinition.getAttributeDefinitions()", 2, ocd
				.getAttributeDefinitions(ObjectClassDefinition.REQUIRED).length);
		assertEquals("ObjectClassDefinition.getAttributeDefinitions()", 4, ocd
				.getAttributeDefinitions(ObjectClassDefinition.ALL).length);
		assertNull("ObjectClassDefinition.getIcon()", ocd.getIcon(32));
		assertNotNull("ObjectClassDefinition.getIcon()", ocd.getIcon(16));

		// Test the methods with Dutch (du_NL) locale
		ocd = mti.getObjectClassDefinition("com.acme.ocd1", "du_NL");
		assertEquals("ObjectClassDefinition.getID()", ocd.getID(),
				"com.acme.ocd1");
		assertEquals("ObjectClassDefinition.getName()", "Persoon(du_NL)", ocd
				.getName());
		assertEquals("ObjectClassDefinition.getDescription()",
				"De beschrijving", ocd.getDescription());
		assertEquals("ObjectClassDefinition.getAttributeDefinitions()", 2, ocd
				.getAttributeDefinitions(ObjectClassDefinition.OPTIONAL).length);
		assertEquals("ObjectClassDefinition.getAttributeDefinitions()", 2, ocd
				.getAttributeDefinitions(ObjectClassDefinition.REQUIRED).length);
		assertEquals("ObjectClassDefinition.getAttributeDefinitions()", 4, ocd
				.getAttributeDefinitions(ObjectClassDefinition.ALL).length);
		assertNull("ObjectClassDefinition.getIcon()", ocd.getIcon(32));
		assertNotNull("ObjectClassDefinition.getIcon()", ocd.getIcon(16));
	}

	/**
	 * Test AttributeDefinition implementation
	 */
	public void testAttributeDefinition() {
		boolean found;
		int count;
		AttributeDefinition[] attributes;
		MetaTypeInformation mti;
		ObjectClassDefinition ocd;
		String[] options;
		String[] expectedOptions;

		mti = mts.getMetaTypeInformation(bundle);

		// Get an object for tests
		ocd = mti.getObjectClassDefinition("com.acme.ocd1", "du");
		attributes = ocd.getAttributeDefinitions(ObjectClassDefinition.ALL);

		// Find the attribute 'sex' and run some tests
		found = false;
		for (int i = 0; i < attributes.length; i++) {
			if (attributes[i].getID().equals("sex")) {
				// Test the method getName()
				assertNotNull(
						"AttributeDefinition.getName() is not null (as expected)",
						attributes[i].getName());
				assertEquals("AttributeDefinition.getName()", "Geslacht",
						attributes[i].getName());

				// Test the method getDescription()
				assertNotNull(
						"AttributeDefinition.getDescription()  is not null (as expected)",
						attributes[i].getDescription());
				assertEquals("AttributeDefinition.getDescription()",
						"Beschrijving", attributes[i].getDescription());

				// Test the method getCardinality()
				assertEquals("AttributeDefinition.getCardinality()", 0,
						attributes[i].getCardinality());

				// Test the method getType()
				assertEquals("AttributeDefinition.getType()",
						AttributeDefinition.STRING, attributes[i].getType());

				// Test the method getOptionValues()
				options = attributes[i].getOptionValues();
				assertEquals("AttributeDefinition.getOptionValues()", 4,
						options.length);

				expectedOptions = new String[] {"male", "female", "yes", "no"};
				assertTrue(
						"AttributeDefinition.getOptionValues() does not return the expected values",
						isArrayEquals(expectedOptions, options));

				// Test the method getOptionLabels()
				options = attributes[i].getOptionLabels();
				assertEquals("AttributeDefinition.getOptionLabels()", 4,
						options.length);

				expectedOptions = new String[] {"Mannelijk", "Vrouwelijk",
						"Ja", "Nee"};
				assertTrue(
						"AttributeDefinition.getOptionLabels() does not return the expected values",
						isArrayEquals(expectedOptions, options));

				// Test the method validate
				options = new String[] {"male", "female", "yes", "no"};
				for (int j = 0; j < options.length; j++) {
					assertEquals("AttributeDefinition.validate()", "",
							attributes[i].validate(options[j]));
				}
				assertTrue(
						"AttributeDefinition.validate() cannot return true with a incorrect value",
						!(attributes[i].validate("incorrect").equals("")));

				// Test the method getDefaultValue()
				assertTrue("AttributeDefinition.getDefaultValue()",
						isArrayEquals(new String[] {""}, attributes[i]
								.getDefaultValue()));

				found = true;
			}
		}
		assertTrue("Attribute with id 'sex' found (as expected)", found);

		// Get other object for tests
		ocd = mti.getObjectClassDefinition("com.acme.ocd2", "du");
		attributes = ocd.getAttributeDefinitions(ObjectClassDefinition.ALL);

		// Execute attribute type tests
		count = 0;
		for (int i = 0; i < attributes.length; i++) {
			if (attributes[i].getID().equals("boolean")) {
				count++;
				assertEquals(
						"A boolean attribute must be of type AttributeDefinition.BOOLEAN",
						AttributeDefinition.BOOLEAN, attributes[i].getType());
			}
			else
				if (attributes[i].getID().equals("byte")) {
					count++;
					assertEquals(
							"A byte attribute must be of type AttributeDefinition.BYTE",
							AttributeDefinition.BYTE, attributes[i].getType());
				}
				else
					if (attributes[i].getID().equals("character")) {
						count++;
						assertEquals(
								"A character attribute must be of type AttributeDefinition.CHARACTER",
								AttributeDefinition.CHARACTER, attributes[i]
										.getType());
					}
					else
						if (attributes[i].getID().equals("double")) {
							count++;
							assertEquals(
									"A double attribute must be of type AttributeDefinition.DOUBLE",
									AttributeDefinition.DOUBLE, attributes[i]
											.getType());
						}
						else
							if (attributes[i].getID().equals("float")) {
								count++;
								assertEquals(
										"A float attribute must be of type AttributeDefinition.FLOAT",
										AttributeDefinition.FLOAT,
										attributes[i].getType());
							}
							else
								if (attributes[i].getID().equals("integer")) {
									count++;
									assertEquals(
											"A integer attribute must be of type AttributeDefinition.INTEGER",
											AttributeDefinition.INTEGER,
											attributes[i].getType());
								}
								else
									if (attributes[i].getID().equals("long")) {
										count++;
										assertEquals(
												"A long attribute must be of type AttributeDefinition.LONG",
												AttributeDefinition.LONG,
												attributes[i].getType());
									}
									else
										if (attributes[i].getID().equals(
												"short")) {
											count++;
											assertEquals(
													"A short attribute must be of type AttributeDefinition.SHORT",
													AttributeDefinition.SHORT,
													attributes[i].getType());
										}
										else
											if (attributes[i].getID().equals(
													"string")) {
												count++;
												assertEquals(
														"A string attribute must be of type AttributeDefinition.STRING",
														AttributeDefinition.STRING,
														attributes[i].getType());
											}
		}

		assertEquals("All types must be tested", attributes.length, count);
	}

	/**
	 * Test MetaTypeService when the bundle has an own implementation of MetaTypeProvider
	 */
	public void testBundleMetaTypeProvider() throws Exception {
		Bundle tb2;
		MetaTypeInformation mti;
		ObjectClassDefinition ocd;
		ServiceReference sr;

		tb2 = installBundle("tb2.jar");
		tb2.start();

		// Check if the service is registered
		sr = getContext().getServiceReference(
				"org.osgi.service.metatype.MetaTypeProvider");
		assertNotNull("Checking if the service is registered", sr);

		// Get an object for tests
		mti = mts.getMetaTypeInformation(tb2);
		assertEquals("Checking the number of PIDs", 1, mti.getPids().length);

		ocd = mti.getObjectClassDefinition("br.org.cesar.ocd1", "pt_BR");
		assertEquals("Checking the implementation class",
				"org.osgi.test.cases.metatype.tb2.ObjectClassDefinitionImpl", ocd
						.getClass().getName());

		tb2.stop();
		tb2.uninstall();
	}

	/**
	 * Uninstall the test bundle
	 * 
	 * @see org.osgi.test.cases.util.DefaultTestBundleControl#clearState()
	 */
	public void clearState() throws Exception {
		bundle.uninstall();
	}

	/**
	 * Compare two arrays in an elements order independent manner.
	 * 
	 * @param _array1 The first array to compare
	 * @param _array2 The second array to compare
	 * @return <code>true</code> If the two arrays have the same elements
	 *         <code>false</code> If the two arrays do not have the same
	 *         elements
	 */
	private boolean isArrayEquals(Object[] _array1, Object[] _array2) {
		boolean result;
		Collection collection;

		result = (_array1.length == _array2.length);

		if (result) {
			collection = Arrays.asList(_array1);

			for (int i = 0; i < _array2.length && result; i++) {
				result = collection.contains(_array2[i]);
			}
		}

		return result;
	}
	
}