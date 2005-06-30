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

/* REVISION HISTORY:
 *
 * Date         Author(s)
 * CR           Headline
 * ===========  ==============================================================
 * 26/01/2005   Andre Assad
 * 1            Implement TCK
 * ===========  ==============================================================
 * 15/02/2005   Leonardo Barros
 * 1            Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-002)
 * ===========  ==============================================================
 * 01/03/2005   Andre Assad
 * 11           Implement TCK Use Cases
 * ===========  ==============================================================
 */

package org.osgi.test.cases.dmt.plugins.tbc.DmtReadOnlyDataPlugin.DmtReadOnly;

import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.plugins.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.plugins.tbc.DmtReadOnlyDataPlugin.TestReadOnlyDataPlugin;
import org.osgi.test.cases.dmt.plugins.tbc.DmtReadOnlyDataPlugin.TestReadOnlyDataPluginActivator;

/**
 * 
 * @methodUnderTest org.osgi.service.dmt.DmtReadOnly#getChildNodeNames
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>getChildNodeNames<code> method, according to MEG reference
 *                     documentation (rfc0085).
 */
public class GetChildNodeNames {

	private DmtTestControl tbc;

	public GetChildNodeNames(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testGetChildNodeNames001();
		testGetChildNodeNames002();

	}

	/**
	 * @testID testGetChildNodeNames001
	 * @testDescription Asserts that DmtAdmin correctly forwards the call of
	 *                  getChildNodeNames to the correct plugin.
	 */
	private void testGetChildNodeNames001() {
		DmtSession session = null;
		try {
			tbc.log("#testGetChildNodeNames001");
			session = tbc.getDmtAdmin().getSession(TestReadOnlyDataPluginActivator.TEST_READ_ONLY_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			String[] child = session.getChildNodeNames(TestReadOnlyDataPluginActivator.TEST_READ_ONLY_PLUGIN_ROOT);
			tbc.assertEquals("Asserts that DmtAdmin fowarded "+ TestReadOnlyDataPlugin.GETCHILDNODENAMES+" to the correct plugin",TestReadOnlyDataPlugin.GETCHILDNODENAMES,child[0]);
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testGetChildNodeNames002
	 * @testDescription Forces a DmtException in order to check if
	 *                  Dmt#getChildNodeNames correctly forwards the 
	 *                  exception to DmtAdmin.
	 */
	private void testGetChildNodeNames002() {
		DmtSession session = null;
		try {
			tbc.log("#testGetChildNodeNames002");
			session = tbc.getDmtAdmin().getSession(TestReadOnlyDataPluginActivator.TEST_READ_ONLY_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			session.getChildNodeNames(TestReadOnlyDataPluginActivator.INTERIOR_NODE_EXCEPTION);
			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtAdmin fowarded the DmtException with the correct subtree: ", TestReadOnlyDataPluginActivator.INTERIOR_NODE_EXCEPTION, e
					.getURI());			
			tbc.assertEquals("Asserts that DmtAdmin fowarded the DmtException with the correct code: ", DmtException.OTHER_ERROR, e
					.getCode());
			tbc.assertTrue("Asserts that DmtAdmin fowarded the DmtException with the correct message. ", e
					.getMessage().indexOf(TestReadOnlyDataPlugin.GETCHILDNODENAMES)>-1);			
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}
}
