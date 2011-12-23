/*
 * Copyright (c) OSGi Alliance (2009, 2011). All Rights Reserved.
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

package org.osgi.test.cases.framework.junit.filter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.test.support.MockFactory;
import org.osgi.test.support.OSGiTestCase;

public abstract class AbstractFilterTests extends OSGiTestCase {

	private static final Matcher	nullMatcher	= new Matcher() {
													public boolean matches(
															Filter f) {
														boolean result = f
																.match((Dictionary) null);
														result &= f
																.matchCase((Dictionary) null);
														result &= f
																.matches(null);
														return result;
													}
												};

	public abstract Filter createFilter(String filterString)
			throws InvalidSyntaxException;

	Hashtable getProperties() {
		Hashtable props = new Hashtable();
		props.put("room", "bedroom");
		props.put("channel", new Object[] {new Integer(34), "101"});
		props.put("status", "(on\\)*");
		List list = new ArrayList(10);
		list.add(new Long(150));
		list.add("100");
		props.put("max record time", list);
		props.put("canrecord", "true(x)");
		props.put("shortvalue", new Short((short) 1000));
		props.put("intvalue", new Integer(100000));
		props.put("longvalue", new Long(10000000000L));
		props.put("bytevalue", new Byte((byte) 10));
		props.put("floatvalue", new Float(1.01));
		props.put("doublevalue", new Double(2.01));
		props.put("charvalue", new Character('A'));
		props.put("booleanvalue", new Boolean(true));
		props.put("weirdvalue", new Hashtable());
		props.put("primintarrayvalue", new int[] {1, 2, 3});
		props.put("primlongarrayvalue", new long[] {1, 2, 3});
		props.put("primbytearrayvalue", new byte[] {(byte) 1, (byte) 2,
				(byte) 3});
		props.put("primshortarrayvalue", new short[] {(short) 1, (short) 2,
				(short) 3});
		props.put("primfloatarrayvalue", new float[] {(float) 1.1, (float) 2.2,
				(float) 3.3});
		props.put("primdoublearrayvalue", new double[] {1.1, 2.2, 3.3});
		props.put("primchararrayvalue", new char[] {'A', 'b', 'C', 'd'});
		props.put("primbooleanarrayvalue", new boolean[] {false});
		props.put("bigintvalue", new BigInteger("4123456"));
		props.put("bigdecvalue", new BigDecimal("4.123456"));
		props.put("*", "foo");
		props.put("!  ab", "b");
		props.put("|   ab", "b");
		props.put("&    ab", "b");
		props.put("!", "c");
		props.put("|", "c");
		props.put("&", "c");
		props.put("empty", "");
		props.put("space", new Character(' '));
		return props;
	}

	public void testCaseInsensitive() throws InvalidSyntaxException {
		final Matcher matcher = new Matcher() {
			final Dictionary		props	= getProperties();
			final ServiceReference	ref		= newDictionaryServiceReference(props);

			public boolean matches(Filter f) {
				boolean result = f.match(props);
				result &= f.match(ref);
				return result;
			}
		};
		assertFilterTrue("(room=*)", matcher);
		assertFilterTrue("(room=bedroom)", matcher);
		assertFilterTrue("(room~= B E D R O O M )", matcher);
		assertFilterFalse("(room=abc)", matcher);
		assertFilterTrue(" ( room >=aaaa)", matcher);
		assertFilterFalse("(room <=aaaa)", matcher);
		assertFilterTrue("  ( room =b*) ", matcher);
		assertFilterTrue("  ( room =*m) ", matcher);
		assertFilterTrue("(room=bed*room)", matcher);
		assertFilterTrue("  ( room =b*oo*m) ", matcher);
		assertFilterTrue("  ( room =*b*oo*m*) ", matcher);
		assertFilterFalse("  ( room =b*b*  *m*) ", matcher);
		assertFilterTrue("  (& (room =bedroom) (channel ~= 34))", matcher);
		assertFilterFalse("  (&  (room =b*)  (room =*x) (channel=34))", matcher);
		assertFilterTrue("(| (room =bed*)(channel=222)) ", matcher);
		assertFilterTrue("(| (room =boom*)(channel=101)) ", matcher);
		assertFilterTrue("  (! (room =ab*b*oo*m*) ) ", matcher);
		assertFilterTrue("  (status =\\(o*\\\\\\)\\*) ", matcher);
		assertFilterTrue("  (canRecord =true\\(x\\)) ", matcher);
		assertFilterTrue("(max Record Time <=140) ", matcher);
		assertFilterTrue("(shortValue >= 100) ", matcher);
		assertFilterTrue("(intValue <= 100001) ", matcher);
		assertFilterTrue("(longValue >= 10000000000 ) ", matcher);
		assertFilterTrue(
				"  (  &  (  byteValue <= 100  )  (  byteValue >= 10  )  )  ",
				matcher);
		assertFilterFalse("(weirdValue = 100) ", matcher);
		assertFilterTrue("(bigIntValue =4123456) ", matcher);
		assertFilterTrue("(bigDecValue =4.123456) ", matcher);
		assertFilterTrue("(floatValue >= 1.0) ", matcher);
		assertFilterTrue("(doubleValue <= 2.011) ", matcher);
		assertFilterTrue("(charValue ~=a) ", matcher);
		assertFilterTrue("(booleanValue = true) ", matcher);
		assertFilterTrue("(primIntArrayValue = 1) ", matcher);
		assertFilterTrue("(primLongArrayValue = 2) ", matcher);
		assertFilterTrue("(primByteArrayValue = 3) ", matcher);
		assertFilterTrue("(primShortArrayValue = 1) ", matcher);
		assertFilterTrue("(primFloatArrayValue = 1.1) ", matcher);
		assertFilterTrue("(primDoubleArrayValue = 2.2) ", matcher);
		assertFilterTrue("(primCharArrayValue ~=D) ", matcher);
		assertFilterTrue("(primBooleanArrayValue = false ) ", matcher);
		assertFilterTrue(
				"(& (| (room =d*m) (room =bed*) (room=abc)) (! (channel=999)))",
				matcher);
		assertFilterTrue("(*=foo)", matcher);
		assertFilterTrue("(!  ab=b)", matcher);
		assertFilterTrue("(|   ab=b)", matcher);
		assertFilterTrue("(&=c)", matcher);
		assertFilterTrue("(!=c)", matcher);
		assertFilterTrue("(|=c)", matcher);
		assertFilterTrue("(&    ab=b)", matcher);
		assertFilterFalse("(!ab=*)", matcher);
		assertFilterFalse("(|ab=*)", matcher);
		assertFilterFalse("(&ab=*)", matcher);
		assertFilterTrue("(empty=)", matcher);
		assertFilterTrue("(empty=*)", matcher);
		assertFilterTrue("(space= )", matcher);
		assertFilterTrue("(space=*)", matcher);
	}

	public void testCaseSensitive() throws InvalidSyntaxException {
		final Matcher matcher = new Matcher() {
			final Hashtable	props	= getProperties();

			public boolean matches(Filter f) {
				boolean result = f.matches(props);
				result &= f.matchCase(props);
				return result;
			}
		};
		assertFilterTrue("(room=*)", matcher);
		assertFilterTrue("(room=bedroom)", matcher);
		assertFilterTrue("(room~= B E D R O O M )", matcher);
		assertFilterFalse("(room=abc)", matcher);
		assertFilterTrue(" ( room >=aaaa)", matcher);
		assertFilterFalse("(room <=aaaa)", matcher);
		assertFilterTrue("  ( room =b*) ", matcher);
		assertFilterTrue("  ( room =*m) ", matcher);
		assertFilterTrue("(room=bed*room)", matcher);
		assertFilterTrue("  ( room =b*oo*m) ", matcher);
		assertFilterTrue("  ( room =*b*oo*m*) ", matcher);
		assertFilterFalse("  ( room =b*b*  *m*) ", matcher);
		assertFilterTrue("  (& (room =bedroom) (channel ~= 34))", matcher);
		assertFilterFalse("  (&  (room =b*)  (room =*x) (channel=34))", matcher);
		assertFilterTrue("(| (room =bed*)(channel=222)) ", matcher);
		assertFilterTrue("(| (room =boom*)(channel=101)) ", matcher);
		assertFilterTrue("  (! (room =ab*b*oo*m*) ) ", matcher);
		assertFilterTrue("  (status =\\(o*\\\\\\)\\*) ", matcher);
		assertFilterTrue("  (canrecord =true\\(x\\)) ", matcher);
		assertFilterTrue("(max record time <=140) ", matcher);
		assertFilterTrue("(shortvalue >= 100) ", matcher);
		assertFilterTrue("(intvalue <= 100001) ", matcher);
		assertFilterTrue("(longvalue >= 10000000000 ) ", matcher);
		assertFilterTrue(
				"  (  &  (  bytevalue <= 100  )  (  bytevalue >= 10  )  )  ",
				matcher);
		assertFilterFalse("(weirdvalue = 100) ", matcher);
		assertFilterTrue("(bigintvalue =4123456) ", matcher);
		assertFilterTrue("(bigdecvalue =4.123456) ", matcher);
		assertFilterTrue("(floatvalue >= 1.0) ", matcher);
		assertFilterTrue("(doublevalue <= 2.011) ", matcher);
		assertFilterTrue("(charvalue ~=a) ", matcher);
		assertFilterTrue("(booleanvalue = true ) ", matcher);
		assertFilterTrue("(primintarrayvalue = 1) ", matcher);
		assertFilterTrue("(primlongarrayvalue = 2) ", matcher);
		assertFilterTrue("(primbytearrayvalue = 3) ", matcher);
		assertFilterTrue("(primshortarrayvalue = 1) ", matcher);
		assertFilterTrue("(primfloatarrayvalue = 1.1) ", matcher);
		assertFilterTrue("(primdoublearrayvalue = 2.2) ", matcher);
		assertFilterTrue("(primchararrayvalue ~=D) ", matcher);
		assertFilterTrue("(primbooleanarrayvalue = false) ", matcher);
		assertFilterTrue(
				"(& (| (room =d*m) (room =bed*) (room=abc)) (! (channel=999)))",
				matcher);
		assertFilterTrue("(*=foo)", matcher);
		assertFilterTrue("(!  ab=b)", matcher);
		assertFilterTrue("(|   ab=b)", matcher);
		assertFilterTrue("(&=c)", matcher);
		assertFilterTrue("(!=c)", matcher);
		assertFilterTrue("(|=c)", matcher);
		assertFilterTrue("(&    ab=b)", matcher);
		assertFilterFalse("(!ab=*)", matcher);
		assertFilterFalse("(|ab=*)", matcher);
		assertFilterFalse("(&ab=*)", matcher);
		assertFilterTrue("(empty=)", matcher);
		assertFilterTrue("(empty=*)", matcher);
		assertFilterTrue("(space= )", matcher);
		assertFilterTrue("(space=*)", matcher);
	}

	public void testInvalidValues() throws InvalidSyntaxException {
		final Matcher matcher = new Matcher() {
			final Hashtable			props	= getProperties();
			final ServiceReference	ref		= newDictionaryServiceReference(props);

			public boolean matches(Filter f) {
				boolean result = f.match(props);
				result &= f.matchCase(props);
				result &= f.matches(props);
				result &= f.match(ref);
				return result;
			}
		};
		assertFilterTrue("(intvalue=*)", matcher);
		assertFilterFalse("(intvalue=b)", matcher);
		assertFilterFalse("(intvalue=)", matcher);
		assertFilterTrue("(longvalue=*)", matcher);
		assertFilterFalse("(longvalue=b)", matcher);
		assertFilterFalse("(longvalue=)", matcher);
		assertFilterTrue("(shortvalue=*)", matcher);
		assertFilterFalse("(shortvalue=b)", matcher);
		assertFilterFalse("(shortvalue=)", matcher);
		assertFilterTrue("(bytevalue=*)", matcher);
		assertFilterFalse("(bytevalue=b)", matcher);
		assertFilterFalse("(bytevalue=)", matcher);
		assertFilterTrue("(charvalue=*)", matcher);
		assertFilterFalse("(charvalue=)", matcher);
		assertFilterTrue("(floatvalue=*)", matcher);
		assertFilterFalse("(floatvalue=b)", matcher);
		assertFilterFalse("(floatvalue=)", matcher);
		assertFilterTrue("(doublevalue=*)", matcher);
		assertFilterFalse("(doublevalue=b)", matcher);
		assertFilterFalse("(doublevalue=)", matcher);
		assertFilterTrue("(booleanvalue=*)", matcher);
		assertFilterFalse("(booleanvalue=b)", matcher);
		assertFilterFalse("(booleanvalue=)", matcher);

	}

	public void testNullProperties() throws InvalidSyntaxException {
		assertFilterFalse("(room=bedroom)", nullMatcher);
	}

	public void testInvalidFilter() {
		assertFilterInvalid("");
		assertFilterInvalid("()");
		assertFilterInvalid("(=foo)");
		assertFilterInvalid("(");
		assertFilterInvalid("(abc = ))");
		assertFilterInvalid("(& (abc = xyz) (& (345))");
		assertFilterInvalid("  (room = b**oo!*m*) ) ");
		assertFilterInvalid("  (room = b**oo)*m*) ) ");
		assertFilterInvalid("  (room = *=b**oo*m*) ) ");
		assertFilterInvalid("  (room = =b**oo*m*) ) ");
	}

	public void testScalarSubstring() throws InvalidSyntaxException {
		final Matcher matcher = new Matcher() {
			final Hashtable			props	= getProperties();
			final ServiceReference	ref		= newDictionaryServiceReference(props);

			public boolean matches(Filter f) {
				boolean result = f.match(props);
				result &= f.matchCase(props);
				result &= f.matches(props);
				result &= f.match(ref);
				return result;
			}
		};
		assertFilterFalse("(shortvalue =100*) ", matcher);
		assertFilterFalse("(intvalue =100*) ", matcher);
		assertFilterFalse("(longvalue =100*) ", matcher);
		assertFilterFalse("(  bytevalue =1*00  )", matcher);
		assertFilterFalse("(bigintvalue =4*23456) ", matcher);
		assertFilterFalse("(bigdecvalue =4*123456) ", matcher);
		assertFilterFalse("(floatvalue =1*0) ", matcher);
		assertFilterFalse("(doublevalue =2*011) ", matcher);
		assertFilterFalse("(charvalue =a*) ", matcher);
		assertFilterFalse("(booleanvalue =t*ue) ", matcher);
	}

	public void testNormalization() throws InvalidSyntaxException {
		Filter f1 = createFilter("( a = bedroom  )");
		Filter f2 = createFilter(" (a= bedroom  ) ");
		assertEquals("not equal", "(a= bedroom  )", f1.toString());
		assertEquals("not equal", "(a= bedroom  )", f2.toString());
		assertEquals("not equal", f1, f2);
		assertEquals("not equal", f2, f1);
		assertEquals("not equal", f1.hashCode(), f2.hashCode());
	}

	private void assertFilterInvalid(String query) {
		try {
			createFilter(query);
			fail("expected InvalidSyntaxException exception");
		}
		catch (InvalidSyntaxException e) {
			// expected
		}
	}

	private void assertFilterTrue(String query, Matcher matcher)
			throws InvalidSyntaxException {
		Filter f1 = createFilter(query);

		boolean val = matcher.matches(f1);
		assertTrue("wrong result", val);

		String normalized = f1.toString();
		Filter f2 = createFilter(normalized);

		val = matcher.matches(f2);
		assertTrue("wrong result", val);

		assertEquals("normalized not equal", normalized, f2.toString());
	}

	private void assertFilterFalse(String query, Matcher matcher)
			throws InvalidSyntaxException {
		Filter f1 = createFilter(query);

		boolean val = matcher.matches(f1);
		assertFalse("wrong result", val);

		String normalized = f1.toString();
		Filter f2 = createFilter(normalized);

		val = matcher.matches(f2);
		assertFalse("wrong result", val);

		assertEquals("normalized not equal", normalized, f2.toString());
	}

	public void testComparable() throws InvalidSyntaxException {
		Object comp42 = new SampleComparable("42");
		Object comp43 = new SampleComparable("43");
		Hashtable hash = new Hashtable();

		Filter f1 = createFilter("(comparable=42)");

		hash.put("comparable", comp42);
		assertTrue("does not match filter", f1.match(hash));
		assertTrue("does not match filter", f1.matchCase(hash));
		assertTrue("does not match filter", f1.matches(hash));
		assertTrue("does not match filter",
				f1.match(newDictionaryServiceReference(hash)));

		hash.put("comparable", comp43);
		assertFalse("does match filter", f1.match(hash));
		assertFalse("does match filter", f1.matchCase(hash));
		assertFalse("does match filter", f1.matches(hash));
		assertFalse("does match filter",
				f1.match(newDictionaryServiceReference(hash)));

		f1 = createFilter("(comparable<=42)");

		hash.put("comparable", comp42);
		assertTrue("does not match filter", f1.match(hash));
		assertTrue("does not match filter", f1.matchCase(hash));
		assertTrue("does not match filter", f1.matches(hash));
		assertTrue("does not match filter",
				f1.match(newDictionaryServiceReference(hash)));

		hash.put("comparable", comp43);
		assertFalse("does match filter", f1.match(hash));
		assertFalse("does match filter", f1.matchCase(hash));
		assertFalse("does match filter", f1.matches(hash));
		assertFalse("does match filter",
				f1.match(newDictionaryServiceReference(hash)));

		f1 = createFilter("(comparable>=42)");

		hash.put("comparable", comp42);
		assertTrue("does not match filter", f1.match(hash));
		assertTrue("does not match filter", f1.matchCase(hash));
		assertTrue("does not match filter", f1.matches(hash));
		assertTrue("does not match filter",
				f1.match(newDictionaryServiceReference(hash)));

		hash.put("comparable", comp43);
		assertTrue("does not match filter", f1.match(hash));
		assertTrue("does not match filter", f1.matchCase(hash));
		assertTrue("does not match filter", f1.matches(hash));
		assertTrue("does not match filter",
				f1.match(newDictionaryServiceReference(hash)));

		f1 = createFilter("(comparable=4*2)");

		hash.put("comparable", comp42);
		assertFalse("does match filter", f1.match(hash));
		assertFalse("does match filter", f1.matchCase(hash));
		assertFalse("does match filter", f1.matches(hash));
		assertFalse("does match filter",
				f1.match(newDictionaryServiceReference(hash)));
	}

	public void testComparableException() throws InvalidSyntaxException {
		Object compbad = new SampleComparable("exception");
		Hashtable hash = new Hashtable();

		Filter f1 = createFilter("(comparable=exception)");

		hash.put("comparable", compbad);
		assertFalse("does match filter", f1.match(hash));
		assertFalse("does match filter", f1.matchCase(hash));
		assertFalse("does match filter", f1.matches(hash));
		assertFalse("does match filter",
				f1.match(newDictionaryServiceReference(hash)));
	}

	public void testObject() throws InvalidSyntaxException {
		Object obj42 = new SampleObject("42");
		Object obj43 = new SampleObject("43");
		Hashtable hash = new Hashtable();

		Filter f1 = createFilter("(object=42)");

		hash.put("object", obj42);
		assertTrue("does not match filter", f1.match(hash));
		assertTrue("does not match filter", f1.matchCase(hash));
		assertTrue("does not match filter", f1.matches(hash));
		assertTrue("does not match filter",
				f1.match(newDictionaryServiceReference(hash)));

		hash.put("object", obj43);
		assertFalse("does match filter", f1.match(hash));
		assertFalse("does match filter", f1.matchCase(hash));
		assertFalse("does match filter", f1.matches(hash));
		assertFalse("does match filter",
				f1.match(newDictionaryServiceReference(hash)));

		f1 = createFilter("(object=4*2)");

		hash.put("object", obj42);
		assertFalse("does match filter", f1.match(hash));
		assertFalse("does match filter", f1.matchCase(hash));
		assertFalse("does match filter", f1.matches(hash));
		assertFalse("does match filter",
				f1.match(newDictionaryServiceReference(hash)));
	}

	public void testObjectException() throws InvalidSyntaxException {
		Object objbad = new SampleObject("exception");
		Hashtable hash = new Hashtable();

		Filter f1 = createFilter("(object=exception)");

		hash.put("object", objbad);
		assertFalse("does match filter", f1.match(hash));
		assertFalse("does match filter", f1.matchCase(hash));
		assertFalse("does match filter", f1.matches(hash));
		assertFalse("does match filter",
				f1.match(newDictionaryServiceReference(hash)));
	}

	public void testValueOf() throws InvalidSyntaxException {
		Object obj42 = new SampleValueOf("42", null);
		Object obj43 = new SampleValueOf("43", null);
		Hashtable hash = new Hashtable();

		Filter f1 = createFilter("(object=42)");

		hash.put("object", obj42);
		assertTrue("does not match filter", f1.match(hash));
		assertTrue("does not match filter", f1.matchCase(hash));
		assertTrue("does not match filter", f1.matches(hash));
		assertTrue("does not match filter",
				f1.match(newDictionaryServiceReference(hash)));

		hash.put("object", obj43);
		assertFalse("does match filter", f1.match(hash));
		assertFalse("does match filter", f1.matchCase(hash));
		assertFalse("does match filter", f1.matches(hash));
		assertFalse("does match filter",
				f1.match(newDictionaryServiceReference(hash)));

		f1 = createFilter("(object=4*2)");

		hash.put("object", obj42);
		assertFalse("does match filter", f1.match(hash));
		assertFalse("does match filter", f1.matchCase(hash));
		assertFalse("does match filter", f1.matches(hash));
		assertFalse("does match filter",
				f1.match(newDictionaryServiceReference(hash)));
	}

	public void testValueOfException() throws InvalidSyntaxException {
		Object objbad = new SampleValueOf("exception", null);
		Hashtable hash = new Hashtable();

		Filter f1 = createFilter("(object=exception)");

		hash.put("object", objbad);
		assertFalse("does match filter", f1.match(hash));
		assertFalse("does match filter", f1.matchCase(hash));
		assertFalse("does match filter", f1.matches(hash));
		assertFalse("does match filter",
				f1.match(newDictionaryServiceReference(hash)));
	}

	public void testValueOfWithUnassignableReturnType() throws InvalidSyntaxException {
		Object obj42 = new SampleValueOfWithUnassignableReturnType("42");
		Object obj43 = new SampleValueOfWithUnassignableReturnType("43");
		Hashtable hash = new Hashtable();

		Filter f1 = createFilter("(object=42)");

		hash.put("object", obj42);
		assertTrue("does not match filter", f1.match(hash));
		assertTrue("does not match filter", f1.matchCase(hash));
		assertTrue("does not match filter", f1.matches(hash));
		assertTrue("does not match filter",
				f1.match(newDictionaryServiceReference(hash)));

		hash.put("object", obj43);
		assertFalse("does match filter", f1.match(hash));
		assertFalse("does match filter", f1.matchCase(hash));
		assertFalse("does match filter", f1.matches(hash));
		assertFalse("does match filter",
				f1.match(newDictionaryServiceReference(hash)));

		f1 = createFilter("(object=4*2)");

		hash.put("object", obj42);
		assertFalse("does match filter", f1.match(hash));
		assertFalse("does match filter", f1.matchCase(hash));
		assertFalse("does match filter", f1.matches(hash));
		assertFalse("does match filter",
				f1.match(newDictionaryServiceReference(hash)));
	}

	public void testNullMapValue() throws InvalidSyntaxException {
		Map map = new HashMap();
		map.put("foo", null);
		Filter f1 = createFilter("(foo=*)");
		assertFalse("does match filter", f1.matches(map));
	}

	public void testComparableValueOf() throws InvalidSyntaxException {
		Object comp42 = new SampleComparableValueOf("42", null);
		Object comp43 = new SampleComparableValueOf("43", null);
		Hashtable hash = new Hashtable();

		Filter f1 = createFilter("(comparable=42)");

		hash.put("comparable", comp42);
		assertTrue("does not match filter", f1.match(hash));
		assertTrue("does not match filter", f1.matchCase(hash));
		assertTrue("does not match filter", f1.matches(hash));
		assertTrue("does not match filter",
				f1.match(newDictionaryServiceReference(hash)));

		hash.put("comparable", comp43);
		assertFalse("does match filter", f1.match(hash));
		assertFalse("does match filter", f1.matchCase(hash));
		assertFalse("does match filter", f1.matches(hash));
		assertFalse("does match filter",
				f1.match(newDictionaryServiceReference(hash)));

		f1 = createFilter("(comparable<=42)");

		hash.put("comparable", comp42);
		assertTrue("does not match filter", f1.match(hash));
		assertTrue("does not match filter", f1.matchCase(hash));
		assertTrue("does not match filter", f1.matches(hash));
		assertTrue("does not match filter",
				f1.match(newDictionaryServiceReference(hash)));

		hash.put("comparable", comp43);
		assertFalse("does match filter", f1.match(hash));
		assertFalse("does match filter", f1.matchCase(hash));
		assertFalse("does match filter", f1.matches(hash));
		assertFalse("does match filter",
				f1.match(newDictionaryServiceReference(hash)));

		f1 = createFilter("(comparable>=42)");

		hash.put("comparable", comp42);
		assertTrue("does not match filter", f1.match(hash));
		assertTrue("does not match filter", f1.matchCase(hash));
		assertTrue("does not match filter", f1.matches(hash));
		assertTrue("does not match filter",
				f1.match(newDictionaryServiceReference(hash)));

		hash.put("comparable", comp43);
		assertTrue("does not match filter", f1.match(hash));
		assertTrue("does not match filter", f1.matchCase(hash));
		assertTrue("does not match filter", f1.matches(hash));
		assertTrue("does not match filter",
				f1.match(newDictionaryServiceReference(hash)));

		f1 = createFilter("(comparable=4*2)");

		hash.put("comparable", comp42);
		assertFalse("does match filter", f1.match(hash));
		assertFalse("does match filter", f1.matchCase(hash));
		assertFalse("does match filter", f1.matches(hash));
		assertFalse("does match filter",
				f1.match(newDictionaryServiceReference(hash)));
	}

	public void testComparableValueOfException() throws InvalidSyntaxException {
		Object compbad = new SampleComparableValueOf("exception", null);
		Hashtable hash = new Hashtable();

		Filter f1 = createFilter("(comparable=exception)");

		hash.put("comparable", compbad);
		assertFalse("does match filter", f1.match(hash));
		assertFalse("does match filter", f1.matchCase(hash));
		assertFalse("does match filter", f1.matches(hash));
		assertFalse("does match filter",
				f1.match(newDictionaryServiceReference(hash)));
	}

	public static class SampleComparableValueOf implements Comparable {
		private final int				value;
		private final RuntimeException	e;

		public static SampleComparableValueOf valueOf(String value) {
			return new SampleComparableValueOf(value, null);
		}

		public SampleComparableValueOf(String value) {
			this.value = -1;
			this.e = null;
		}
		private SampleComparableValueOf(String value, Object whatever) {
			int v = -1;
			RuntimeException r = null;
			try {
				v = Integer.parseInt(value);
			}
			catch (RuntimeException re) {
				r = re;
			}
			this.value = v;
			this.e = r;
		}

		public int compareTo(Object o) {
			if (e != null) {
				e.fillInStackTrace();
				throw e;
			}
			return value - ((SampleComparableValueOf) o).value;
		}

		public String toString() {
			if (e != null) {
				e.fillInStackTrace();
				return e.toString();
			}
			return String.valueOf(value);
		}
	}

	public static class SampleComparable implements Comparable {
		private final int				value;
		private final RuntimeException	e;

		public SampleComparable(String value) {
			int v = -1;
			RuntimeException r = null;
			try {
				v = Integer.parseInt(value);
			}
			catch (RuntimeException re) {
				r = re;
			}
			this.value = v;
			this.e = r;
		}

		public int compareTo(Object o) {
			if (e != null) {
				e.fillInStackTrace();
				throw e;
			}
			return value - ((SampleComparable) o).value;
		}

		public String toString() {
			if (e != null) {
				e.fillInStackTrace();
				return e.toString();
			}
			return String.valueOf(value);
		}
	}

	public static class SampleObject {
		private final int				value;
		private final RuntimeException	e;

		public SampleObject(String value) {
			int v = -1;
			RuntimeException r = null;
			try {
				v = Integer.parseInt(value);
			}
			catch (RuntimeException re) {
				r = re;
			}
			this.value = v;
			this.e = r;
		}

		public boolean equals(Object o) {
			if (e != null) {
				e.fillInStackTrace();
				throw e;
			}
			if (o instanceof SampleObject) {
				return value == ((SampleObject) o).value;
			}
			return false;
		}

		public String toString() {
			if (e != null) {
				e.fillInStackTrace();
				return e.toString();
			}
			return String.valueOf(value);
		}
	}

	public static class SampleValueOf {
		private final int				value;
		private final RuntimeException	e;

		public static SampleValueOf valueOf(String value) {
			return new SampleValueOf(value, null);
		}

		public SampleValueOf(String value) {
			this.value = -1;
			this.e = null;
		}
		private SampleValueOf(String value, Object whatever) {
			int v = -1;
			RuntimeException r = null;
			try {
				v = Integer.parseInt(value);
			}
			catch (RuntimeException re) {
				r = re;
			}
			this.value = v;
			this.e = r;
		}

		public boolean equals(Object o) {
			if (e != null) {
				e.fillInStackTrace();
				throw e;
			}
			if (o instanceof SampleValueOf) {
				return value == ((SampleValueOf) o).value;
			}
			return false;
		}

		public String toString() {
			if (e != null) {
				e.fillInStackTrace();
				return e.toString();
			}
			return String.valueOf(value);
		}
	}

	public static class SampleValueOfWithUnassignableReturnType {
		private final int				value;
		private final RuntimeException	e;

		public static Integer valueOf(String value) {
			return Integer.parseInt(value);
		}

		public SampleValueOfWithUnassignableReturnType(String value) {
			int v = -1;
			RuntimeException r = null;
			try {
				v = Integer.parseInt(value);
			}
			catch (RuntimeException re) {
				r = re;
			}
			this.value = v;
			this.e = r;
		}

		public boolean equals(Object o) {
			if (e != null) {
				e.fillInStackTrace();
				throw e;
			}
			if (o instanceof SampleValueOfWithUnassignableReturnType) {
				return value == ((SampleValueOfWithUnassignableReturnType) o).value;
			}
			return false;
		}

		public String toString() {
			if (e != null) {
				e.fillInStackTrace();
				return e.toString();
			}
			return String.valueOf(value);
		}
	}

	public static ServiceReference newDictionaryServiceReference(
			Dictionary dictionary) {
		return MockFactory.newMock(ServiceReference.class,
				new DictionaryServiceReference(dictionary));
	}

	private static class DictionaryServiceReference {
		private final Dictionary	dictionary;
		private final String[]		keys;

		DictionaryServiceReference(Dictionary dictionary) {
			if (dictionary == null) {
				this.dictionary = null;
				this.keys = new String[] {};
				return;
			}
			this.dictionary = dictionary;
			List keyList = new ArrayList(dictionary.size());
			for (Enumeration e = dictionary.keys(); e.hasMoreElements();) {
				Object k = e.nextElement();
				if (k instanceof String) {
					String key = (String) k;
					for (Iterator i = keyList.iterator(); i.hasNext();) {
						if (key.equalsIgnoreCase((String) i.next())) {
							throw new IllegalArgumentException();
						}
					}
					keyList.add(key);
				}
			}
			this.keys = (String[]) keyList.toArray(new String[keyList.size()]);
		}

		public Object getProperty(String k) {
			for (int i = 0, length = keys.length; i < length; i++) {
				String key = keys[i];
				if (key.equalsIgnoreCase(k)) {
					return dictionary.get(key);
				}
			}
			return null;
		}

		public String[] getPropertyKeys() {
			return keys.clone();
		}
	}

	static interface Matcher {
		public boolean matches(Filter f);
	}
}
