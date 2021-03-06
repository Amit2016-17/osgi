/*
 * Copyright (c) OSGi Alliance (2004, 2020). All Rights Reserved.
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
 * Mar 04, 2005  Andre Assad
 * 11		     Implement DMT Use Cases
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TransactionalDataSession;

import java.util.Date;

import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.tc3.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TestDataPlugin;
import org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TestDataPluginActivator;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

import junit.framework.TestCase;

/**
 * @author Andre Assad
 * 
 * This test case validates the implementation of <code>getNodeTimestamp</code> method, 
 * according to MEG specification
 */
public class GetNodeTimestamp {
	private DmtTestControl tbc;

	public GetNodeTimestamp(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
        testGetNodeTimestamp001();
        testGetNodeTimestamp002();
	}

	/**
	 * Asserts that DmtAdmin correctly forwards the call of getNodeTimestamp 
	 * to the correct plugin.
	 * 
	 * @spec ReadableDataSession.getNodeTimestamp(String[])
	 */
	public void testGetNodeTimestamp001() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetNodeTimestamp001");
			session = tbc.getDmtAdmin().getSession(TestDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			Date date = session.getNodeTimestamp(TestDataPluginActivator.ROOT);
			TestCase.assertEquals("Asserts that DmtAdmin fowarded "+ TestDataPlugin.GETNODETIMESTAMP
					+" to the correct plugin",TestDataPlugin.GETNODETIMESTAMP_VALUE,date);
		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
		} finally {
			tbc.cleanUp(session,true);
		}

	}

	/**
	 * Asserts that DmtAdmin correctly forwards the DmtException thrown by the plugin
	 * 
	 * @spec ReadableDataSession.getNodeTimestamp(String[])
	 */
	public void testGetNodeTimestamp002() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetNodeTimestamp002");
			session = tbc.getDmtAdmin().getSession(TestDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			session.getNodeTimestamp(TestDataPluginActivator.INTERIOR_NODE_EXCEPTION);
			DefaultTestBundleControl.failException("", DmtException.class);
			
		} catch (DmtException e) {
			
			TestCase.assertEquals("Asserts that DmtAdmin fowarded the DmtException with the correct subtree: ", 
					TestDataPluginActivator.INTERIOR_NODE_EXCEPTION, e.getURI());			
			TestCase.assertEquals("Asserts that DmtAdmin fowarded the DmtException with the correct code: ", 
					DmtException.URI_TOO_LONG, e.getCode());
			TestCase.assertTrue("Asserts that DmtAdmin fowarded the DmtException with the correct message. ", 
					e.getMessage().indexOf(TestDataPlugin.GETNODETIMESTAMP)>-1);
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.cleanUp(session,true);
		}
	}
    
}
