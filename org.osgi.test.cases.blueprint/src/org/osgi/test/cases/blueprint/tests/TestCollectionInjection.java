/*
 * Copyright (c) IBM Corporation (2009). All Rights Reserved.
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

package org.osgi.test.cases.blueprint.tests;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import org.osgi.test.cases.blueprint.framework.ArgumentMetadataValidator;
import org.osgi.test.cases.blueprint.framework.MapValueEntry;
import org.osgi.test.cases.blueprint.framework.MetadataEventSet;
import org.osgi.test.cases.blueprint.framework.PropertyMetadataValidator;
import org.osgi.test.cases.blueprint.framework.PropertyValueValidator;
import org.osgi.test.cases.blueprint.framework.StandardTestController;
import org.osgi.test.cases.blueprint.framework.TestArgument;
import org.osgi.test.cases.blueprint.framework.TestListValue;
import org.osgi.test.cases.blueprint.framework.TestMapValue;
import org.osgi.test.cases.blueprint.framework.TestNullValue;
import org.osgi.test.cases.blueprint.framework.TestProperty;
import org.osgi.test.cases.blueprint.framework.TestPropsValue;
import org.osgi.test.cases.blueprint.framework.TestSetValue;
import org.osgi.test.cases.blueprint.framework.TestStringValue;
import org.osgi.test.cases.blueprint.framework.TestValue;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * This class contains primitive string to type injection convertions
 *
 * @author $Id$
 */
public class TestCollectionInjection extends DefaultTestBundleControl {
    private void addConstructorValidator(MetadataEventSet startEvents, String id, Object value, Class type) {
        startEvents.validateComponentArgument(id, "arg1", value, type);
    }

	public void testListConstructor() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/list_constructor_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();
        // Empty list
        List expected = new ArrayList();
        addConstructorValidator(startEvents, "compEmptyList", expected, List.class);
        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compEmptyList", new TestArgument(
            new TestListValue(new TestValue[0]))));

        // null list item
        addConstructorValidator(startEvents, "compNullList", null, List.class);
        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compNullList", new TestArgument(
            new TestNullValue(), List.class)));

        // simple list of strings
        expected = new ArrayList();
        expected.add("abc");
        expected.add("def");
        addConstructorValidator(startEvents, "compStringItems", expected, List.class);
        // validate the metadata for this one too (the valueType for the items must be null because
        // the collection did not specify anything).
        startEvents.addValidator(new ArgumentMetadataValidator("compStringItems", new TestArgument(
            new TestListValue(new TestValue[] { new TestStringValue("abc"), new TestStringValue("def") }))));

        // simple list of strings with duplicates
        expected = new ArrayList();
        expected.add("abc");
        expected.add("abc");
        addConstructorValidator(startEvents, "compDupItems", expected, List.class);
        // list of "converted" strings
        expected = new ArrayList();
        expected.add(Boolean.TRUE);
        expected.add(Boolean.FALSE);
        addConstructorValidator(startEvents, "compBooleanItems", expected, List.class);
        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compBooleanItems", new TestArgument(
            new TestListValue(new TestValue[] { new TestStringValue(Boolean.class, "true"),
            new TestStringValue(Boolean.class, "false") }))));

        // list containing a mixed bag of types
        expected = new ArrayList();
        expected.add("abc");
        expected.add(Boolean.FALSE);
        expected.add(Byte.valueOf((byte)3));
        expected.add(Character.valueOf('4'));
        expected.add(Integer.valueOf(5));
        expected.add(Short.valueOf((short)6));
        expected.add(Long.valueOf(7));
        expected.add(Double.valueOf(8.0));
		expected.add(Float.valueOf(9.0f));
        expected.add(new URL("http://www.osgi.org"));
        expected.add(String.class);
        expected.add(new Locale("en", "US"));
        expected.add(null);
        addConstructorValidator(startEvents, "compMixedItems", expected, List.class);

        // large metadata validation step here.
        startEvents.addValidator(new ArgumentMetadataValidator("compMixedItems", new TestArgument(
            new TestListValue(new TestValue[] {
                new TestStringValue(String.class, "abc"),
                new TestStringValue(Boolean.class, "false"),
                new TestStringValue(Byte.class, "3"),
                new TestStringValue(Character.class, "4"),
                new TestStringValue(Integer.class, "5"),
                new TestStringValue(Short.class, "6"),
                new TestStringValue(Long.class, "7"),
                new TestStringValue(Double.class, "8.0"),
                new TestStringValue(Float.class, "9.0"),
                new TestStringValue(URL.class, "http://www.osgi.org"),
                new TestStringValue(Class.class, "java.lang.String"),
                new TestStringValue(Locale.class, "en_US"),
                new TestNullValue(),
            }))));

        // a typed list of Doubles
        expected = new ArrayList();
        expected.add(Double.valueOf(0.0));
        expected.add(Double.valueOf(1.0));
        addConstructorValidator(startEvents, "compTypedItems", expected, List.class);

        startEvents.addValidator(new ArgumentMetadataValidator("compTypedItems", new TestArgument(
            new TestListValue(new TestValue[] {
                new TestStringValue("0.0"),
                new TestStringValue("1.0")
            }, Double.class))));

        // a type list of Doubles with an element override
        expected = new ArrayList();
        expected.add(Double.valueOf(0.0));
        expected.add(Double.valueOf(1.0));
        expected.add(Boolean.TRUE);
        addConstructorValidator(startEvents, "compTypeOverride", expected, List.class);

        startEvents.addValidator(new ArgumentMetadataValidator("compTypeOverride", new TestArgument(
            new TestListValue(new TestValue[] {
                // these are not specified, but inherit from the collection type.
                new TestStringValue("0.0"),
                new TestStringValue("1.0"),
                new TestStringValue(Boolean.class, "true")
            }, Double.class))));

        // list nested inside of a list
        List innerList = new ArrayList();
        innerList.add("abc");
        innerList.add("def");
        expected = new ArrayList();
        expected.add(innerList);
        addConstructorValidator(startEvents, "compNestedList", expected, List.class);

        // set nested inside of a list
        Set innerSet = new HashSet();
        innerSet.add("abc");
        innerSet.add("def");
        expected = new ArrayList();
        expected.add(innerSet);
        addConstructorValidator(startEvents, "compNestedSet", expected, List.class);

        // Map nested inside of a list
        Map innerMap = new HashMap();
        innerMap.put("abc", "def");
        innerMap.put("ghi", "jkl");
        expected = new ArrayList();
        expected.add(innerMap);
        addConstructorValidator(startEvents, "compNestedMap", expected, List.class);

        // Properties nested inside of a list
        Properties innerProps = new Properties();
        innerProps.put("abc", "def");
        innerProps.put("ghi", "jkl");
        expected = new ArrayList();
        expected.add(innerProps);
        addConstructorValidator(startEvents, "compNestedProps", expected, List.class);

        controller.run();
    }

	public void testListStaticFactoryConstructor() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/list_static_factory_constructor_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();
        // Empty list
        List expected = new ArrayList();
        addConstructorValidator(startEvents, "compEmptyList", expected, List.class);
        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compEmptyList", new TestArgument(
            new TestListValue(new TestValue[0]))));

        // null list item
        addConstructorValidator(startEvents, "compNullList", null, List.class);
        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compNullList", new TestArgument(
            new TestNullValue(), List.class)));

        // simple list of strings
        expected = new ArrayList();
        expected.add("abc");
        expected.add("def");
        addConstructorValidator(startEvents, "compStringItems", expected, List.class);
        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compStringItems", new TestArgument(
            new TestListValue(new TestValue[] { new TestStringValue("abc"), new TestStringValue("def") }))));

        // simple list of strings with duplicates
        expected = new ArrayList();
        expected.add("abc");
        expected.add("abc");
        addConstructorValidator(startEvents, "compDupItems", expected, List.class);
        // list of "converted" strings
        expected = new ArrayList();
        expected.add(Boolean.TRUE);
        expected.add(Boolean.FALSE);
        addConstructorValidator(startEvents, "compBooleanItems", expected, List.class);
        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compBooleanItems", new TestArgument(
            new TestListValue(new TestValue[] { new TestStringValue(Boolean.class, "true"),
            new TestStringValue(Boolean.class, "false") }))));

        // list containing a mixed bag of types
        expected = new ArrayList();
        expected.add("abc");
        expected.add(Boolean.FALSE);
        expected.add(Byte.valueOf((byte)3));
        expected.add(Character.valueOf('4'));
        expected.add(Integer.valueOf(5));
        expected.add(Short.valueOf((short)6));
        expected.add(Long.valueOf(7));
        expected.add(Double.valueOf(8.0));
		expected.add(Float.valueOf(9.0f));
        expected.add(new URL("http://www.osgi.org"));
        expected.add(String.class);
        expected.add(new Locale("en", "US"));
        expected.add(null);
        addConstructorValidator(startEvents, "compMixedItems", expected, List.class);

        // large metadata validation step here.
        startEvents.addValidator(new ArgumentMetadataValidator("compMixedItems", new TestArgument(
            new TestListValue(new TestValue[] {
                new TestStringValue(String.class, "abc"),
                new TestStringValue(Boolean.class, "false"),
                new TestStringValue(Byte.class, "3"),
                new TestStringValue(Character.class, "4"),
                new TestStringValue(Integer.class, "5"),
                new TestStringValue(Short.class, "6"),
                new TestStringValue(Long.class, "7"),
                new TestStringValue(Double.class, "8.0"),
                new TestStringValue(Float.class, "9.0"),
                new TestStringValue(URL.class, "http://www.osgi.org"),
                new TestStringValue(Class.class, "java.lang.String"),
                new TestStringValue(Locale.class, "en_US"),
                new TestNullValue(),
            }))));

        // a typed list of Doubles
        expected = new ArrayList();
        expected.add(Double.valueOf(0.0));
        expected.add(Double.valueOf(1.0));
        addConstructorValidator(startEvents, "compTypedItems", expected, List.class);

        startEvents.addValidator(new ArgumentMetadataValidator("compTypedItems", new TestArgument(
            new TestListValue(new TestValue[] {
                new TestStringValue("0.0"),
                new TestStringValue("1.0")
            }, Double.class))));

        // a type list of Doubles with an element override
        expected = new ArrayList();
        expected.add(Double.valueOf(0.0));
        expected.add(Double.valueOf(1.0));
        expected.add(Boolean.TRUE);
        addConstructorValidator(startEvents, "compTypeOverride", expected, List.class);

        startEvents.addValidator(new ArgumentMetadataValidator("compTypeOverride", new TestArgument(
            new TestListValue(new TestValue[] {
                new TestStringValue("0.0"),
                new TestStringValue("1.0"),
                new TestStringValue(Boolean.class, "true")
            }, Double.class))));

        // list nested inside of a list
        List innerList = new ArrayList();
        innerList.add("abc");
        innerList.add("def");
        expected = new ArrayList();
        expected.add(innerList);
        addConstructorValidator(startEvents, "compNestedList", expected, List.class);

        // set nested inside of a list
        Set innerSet = new HashSet();
        innerSet.add("abc");
        innerSet.add("def");
        expected = new ArrayList();
        expected.add(innerSet);
        addConstructorValidator(startEvents, "compNestedSet", expected, List.class);

        // Map nested inside of a list
        Map innerMap = new HashMap();
        innerMap.put("abc", "def");
        innerMap.put("ghi", "jkl");
        expected = new ArrayList();
        expected.add(innerMap);
        addConstructorValidator(startEvents, "compNestedMap", expected, List.class);

        // Properties nested inside of a list
        Properties innerProps = new Properties();
        innerProps.put("abc", "def");
        innerProps.put("ghi", "jkl");
        expected = new ArrayList();
        expected.add(innerProps);
        addConstructorValidator(startEvents, "compNestedProps", expected, List.class);

        controller.run();
    }

	public void testListInstanceFactoryConstructor() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/list_factory_constructor_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();
        // Empty list
        List expected = new ArrayList();
        addConstructorValidator(startEvents, "compEmptyList", expected, List.class);
        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compEmptyList", new TestArgument(
            new TestListValue(new TestValue[0]))));

        // null list item
        addConstructorValidator(startEvents, "compNullList", null, List.class);
        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compNullList", new TestArgument(
            new TestNullValue(), List.class)));

        // simple list of strings
        expected = new ArrayList();
        expected.add("abc");
        expected.add("def");
        addConstructorValidator(startEvents, "compStringItems", expected, List.class);
        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compStringItems", new TestArgument(
            new TestListValue(new TestValue[] { new TestStringValue("abc"), new TestStringValue("def") }))));

        // simple list of strings with duplicates
        expected = new ArrayList();
        expected.add("abc");
        expected.add("abc");
        addConstructorValidator(startEvents, "compDupItems", expected, List.class);
        // list of "converted" strings
        expected = new ArrayList();
        expected.add(Boolean.TRUE);
        expected.add(Boolean.FALSE);
        addConstructorValidator(startEvents, "compBooleanItems", expected, List.class);
        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compBooleanItems", new TestArgument(
            new TestListValue(new TestValue[] { new TestStringValue(Boolean.class, "true"),
            new TestStringValue(Boolean.class, "false") }))));

        // list containing a mixed bag of types
        expected = new ArrayList();
        expected.add("abc");
        expected.add(Boolean.FALSE);
        expected.add(Byte.valueOf((byte)3));
        expected.add(Character.valueOf('4'));
        expected.add(Integer.valueOf(5));
        expected.add(Short.valueOf((short)6));
        expected.add(Long.valueOf(7));
        expected.add(Double.valueOf(8.0));
		expected.add(Float.valueOf(9.0f));
        expected.add(new URL("http://www.osgi.org"));
        expected.add(String.class);
        expected.add(new Locale("en", "US"));
        expected.add(null);
        addConstructorValidator(startEvents, "compMixedItems", expected, List.class);

        // large metadata validation step here.
        startEvents.addValidator(new ArgumentMetadataValidator("compMixedItems", new TestArgument(
            new TestListValue(new TestValue[] {
                new TestStringValue(String.class, "abc"),
                new TestStringValue(Boolean.class, "false"),
                new TestStringValue(Byte.class, "3"),
                new TestStringValue(Character.class, "4"),
                new TestStringValue(Integer.class, "5"),
                new TestStringValue(Short.class, "6"),
                new TestStringValue(Long.class, "7"),
                new TestStringValue(Double.class, "8.0"),
                new TestStringValue(Float.class, "9.0"),
                new TestStringValue(URL.class, "http://www.osgi.org"),
                new TestStringValue(Class.class, "java.lang.String"),
                new TestStringValue(Locale.class, "en_US"),
                new TestNullValue(),
            }))));

        // a typed list of Doubles
        expected = new ArrayList();
        expected.add(Double.valueOf(0.0));
        expected.add(Double.valueOf(1.0));
        addConstructorValidator(startEvents, "compTypedItems", expected, List.class);

        startEvents.addValidator(new ArgumentMetadataValidator("compTypedItems", new TestArgument(
            new TestListValue(new TestValue[] {
                new TestStringValue("0.0"),
                new TestStringValue("1.0")
            }, Double.class))));

        // a type list of Doubles with an element override
        expected = new ArrayList();
        expected.add(Double.valueOf(0.0));
        expected.add(Double.valueOf(1.0));
        expected.add(Boolean.TRUE);
        addConstructorValidator(startEvents, "compTypeOverride", expected, List.class);

        startEvents.addValidator(new ArgumentMetadataValidator("compTypeOverride", new TestArgument(
            new TestListValue(new TestValue[] {
                new TestStringValue("0.0"),
                new TestStringValue("1.0"),
                new TestStringValue(Boolean.class, "true")
            }, Double.class))));

        // list nested inside of a list
        List innerList = new ArrayList();
        innerList.add("abc");
        innerList.add("def");
        expected = new ArrayList();
        expected.add(innerList);
        addConstructorValidator(startEvents, "compNestedList", expected, List.class);

        // set nested inside of a list
        Set innerSet = new HashSet();
        innerSet.add("abc");
        innerSet.add("def");
        expected = new ArrayList();
        expected.add(innerSet);
        addConstructorValidator(startEvents, "compNestedSet", expected, List.class);

        // Map nested inside of a list
        Map innerMap = new HashMap();
        innerMap.put("abc", "def");
        innerMap.put("ghi", "jkl");
        expected = new ArrayList();
        expected.add(innerMap);
        addConstructorValidator(startEvents, "compNestedMap", expected, List.class);

        // Properties nested inside of a list
        Properties innerProps = new Properties();
        innerProps.put("abc", "def");
        innerProps.put("ghi", "jkl");
        expected = new ArrayList();
        expected.add(innerProps);
        addConstructorValidator(startEvents, "compNestedProps", expected, List.class);

        controller.run();
    }


    private void addPropertyValidator(MetadataEventSet startEvents, String compName, String propertyName, Object propertyValue, Class type) {
        startEvents.validateComponentProperty(compName, propertyName, propertyValue, type);
    }


    private void addPropertyValidator(MetadataEventSet startEvents, String compName, String propertyName, Object propertyValue, Class type, Class implementation) {
        startEvents.addValidator(new PropertyValueValidator(compName, propertyName, propertyValue, type, implementation));
    }



	public void testListProperty() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/list_property_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();
        // Empty list
        List expected = new ArrayList();
        addPropertyValidator(startEvents, "compEmptyList", "list", expected, List.class);
        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compEmptyList", new TestProperty(
            new TestListValue(new TestValue[0])
            , "list")));

        // null list item
        addPropertyValidator(startEvents, "compNullList", "list", null, List.class);
        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compNullList", new TestProperty(
            new TestNullValue()
            , "list")));

        // simple list of strings
        expected = new ArrayList();
        expected.add("abc");
        expected.add("def");
        addPropertyValidator(startEvents, "compStringItems", "list", expected, List.class);
        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compStringItems", new TestProperty(
            new TestListValue(new TestValue[] { new TestStringValue("abc"), new TestStringValue("def") })
            , "list")));

        // simple list of strings with duplicates
        expected = new ArrayList();
        expected.add("abc");
        expected.add("abc");
        addPropertyValidator(startEvents, "compDupItems", "list", expected, List.class);
        // list of "converted" strings
        expected = new ArrayList();
        expected.add(Boolean.TRUE);
        expected.add(Boolean.FALSE);
        addPropertyValidator(startEvents, "compBooleanItems", "list", expected, List.class);
        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compBooleanItems", new TestProperty(
            new TestListValue(new TestValue[] { new TestStringValue(Boolean.class, "true"),
            new TestStringValue(Boolean.class, "false") })
            , "list")));

        // list containing a mixed bag of types
        expected = new ArrayList();
        expected.add("abc");
        expected.add(Boolean.FALSE);
        expected.add(Byte.valueOf((byte)3));
        expected.add(Character.valueOf('4'));
        expected.add(Integer.valueOf(5));
        expected.add(Short.valueOf((short)6));
        expected.add(Long.valueOf(7));
        expected.add(Double.valueOf(8.0));
		expected.add(Float.valueOf(9.0f));
        expected.add(new URL("http://www.osgi.org"));
        expected.add(String.class);
        expected.add(new Locale("en", "US"));
        expected.add(null);
        addPropertyValidator(startEvents, "compMixedItems", "list", expected, List.class);
        startEvents.addValidator(new PropertyMetadataValidator("compMixedItems", new TestProperty(
            new TestListValue(new TestValue[] {
                new TestStringValue(String.class, "abc"),
                new TestStringValue(Boolean.class, "false"),
                new TestStringValue(Byte.class, "3"),
                new TestStringValue(Character.class, "4"),
                new TestStringValue(Integer.class, "5"),
                new TestStringValue(Short.class, "6"),
                new TestStringValue(Long.class, "7"),
                new TestStringValue(Double.class, "8.0"),
                new TestStringValue(Float.class, "9.0"),
                new TestStringValue(URL.class, "http://www.osgi.org"),
                new TestStringValue(Class.class, "java.lang.String"),
                new TestStringValue(Locale.class, "en_US"),
                new TestNullValue()
            })
            , "list")));

        // a typed list of Doubles
        expected = new ArrayList();
        expected.add(Double.valueOf(0.0));
        expected.add(Double.valueOf(1.0));
        addPropertyValidator(startEvents, "compTypedItems", "list", expected, List.class);

        startEvents.addValidator(new PropertyMetadataValidator("compTypedItems", new TestProperty(
            new TestListValue(new TestValue[] {
                new TestStringValue("0.0"),
                new TestStringValue("1.0")
            }, Double.class)
            , "list")));

        // a type list of Doubles with an element override
        expected = new ArrayList();
        expected.add(Double.valueOf(0.0));
        expected.add(Double.valueOf(1.0));
        expected.add(Boolean.TRUE);
        addPropertyValidator(startEvents, "compTypeOverride", "list", expected, List.class);

        startEvents.addValidator(new PropertyMetadataValidator("compTypeOverride", new TestProperty(
            new TestListValue(new TestValue[] {
                new TestStringValue("0.0"),
                new TestStringValue("1.0"),
                new TestStringValue(Boolean.class, "true")
            }, Double.class)
            , "list")));

        // list nested inside of a list
        List innerList = new ArrayList();
        innerList.add("abc");
        innerList.add("def");
        expected = new ArrayList();
        expected.add(innerList);
        addPropertyValidator(startEvents, "compNestedList", "list", expected, List.class);

        // set nested inside of a list
        Set innerSet = new HashSet();
        innerSet.add("abc");
        innerSet.add("def");
        expected = new ArrayList();
        expected.add(innerSet);
        addPropertyValidator(startEvents, "compNestedSet", "list", expected, List.class);

        // Map nested inside of a list
        Map innerMap = new HashMap();
        innerMap.put("abc", "def");
        innerMap.put("ghi", "jkl");
        expected = new ArrayList();
        expected.add(innerMap);
        addPropertyValidator(startEvents, "compNestedMap", "list", expected, List.class);

        // Properties nested inside of a list
        Properties innerProps = new Properties();
        innerProps.put("abc", "def");
        innerProps.put("ghi", "jkl");
        expected = new ArrayList();
        expected.add(innerProps);
        addPropertyValidator(startEvents, "compNestedProps", "list", expected, List.class);

        controller.run();
    }


	public void testConvertedList() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/converted_list_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        // simple list of strings
        List expected = new ArrayList();
        expected.add("abc");
        expected.add("def");
        addPropertyValidator(startEvents, "arrayList", "arrayList", expected, ArrayList.class);
        addPropertyValidator(startEvents, "arrayListFromSet", "arrayList", expected, ArrayList.class);
        addPropertyValidator(startEvents, "implicitListFromSet", "list", expected, List.class);

        // simple list of strings
        expected = new LinkedList();
        expected.add("abc");
        expected.add("def");
        addPropertyValidator(startEvents, "linkedList", "linkedList", expected, LinkedList.class);
        addPropertyValidator(startEvents, "linkedListFromSet", "linkedList", expected, LinkedList.class);

        // simple list of strings
        expected = new Vector();
        expected.add("abc");
        expected.add("def");
        addPropertyValidator(startEvents, "vector", "vector", expected, Vector.class);
        addPropertyValidator(startEvents, "vectorFromSet", "vector", expected, Vector.class);

        controller.run();
    }


	public void testConvertedSet() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/converted_set_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        // simple list of strings
        Set expected = new HashSet();
        expected.add("abc");
        expected.add("def");
        addPropertyValidator(startEvents, "hashSet", "hashSet", expected, HashSet.class);
        addPropertyValidator(startEvents, "hashSetFromList", "hashSet", expected, HashSet.class);
        addPropertyValidator(startEvents, "implicitSetFromList", "set", expected, Set.class);

        // simple list of strings
        expected = new LinkedHashSet();
        expected.add("abc");
        expected.add("def");
        addPropertyValidator(startEvents, "linkedHashSet", "linkedHashSet", expected, LinkedHashSet.class);
        addPropertyValidator(startEvents, "linkedHashSetFromList", "linkedHashSet", expected, LinkedHashSet.class);

        // simple list of strings
        expected = new TreeSet();
        expected.add("abc");
        expected.add("def");
        addPropertyValidator(startEvents, "treeSet", "treeSet", expected, TreeSet.class);
        addPropertyValidator(startEvents, "treeSetFromList", "treeSet", expected, TreeSet.class);

        controller.run();
    }

	public void testConvertedMap() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/converted_map_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        // simple list of strings
        Map expected = new HashMap();
        expected.put("abc", "1");
        expected.put("def", "2");
        addPropertyValidator(startEvents, "hashMap", "hashMap", expected, HashMap.class);
        addPropertyValidator(startEvents, "treeMap", "treeMap", expected, TreeMap.class);
        addPropertyValidator(startEvents, "hashtable", "hashtable", expected, Hashtable.class);

        controller.run();
    }

	public void testStaticFactoryListProperty() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/list_static_factory_property_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();
        // Empty list
        List expected = new ArrayList();
        addPropertyValidator(startEvents, "compEmptyList", "list", expected, List.class);
        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compEmptyList", new TestProperty(
            new TestListValue(new TestValue[0])
            , "list")));

        // null list item
        addPropertyValidator(startEvents, "compNullList", "list", null, List.class);
        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compNullList", new TestProperty(
            new TestNullValue()
            , "list")));

        // simple list of strings
        expected = new ArrayList();
        expected.add("abc");
        expected.add("def");
        addPropertyValidator(startEvents, "compStringItems", "list", expected, List.class);
        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compStringItems", new TestProperty(
            new TestListValue(new TestValue[] { new TestStringValue("abc"), new TestStringValue("def") })
            , "list")));

        // simple list of strings with duplicates
        expected = new ArrayList();
        expected.add("abc");
        expected.add("abc");
        addPropertyValidator(startEvents, "compDupItems", "list", expected, List.class);
        // list of "converted" strings
        expected = new ArrayList();
        expected.add(Boolean.TRUE);
        expected.add(Boolean.FALSE);
        addPropertyValidator(startEvents, "compBooleanItems", "list", expected, List.class);
        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compBooleanItems", new TestProperty(
            new TestListValue(new TestValue[] { new TestStringValue(Boolean.class, "true"),
            new TestStringValue(Boolean.class, "false") })
            , "list")));

        // list containing a mixed bag of types
        expected = new ArrayList();
        expected.add("abc");
        expected.add(Boolean.FALSE);
        expected.add(Byte.valueOf((byte)3));
        expected.add(Character.valueOf('4'));
        expected.add(Integer.valueOf(5));
        expected.add(Short.valueOf((short)6));
        expected.add(Long.valueOf(7));
        expected.add(Double.valueOf(8.0));
		expected.add(Float.valueOf(9.0f));
        expected.add(new URL("http://www.osgi.org"));
        expected.add(String.class);
        expected.add(new Locale("en", "US"));
        expected.add(null);
        addPropertyValidator(startEvents, "compMixedItems", "list", expected, List.class);
        startEvents.addValidator(new PropertyMetadataValidator("compMixedItems", new TestProperty(
            new TestListValue(new TestValue[] {
                new TestStringValue(String.class, "abc"),
                new TestStringValue(Boolean.class, "false"),
                new TestStringValue(Byte.class, "3"),
                new TestStringValue(Character.class, "4"),
                new TestStringValue(Integer.class, "5"),
                new TestStringValue(Short.class, "6"),
                new TestStringValue(Long.class, "7"),
                new TestStringValue(Double.class, "8.0"),
                new TestStringValue(Float.class, "9.0"),
                new TestStringValue(URL.class, "http://www.osgi.org"),
                new TestStringValue(Class.class, "java.lang.String"),
                new TestStringValue(Locale.class, "en_US"),
                new TestNullValue()
            })
            , "list")));

        // a typed list of Doubles
        expected = new ArrayList();
        expected.add(Double.valueOf(0.0));
        expected.add(Double.valueOf(1.0));
        addPropertyValidator(startEvents, "compTypedItems", "list", expected, List.class);

        startEvents.addValidator(new PropertyMetadataValidator("compTypedItems", new TestProperty(
            new TestListValue(new TestValue[] {
                new TestStringValue("0.0"),
                new TestStringValue("1.0")
            }, Double.class)
            , "list")));

        // a type list of Doubles with an element override
        expected = new ArrayList();
        expected.add(Double.valueOf(0.0));
        expected.add(Double.valueOf(1.0));
        expected.add(Boolean.TRUE);
        addPropertyValidator(startEvents, "compTypeOverride", "list", expected, List.class);

        startEvents.addValidator(new PropertyMetadataValidator("compTypeOverride", new TestProperty(
            new TestListValue(new TestValue[] {
                new TestStringValue("0.0"),
                new TestStringValue("1.0"),
                new TestStringValue(Boolean.class, "true")
            }, Double.class)
            , "list")));

        // list nested inside of a list
        List innerList = new ArrayList();
        innerList.add("abc");
        innerList.add("def");
        expected = new ArrayList();
        expected.add(innerList);
        addPropertyValidator(startEvents, "compNestedList", "list", expected, List.class);

        // set nested inside of a list
        Set innerSet = new HashSet();
        innerSet.add("abc");
        innerSet.add("def");
        expected = new ArrayList();
        expected.add(innerSet);
        addPropertyValidator(startEvents, "compNestedSet", "list", expected, List.class);

        // Map nested inside of a list
        Map innerMap = new HashMap();
        innerMap.put("abc", "def");
        innerMap.put("ghi", "jkl");
        expected = new ArrayList();
        expected.add(innerMap);
        addPropertyValidator(startEvents, "compNestedMap", "list", expected, List.class);

        // Properties nested inside of a list
        Properties innerProps = new Properties();
        innerProps.put("abc", "def");
        innerProps.put("ghi", "jkl");
        expected = new ArrayList();
        expected.add(innerProps);
        addPropertyValidator(startEvents, "compNestedProps", "list", expected, List.class);

        controller.run();
    }

	public void testInstanceFactoryListProperty() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/list_factory_property_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();
        // Empty list
        List expected = new ArrayList();
        addPropertyValidator(startEvents, "compEmptyList", "list", expected, List.class);
        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compEmptyList", new TestProperty(
            new TestListValue(new TestValue[0])
            , "list")));

        // null list item
        addPropertyValidator(startEvents, "compNullList", "list", null, List.class);
        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compNullList", new TestProperty(
            new TestNullValue()
            , "list")));

        // simple list of strings
        expected = new ArrayList();
        expected.add("abc");
        expected.add("def");
        addPropertyValidator(startEvents, "compStringItems", "list", expected, List.class);
        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compStringItems", new TestProperty(
            new TestListValue(new TestValue[] { new TestStringValue("abc"), new TestStringValue("def") })
            , "list")));

        // list of "converted" strings
        expected = new ArrayList();
        expected.add(Boolean.TRUE);
        expected.add(Boolean.FALSE);
        addPropertyValidator(startEvents, "compBooleanItems", "list", expected, List.class);
        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compBooleanItems", new TestProperty(
            new TestListValue(new TestValue[] { new TestStringValue(Boolean.class, "true"),
            new TestStringValue(Boolean.class, "false") })
            , "list")));

        // list containing a mixed bag of types
        expected = new ArrayList();
        expected.add("abc");
        expected.add(Boolean.FALSE);
        expected.add(Byte.valueOf((byte)3));
        expected.add(Character.valueOf('4'));
        expected.add(Integer.valueOf(5));
        expected.add(Short.valueOf((short)6));
        expected.add(Long.valueOf(7));
        expected.add(Double.valueOf(8.0));
		expected.add(Float.valueOf(9.0f));
        expected.add(new URL("http://www.osgi.org"));
        expected.add(String.class);
        expected.add(new Locale("en", "US"));
        expected.add(null);
        addPropertyValidator(startEvents, "compMixedItems", "list", expected, List.class);
        startEvents.addValidator(new PropertyMetadataValidator("compMixedItems", new TestProperty(
            new TestListValue(new TestValue[] {
                new TestStringValue(String.class, "abc"),
                new TestStringValue(Boolean.class, "false"),
                new TestStringValue(Byte.class, "3"),
                new TestStringValue(Character.class, "4"),
                new TestStringValue(Integer.class, "5"),
                new TestStringValue(Short.class, "6"),
                new TestStringValue(Long.class, "7"),
                new TestStringValue(Double.class, "8.0"),
                new TestStringValue(Float.class, "9.0"),
                new TestStringValue(URL.class, "http://www.osgi.org"),
                new TestStringValue(Class.class, "java.lang.String"),
                new TestStringValue(Locale.class, "en_US"),
                new TestNullValue()
            })
            , "list")));


        // a typed list of Doubles
        expected = new ArrayList();
        expected.add(Double.valueOf(0.0));
        expected.add(Double.valueOf(1.0));
        addPropertyValidator(startEvents, "compTypedItems", "list", expected, List.class);

        startEvents.addValidator(new PropertyMetadataValidator("compTypedItems", new TestProperty(
            new TestListValue(new TestValue[] {
                new TestStringValue("0.0"),
                new TestStringValue("1.0")
            }, Double.class)
            , "list")));

        // a type list of Doubles with an element override
        expected = new ArrayList();
        expected.add(Double.valueOf(0.0));
        expected.add(Double.valueOf(1.0));
        expected.add(Boolean.TRUE);
        addPropertyValidator(startEvents, "compTypeOverride", "list", expected, List.class);

        startEvents.addValidator(new PropertyMetadataValidator("compTypeOverride", new TestProperty(
            new TestListValue(new TestValue[] {
                new TestStringValue("0.0"),
                new TestStringValue("1.0"),
                new TestStringValue(Boolean.class, "true")
            }, Double.class)
            , "list")));

        // list nested inside of a list
        List innerList = new ArrayList();
        innerList.add("abc");
        innerList.add("def");
        expected = new ArrayList();
        expected.add(innerList);
        addPropertyValidator(startEvents, "compNestedList", "list", expected, List.class);

        // set nested inside of a list
        Set innerSet = new HashSet();
        innerSet.add("abc");
        innerSet.add("def");
        expected = new ArrayList();
        expected.add(innerSet);
        addPropertyValidator(startEvents, "compNestedSet", "list", expected, List.class);

        // Map nested inside of a list
        Map innerMap = new HashMap();
        innerMap.put("abc", "def");
        innerMap.put("ghi", "jkl");
        expected = new ArrayList();
        expected.add(innerMap);
        addPropertyValidator(startEvents, "compNestedMap", "list", expected, List.class);

        // Properties nested inside of a list
        Properties innerProps = new Properties();
        innerProps.put("abc", "def");
        innerProps.put("ghi", "jkl");
        expected = new ArrayList();
        expected.add(innerProps);
        addPropertyValidator(startEvents, "compNestedProps", "list", expected, List.class);

        controller.run();
    }

	public void testSetConstructor() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(), getWebServer()+"www/set_constructor_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();
        // Empty set
        Set expected = new HashSet();
        addConstructorValidator(startEvents, "compEmptySet", expected, Set.class);
        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compEmptySet", new TestArgument(
            new TestSetValue(new TestValue[0]))));

        // null set item
        addConstructorValidator(startEvents, "compNullSet", null, Set.class);
        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compNullSet", new TestArgument(
            new TestNullValue(), Set.class)));

        // simple set of strings
        expected = new HashSet();
        expected.add("abc");
        expected.add("def");
        addConstructorValidator(startEvents, "compStringItems", expected, Set.class);
        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compStringItems", new TestArgument(
            new TestSetValue(new TestValue[] { new TestStringValue("abc"), new TestStringValue("def") }))));

        // simple set of strings with duplicates...this should be collapsed to a single
        // instance using set semantics.
        expected = new HashSet();
        expected.add("abc");
        addConstructorValidator(startEvents, "compDupItems", expected, Set.class);
        // set of "converted" strings
        expected = new HashSet();
        expected.add(Boolean.TRUE);
        expected.add(Boolean.FALSE);
        addConstructorValidator(startEvents, "compBooleanItems", expected, Set.class);
        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compBooleanItems", new TestArgument(
            new TestSetValue(new TestValue[] { new TestStringValue(Boolean.class, "true"),
            new TestStringValue(Boolean.class, "false") }))));

        // set containing a mixed bag of types
        expected = new HashSet();
        expected.add("abc");
        expected.add(Boolean.FALSE);
        expected.add(Byte.valueOf((byte)3));
        expected.add(Character.valueOf('4'));
        expected.add(Integer.valueOf(5));
        expected.add(Short.valueOf((short)6));
        expected.add(Long.valueOf(7));
        expected.add(Double.valueOf(8.0));
		expected.add(Float.valueOf(9.0f));
        expected.add(new URL("http://www.osgi.org"));
        expected.add(String.class);
        expected.add(new Locale("en", "US"));
        expected.add(null);
        addConstructorValidator(startEvents, "compMixedItems", expected, Set.class);

        // large metadata validation step here.
        startEvents.addValidator(new ArgumentMetadataValidator("compMixedItems", new TestArgument(
            new TestSetValue(new TestValue[] {
                new TestStringValue(String.class, "abc"),
                new TestStringValue(Boolean.class, "false"),
                new TestStringValue(Byte.class, "3"),
                new TestStringValue(Character.class, "4"),
                new TestStringValue(Integer.class, "5"),
                new TestStringValue(Short.class, "6"),
                new TestStringValue(Long.class, "7"),
                new TestStringValue(Double.class, "8.0"),
                new TestStringValue(Float.class, "9.0"),
                new TestStringValue(URL.class, "http://www.osgi.org"),
                new TestStringValue(Class.class, "java.lang.String"),
                new TestStringValue(Locale.class, "en_US"),
                new TestNullValue()
            }))));

        // a typed set of elements
        expected = new HashSet();
        expected.add(Double.valueOf(0.0));
        expected.add(Double.valueOf(1.0));
        addConstructorValidator(startEvents, "compTypedItems", expected, Set.class);

        startEvents.addValidator(new ArgumentMetadataValidator("compTypedItems", new TestArgument(
            new TestSetValue(new TestValue[] {
                new TestStringValue("0.0"),
                new TestStringValue("1.0")
            }, Double.class))));

        // a typed set of elements, with an element override
        expected = new HashSet();
        expected.add(Double.valueOf(0.0));
        expected.add(Double.valueOf(1.0));
        expected.add(Boolean.TRUE);
        addConstructorValidator(startEvents, "compTypeOverride", expected, Set.class);

        startEvents.addValidator(new ArgumentMetadataValidator("compTypeOverride", new TestArgument(
            new TestSetValue(new TestValue[] {
                new TestStringValue("0.0"),
                new TestStringValue("1.0"),
                new TestStringValue(Boolean.class, "true")
            }, Double.class))));

        // set nested inside of a set
        Set innerSet = new HashSet();
        innerSet.add("abc");
        innerSet.add("def");
        expected = new HashSet();
        expected.add(innerSet);
        addConstructorValidator(startEvents, "compNestedSet", expected, Set.class);

        // List nested inside of a set
        List innerList = new ArrayList();
        innerList.add("abc");
        innerList.add("def");
        expected = new HashSet();
        expected.add(innerList);
        addConstructorValidator(startEvents, "compNestedList", expected, Set.class);

        // Map nested inside of a set
        Map innerMap = new HashMap();
        innerMap.put("abc", "def");
        innerMap.put("ghi", "jkl");
        expected = new HashSet();
        expected.add(innerMap);
        addConstructorValidator(startEvents, "compNestedMap", expected, Set.class);

        // Properties nested inside of a set
        Properties innerProps = new Properties();
        innerProps.put("abc", "def");
        innerProps.put("ghi", "jkl");
        expected = new HashSet();
        expected.add(innerProps);
        addConstructorValidator(startEvents, "compNestedProps", expected, Set.class);

        controller.run();
    }

	public void testSetStaticFactoryConstructor() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(), getWebServer()+"www/set_static_factory_constructor_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();
        // Empty set
        Set expected = new HashSet();
        addConstructorValidator(startEvents, "compEmptySet", expected, Set.class);
        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compEmptySet", new TestArgument(
            new TestSetValue(new TestValue[0]))));

        // null set item
        addConstructorValidator(startEvents, "compNullSet", null, Set.class);
        // validate the metadata for this one too
        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compNullSet", new TestArgument(
            new TestNullValue(), Set.class)));

        // simple set of strings
        expected = new HashSet();
        expected.add("abc");
        expected.add("def");
        addConstructorValidator(startEvents, "compStringItems", expected, Set.class);
        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compStringItems", new TestArgument(
            new TestSetValue(new TestValue[] { new TestStringValue("abc"), new TestStringValue("def") }))));

        // simple set of strings with duplicates...this should be collapsed to a single
        // instance using set semantics.
        expected = new HashSet();
        expected.add("abc");
        addConstructorValidator(startEvents, "compDupItems", expected, Set.class);
        // set of "converted" strings
        expected = new HashSet();
        expected.add(Boolean.TRUE);
        expected.add(Boolean.FALSE);
        addConstructorValidator(startEvents, "compBooleanItems", expected, Set.class);
        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compBooleanItems", new TestArgument(
            new TestSetValue(new TestValue[] { new TestStringValue(Boolean.class, "true"),
            new TestStringValue(Boolean.class, "false") }))));

        // set containing a mixed bag of types
        expected = new HashSet();
        expected.add("abc");
        expected.add(Boolean.FALSE);
        expected.add(Byte.valueOf((byte)3));
        expected.add(Character.valueOf('4'));
        expected.add(Integer.valueOf(5));
        expected.add(Short.valueOf((short)6));
        expected.add(Long.valueOf(7));
        expected.add(Double.valueOf(8.0));
		expected.add(Float.valueOf(9.0f));
        expected.add(new URL("http://www.osgi.org"));
        expected.add(String.class);
        expected.add(new Locale("en", "US"));
        expected.add(null);
        addConstructorValidator(startEvents, "compMixedItems", expected, Set.class);

        // large metadata validation step here.
        startEvents.addValidator(new ArgumentMetadataValidator("compMixedItems", new TestArgument(
            new TestSetValue(new TestValue[] {
                new TestStringValue(String.class, "abc"),
                new TestStringValue(Boolean.class, "false"),
                new TestStringValue(Byte.class, "3"),
                new TestStringValue(Character.class, "4"),
                new TestStringValue(Integer.class, "5"),
                new TestStringValue(Short.class, "6"),
                new TestStringValue(Long.class, "7"),
                new TestStringValue(Double.class, "8.0"),
                new TestStringValue(Float.class, "9.0"),
                new TestStringValue(URL.class, "http://www.osgi.org"),
                new TestStringValue(Class.class, "java.lang.String"),
                new TestStringValue(Locale.class, "en_US"),
                new TestNullValue()
            }))));

        // a typed set of elements
        expected = new HashSet();
        expected.add(Double.valueOf(0.0));
        expected.add(Double.valueOf(1.0));
        addConstructorValidator(startEvents, "compTypedItems", expected, Set.class);

        startEvents.addValidator(new ArgumentMetadataValidator("compTypedItems", new TestArgument(
            new TestSetValue(new TestValue[] {
                new TestStringValue("0.0"),
                new TestStringValue("1.0")
            }, Double.class))));

        // a typed set of elements, with an element override
        expected = new HashSet();
        expected.add(Double.valueOf(0.0));
        expected.add(Double.valueOf(1.0));
        expected.add(Boolean.TRUE);
        addConstructorValidator(startEvents, "compTypeOverride", expected, Set.class);

        startEvents.addValidator(new ArgumentMetadataValidator("compTypeOverride", new TestArgument(
            new TestSetValue(new TestValue[] {
                new TestStringValue("0.0"),
                new TestStringValue("1.0"),
                new TestStringValue(Boolean.class, "true")
            }, Double.class))));

        // set nested inside of a set
        Set innerSet = new HashSet();
        innerSet.add("abc");
        innerSet.add("def");
        expected = new HashSet();
        expected.add(innerSet);
        addConstructorValidator(startEvents, "compNestedSet", expected, Set.class);


        // List nested inside of a set
        List innerList = new ArrayList();
        innerList.add("abc");
        innerList.add("def");
        expected = new HashSet();
        expected.add(innerList);
        addConstructorValidator(startEvents, "compNestedList", expected, Set.class);

        // Map nested inside of a set
        Map innerMap = new HashMap();
        innerMap.put("abc", "def");
        innerMap.put("ghi", "jkl");
        expected = new HashSet();
        expected.add(innerMap);
        addConstructorValidator(startEvents, "compNestedMap", expected, Set.class);

        // Properties nested inside of a set
        Properties innerProps = new Properties();
        innerProps.put("abc", "def");
        innerProps.put("ghi", "jkl");
        expected = new HashSet();
        expected.add(innerProps);
        addConstructorValidator(startEvents, "compNestedProps", expected, Set.class);

        controller.run();
    }

	public void testSetInstanceFactoryConstructor() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(), getWebServer()+"www/set_factory_constructor_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();
        // Empty set
        Set expected = new HashSet();
        addConstructorValidator(startEvents, "compEmptySet", expected, Set.class);
        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compEmptySet", new TestArgument(
            new TestSetValue(new TestValue[0]))));

        // null set item
        addConstructorValidator(startEvents, "compNullSet", null, Set.class);
        // validate the metadata for this one too
        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compNullSet", new TestArgument(
            new TestNullValue(), Set.class)));

        // simple set of strings
        expected = new HashSet();
        expected.add("abc");
        expected.add("def");
        addConstructorValidator(startEvents, "compStringItems", expected, Set.class);
        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compStringItems", new TestArgument(
            new TestSetValue(new TestValue[] { new TestStringValue("abc"), new TestStringValue("def") }))));

        // simple set of strings with duplicates...this should be collapsed to a single
        // instance using set semantics.
        expected = new HashSet();
        expected.add("abc");
        addConstructorValidator(startEvents, "compDupItems", expected, Set.class);
        // set of "converted" strings
        expected = new HashSet();
        expected.add(Boolean.TRUE);
        expected.add(Boolean.FALSE);
        addConstructorValidator(startEvents, "compBooleanItems", expected, Set.class);
        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compBooleanItems", new TestArgument(
            new TestSetValue(new TestValue[] { new TestStringValue(Boolean.class, "true"),
            new TestStringValue(Boolean.class, "false") }))));

        // set containing a mixed bag of types
        expected = new HashSet();
        expected.add("abc");
        expected.add(Boolean.FALSE);
        expected.add(Byte.valueOf((byte)3));
        expected.add(Character.valueOf('4'));
        expected.add(Integer.valueOf(5));
        expected.add(Short.valueOf((short)6));
        expected.add(Long.valueOf(7));
        expected.add(Double.valueOf(8.0));
		expected.add(Float.valueOf(9.0f));
        expected.add(new URL("http://www.osgi.org"));
        expected.add(String.class);
        expected.add(new Locale("en", "US"));
        expected.add(null);
        addConstructorValidator(startEvents, "compMixedItems", expected, Set.class);

        // large metadata validation step here.
        startEvents.addValidator(new ArgumentMetadataValidator("compMixedItems", new TestArgument(
            new TestSetValue(new TestValue[] {
                new TestStringValue(String.class, "abc"),
                new TestStringValue(Boolean.class, "false"),
                new TestStringValue(Byte.class, "3"),
                new TestStringValue(Character.class, "4"),
                new TestStringValue(Integer.class, "5"),
                new TestStringValue(Short.class, "6"),
                new TestStringValue(Long.class, "7"),
                new TestStringValue(Double.class, "8.0"),
                new TestStringValue(Float.class, "9.0"),
                new TestStringValue(URL.class, "http://www.osgi.org"),
                new TestStringValue(Class.class, "java.lang.String"),
                new TestStringValue(Locale.class, "en_US"),
                new TestNullValue()
            }))));

        // a typed set of elements
        expected = new HashSet();
        expected.add(Double.valueOf(0.0));
        expected.add(Double.valueOf(1.0));
        addConstructorValidator(startEvents, "compTypedItems", expected, Set.class);

        startEvents.addValidator(new ArgumentMetadataValidator("compTypedItems", new TestArgument(
            new TestSetValue(new TestValue[] {
                new TestStringValue("0.0"),
                new TestStringValue("1.0")
            }, Double.class))));

        // a typed set of elements, with an element override
        expected = new HashSet();
        expected.add(Double.valueOf(0.0));
        expected.add(Double.valueOf(1.0));
        expected.add(Boolean.TRUE);
        addConstructorValidator(startEvents, "compTypeOverride", expected, Set.class);

        startEvents.addValidator(new ArgumentMetadataValidator("compTypeOverride", new TestArgument(
            new TestSetValue(new TestValue[] {
                new TestStringValue("0.0"),
                new TestStringValue("1.0"),
                new TestStringValue(Boolean.class, "true")
            }, Double.class))));

        // set nested inside of a set
        Set innerSet = new HashSet();
        innerSet.add("abc");
        innerSet.add("def");
        expected = new HashSet();
        expected.add(innerSet);
        addConstructorValidator(startEvents, "compNestedSet", expected, Set.class);


        // List nested inside of a set
        List innerList = new ArrayList();
        innerList.add("abc");
        innerList.add("def");
        expected = new HashSet();
        expected.add(innerList);
        addConstructorValidator(startEvents, "compNestedList", expected, Set.class);

        // Map nested inside of a set
        Map innerMap = new HashMap();
        innerMap.put("abc", "def");
        innerMap.put("ghi", "jkl");
        expected = new HashSet();
        expected.add(innerMap);
        addConstructorValidator(startEvents, "compNestedMap", expected, Set.class);

        // Properties nested inside of a set
        Properties innerProps = new Properties();
        innerProps.put("abc", "def");
        innerProps.put("ghi", "jkl");
        expected = new HashSet();
        expected.add(innerProps);
        addConstructorValidator(startEvents, "compNestedProps", expected, Set.class);

        controller.run();
    }

	public void testSetProperty() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(), getWebServer()+"www/set_property_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();
        // Empty set
        Set expected = new HashSet();
        addPropertyValidator(startEvents, "compEmptySet", "set", expected, Set.class);
        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compEmptySet", new TestProperty(
            new TestSetValue(new TestValue[0])
            , "set")));

        // null set item
        addPropertyValidator(startEvents, "compNullSet", "set", null, Set.class);
        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compNullSet", new TestProperty(
            new TestNullValue()
            , "set")));

        // simple set of strings
        expected = new HashSet();
        expected.add("abc");
        expected.add("def");
        addPropertyValidator(startEvents, "compStringItems", "set", expected, Set.class);
        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compStringItems", new TestProperty(
            new TestSetValue(new TestValue[] { new TestStringValue("abc"), new TestStringValue("def") })
            , "set")));

        // simple set of strings with duplicates...this should be collapsed to a single
        // instance using set semantics.
        expected = new HashSet();
        expected.add("abc");
        addPropertyValidator(startEvents, "compDupItems", "set", expected, Set.class);
        // set of "converted" strings
        expected = new HashSet();
        expected.add(Boolean.TRUE);
        expected.add(Boolean.FALSE);
        addPropertyValidator(startEvents, "compBooleanItems", "set", expected, Set.class);
        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compBooleanItems", new TestProperty(
            new TestSetValue(new TestValue[] { new TestStringValue(Boolean.class, "true"),
            new TestStringValue(Boolean.class, "false") })
            , "set")));

        // set containing a mixed bag of types
        expected = new HashSet();
        expected.add("abc");
        expected.add(Boolean.FALSE);
        expected.add(Byte.valueOf((byte)3));
        expected.add(Character.valueOf('4'));
        expected.add(Integer.valueOf(5));
        expected.add(Short.valueOf((short)6));
        expected.add(Long.valueOf(7));
        expected.add(Double.valueOf(8.0));
		expected.add(Float.valueOf(9.0f));
        expected.add(new URL("http://www.osgi.org"));
        expected.add(String.class);
        expected.add(new Locale("en", "US"));
        expected.add(null);
        addPropertyValidator(startEvents, "compMixedItems", "set", expected, Set.class);

        startEvents.addValidator(new PropertyMetadataValidator("compMixedItems", new TestProperty(
            new TestSetValue(new TestValue[] {
                new TestStringValue(String.class, "abc"),
                new TestStringValue(Boolean.class, "false"),
                new TestStringValue(Byte.class, "3"),
                new TestStringValue(Character.class, "4"),
                new TestStringValue(Integer.class, "5"),
                new TestStringValue(Short.class, "6"),
                new TestStringValue(Long.class, "7"),
                new TestStringValue(Double.class, "8.0"),
                new TestStringValue(Float.class, "9.0"),
                new TestStringValue(URL.class, "http://www.osgi.org"),
                new TestStringValue(Class.class, "java.lang.String"),
                new TestStringValue(Locale.class, "en_US"),
                new TestNullValue()
            })
            , "set")));

        // a typed set of elements
        expected = new HashSet();
        expected.add(Double.valueOf(0.0));
        expected.add(Double.valueOf(1.0));
        addPropertyValidator(startEvents, "compTypedItems", "set", expected, Set.class);

        // Section 121.8.3 states that if the value type of a <value> used in a collection
        // is not set, then its metadata must return the valueType of the collection (which may
        // be null).  The value type of the collection is set to Double.
        startEvents.addValidator(new PropertyMetadataValidator("compTypedItems", new TestProperty(
            new TestSetValue(new TestValue[] {
                new TestStringValue("0.0"),
                new TestStringValue("1.0")
            }, Double.class)
            , "set")));

        // a typed set of elements, with an element override
        expected = new HashSet();
        expected.add(Double.valueOf(0.0));
        expected.add(Double.valueOf(1.0));
        expected.add(Boolean.TRUE);
        addPropertyValidator(startEvents, "compTypeOverride", "set", expected, Set.class);

        startEvents.addValidator(new PropertyMetadataValidator("compTypeOverride", new TestProperty(
            new TestSetValue(new TestValue[] {
                new TestStringValue("0.0"),
                new TestStringValue("1.0"),
                new TestStringValue(Boolean.class, "true")
            }, Double.class)
            , "set")));

        // set nested inside of a set
        Set innerSet = new HashSet();
        innerSet.add("abc");
        innerSet.add("def");
        expected = new HashSet();
        expected.add(innerSet);
        addPropertyValidator(startEvents, "compNestedSet", "set", expected, Set.class);


        // List nested inside of a set
        List innerList = new ArrayList();
        innerList.add("abc");
        innerList.add("def");
        expected = new HashSet();
        expected.add(innerList);
        addPropertyValidator(startEvents, "compNestedList", "set", expected, Set.class);

        // Map nested inside of a set
        Map innerMap = new HashMap();
        innerMap.put("abc", "def");
        innerMap.put("ghi", "jkl");
        expected = new HashSet();
        expected.add(innerMap);
        addPropertyValidator(startEvents, "compNestedMap", "set", expected, Set.class);

        // Properties nested inside of a set
        Properties innerProps = new Properties();
        innerProps.put("abc", "def");
        innerProps.put("ghi", "jkl");
        expected = new HashSet();
        expected.add(innerProps);
        addPropertyValidator(startEvents, "compNestedProps", "set", expected, Set.class);

        controller.run();
    }

	public void testStaticFactorySetProperty() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(), getWebServer()+"www/set_static_factory_property_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();
        // Empty set
        Set expected = new HashSet();
        addPropertyValidator(startEvents, "compEmptySet", "set", expected, Set.class);
        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compEmptySet", new TestProperty(
            new TestSetValue(new TestValue[0])
            , "set")));

        // null set item
        addPropertyValidator(startEvents, "compNullSet", "set", null, Set.class);
        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compNullSet", new TestProperty(
            new TestNullValue()
            , "set")));

        // simple set of strings
        expected = new HashSet();
        expected.add("abc");
        expected.add("def");
        addPropertyValidator(startEvents, "compStringItems", "set", expected, Set.class);
        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compStringItems", new TestProperty(
            new TestSetValue(new TestValue[] { new TestStringValue("abc"), new TestStringValue("def") })
            , "set")));

        // simple set of strings with duplicates...this should be collapsed to a single
        // instance using set semantics.
        expected = new HashSet();
        expected.add("abc");
        addPropertyValidator(startEvents, "compDupItems", "set", expected, Set.class);
        // set of "converted" strings
        expected = new HashSet();
        expected.add(Boolean.TRUE);
        expected.add(Boolean.FALSE);
        addPropertyValidator(startEvents, "compBooleanItems", "set", expected, Set.class);
        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compBooleanItems", new TestProperty(
            new TestSetValue(new TestValue[] { new TestStringValue(Boolean.class, "true"),
            new TestStringValue(Boolean.class, "false") })
            , "set")));

        // set containing a mixed bag of types
        expected = new HashSet();
        expected.add("abc");
        expected.add(Boolean.FALSE);
        expected.add(Byte.valueOf((byte)3));
        expected.add(Character.valueOf('4'));
        expected.add(Integer.valueOf(5));
        expected.add(Short.valueOf((short)6));
        expected.add(Long.valueOf(7));
        expected.add(Double.valueOf(8.0));
		expected.add(Float.valueOf(9.0f));
        expected.add(new URL("http://www.osgi.org"));
        expected.add(String.class);
        expected.add(new Locale("en", "US"));
        expected.add(null);
        addPropertyValidator(startEvents, "compMixedItems", "set", expected, Set.class);

        startEvents.addValidator(new PropertyMetadataValidator("compMixedItems", new TestProperty(
            new TestSetValue(new TestValue[] {
                new TestStringValue(String.class, "abc"),
                new TestStringValue(Boolean.class, "false"),
                new TestStringValue(Byte.class, "3"),
                new TestStringValue(Character.class, "4"),
                new TestStringValue(Integer.class, "5"),
                new TestStringValue(Short.class, "6"),
                new TestStringValue(Long.class, "7"),
                new TestStringValue(Double.class, "8.0"),
                new TestStringValue(Float.class, "9.0"),
                new TestStringValue(URL.class, "http://www.osgi.org"),
                new TestStringValue(Class.class, "java.lang.String"),
                new TestStringValue(Locale.class, "en_US"),
                new TestNullValue()
            })
            , "set")));

        // a typed set of elements
        expected = new HashSet();
        expected.add(Double.valueOf(0.0));
        expected.add(Double.valueOf(1.0));
        addPropertyValidator(startEvents, "compTypedItems", "set", expected, Set.class);

        startEvents.addValidator(new PropertyMetadataValidator("compTypedItems", new TestProperty(
            new TestSetValue(new TestValue[] {
                new TestStringValue("0.0"),
                new TestStringValue("1.0")
            }, Double.class)
            , "set")));

        // a typed set of elements, with an element override
        expected = new HashSet();
        expected.add(Double.valueOf(0.0));
        expected.add(Double.valueOf(1.0));
        expected.add(Boolean.TRUE);
        addPropertyValidator(startEvents, "compTypeOverride", "set", expected, Set.class);

        startEvents.addValidator(new PropertyMetadataValidator("compTypeOverride", new TestProperty(
            new TestSetValue(new TestValue[] {
                new TestStringValue("0.0"),
                new TestStringValue("1.0"),
                new TestStringValue(Boolean.class, "true")
            }, Double.class)
            , "set")));

        // set nested inside of a set
        Set innerSet = new HashSet();
        innerSet.add("abc");
        innerSet.add("def");
        expected = new HashSet();
        expected.add(innerSet);
        addPropertyValidator(startEvents, "compNestedSet", "set", expected, Set.class);


        // List nested inside of a set
        List innerList = new ArrayList();
        innerList.add("abc");
        innerList.add("def");
        expected = new HashSet();
        expected.add(innerList);
        addPropertyValidator(startEvents, "compNestedList", "set", expected, Set.class);

        // Map nested inside of a set
        Map innerMap = new HashMap();
        innerMap.put("abc", "def");
        innerMap.put("ghi", "jkl");
        expected = new HashSet();
        expected.add(innerMap);
        addPropertyValidator(startEvents, "compNestedMap", "set", expected, Set.class);

        // Properties nested inside of a set
        Properties innerProps = new Properties();
        innerProps.put("abc", "def");
        innerProps.put("ghi", "jkl");
        expected = new HashSet();
        expected.add(innerProps);
        addPropertyValidator(startEvents, "compNestedProps", "set", expected, Set.class);

        controller.run();
    }

	public void testInstanceFactorySetProperty() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(), getWebServer()+"www/set_factory_property_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();
        // Empty set
        Set expected = new HashSet();
        addPropertyValidator(startEvents, "compEmptySet", "set", expected, Set.class);
        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compEmptySet", new TestProperty(
            new TestSetValue(new TestValue[0])
            , "set")));

        // null set item
        addPropertyValidator(startEvents, "compNullSet", "set", null, Set.class);
        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compNullSet", new TestProperty(
            new TestNullValue()
            , "set")));

        // simple set of strings
        expected = new HashSet();
        expected.add("abc");
        expected.add("def");
        addPropertyValidator(startEvents, "compStringItems", "set", expected, Set.class);
        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compStringItems", new TestProperty(
            new TestSetValue(new TestValue[] { new TestStringValue("abc"), new TestStringValue("def") })
            , "set")));

        // set of "converted" strings
        expected = new HashSet();
        expected.add(Boolean.TRUE);
        expected.add(Boolean.FALSE);
        addPropertyValidator(startEvents, "compBooleanItems", "set", expected, Set.class);
        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compBooleanItems", new TestProperty(
            new TestSetValue(new TestValue[] { new TestStringValue(Boolean.class, "true"),
            new TestStringValue(Boolean.class, "false") })
            , "set")));
        // set containing a mixed bag of types
        expected = new HashSet();
        expected.add("abc");
        expected.add(Boolean.FALSE);
        expected.add(Byte.valueOf((byte)3));
        expected.add(Character.valueOf('4'));
        expected.add(Integer.valueOf(5));
        expected.add(Short.valueOf((short)6));
        expected.add(Long.valueOf(7));
        expected.add(Double.valueOf(8.0));
		expected.add(Float.valueOf(9.0f));
        expected.add(new URL("http://www.osgi.org"));
        expected.add(String.class);
        expected.add(new Locale("en", "US"));
        expected.add(null);
        addPropertyValidator(startEvents, "compMixedItems", "set", expected, Set.class);

        startEvents.addValidator(new PropertyMetadataValidator("compMixedItems", new TestProperty(
            new TestSetValue(new TestValue[] {
                new TestStringValue(String.class, "abc"),
                new TestStringValue(Boolean.class, "false"),
                new TestStringValue(Byte.class, "3"),
                new TestStringValue(Character.class, "4"),
                new TestStringValue(Integer.class, "5"),
                new TestStringValue(Short.class, "6"),
                new TestStringValue(Long.class, "7"),
                new TestStringValue(Double.class, "8.0"),
                new TestStringValue(Float.class, "9.0"),
                new TestStringValue(URL.class, "http://www.osgi.org"),
                new TestStringValue(Class.class, "java.lang.String"),
                new TestStringValue(Locale.class, "en_US"),
                new TestNullValue()
            })
            , "set")));

        // a typed set of elements
        expected = new HashSet();
        expected.add(Double.valueOf(0.0));
        expected.add(Double.valueOf(1.0));
        addPropertyValidator(startEvents, "compTypedItems", "set", expected, Set.class);

        startEvents.addValidator(new PropertyMetadataValidator("compTypedItems", new TestProperty(
            new TestSetValue(new TestValue[] {
                new TestStringValue("0.0"),
                new TestStringValue("1.0")
            }, Double.class)
            , "set")));

        // a typed set of elements, with an element override
        expected = new HashSet();
        expected.add(Double.valueOf(0.0));
        expected.add(Double.valueOf(1.0));
        expected.add(Boolean.TRUE);
        addPropertyValidator(startEvents, "compTypeOverride", "set", expected, Set.class);

        startEvents.addValidator(new PropertyMetadataValidator("compTypeOverride", new TestProperty(
            new TestSetValue(new TestValue[] {
                new TestStringValue("0.0"),
                new TestStringValue("1.0"),
                new TestStringValue(Boolean.class, "true")
            }, Double.class)
            , "set")));

        // set nested inside of a set
        Set innerSet = new HashSet();
        innerSet.add("abc");
        innerSet.add("def");
        expected = new HashSet();
        expected.add(innerSet);
        addPropertyValidator(startEvents, "compNestedSet", "set", expected, Set.class);


        // List nested inside of a set
        List innerList = new ArrayList();
        innerList.add("abc");
        innerList.add("def");
        expected = new HashSet();
        expected.add(innerList);
        addPropertyValidator(startEvents, "compNestedList", "set", expected, Set.class);

        // Map nested inside of a set
        Map innerMap = new HashMap();
        innerMap.put("abc", "def");
        innerMap.put("ghi", "jkl");
        expected = new HashSet();
        expected.add(innerMap);
        addPropertyValidator(startEvents, "compNestedMap", "set", expected, Set.class);

        // Properties nested inside of a set
        Properties innerProps = new Properties();
        innerProps.put("abc", "def");
        innerProps.put("ghi", "jkl");
        expected = new HashSet();
        expected.add(innerProps);
        addPropertyValidator(startEvents, "compNestedProps", "set", expected, Set.class);

        controller.run();
    }

	public void testMapConstructor() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/map_constructor_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();
        // Empty Map
        Map expected = new HashMap();
        addConstructorValidator(startEvents, "compEmptyMap", expected, Map.class);
        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compEmptyMap", new TestArgument(
            new TestMapValue(new MapValueEntry[0]))));

        // null Map item
        addConstructorValidator(startEvents, "compNullMap", null, Map.class);
        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compNullMap", new TestArgument(
            new TestNullValue(), Map.class)));

        // simple Map of strings
        expected = new HashMap();
        expected.put("abc", "1");
        expected.put("def", "2");
        addConstructorValidator(startEvents, "compStringItems", expected, Map.class);

        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compStringItems", new TestArgument(
            new TestMapValue(new MapValueEntry[]{
                new MapValueEntry("abc", "1"),
                new MapValueEntry("def", "2"),
            }))));

        // simple Map of strings with duplicates...this should be collapsed to a single
        // instance using Map semantics.
        expected = new HashMap();
        expected.put("abc", "2");
        addConstructorValidator(startEvents, "compDupItems", expected, Map.class);
        // Map of "converted" strings
        expected = new HashMap();
        expected.put("true", Boolean.TRUE);
        expected.put("false", Boolean.FALSE);
        addConstructorValidator(startEvents, "compBooleanItems", expected, Map.class);

        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compBooleanItems", new TestArgument(
            new TestMapValue(new MapValueEntry[]{
                new MapValueEntry("true", new TestStringValue(Boolean.class, "true")),
                new MapValueEntry("false", new TestStringValue(Boolean.class, "false"))
            }))));

        // Map of "converted" keys
        expected = new HashMap();
        expected.put(Boolean.TRUE, "true");
        expected.put(Boolean.FALSE, "false");
        addConstructorValidator(startEvents, "compBooleanKeys", expected, Map.class);

        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compBooleanKeys", new TestArgument(
            new TestMapValue(new MapValueEntry[]{
                new MapValueEntry(new TestStringValue(Boolean.class, "true"), new TestStringValue("true")),
                new MapValueEntry(new TestStringValue(Boolean.class, "false"), new TestStringValue("false"))
            }))));

        // Map with default value type
        expected = new HashMap();
        expected.put("true", Boolean.TRUE);
        expected.put("false", Boolean.FALSE);
        expected.put("double", Double.valueOf(1.0));
        addConstructorValidator(startEvents, "compTypedItems", expected, Map.class);

        // validate the metadata for this one too (the entry values will inherit the value type
        // in the individual metadata items).
        startEvents.addValidator(new ArgumentMetadataValidator("compTypedItems", new TestArgument(
            new TestMapValue(new MapValueEntry[]{
                new MapValueEntry("true", new TestStringValue("true")),
                new MapValueEntry("false", new TestStringValue("false")),
                new MapValueEntry("double", new TestStringValue(Double.class, "1.0"))
            }, null, Boolean.class
            ))));

        // Map of using default type keys
        expected = new HashMap();
        expected.put(Boolean.TRUE, "true");
        expected.put(Boolean.FALSE, "false");
        expected.put(Double.valueOf(1.0), "double");
        addConstructorValidator(startEvents, "compTypedKeys", expected, Map.class);

        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compTypedKeys", new TestArgument(
            new TestMapValue(new MapValueEntry[]{
                new MapValueEntry(new TestStringValue("true"), "true"),
                new MapValueEntry(new TestStringValue("false"), "false"),
                new MapValueEntry(new TestStringValue(Double.class, "1.0"), new TestStringValue("double"))
            }, Boolean.class, null
            ))));

        // Map containing a mixed bag of types
        expected = new HashMap();
        expected.put("String", "abc");
        expected.put("Boolean", Boolean.FALSE);
        expected.put("Byte", Byte.valueOf((byte)3));
        expected.put("Character", Character.valueOf('4'));
        expected.put("Integer", Integer.valueOf(5));
        expected.put("Short", Short.valueOf((short)6));
        expected.put("Long", Long.valueOf(7));
        expected.put("Double", Double.valueOf(8.0));
		expected.put("Float", Float.valueOf(9.0f));
        expected.put("URL", new URL("http://www.osgi.org"));
        expected.put("Class", String.class);
        expected.put("Locale", new Locale("en", "US"));
        expected.put("null", null);
        addConstructorValidator(startEvents, "compMixedItems", expected, Map.class);

        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compMixedItems", new TestArgument(
            new TestMapValue(new MapValueEntry[]{
                new MapValueEntry("String", new TestStringValue(String.class, "abc")),
                new MapValueEntry("Boolean", new TestStringValue(Boolean.class, "false")),
                new MapValueEntry("Byte", new TestStringValue(Byte.class, "3")),
                new MapValueEntry("Character", new TestStringValue(Character.class, "4")),
                new MapValueEntry("Integer", new TestStringValue(Integer.class, "5")),
                new MapValueEntry("Short", new TestStringValue(Short.class, "6")),
                new MapValueEntry("Long", new TestStringValue(Long.class, "7")),
                new MapValueEntry("Double", new TestStringValue(Double.class, "8.0")),
                new MapValueEntry("Float", new TestStringValue(Float.class, "9.0")),
                new MapValueEntry("URL", new TestStringValue(URL.class, "http://www.osgi.org")),
                new MapValueEntry("Class", new TestStringValue(Class.class, "java.lang.String")),
                new MapValueEntry("Locale", new TestStringValue(Locale.class, "en_US")),
                new MapValueEntry("null", new TestNullValue())
            }
            ))));

        // Map nested inside of a Map
        Map innerMap = new HashMap();
        innerMap.put("abc", "1");
        innerMap.put("def", "2");
        expected = new HashMap();
        expected.put("inner", innerMap);
        addConstructorValidator(startEvents, "compNestedMap", expected, Map.class);

        // List nested inside of a Map
        List innerList = new ArrayList();
        innerList.add("abc");
        innerList.add("def");
        expected = new HashMap();
        expected.put("inner", innerList);
        addConstructorValidator(startEvents, "compNestedList", expected, Map.class);

        // Set nested inside of a Map
        Set innerSet = new HashSet();
        innerSet.add("abc");
        innerSet.add("def");
        expected = new HashMap();
        expected.put("inner", innerSet);
        addConstructorValidator(startEvents, "compNestedSet", expected, Map.class);

        // Properties nested inside of a Map
        Properties innerProps = new Properties();
        innerProps.put("abc", "def");
        innerProps.put("ghi", "jkl");
        expected = new HashMap();
        expected.put("inner", innerProps);
        addConstructorValidator(startEvents, "compNestedProps", expected, Map.class);

        controller.run();
    }

	public void testFactoryMapConstructor() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(), getWebServer()+"www/map_factory_constructor_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();
        // Empty Map
        Map expected = new HashMap();
        addConstructorValidator(startEvents, "compEmptyMap", expected, Map.class);
        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compEmptyMap", new TestArgument(
            new TestMapValue(new MapValueEntry[0]))));

        // null Map item
        addConstructorValidator(startEvents, "compNullMap", null, Map.class);
        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compNullMap", new TestArgument(
            new TestNullValue(), Map.class)));

        // simple Map of strings
        expected = new HashMap();
        expected.put("abc", "1");
        expected.put("def", "2");
        addConstructorValidator(startEvents, "compStringItems", expected, Map.class);

        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compStringItems", new TestArgument(
            new TestMapValue(new MapValueEntry[]{
                new MapValueEntry("abc", "1"),
                new MapValueEntry("def", "2"),
            }))));

        // simple Map of strings with duplicates...this should be collapsed to a single
        // instance using Map semantics.
        expected = new HashMap();
        expected.put("abc", "2");
        addConstructorValidator(startEvents, "compDupItems", expected, Map.class);
        // Map of "converted" strings
        expected = new HashMap();
        expected.put("true", Boolean.TRUE);
        expected.put("false", Boolean.FALSE);
        addConstructorValidator(startEvents, "compBooleanItems", expected, Map.class);

        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compBooleanItems", new TestArgument(
            new TestMapValue(new MapValueEntry[]{
                new MapValueEntry("true", new TestStringValue(Boolean.class, "true")),
                new MapValueEntry("false", new TestStringValue(Boolean.class, "false"))
            }))));

        // Map of "converted" keys
        expected = new HashMap();
        expected.put(Boolean.TRUE, "true");
        expected.put(Boolean.FALSE, "false");
        addConstructorValidator(startEvents, "compBooleanKeys", expected, Map.class);

        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compBooleanKeys", new TestArgument(
            new TestMapValue(new MapValueEntry[]{
                new MapValueEntry(new TestStringValue(Boolean.class, "true"), new TestStringValue("true")),
                new MapValueEntry(new TestStringValue(Boolean.class, "false"), new TestStringValue("false"))
            }))));

        // Map with default value type
        expected = new HashMap();
        expected.put("true", Boolean.TRUE);
        expected.put("false", Boolean.FALSE);
        expected.put("double", Double.valueOf(1.0));
        addConstructorValidator(startEvents, "compTypedItems", expected, Map.class);

        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compTypedItems", new TestArgument(
            new TestMapValue(new MapValueEntry[]{
                new MapValueEntry("true", new TestStringValue("true")),
                new MapValueEntry("false", new TestStringValue("false")),
                new MapValueEntry("double", new TestStringValue(Double.class, "1.0"))
            }, null, Boolean.class
            ))));

        // Map of using default type keys
        expected = new HashMap();
        expected.put(Boolean.TRUE, "true");
        expected.put(Boolean.FALSE, "false");
        expected.put(Double.valueOf(1.0), "double");
        addConstructorValidator(startEvents, "compTypedKeys", expected, Map.class);

        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compTypedKeys", new TestArgument(
            new TestMapValue(new MapValueEntry[]{
                new MapValueEntry(new TestStringValue("true"), "true"),
                new MapValueEntry(new TestStringValue("false"), "false"),
                new MapValueEntry(new TestStringValue(Double.class, "1.0"), new TestStringValue("double"))
            }, Boolean.class, null
            ))));

        // Map containing a mixed bag of types
        expected = new HashMap();
        expected.put("String", "abc");
        expected.put("Boolean", Boolean.FALSE);
        expected.put("Byte", Byte.valueOf((byte)3));
        expected.put("Character", Character.valueOf('4'));
        expected.put("Integer", Integer.valueOf(5));
        expected.put("Short", Short.valueOf((short)6));
        expected.put("Long", Long.valueOf(7));
        expected.put("Double", Double.valueOf(8.0));
		expected.put("Float", Float.valueOf(9.0f));
        expected.put("URL", new URL("http://www.osgi.org"));
        expected.put("Class", String.class);
        expected.put("Locale", new Locale("en", "US"));
        expected.put("null", null);
        addConstructorValidator(startEvents, "compMixedItems", expected, Map.class);

        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compMixedItems", new TestArgument(
            new TestMapValue(new MapValueEntry[]{
                new MapValueEntry("String", new TestStringValue(String.class, "abc")),
                new MapValueEntry("Boolean", new TestStringValue(Boolean.class, "false")),
                new MapValueEntry("Byte", new TestStringValue(Byte.class, "3")),
                new MapValueEntry("Character", new TestStringValue(Character.class, "4")),
                new MapValueEntry("Integer", new TestStringValue(Integer.class, "5")),
                new MapValueEntry("Short", new TestStringValue(Short.class, "6")),
                new MapValueEntry("Long", new TestStringValue(Long.class, "7")),
                new MapValueEntry("Double", new TestStringValue(Double.class, "8.0")),
                new MapValueEntry("Float", new TestStringValue(Float.class, "9.0")),
                new MapValueEntry("URL", new TestStringValue(URL.class, "http://www.osgi.org")),
                new MapValueEntry("Class", new TestStringValue(Class.class, "java.lang.String")),
                new MapValueEntry("Locale", new TestStringValue(Locale.class, "en_US")),
                new MapValueEntry("null", new TestNullValue())
            }
            ))));

        // Map nested inside of a Map
        Map innerMap = new HashMap();
        innerMap.put("abc", "1");
        innerMap.put("def", "2");
        expected = new HashMap();
        expected.put("inner", innerMap);
        addConstructorValidator(startEvents, "compNestedMap", expected, Map.class);

        // List nested inside of a Map
        List innerList = new ArrayList();
        innerList.add("abc");
        innerList.add("def");
        expected = new HashMap();
        expected.put("inner", innerList);
        addConstructorValidator(startEvents, "compNestedList", expected, Map.class);

        // List nested inside of a Map
        Set innerSet = new HashSet();
        innerSet.add("abc");
        innerSet.add("def");
        expected = new HashMap();
        expected.put("inner", innerSet);
        addConstructorValidator(startEvents, "compNestedSet", expected, Map.class);

        // Properties nested inside of a Map
        Properties innerProps = new Properties();
        innerProps.put("abc", "def");
        innerProps.put("ghi", "jkl");
        expected = new HashMap();
        expected.put("inner", innerProps);
        addConstructorValidator(startEvents, "compNestedProps", expected, Map.class);

        controller.run();
    }

	public void testStaticFactoryMapConstructor() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(), getWebServer()+"www/map_static_factory_constructor_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();
        // Empty Map
        Map expected = new HashMap();
        addConstructorValidator(startEvents, "compEmptyMap", expected, Map.class);
        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compEmptyMap", new TestArgument(
            new TestMapValue(new MapValueEntry[0]))));

        // null Map item
        addConstructorValidator(startEvents, "compNullMap", null, Map.class);
        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compNullMap", new TestArgument(
            new TestNullValue(), Map.class)));

        // simple Map of strings
        expected = new HashMap();
        expected.put("abc", "1");
        expected.put("def", "2");
        addConstructorValidator(startEvents, "compStringItems", expected, Map.class);

        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compStringItems", new TestArgument(
            new TestMapValue(new MapValueEntry[]{
                new MapValueEntry("abc", "1"),
                new MapValueEntry("def", "2"),
            }))));

        // simple Map of strings with duplicates...this should be collapsed to a single
        // instance using Map semantics.
        expected = new HashMap();
        expected.put("abc", "2");
        addConstructorValidator(startEvents, "compDupItems", expected, Map.class);
        // Map of "converted" strings
        expected = new HashMap();
        expected.put("true", Boolean.TRUE);
        expected.put("false", Boolean.FALSE);
        addConstructorValidator(startEvents, "compBooleanItems", expected, Map.class);

        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compBooleanItems", new TestArgument(
            new TestMapValue(new MapValueEntry[]{
                new MapValueEntry("true", new TestStringValue(Boolean.class, "true")),
                new MapValueEntry("false", new TestStringValue(Boolean.class, "false"))
            }))));

        // Map of "converted" keys
        expected = new HashMap();
        expected.put(Boolean.TRUE, "true");
        expected.put(Boolean.FALSE, "false");
        addConstructorValidator(startEvents, "compBooleanKeys", expected, Map.class);

        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compBooleanKeys", new TestArgument(
            new TestMapValue(new MapValueEntry[]{
                new MapValueEntry(new TestStringValue(Boolean.class, "true"), new TestStringValue("true")),
                new MapValueEntry(new TestStringValue(Boolean.class, "false"), new TestStringValue("false"))
            }))));

        // Map with default value type
        expected = new HashMap();
        expected.put("true", Boolean.TRUE);
        expected.put("false", Boolean.FALSE);
        expected.put("double", Double.valueOf(1.0));
        addConstructorValidator(startEvents, "compTypedItems", expected, Map.class);

        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compTypedItems", new TestArgument(
            new TestMapValue(new MapValueEntry[]{
                new MapValueEntry("true", new TestStringValue("true")),
                new MapValueEntry("false", new TestStringValue("false")),
                new MapValueEntry("double", new TestStringValue(Double.class, "1.0"))
            }, null, Boolean.class
            ))));

        // Map of using default type keys
        expected = new HashMap();
        expected.put(Boolean.TRUE, "true");
        expected.put(Boolean.FALSE, "false");
        expected.put(Double.valueOf(1.0), "double");
        addConstructorValidator(startEvents, "compTypedKeys", expected, Map.class);

        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compTypedKeys", new TestArgument(
            new TestMapValue(new MapValueEntry[]{
                new MapValueEntry(new TestStringValue("true"), "true"),
                new MapValueEntry(new TestStringValue("false"), "false"),
                new MapValueEntry(new TestStringValue(Double.class, "1.0"), new TestStringValue("double"))
            }, Boolean.class, null
            ))));

        // Map containing a mixed bag of types
        expected = new HashMap();
        expected.put("String", "abc");
        expected.put("Boolean", Boolean.FALSE);
        expected.put("Byte", Byte.valueOf((byte)3));
        expected.put("Character", Character.valueOf('4'));
        expected.put("Integer", Integer.valueOf(5));
        expected.put("Short", Short.valueOf((short)6));
        expected.put("Long", Long.valueOf(7));
        expected.put("Double", Double.valueOf(8.0));
		expected.put("Float", Float.valueOf(9.0f));
        expected.put("URL", new URL("http://www.osgi.org"));
        expected.put("Class", String.class);
        expected.put("Locale", new Locale("en", "US"));
        expected.put("null", null);
        addConstructorValidator(startEvents, "compMixedItems", expected, Map.class);

        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compMixedItems", new TestArgument(
            new TestMapValue(new MapValueEntry[]{
                new MapValueEntry("String", new TestStringValue(String.class, "abc")),
                new MapValueEntry("Boolean", new TestStringValue(Boolean.class, "false")),
                new MapValueEntry("Byte", new TestStringValue(Byte.class, "3")),
                new MapValueEntry("Character", new TestStringValue(Character.class, "4")),
                new MapValueEntry("Integer", new TestStringValue(Integer.class, "5")),
                new MapValueEntry("Short", new TestStringValue(Short.class, "6")),
                new MapValueEntry("Long", new TestStringValue(Long.class, "7")),
                new MapValueEntry("Double", new TestStringValue(Double.class, "8.0")),
                new MapValueEntry("Float", new TestStringValue(Float.class, "9.0")),
                new MapValueEntry("URL", new TestStringValue(URL.class, "http://www.osgi.org")),
                new MapValueEntry("Class", new TestStringValue(Class.class, "java.lang.String")),
                new MapValueEntry("Locale", new TestStringValue(Locale.class, "en_US")),
                new MapValueEntry("null", new TestNullValue())
            }
            ))));


        // Map nested inside of a Map
        Map innerMap = new HashMap();
        innerMap.put("abc", "1");
        innerMap.put("def", "2");
        expected = new HashMap();
        expected.put("inner", innerMap);
        addConstructorValidator(startEvents, "compNestedMap", expected, Map.class);

        // List nested inside of a Map
        List innerList = new ArrayList();
        innerList.add("abc");
        innerList.add("def");
        expected = new HashMap();
        expected.put("inner", innerList);
        addConstructorValidator(startEvents, "compNestedList", expected, Map.class);

        // List nested inside of a Map
        Set innerSet = new HashSet();
        innerSet.add("abc");
        innerSet.add("def");
        expected = new HashMap();
        expected.put("inner", innerSet);
        addConstructorValidator(startEvents, "compNestedSet", expected, Map.class);

        // Properties nested inside of a Map
        Properties innerProps = new Properties();
        innerProps.put("abc", "def");
        innerProps.put("ghi", "jkl");
        expected = new HashMap();
        expected.put("inner", innerProps);
        addConstructorValidator(startEvents, "compNestedProps", expected, Map.class);

        controller.run();
    }

	public void testMapProperty() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(), getWebServer()+"www/map_property_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();
        // Empty Map
        Map expected = new HashMap();
        addPropertyValidator(startEvents, "compEmptyMap", "map", expected, Map.class);
        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compEmptyMap", new TestProperty(
            new TestMapValue(new MapValueEntry[0])
            , "map")));

        // null Map item
        addPropertyValidator(startEvents, "compNullMap", "map", null, Map.class);
        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compNullMap", new TestProperty(
            new TestNullValue(), "map")));

        // simple Map of strings
        expected = new HashMap();
        expected.put("abc", "1");
        expected.put("def", "2");
        addPropertyValidator(startEvents, "compStringItems", "map", expected, Map.class);

        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compStringItems", new TestProperty(
            new TestMapValue(new MapValueEntry[]{
                new MapValueEntry("abc", "1"),
                new MapValueEntry("def", "2")
            }), "map")));

        // simple Map of strings with duplicates...this should be collapsed to a single
        // instance using Map semantics.
        expected = new HashMap();
        expected.put("abc", "2");
        addPropertyValidator(startEvents, "compDupItems", "map", expected, Map.class);
        // Map of "converted" strings
        expected = new HashMap();
        expected.put("true", Boolean.TRUE);
        expected.put("false", Boolean.FALSE);
        addPropertyValidator(startEvents, "compBooleanItems", "map", expected, Map.class);

        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compBooleanItems", new TestProperty(
            new TestMapValue(new MapValueEntry[]{
                new MapValueEntry("true", new TestStringValue(Boolean.class, "true")),
                new MapValueEntry("false", new TestStringValue(Boolean.class, "false"))
            }), "map")));

        // Map of "converted" keys
        expected = new HashMap();
        expected.put(Boolean.TRUE, "true");
        expected.put(Boolean.FALSE, "false");
        addPropertyValidator(startEvents, "compBooleanKeys", "map", expected, Map.class);

        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compBooleanKeys", new TestProperty(
            new TestMapValue(new MapValueEntry[]{
                new MapValueEntry(new TestStringValue(Boolean.class, "true"), new TestStringValue("true")),
                new MapValueEntry(new TestStringValue(Boolean.class, "false"), new TestStringValue("false"))
            }), "map")));

        // Map with default value type
        expected = new HashMap();
        expected.put("true", Boolean.TRUE);
        expected.put("false", Boolean.FALSE);
        expected.put("double", Double.valueOf(1.0));
        addPropertyValidator(startEvents, "compTypedItems", "map", expected, Map.class);

        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compTypedItems", new TestProperty(
            new TestMapValue(new MapValueEntry[]{
                new MapValueEntry("true", new TestStringValue("true")),
                new MapValueEntry("false", new TestStringValue("false")),
                new MapValueEntry("double", new TestStringValue(Double.class, "1.0"))
            }, null, Boolean.class
            ), "map")));

        // Map of using default type keys
        expected = new HashMap();
        expected.put(Boolean.TRUE, "true");
        expected.put(Boolean.FALSE, "false");
        expected.put(Double.valueOf(1.0), "double");
        addPropertyValidator(startEvents, "compTypedKeys", "map", expected, Map.class);

        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compTypedKeys", new TestProperty(
            new TestMapValue(new MapValueEntry[]{
                new MapValueEntry(new TestStringValue("true"), "true"),
                new MapValueEntry(new TestStringValue("false"), "false"),
                new MapValueEntry(new TestStringValue(Double.class, "1.0"), new TestStringValue("double"))
            }, Boolean.class, null
            ), "map")));

        // Map containing a mixed bag of types
        expected = new HashMap();
        expected.put("String", "abc");
        expected.put("Boolean", Boolean.FALSE);
        expected.put("Byte", Byte.valueOf((byte)3));
        expected.put("Character", Character.valueOf('4'));
        expected.put("Integer", Integer.valueOf(5));
        expected.put("Short", Short.valueOf((short)6));
        expected.put("Long", Long.valueOf(7));
        expected.put("Double", Double.valueOf(8.0));
		expected.put("Float", Float.valueOf(9.0f));
        expected.put("URL", new URL("http://www.osgi.org"));
        expected.put("Class", String.class);
        expected.put("Locale", new Locale("en", "US"));
        expected.put("null", null);
        addPropertyValidator(startEvents, "compMixedItems", "map", expected, Map.class);

        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compMixedItems", new TestProperty(
            new TestMapValue(new MapValueEntry[]{
                new MapValueEntry("String", new TestStringValue(String.class, "abc")),
                new MapValueEntry("Boolean", new TestStringValue(Boolean.class, "false")),
                new MapValueEntry("Byte", new TestStringValue(Byte.class, "3")),
                new MapValueEntry("Character", new TestStringValue(Character.class, "4")),
                new MapValueEntry("Integer", new TestStringValue(Integer.class, "5")),
                new MapValueEntry("Short", new TestStringValue(Short.class, "6")),
                new MapValueEntry("Long", new TestStringValue(Long.class, "7")),
                new MapValueEntry("Double", new TestStringValue(Double.class, "8.0")),
                new MapValueEntry("Float", new TestStringValue(Float.class, "9.0")),
                new MapValueEntry("URL", new TestStringValue(URL.class, "http://www.osgi.org")),
                new MapValueEntry("Class", new TestStringValue(Class.class, "java.lang.String")),
                new MapValueEntry("Locale", new TestStringValue(Locale.class, "en_US")),
                new MapValueEntry("null", new TestNullValue())
            }
            ), "map")));


        // Map nested inside of a Map
        Map innerMap = new HashMap();
        innerMap.put("abc", "1");
        innerMap.put("def", "2");
        expected = new HashMap();
        expected.put("inner", innerMap);
        addPropertyValidator(startEvents, "compNestedMap", "map", expected, Map.class);


        // List nested inside of a Map
        List innerList = new ArrayList();
        innerList.add("abc");
        innerList.add("def");
        expected = new HashMap();
        expected.put("inner", innerList);
        addPropertyValidator(startEvents, "compNestedList", "map", expected, Map.class);

        // Set nested inside of a Map
        Set innerSet = new HashSet();
        innerSet.add("abc");
        innerSet.add("def");
        expected = new HashMap();
        expected.put("inner", innerSet);
        addPropertyValidator(startEvents, "compNestedSet", "map", expected, Map.class);

        // Properties nested inside of a Map
        Properties innerProps = new Properties();
        innerProps.put("abc", "def");
        innerProps.put("ghi", "jkl");
        expected = new HashMap();
        expected.put("inner", innerProps);
        addPropertyValidator(startEvents, "compNestedProps", "map", expected, Map.class);

        controller.run();
    }

	public void testStaticFactoryMapProperty() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(), getWebServer()+"www/map_factory_property_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();
        // Empty Map
        Map expected = new HashMap();
        addPropertyValidator(startEvents, "compEmptyMap", "map", expected, Map.class);
        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compEmptyMap", new TestProperty(
            new TestMapValue(new MapValueEntry[0])
            , "map")));

        // null Map item
        addPropertyValidator(startEvents, "compNullMap", "map", null, Map.class);
        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compNullMap", new TestProperty(
            new TestNullValue(), "map")));

        // simple Map of strings
        expected = new HashMap();
        expected.put("abc", "1");
        expected.put("def", "2");
        addPropertyValidator(startEvents, "compStringItems", "map", expected, Map.class);

        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compStringItems", new TestProperty(
            new TestMapValue(new MapValueEntry[]{
                new MapValueEntry("abc", "1"),
                new MapValueEntry("def", "2")
            }), "map")));

        // simple Map of strings with duplicates...this should be collapsed to a single
        // instance using Map semantics.
        expected = new HashMap();
        expected.put("abc", "2");
        addPropertyValidator(startEvents, "compDupItems", "map", expected, Map.class);
        // Map of "converted" strings
        expected = new HashMap();
        expected.put("true", Boolean.TRUE);
        expected.put("false", Boolean.FALSE);
        addPropertyValidator(startEvents, "compBooleanItems", "map", expected, Map.class);

        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compBooleanItems", new TestProperty(
            new TestMapValue(new MapValueEntry[]{
                new MapValueEntry("true", new TestStringValue(Boolean.class, "true")),
                new MapValueEntry("false", new TestStringValue(Boolean.class, "false"))
            }), "map")));

        // Map of "converted" keys
        expected = new HashMap();
        expected.put(Boolean.TRUE, "true");
        expected.put(Boolean.FALSE, "false");
        addPropertyValidator(startEvents, "compBooleanKeys", "map", expected, Map.class);

        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compBooleanKeys", new TestProperty(
            new TestMapValue(new MapValueEntry[]{
                new MapValueEntry(new TestStringValue(Boolean.class, "true"), new TestStringValue("true")),
                new MapValueEntry(new TestStringValue(Boolean.class, "false"), new TestStringValue("false"))
            }), "map")));

        // Map with default value type
        expected = new HashMap();
        expected.put("true", Boolean.TRUE);
        expected.put("false", Boolean.FALSE);
        expected.put("double", Double.valueOf(1.0));
        addPropertyValidator(startEvents, "compTypedItems", "map", expected, Map.class);

        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compTypedItems", new TestProperty(
            new TestMapValue(new MapValueEntry[]{
                new MapValueEntry("true", new TestStringValue("true")),
                new MapValueEntry("false", new TestStringValue("false")),
                new MapValueEntry("double", new TestStringValue(Double.class, "1.0"))
            }, null, Boolean.class
            ), "map")));

        // Map of using default type keys
        expected = new HashMap();
        expected.put(Boolean.TRUE, "true");
        expected.put(Boolean.FALSE, "false");
        expected.put(Double.valueOf(1.0), "double");
        addPropertyValidator(startEvents, "compTypedKeys", "map", expected, Map.class);

        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compTypedKeys", new TestProperty(
            new TestMapValue(new MapValueEntry[]{
                new MapValueEntry(new TestStringValue("true"), "true"),
                new MapValueEntry(new TestStringValue("false"), "false"),
                new MapValueEntry(new TestStringValue(Double.class, "1.0"), new TestStringValue("double"))
            }, Boolean.class, null
            ), "map")));

        // Map containing a mixed bag of types
        expected = new HashMap();
        expected.put("String", "abc");
        expected.put("Boolean", Boolean.FALSE);
        expected.put("Byte", Byte.valueOf((byte)3));
        expected.put("Character", Character.valueOf('4'));
        expected.put("Integer", Integer.valueOf(5));
        expected.put("Short", Short.valueOf((short)6));
        expected.put("Long", Long.valueOf(7));
        expected.put("Double", Double.valueOf(8.0));
		expected.put("Float", Float.valueOf(9.0f));
        expected.put("URL", new URL("http://www.osgi.org"));
        expected.put("Class", String.class);
        expected.put("Locale", new Locale("en", "US"));
        expected.put("null", null);
        addPropertyValidator(startEvents, "compMixedItems", "map", expected, Map.class);

        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compMixedItems", new TestProperty(
            new TestMapValue(new MapValueEntry[]{
                new MapValueEntry("String", new TestStringValue(String.class, "abc")),
                new MapValueEntry("Boolean", new TestStringValue(Boolean.class, "false")),
                new MapValueEntry("Byte", new TestStringValue(Byte.class, "3")),
                new MapValueEntry("Character", new TestStringValue(Character.class, "4")),
                new MapValueEntry("Integer", new TestStringValue(Integer.class, "5")),
                new MapValueEntry("Short", new TestStringValue(Short.class, "6")),
                new MapValueEntry("Long", new TestStringValue(Long.class, "7")),
                new MapValueEntry("Double", new TestStringValue(Double.class, "8.0")),
                new MapValueEntry("Float", new TestStringValue(Float.class, "9.0")),
                new MapValueEntry("URL", new TestStringValue(URL.class, "http://www.osgi.org")),
                new MapValueEntry("Class", new TestStringValue(Class.class, "java.lang.String")),
                new MapValueEntry("Locale", new TestStringValue(Locale.class, "en_US")),
                new MapValueEntry("null", new TestNullValue())
            }
            ), "map")));

        // Map nested inside of a Map
        Map innerMap = new HashMap();
        innerMap.put("abc", "1");
        innerMap.put("def", "2");
        expected = new HashMap();
        expected.put("inner", innerMap);
        addPropertyValidator(startEvents, "compNestedMap", "map", expected, Map.class);


        // List nested inside of a Map
        List innerList = new ArrayList();
        innerList.add("abc");
        innerList.add("def");
        expected = new HashMap();
        expected.put("inner", innerList);
        addPropertyValidator(startEvents, "compNestedList", "map", expected, Map.class);

        // Set nested inside of a Map
        Set innerSet = new HashSet();
        innerSet.add("abc");
        innerSet.add("def");
        expected = new HashMap();
        expected.put("inner", innerSet);
        addPropertyValidator(startEvents, "compNestedSet", "map", expected, Map.class);

        // Properties nested inside of a Map
        Properties innerProps = new Properties();
        innerProps.put("abc", "def");
        innerProps.put("ghi", "jkl");
        expected = new HashMap();
        expected.put("inner", innerProps);
        addPropertyValidator(startEvents, "compNestedProps", "map", expected, Map.class);

        controller.run();
    }

	public void testFactoryMapProperty() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(), getWebServer()+"www/map_static_factory_property_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();
        // Empty Map
        Map expected = new HashMap();
        addPropertyValidator(startEvents, "compEmptyMap", "map", expected, Map.class);
        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compEmptyMap", new TestProperty(
            new TestMapValue(new MapValueEntry[0])
            , "map")));

        // null Map item
        addPropertyValidator(startEvents, "compNullMap", "map", null, Map.class);
        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compNullMap", new TestProperty(
            new TestNullValue(), "map")));

        // simple Map of strings
        expected = new HashMap();
        expected.put("abc", "1");
        expected.put("def", "2");
        addPropertyValidator(startEvents, "compStringItems", "map", expected, Map.class);

        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compStringItems", new TestProperty(
            new TestMapValue(new MapValueEntry[]{
                new MapValueEntry("abc", "1"),
                new MapValueEntry("def", "2")
            }), "map")));

        // simple Map of strings with duplicates...this should be collapsed to a single
        // instance using Map semantics.
        expected = new HashMap();
        expected.put("abc", "2");
        addPropertyValidator(startEvents, "compDupItems", "map", expected, Map.class);
        // Map of "converted" strings
        expected = new HashMap();
        expected.put("true", Boolean.TRUE);
        expected.put("false", Boolean.FALSE);
        addPropertyValidator(startEvents, "compBooleanItems", "map", expected, Map.class);

        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compBooleanItems", new TestProperty(
            new TestMapValue(new MapValueEntry[]{
                new MapValueEntry("true", new TestStringValue(Boolean.class, "true")),
                new MapValueEntry("false", new TestStringValue(Boolean.class, "false"))
            }), "map")));

        // Map of "converted" keys
        expected = new HashMap();
        expected.put(Boolean.TRUE, "true");
        expected.put(Boolean.FALSE, "false");
        addPropertyValidator(startEvents, "compBooleanKeys", "map", expected, Map.class);

        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compBooleanKeys", new TestProperty(
            new TestMapValue(new MapValueEntry[]{
                new MapValueEntry(new TestStringValue(Boolean.class, "true"), new TestStringValue("true")),
                new MapValueEntry(new TestStringValue(Boolean.class, "false"), new TestStringValue("false"))
            }), "map")));

        // Map with default value type
        expected = new HashMap();
        expected.put("true", Boolean.TRUE);
        expected.put("false", Boolean.FALSE);
        expected.put("double", Double.valueOf(1.0));
        addPropertyValidator(startEvents, "compTypedItems", "map", expected, Map.class);

        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compTypedItems", new TestProperty(
            new TestMapValue(new MapValueEntry[]{
                new MapValueEntry("true", new TestStringValue("true")),
                new MapValueEntry("false", new TestStringValue("false")),
                new MapValueEntry("double", new TestStringValue(Double.class, "1.0"))
            }, null, Boolean.class
            ), "map")));

        // Map of using default type keys
        expected = new HashMap();
        expected.put(Boolean.TRUE, "true");
        expected.put(Boolean.FALSE, "false");
        expected.put(Double.valueOf(1.0), "double");
        addPropertyValidator(startEvents, "compTypedKeys", "map", expected, Map.class);
        // Map containing a mixed bag of types
        expected = new HashMap();
        expected.put("String", "abc");
        expected.put("Boolean", Boolean.FALSE);
        expected.put("Byte", Byte.valueOf((byte)3));
        expected.put("Character", Character.valueOf('4'));
        expected.put("Integer", Integer.valueOf(5));
        expected.put("Short", Short.valueOf((short)6));
        expected.put("Long", Long.valueOf(7));
        expected.put("Double", Double.valueOf(8.0));
		expected.put("Float", Float.valueOf(9.0f));
        expected.put("URL", new URL("http://www.osgi.org"));
        expected.put("Class", String.class);
        expected.put("Locale", new Locale("en", "US"));
        expected.put("null", null);
        addPropertyValidator(startEvents, "compMixedItems", "map", expected, Map.class);

        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compMixedItems", new TestProperty(
            new TestMapValue(new MapValueEntry[]{
                new MapValueEntry("String", new TestStringValue(String.class, "abc")),
                new MapValueEntry("Boolean", new TestStringValue(Boolean.class, "false")),
                new MapValueEntry("Byte", new TestStringValue(Byte.class, "3")),
                new MapValueEntry("Character", new TestStringValue(Character.class, "4")),
                new MapValueEntry("Integer", new TestStringValue(Integer.class, "5")),
                new MapValueEntry("Short", new TestStringValue(Short.class, "6")),
                new MapValueEntry("Long", new TestStringValue(Long.class, "7")),
                new MapValueEntry("Double", new TestStringValue(Double.class, "8.0")),
                new MapValueEntry("Float", new TestStringValue(Float.class, "9.0")),
                new MapValueEntry("URL", new TestStringValue(URL.class, "http://www.osgi.org")),
                new MapValueEntry("Class", new TestStringValue(Class.class, "java.lang.String")),
                new MapValueEntry("Locale", new TestStringValue(Locale.class, "en_US")),
                new MapValueEntry("null", new TestNullValue())
            }
            ), "map")));

        // Map nested inside of a Map
        Map innerMap = new HashMap();
        innerMap.put("abc", "1");
        innerMap.put("def", "2");
        expected = new HashMap();
        expected.put("inner", innerMap);
        addPropertyValidator(startEvents, "compNestedMap", "map", expected, Map.class);


        // List nested inside of a Map
        List innerList = new ArrayList();
        innerList.add("abc");
        innerList.add("def");
        expected = new HashMap();
        expected.put("inner", innerList);
        addPropertyValidator(startEvents, "compNestedList", "map", expected, Map.class);

        // Set nested inside of a Map
        Set innerSet = new HashSet();
        innerSet.add("abc");
        innerSet.add("def");
        expected = new HashMap();
        expected.put("inner", innerSet);
        addPropertyValidator(startEvents, "compNestedSet", "map", expected, Map.class);

        // Properties nested inside of a Map
        Properties innerProps = new Properties();
        innerProps.put("abc", "def");
        innerProps.put("ghi", "jkl");
        expected = new HashMap();
        expected.put("inner", innerProps);
        addPropertyValidator(startEvents, "compNestedProps", "map", expected, Map.class);

        controller.run();
    }

	// Props
	private void addPropsConstructorTestItem(MetadataEventSet startEvents)throws Exception{
	    // Empty
        this.addConstructorValidator(startEvents, "compEmptyProps", new Properties(), Properties.class);
        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compEmptyProps", new TestArgument(
            new TestPropsValue())));
        // null
        this.addConstructorValidator(startEvents, "compNullProps", null, Properties.class);
        // validate the metadata for this one too
        startEvents.addValidator(new ArgumentMetadataValidator("compNullProps", new TestArgument(
            new TestNullValue(), Properties.class)));

        // strings
        Properties props = new Properties();
        props.put("administrator", "administrator@example.org");
        props.put("support", "support@example.org");
        props.put("development", "development@example.org");
        this.addConstructorValidator(startEvents, "compStringItems", props, Properties.class);

        TestPropsValue expected = new TestPropsValue();
        expected.put("administrator", "administrator@example.org");
        expected.put("support", "support@example.org");
        expected.put("development", "development@example.org");
        startEvents.addValidator(new ArgumentMetadataValidator("compStringItems",
            new TestArgument(expected)));

    }

	public void testPropsConstructor() throws Exception {
	    StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/props_constructor_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        this.addPropsConstructorTestItem(startEvents);

        controller.run();
	}

	public void testInstanceFactoryPropsConstructor() throws Exception {
	    StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/props_factory_constructor_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        this.addPropsConstructorTestItem(startEvents);

        controller.run();
    }

	public void testStaticFactoryPropsConstructor() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/props_static_factory_constructor_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        this.addPropsConstructorTestItem(startEvents);

        controller.run();
	}

    private void addPropsPropertyTestItem(MetadataEventSet startEvents)throws Exception{
        // Empty
        this.addPropertyValidator(startEvents, "compEmptyProps", "properties", new Properties(), Properties.class);
        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compEmptyProps", new TestProperty(
                new TestPropsValue()
                , "properties")));

        // null
        this.addPropertyValidator(startEvents, "compNullProps", "properties", null, Properties.class);

        // validate the metadata for this one too
        startEvents.addValidator(new PropertyMetadataValidator("compNullProps", new TestProperty(
                new TestNullValue()
                , "properties")));

        // strings
        Properties expectedValue = new Properties();
        expectedValue.put("administrator", "administrator@example.org");
        expectedValue.put("support", "support@example.org");
        expectedValue.put("development", "development@example.org");
        this.addPropertyValidator(startEvents, "compStringItems", "properties", expectedValue, Properties.class);
        // validate the metadata for this one too
        TestPropsValue expected = new TestPropsValue();
        expected.put("administrator", "administrator@example.org");
        expected.put("support", "support@example.org");
        expected.put("development", "development@example.org");
        startEvents.addValidator(new PropertyMetadataValidator("compStringItems",
                new TestProperty(expected, "properties")));
    }

    public void testPropsProperty() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(), getWebServer()+"www/props_property_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        this.addPropsPropertyTestItem(startEvents);

        controller.run();
    }

    public void testInstanceFactoryPropsProperty() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(), getWebServer()+"www/props_factory_property_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        this.addPropsPropertyTestItem(startEvents);

        controller.run();
    }

    public void testStaticFactoryPropsProperty() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(), getWebServer()+"www/props_static_factory_property_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        this.addPropsPropertyTestItem(startEvents);

        controller.run();
    }


    /**
     * Test the defined set of collection-to-collection
     * conversion operations.
     *
     * @exception Exception
     */
	public void testBuiltinCollectionConversion() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/builtin_collection_conversion.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        // this is the result when a set class is the target
        Collection expectedSet = new HashSet();
        expectedSet.add("abc");
        expectedSet.add("def");

        addPropertyValidator(startEvents, "setToHashSet", "hashSet", expectedSet, HashSet.class);
        addPropertyValidator(startEvents, "listToHashSet", "hashSet", expectedSet, HashSet.class);
        addPropertyValidator(startEvents, "arrayToHashSet", "hashSet", expectedSet, HashSet.class);

        // generic set conversion
        addPropertyValidator(startEvents, "setToSet", "set", expectedSet, Set.class, LinkedHashSet.class);
        addPropertyValidator(startEvents, "listToSet", "set", expectedSet, Set.class, LinkedHashSet.class);
        addPropertyValidator(startEvents, "arrayToSet", "set", expectedSet, Set.class, LinkedHashSet.class);

        // since Collection and Set are compatible, this should not be converted into the
        // default collection implementation.
        addPropertyValidator(startEvents, "setToCollection", "collection", expectedSet, Collection.class, LinkedHashSet.class);

        // <list> will remain the same, and <array> will be converted into an array list
        Collection expectedList = new ArrayList();
        expectedList.add("abc");
        expectedList.add("def");
        expectedList.add("def");

        // this is the result expected when the source is a set...the dup has been removed
        Collection expectedSetList = new ArrayList();
        expectedSetList.add("abc");
        expectedSetList.add("def");

        addPropertyValidator(startEvents, "listToCollection", "collection", expectedList, Collection.class, ArrayList.class);
        addPropertyValidator(startEvents, "arrayToCollection", "collection", expectedList, Collection.class, ArrayList.class);

        // these should all end up being the same type
        addPropertyValidator(startEvents, "setToSortedSet", "sortedSet", expectedSet, SortedSet.class, TreeSet.class);
        addPropertyValidator(startEvents, "listToSortedSet", "sortedSet", expectedSet, SortedSet.class, TreeSet.class);
        addPropertyValidator(startEvents, "arrayToSortedSet", "sortedSet", expectedSet, SortedSet.class, TreeSet.class);

        // these should all end up being the same type
        addPropertyValidator(startEvents, "setToStack", "stack", expectedSetList, Stack.class);
        addPropertyValidator(startEvents, "listToStack", "stack", expectedList, Stack.class);
        addPropertyValidator(startEvents, "arrayToStack", "stack", expectedList, Stack.class);

        addPropertyValidator(startEvents, "setToLinkedHashSet", "linkedHashSet", expectedSet, LinkedHashSet.class);
        addPropertyValidator(startEvents, "listToLinkedHashSet", "linkedHashSet", expectedSet, LinkedHashSet.class);
        addPropertyValidator(startEvents, "arrayToLinkedHashSet", "linkedHashSet", expectedSet, LinkedHashSet.class);

        addPropertyValidator(startEvents, "setToTreeSet", "treeSet", expectedSet, TreeSet.class);
        addPropertyValidator(startEvents, "listToTreeSet", "treeSet", expectedSet, TreeSet.class);
        addPropertyValidator(startEvents, "arrayToTreeSet", "treeSet", expectedSet, TreeSet.class);

        addPropertyValidator(startEvents, "listToArrayList", "arrayList", expectedList, ArrayList.class);
        addPropertyValidator(startEvents, "setToArrayList", "arrayList", expectedSetList, ArrayList.class);
        addPropertyValidator(startEvents, "arrayToArrayList", "arrayList", expectedList, ArrayList.class);

        // generic list conversion
        addPropertyValidator(startEvents, "setToList", "list", expectedSetList, List.class, ArrayList.class);
        addPropertyValidator(startEvents, "listToList", "list", expectedList, List.class, ArrayList.class);
        addPropertyValidator(startEvents, "arrayToList", "list", expectedList, List.class, ArrayList.class);

        addPropertyValidator(startEvents, "listToLinkedList", "linkedList", expectedList, LinkedList.class);
        addPropertyValidator(startEvents, "setToLinkedList", "linkedList", expectedSetList, LinkedList.class);
        addPropertyValidator(startEvents, "arrayToLinkedList", "linkedList", expectedList, LinkedList.class);

        // simple list of strings
        expectedSetList = new Vector();
        expectedSetList.add("abc");
        expectedSetList.add("def");

        expectedList = new Vector();
        expectedList.add("abc");
        expectedList.add("def");
        expectedList.add("def");

        addPropertyValidator(startEvents, "listToVector", "vector", expectedList, Vector.class);
        addPropertyValidator(startEvents, "setToVector", "vector", expectedSetList, Vector.class);
        addPropertyValidator(startEvents, "arrayToVector", "vector", expectedList, Vector.class);

        controller.run();
    }


    /**
     * Test the builtin map conversion options.
     *
     * @exception Exception
     */
	public void testBuiltinMapConversion() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/builtin_map_collection_conversion.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        // simple hash map of strings
        Map expected = new HashMap();
        expected.put("abc", "1");
        expected.put("def", "2");
        addPropertyValidator(startEvents, "mapToHashMap", "hashMap", expected, HashMap.class);
        addPropertyValidator(startEvents, "mapToTreeMap", "treeMap", expected, TreeMap.class);
        addPropertyValidator(startEvents, "mapToHashtable", "hashtable", expected, Hashtable.class);
        addPropertyValidator(startEvents, "mapToSortedMap", "sortedMap", expected, SortedMap.class, TreeMap.class);
        addPropertyValidator(startEvents, "mapToDictionary", "dictionary", expected, Dictionary.class, Hashtable.class);

        controller.run();
    }
}
