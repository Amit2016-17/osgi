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
 * 
 */

/*
 * REVISION HISTORY:
 *
 * Date          Author(s)
 * CR            Headline
 * ============  ==============================================================
 * Apr 12, 2005  Alexandre Alves
 * 26            Implement MEG TCK
 * ============  ==============================================================
 */
package org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentAdminPermission;

import org.osgi.service.deploymentadmin.DeploymentAdminPermission;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentTestControl;

/**
 * This Test Class Validates the constants according to MEG specification.
 */
public class DeploymentAdminPermissionConstants extends DeploymentTestControl {
	/**
	 * Tests all constants values according to constants fields values.
	 * 
	 * @spec 114.14.3 
	 */
	public void testConstants001() {
		log("#testConstants001");
		assertConstant("install", "INSTALL", DeploymentAdminPermission.class);
		assertConstant("cancel", "CANCEL", DeploymentAdminPermission.class);
		assertConstant("list", "LIST", DeploymentAdminPermission.class);
		assertConstant("uninstall", "UNINSTALL",
				DeploymentAdminPermission.class);
		assertConstant("uninstall_forced", "UNINSTALL_FORCED",
				DeploymentAdminPermission.class);
	}

}
