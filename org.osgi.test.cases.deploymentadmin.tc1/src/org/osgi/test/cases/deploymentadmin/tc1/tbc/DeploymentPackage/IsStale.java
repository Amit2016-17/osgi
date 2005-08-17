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
 * Date          Author(s)
 * CR            Headline
 * ============  ====================================================================
 * Jun 06, 2005  Andre Assad
 * 111           Implement/Change test cases according to Spec updates of 6th of June
 * ============  ====================================================================
 */

package org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentPackage;

import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentConstants;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.MessagesConstants;

/**
 * @author Andre Assad
 *
 * This Test Class Validates the implementation of <code>isStale<code> method of DeploymentPackage, 
 * according to MEG specification.
 */

public class IsStale {

	private DeploymentTestControl tbc;

	public IsStale(DeploymentTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testIsStale001();
		testIsStale002();
		
	}
	
	/**
	 * This test case asserts that a deployment package is not stale after installation.
	 * 
	 * @spec DeploymentPackage.isStale()
	 */
	private void testIsStale001() {
		tbc.log("#testIsStale001");
		DeploymentPackage dp = null;
		try {
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP).getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp);
			tbc.assertTrue("Deployment Package is not stale", !dp.isStale());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp);
		}
	}
	
	/**
	 * This test case asserts that a deployment package is stale after uninstallation.
	 * 
	 * @spec DeploymentPackage.isStale()
	 */
	private void testIsStale002() {
		tbc.log("#testIsStale002");
		DeploymentPackage dp = null;
		try {
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP).getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp);
			tbc.uninstall(dp);
			tbc.assertTrue("Deployment Package is stale", dp.isStale());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
}
