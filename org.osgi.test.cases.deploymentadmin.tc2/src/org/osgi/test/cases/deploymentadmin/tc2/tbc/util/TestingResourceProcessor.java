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
 * ===========   ==============================================================
 * May 06, 2005  Luiz Felipe Guimaraes
 * 26            Implement MEGTCK for the deployment RFC-88 
 * ===========   ==============================================================
 */
package org.osgi.test.cases.deploymentadmin.tc2.tbc.util;

import org.osgi.service.deploymentadmin.ResourceProcessor;


public interface TestingResourceProcessor extends ResourceProcessor {
	public boolean isInstallUpdateOrdered();
	public boolean isUpdateRemoveResourceOrdered();
	public boolean isUninstallOrdered();
	public boolean isUninstallResourceOrdered();
	public void resetCount();
	public void setSimulateExceptionOnPrepare(boolean simulateExceptionOnPrepare);
	public void setSimulateExceptionOnDropped(boolean simulateExceptionOnDropped);
	public void setSimulateExceptionOnCommit(boolean simulateExceptionOnCommit);
	public boolean exceptionAtDroppedOrdered();	
	public boolean exceptionAtPrepareOrdered();
	public boolean joinedSession();
	public boolean rollbackCalled();
	public long sessionJoinTime();
	public long sessionPrepareTime();
	public long sessionCommitTime();
	public long sessionRollbackTime();
}
