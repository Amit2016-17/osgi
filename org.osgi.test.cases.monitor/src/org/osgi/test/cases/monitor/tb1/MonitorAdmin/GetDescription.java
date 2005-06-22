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
 * 
 */

/*
 * REVISION HISTORY:
 *
 * Date         Author(s)
 * CR           Headline
 * ===========  ==============================================================
 * Mar 29, 2005	Leonardo Barros
 * 14           Implement MEG TCK
 * ===========  ==============================================================
 * May 20, 2005	Alexandre Alves
 * 92           Make changes according to monitor RFC updates
 * ===========  ==============================================================
 **/
package org.osgi.test.cases.monitor.tb1.MonitorAdmin;

import org.osgi.service.monitor.MonitorPermission;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.monitor.tbc.MonitorTestControl;
import org.osgi.test.cases.monitor.tbc.TestInterface;
import org.osgi.test.cases.monitor.tbc.util.MessagesConstants;

/**
 * @methodUnderTest org.osgi.service.monitor.MonitorAdmin#getDescription
 * @generalDescription This Test Class Validates the implementation of
 *                     <code>getDescription<code> method, according to MEG reference
 *                     documentation.
 */
public class GetDescription implements TestInterface {
	private MonitorTestControl tbc;

	/**
	 * @param tbc
	 */
	public GetDescription(MonitorTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testGetDescription001();
		testGetDescription002();
		testGetDescription003();
		testGetDescription004();
		testGetDescription005();
		testGetDescription006();
		testGetDescription007();
	}

	/**
	 * @testID testGetDescription001
	 * @testDescription Asserts if IllegalArgumentException is thrown when null
	 *                  is passed as argument to getDescription
	 *                  method
	 */
	public void testGetDescription001() {
		try { 
			tbc.log("#testGetDescription001");
			tbc.getMonitorAdmin().getDescription(null);
			tbc.failException("", IllegalArgumentException.class);
			
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalArgumentException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(),
							e.getClass().getName() }));
		}
	}

	/**
	 * @testID testGetDescription002
	 * @testDescription Tests if IllegalArgumentException is thrown when
	 *                  invalid characters is passed as parameter
	 */
	public void testGetDescription002() {
		try {
			tbc.log("#testGetDescription002");
			tbc.getMonitorAdmin().getDescription(MonitorTestControl.INVALID_ID);

			tbc.failException("", IllegalArgumentException.class);
			
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalArgumentException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(),
							e.getClass().getName() }));
		} 
	}

	/**
	 * @testID testGetDescription003
	 * @testDescription Tests if IllegalArgumentException is thrown when an
	 *                  invalid path is passed as parameter
	 */
	public void testGetDescription003() {
		try {
			tbc.log("#testGetDescription003");
			tbc.getMonitorAdmin().getDescription(MonitorTestControl.INEXISTENT_SVS);

			tbc.failException("", IllegalArgumentException.class);
			
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalArgumentException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(),
							e.getClass().getName() }));
		} 
	}

	/**
	 * @testID testGetDescription004
	 * @testDescription Tests if an exception is not thrown when a valid path is
	 *                  passed as argument.
	 */
	public void testGetDescription004() {
		tbc.log("#testGetDescription004");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());
			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class.getName(),MonitorTestControl.SVS[0], MonitorPermission.READ));

			tbc.getMonitorAdmin().getDescription(MonitorTestControl.SVS[0]);
			tbc
					.pass("A description is corretly returned when a valid path is passed as argument");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.getPermissionAdmin().setPermissions(tbc.getTb1Location(), infos);
		}
	}

	/**
	 * @testID testGetDescription005
	 * @testDescription Asserts if a SecurityException is thrown when the caller
	 *                  has no read permission
	 */
	public void testGetDescription005() {
		tbc.log("#testGetDescription005");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class.getName(),MonitorTestControl.SVS[0], null));

			tbc.getMonitorAdmin().getDescription(MonitorTestControl.SVS[0]);

			tbc.failException("", SecurityException.class);

		} catch (SecurityException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { SecurityException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							SecurityException.class.getName(),
							e.getClass().getName() }));
		} finally {
			tbc.getPermissionAdmin().setPermissions(
					tbc.getTb1Location(), infos);
		}
	}

	/**
	 * @testID testGetDescription006
	 * @testDescription Asserts if a SecurityException is thrown when the caller
	 *                  has no read permission
	 */
	public void testGetDescription006() {
		tbc.log("#testGetDescription006");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class.getName(),MonitorTestControl.SVS[0], MonitorPermission.PUBLISH));

			tbc.getMonitorAdmin().getDescription(MonitorTestControl.SVS[0]);

			tbc.failException("", SecurityException.class);

		} catch (SecurityException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { SecurityException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							SecurityException.class.getName(),
							e.getClass().getName() }));
		} finally {
			tbc.getPermissionAdmin().setPermissions(
					tbc.getTb1Location(), infos);
		}
	}
	
	/**
	 * @testID testGetDescription007
	 * @testDescription Asserts if a SecurityException is thrown when the caller
	 *                  has read permission for other StatusVariable
	 */
	public void testGetDescription007() {
		tbc.log("#testGetDescription007");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class.getName(),MonitorTestControl.SVS[1], MonitorPermission.READ));

			tbc.getMonitorAdmin().getDescription(MonitorTestControl.SVS[0]);

			tbc.failException("", SecurityException.class);

		} catch (SecurityException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { SecurityException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							SecurityException.class.getName(),
							e.getClass().getName() }));
		} finally {
			tbc.getPermissionAdmin().setPermissions(
					tbc.getTb1Location(), infos);
		}
	}
	
}