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
 * 05/07/2005   Luiz Felipe Guimaraes
 * 1            Implement TCK
 * ===========  ==============================================================
 */

package org.osgi.test.cases.dmt.tc1.tbc.AlertItem;

import org.osgi.service.dmt.DmtData;
import org.osgi.test.cases.dmt.tc1.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc1.tbc.DmtTestControl;

/**
 * This class validates the implementation of <code>toString<code> method of AlertItem, 
 * according to MEG specification.
 */
public class ToString {
	private DmtTestControl tbc;

	public ToString(DmtTestControl tbc) {
		this.tbc = tbc;

	}

	public void run() {
		testToString001();
		testToString002();
	}

	/**
	 * Asserts that the toString() returns the expected value
	 * 
	 * @spec AlertItem.toString()
	 */
	private void testToString001() {
		try {		
			tbc.log("#testToString001");
			String mark = "mark";
			DmtData data = new DmtData("test");
			org.osgi.service.dmt.AlertItem alert = new org.osgi.service.dmt.AlertItem(DmtConstants.OSGi_LOG,DmtConstants.MIMETYPE,mark,data);
			tbc.assertEquals("Asserts that the expected string is returned","AlertItem(" + alert.getSource() + ", "+ alert.getType() +", "+ alert.getMark() + ", "+ alert.getData() +")",alert.toString());
		} catch(Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		}
			
	}
	/**
	 * Asserts that toString() returns null when it is passed in the constructor
	 * 
	 * @spec AlertItem.toString()
	 */
	private void testToString002() {
		try {		
			tbc.log("#testToString002");
			org.osgi.service.dmt.AlertItem alert = new org.osgi.service.dmt.AlertItem((String)null,null,null,null);
			tbc.assertEquals("Asserts that the expected string is returned","AlertItem(null, null, null, null)",alert.toString());
		} catch(Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		}
			
	}
}
