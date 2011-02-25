/*
 * Copyright (c) OSGi Alliance (2004, 2010). All Rights Reserved.
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
 * 26/07/2005    Eduardo Oliveira
 * 147           [MEGTCK][DEPLOYMENT MO] Implement Deployment Management Objects
 * ============  ==============================================================
 */

package org.osgi.test.cases.deploymentadmin.mo.tb1;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.cases.deploymentadmin.mo.tb1.CommandExecution.DownloadAndInstallAndActivate;
import org.osgi.test.cases.deploymentadmin.mo.tb1.CommandExecution.InstallAndActivate;
import org.osgi.test.cases.deploymentadmin.mo.tb1.CommandExecution.Remove;
import org.osgi.test.cases.deploymentadmin.mo.tbc.DeploymentmoTestControl;
import org.osgi.test.cases.deploymentadmin.mo.tbc.TB1Service;
import org.osgi.test.cases.deploymentadmin.mo.tbc.TestInterface;


public class Activator implements BundleActivator, TB1Service {
	
	ServiceRegistration sr;
	
	public void start(BundleContext bc) throws Exception {
		sr = bc.registerService(TB1Service.class.getName(), this, null);
		System.out.println("TB1Service started.");

	}

	public void stop(BundleContext arg0) throws Exception {
		sr.unregister();

	}
	
	public TestInterface[] getTestClasses(DeploymentmoTestControl tbc) {
		return new TestInterface[] {
				new InstallAndActivate((DeploymentmoTestControl) tbc),
				new DownloadAndInstallAndActivate((DeploymentmoTestControl) tbc),
				new Remove((DeploymentmoTestControl) tbc)
				};
	}
}
