/*
 * Copyright (c) OSGi Alliance (2004, 2011). All Rights Reserved.
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
 * This test case validates the implementation of <code>AlertItem</code> constructor, 
 * according to MEG specification.
 */
public class AlertItem extends DmtTestControl {
	private String mark = "mark";
	private DmtData data = new DmtData("test");
	private String[] nodeUri = {".","OSGi","Log"};
	private String nodeUriMangled = "./OSGi/Log";
	
	/**
	 * Asserts that the get methods returns the expected value 
	 * using the constructor that takes a nodeUri as a String
	 * 
	 * @spec AlertItem.AlertItem(String,String,String,DmtData)
	 */
	public void testAlertItem001() {
		try {		
			log("#testAlertItem001");
			org.osgi.service.dmt.notification.AlertItem alert = new org.osgi.service.dmt.notification.AlertItem(DmtConstants.OSGi_LOG,DmtConstants.MIMETYPE,mark,data);
			assertEquals("Asserts that the expected data is returned", data,
					alert.getData());
			assertEquals("Asserts that the expected mark is returned", mark,
					alert.getMark());
			assertEquals("Asserts that the expected type is returned",
					DmtConstants.MIMETYPE, alert.getType());
			assertEquals("Asserts that the expected source is returned",
					DmtConstants.OSGi_LOG, alert.getSource());
		} catch(Exception e) {
			failUnexpectedException(e);
		}
			
	}
	/**
	 * Asserts that null can be passed on the source parameter
	 * using the constructor that takes a nodeUri as a String
	 * 
	 * @spec AlertItem.AlertItem(String,String,String,DmtData)
	 */
	public void testAlertItem002() {
		try {		
			log("#testAlertItem002");
            org.osgi.service.dmt.notification.AlertItem alert = new org.osgi.service.dmt.notification.AlertItem((String)null,DmtConstants.MIMETYPE,mark,data);
            assertEquals("Asserts that the expected data is returned", data,
					alert.getData());
			assertEquals("Asserts that the expected mark is returned", mark,
					alert.getMark());
			assertEquals("Asserts that the expected type is returned",
					DmtConstants.MIMETYPE, alert.getType());
			assertNull("Asserts that the expected source is returned", alert
					.getSource());
            
			
		} catch(Exception e) {
			failUnexpectedException(e);
		}
			
	}
    /**
     * Asserts that null can be passed on the type parameter
     * using the constructor that takes a nodeUri as a String
     * 
     * @spec AlertItem.AlertItem(String,String,String,DmtData)
     */
    public void testAlertItem003() {
        try {       
            log("#testAlertItem003");
            org.osgi.service.dmt.notification.AlertItem alert = new org.osgi.service.dmt.notification.AlertItem(DmtConstants.OSGi_LOG,null,mark,data);
            assertEquals("Asserts that the expected data is returned", data,
					alert.getData());
			assertEquals("Asserts that the expected mark is returned", mark,
					alert.getMark());
			assertNull("Asserts that the expected type is returned", alert
					.getType());
			assertEquals("Asserts that the expected source is returned",
					DmtConstants.OSGi_LOG, alert.getSource());
            
            
        } catch(Exception e) {
        	failUnexpectedException(e);
        }
            
    }
    /**
     * Asserts that null can be passed on the mark parameter
     * using the constructor that takes a nodeUri as a String
     * 
     * @spec AlertItem.AlertItem(String,String,String,DmtData)
     */
    public void testAlertItem004() {
        try {       
            log("#testAlertItem004");
            org.osgi.service.dmt.notification.AlertItem alert = new org.osgi.service.dmt.notification.AlertItem(DmtConstants.OSGi_LOG,DmtConstants.MIMETYPE,null,data);
            assertEquals("Asserts that the expected data is returned", data,
					alert.getData());
			assertNull("Asserts that the expected mark is returned", alert
					.getMark());
			assertEquals("Asserts that the expected type is returned",
					DmtConstants.MIMETYPE, alert.getType());
			assertEquals("Asserts that the expected source is returned",
					DmtConstants.OSGi_LOG, alert.getSource());
            
            
        } catch(Exception e) {
        	failUnexpectedException(e);
        }
            
    }
    /**
     * Asserts that null can be passed on the data parameter
     * using the constructor that takes a nodeUri as a String
     * 
     * @spec AlertItem.AlertItem(String,String,String,DmtData)
     */
    public void testAlertItem005() {
        try {       
            log("#testAlertItem005");
            org.osgi.service.dmt.notification.AlertItem alert = new org.osgi.service.dmt.notification.AlertItem(DmtConstants.OSGi_LOG,DmtConstants.MIMETYPE,mark,null);
            assertEquals("Asserts that the expected mark is returned", mark,
					alert.getMark());
			assertEquals("Asserts that the expected type is returned",
					DmtConstants.MIMETYPE, alert.getType());
			assertEquals("Asserts that the expected source is returned",
					DmtConstants.OSGi_LOG, alert.getSource());
			assertNull("Asserts that the expected data is returned", alert
					.getData());
            
        } catch(Exception e) {
        	failUnexpectedException(e);
        }
            
    }
    /**
	 * Asserts that the get methods returns the expected value 
	 * using the constructor that takes a nodeUri as an array of String
	 * 
	 * @spec AlertItem.AlertItem(String[],String,String,DmtData)
	 */
	public void testAlertItem006() {
		try {		
			log("#testAlertItem006");
			org.osgi.service.dmt.notification.AlertItem alert = new org.osgi.service.dmt.notification.AlertItem(nodeUri,DmtConstants.MIMETYPE,mark,data);
			assertEquals("Asserts that the expected data is returned", data,
					alert.getData());
			assertEquals("Asserts that the expected mark is returned", mark,
					alert.getMark());
			assertEquals("Asserts that the expected type is returned",
					DmtConstants.MIMETYPE, alert.getType());
			assertEquals("Asserts that the expected source is returned",
					nodeUriMangled, alert.getSource());
		} catch(Exception e) {
			failUnexpectedException(e);
		}
			
	}
	/**
	 * Asserts that null can be passed on the source parameter
	 * using the constructor that takes a nodeUri as an array of String
	 * 
	 * @spec AlertItem.AlertItem(String[],String,String,DmtData)
	 */
	public void testAlertItem007() {
		try {		
			log("#testAlertItem007");
            org.osgi.service.dmt.notification.AlertItem alert = new org.osgi.service.dmt.notification.AlertItem((String[])null,DmtConstants.MIMETYPE,mark,data);
            assertEquals("Asserts that the expected data is returned", data,
					alert.getData());
			assertEquals("Asserts that the expected mark is returned", mark,
					alert.getMark());
			assertEquals("Asserts that the expected type is returned",
					DmtConstants.MIMETYPE, alert.getType());
			assertNull("Asserts that the expected source is returned", alert
					.getSource());
            
			
		} catch(Exception e) {
			failUnexpectedException(e);
		}
			
	}
    /**
     * Asserts that null can be passed on the type parameter
     * using the constructor that takes a nodeUri as an array of String
     * 
     * @spec AlertItem.AlertItem(String[],String,String,DmtData)
     */
    public void testAlertItem008() {
        try {       
            log("#testAlertItem008");

            org.osgi.service.dmt.notification.AlertItem alert = new org.osgi.service.dmt.notification.AlertItem(nodeUri,null,mark,data);
            assertEquals("Asserts that the expected data is returned", data,
					alert.getData());
			assertEquals("Asserts that the expected mark is returned", mark,
					alert.getMark());
			assertNull("Asserts that the expected type is returned", alert
					.getType());
			assertEquals("Asserts that the expected source is returned",
					nodeUriMangled, alert.getSource());
            
            
        } catch(Exception e) {
        	failUnexpectedException(e);
        }
            
    }
    /**
     * Asserts that null can be passed on the mark parameter
     * using the constructor that takes a nodeUri as an array of String
     * 
     * @spec AlertItem.AlertItem(String[],String,String,DmtData)
     */
    public void testAlertItem009() {
        try {       
            log("#testAlertItem009");
            org.osgi.service.dmt.notification.AlertItem alert = new org.osgi.service.dmt.notification.AlertItem(nodeUri,DmtConstants.MIMETYPE,null,data);
            assertEquals("Asserts that the expected data is returned", data,
					alert.getData());
			assertNull("Asserts that the expected mark is returned", alert
					.getMark());
			assertEquals("Asserts that the expected type is returned",
					DmtConstants.MIMETYPE, alert.getType());
			assertEquals("Asserts that the expected source is returned",
					nodeUriMangled, alert.getSource());
            
            
        } catch(Exception e) {
        	failUnexpectedException(e);
        }
            
    }
    /**
     * Asserts that null can be passed on the data parameter
     * using the constructor that takes a nodeUri as an array of String
     * 
     * @spec AlertItem.AlertItem(String[],String,String,DmtData)
     */
    public void testAlertItem010() {
        try {       
            log("#testAlertItem010");
            org.osgi.service.dmt.notification.AlertItem alert = new org.osgi.service.dmt.notification.AlertItem(nodeUri,DmtConstants.MIMETYPE,mark,null);
            assertEquals("Asserts that the expected mark is returned", mark,
					alert.getMark());
			assertEquals("Asserts that the expected type is returned",
					DmtConstants.MIMETYPE, alert.getType());
			assertEquals("Asserts that the expected source is returned",
					nodeUriMangled, alert.getSource());
			assertNull("Asserts that the expected data is returned", alert
					.getData());
            
        } catch(Exception e) {
        	failUnexpectedException(e);
        }
            
    }
}
