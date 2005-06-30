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
 * Mar 14, 2005  Luiz Felipe Guimaraes
 * 11		     Implement DMT Use Cases
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.plugins.tbc.DmtReadOnlyDataPlugin.DmtReadOnly;

import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.plugins.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.plugins.tbc.DmtReadOnlyDataPlugin.TestReadOnlyDataPlugin;
import org.osgi.test.cases.dmt.plugins.tbc.DmtReadOnlyDataPlugin.TestReadOnlyDataPluginActivator;

/**
 * @author Luiz Felipe Guimaraes
 * 
 * @methodUnderTest org.osgi.service.dmt.DmtReadOnly#isLeafNode
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>isLeafNode<code> method, according to MEG reference
 *                     documentation (rfc0085).
 */
public class IsLeafNode {
	private DmtTestControl tbc;

	public IsLeafNode(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testIsLeafNode001();
		testIsLeafNode002();
	}

	/**
	 * @testID testIsLeafNode001
	 * @testDescription Asserts that DmtAdmin correctly forwards the call of
	 *                  isLeafNode to the correct plugin.
	 */

	private void testIsLeafNode001() {
		DmtSession session = null;
		try {
			tbc.log("#testIsLeafNode001");
			session = tbc.getDmtAdmin().getSession(TestReadOnlyDataPluginActivator.TEST_READ_ONLY_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.assertTrue("Asserts that DmtAdmin fowarded "+ TestReadOnlyDataPlugin.ISLEAFNODE
					+" to the correct plugin",session.isLeafNode(TestReadOnlyDataPluginActivator.LEAF_NODE));

		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testIsLeafNode002
	 * @testDescription Asserts that DmtAdmin correctly forwards the call of
	 *                  isLeafNode to the correct plugin, by passing an invalid
	 *                  URI under Test Plugin subtree.
	 */

	private void testIsLeafNode002() {
		DmtSession session = null;
		try {
			tbc.log("#testIsLeafNode002");
			session = tbc.getDmtAdmin().getSession(TestReadOnlyDataPluginActivator.TEST_READ_ONLY_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.assertTrue("Asserts that DmtAdmin fowarded "+ TestReadOnlyDataPlugin.ISLEAFNODE
					+" to the correct plugin",!session.isLeafNode(TestReadOnlyDataPluginActivator.INTERIOR_NODE));

		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}
}
