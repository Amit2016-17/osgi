/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi 
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */
package org.osgi.test.cases.div;

import org.osgi.test.cases.util.*;

/**
 * A TestCase testing diverse issues.
 * 
 * @author Ericsson Radio Systems AB
 */
public class DivTestCase extends DefaultTestCase {
	public String getDescription() {
		return "Diverse tests: native, manifest, framework event ";
	}

	public String getName() {
		return "test.cases.div";
	}
}
