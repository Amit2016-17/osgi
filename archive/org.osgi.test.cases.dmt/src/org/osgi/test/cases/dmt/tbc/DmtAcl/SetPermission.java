/*
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

/* 
 * REVISION HISTORY:
 *
 * Date          Author(s)
 * CR            Headline
 * ============  ==============================================================
 * Jan 31, 2005  Andr� Assad
 * 1             Implement MEG TCK
 * ============  ==============================================================
 * Feb 11, 2005  Andr� Assad
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tbc.DmtAcl;

import org.osgi.test.cases.dmt.tbc.DmtTestControl;

/**
 * @author Andre Assad
 * 
 * @methodUnderTest org.osgi.service.dmt.DmtAcl#setPermission
 * @generalDescription This Test Class Validates the implementation of
 *                     <code>setPermission<code> method, according to MEG reference
 *                     documentation (rfc0085).
 */
public class SetPermission {
	private DmtTestControl tbc;

	/**
	 * @param tbc
	 */
	public SetPermission(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testSetPermission001();
		testSetPermission002();
		testSetPermission003();
		testSetPermission004();
		testSetPermission005();
	}

	/**
	 * @testID testSetPermission001
	 * @testDescription This method asserts that a principal gets all the
	 *                  permissions previously set.
	 */
	public void testSetPermission001() {
		String principal = "www.cesar.org.br";
		org.osgi.service.dmt.DmtAcl dmtAcl = new org.osgi.service.dmt.DmtAcl();

		dmtAcl.setPermission(principal, org.osgi.service.dmt.DmtAcl.ADD
				| org.osgi.service.dmt.DmtAcl.EXEC);

		tbc.assertEquals("Asserting Get Permission",
				org.osgi.service.dmt.DmtAcl.ADD
						| org.osgi.service.dmt.DmtAcl.EXEC, dmtAcl
						.getPermissions(principal));
	}

	/**
	 * @testID testSetPermission002
	 * @testDescription This method asserts that a set of permissions the
	 *                  principal had is overriden by new ones.
	 */
	public void testSetPermission002() {
		String principal = "www.cesar.org.br";
		org.osgi.service.dmt.DmtAcl dmtAcl = new org.osgi.service.dmt.DmtAcl(
				"Add=" + principal + "&Delete=" + principal);

		dmtAcl.setPermission(principal, org.osgi.service.dmt.DmtAcl.ADD
				| org.osgi.service.dmt.DmtAcl.EXEC);

		tbc.assertEquals("Asserting Get Permission",
				org.osgi.service.dmt.DmtAcl.ADD
						| org.osgi.service.dmt.DmtAcl.EXEC, dmtAcl
						.getPermissions(principal));
	}

	/**
	 * @testID testSetPermission003
	 * @testDescription Asserts that an IllegalArgumentException is thrown
	 *                  whenever an invalid permission is set to a principal.
	 */
	public void testSetPermission003() {
		String principal = "www.cesar.org.br";
		org.osgi.service.dmt.DmtAcl dmtAcl = new org.osgi.service.dmt.DmtAcl(
				"Add=" + principal + "&Delete=" + principal);

		try {
			dmtAcl.setPermission(principal,
					org.osgi.service.dmt.DmtAcl.ADD | 2005);
			// should not reach this point
			tbc.fail("No Exception thrown");
		} catch (IllegalArgumentException e) {
			tbc.pass("IllegalArgumentException correctly thrown");
		}
	}

	/**
	 * @testID testSetPermission004
	 * @testDescription Asserts that an IllegalArgumentException is thrown
	 *                  whenever a valid permission is set to an invalid
	 *                  principal.
	 */
	public void testSetPermission004() {
		String principal = "www.cesar.org.br";
		org.osgi.service.dmt.DmtAcl dmtAcl = new org.osgi.service.dmt.DmtAcl(
				"Add=" + principal + "&Delete=" + principal);

		try {
			dmtAcl.setPermission("INVALID_PRINCIPAL",
					org.osgi.service.dmt.DmtAcl.ADD
							| org.osgi.service.dmt.DmtAcl.REPLACE);
			// should not reach this point
			tbc.fail("No Exception thrown");
		} catch (IllegalArgumentException e) {
			tbc.pass("IllegalArgumentException correctly thrown");
		}
	}

	/**
	 * @testID testSetPermission005
	 * @testDescription Asserts that an IllegalArgumentException is thrown
	 *                  whenever a valid permission is set to a principal which
	 *                  was not created on the constructor.
	 */
	public void testSetPermission005() {
		String principal_1 = "www.cesar.org.br";
		String principal_2 = "www.cin.ufpe.br";
		org.osgi.service.dmt.DmtAcl dmtAcl = new org.osgi.service.dmt.DmtAcl(
				"Add=" + principal_1 + "&Delete=" + principal_1 + "&Exec="
						+ principal_1 + "&Get=*");
		try {
			dmtAcl.setPermission(principal_2, org.osgi.service.dmt.DmtAcl.ADD
					| org.osgi.service.dmt.DmtAcl.GET
					| org.osgi.service.dmt.DmtAcl.EXEC);
			
			tbc.assertEquals("Assert " + principal_2 + " permission.",
					org.osgi.service.dmt.DmtAcl.ADD
							| org.osgi.service.dmt.DmtAcl.GET
							| org.osgi.service.dmt.DmtAcl.EXEC, dmtAcl.getPermissions(principal_2));
		} catch (IllegalArgumentException e) {
			tbc.fail("Unexpected IllegalArgumentException");
		}
	}
}
