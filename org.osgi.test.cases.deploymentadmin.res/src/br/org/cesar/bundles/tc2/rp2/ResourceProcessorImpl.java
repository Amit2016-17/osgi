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
 * ============  ==============================================================
 * May 03, 2005  Luiz Felipe Guimaraes
 * 26            Implement MEG TCK for the deployment RFC-88
 * ============  ==============================================================
 * Jul 14, 2005  Andre Assad
 * 145           Implement spec review issues
 * ============  ==============================================================
 */

package br.org.cesar.bundles.tc2.rp2;

import java.io.File;
import java.io.InputStream;
import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.deploymentadmin.spi.DeploymentSession;
import org.osgi.service.deploymentadmin.spi.ResourceProcessor;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentConstants;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.util.TestingResourceProcessor;

/**
 * @author Luiz Felipe Guimaraes
 * 
 * A testing resource processor to test deployment packages
 * installation.
 */
public class ResourceProcessorImpl implements BundleActivator, TestingResourceProcessor {
	private ServiceRegistration sr;
	private boolean simulateExceptionOnDropped;
	private boolean simulateExceptionOnPrepare;
	private boolean simulateExceptionOnCommit;

	private DeploymentSession session;

	//Order that DeploymentAdmin calls the methods.
	private int order[] = new int[8];
	private int count;
	private final int FINISH=-1;
	private final int BEGIN=0;
	private final int PROCESS=1;
	private final int DROPPED=2;
	private final int DROP_ALL_RESOURCES=3;
	private final int PREPARE=4;
	private final int COMMIT=5;
	private final int ROLLBACK=6;
	private final int CANCEL=7;
	
	private boolean joined;
	private long joinedTime;
	private long prepareTime;
	private long commitedTime;
	private long rollbackTime;
	private boolean rollbackCalled;
	
	public void start(BundleContext bc) throws Exception {
		Dictionary props = new Hashtable();
		props.put("service.pid", DeploymentConstants.PID_RESOURCE_PROCESSOR2);
		sr = bc.registerService(ResourceProcessor.class.getName(), this, props);
		System.out.println("Resource Processor started.");
		resetCount();
	}

	public void stop(BundleContext bc) throws Exception {
		sr.unregister();

	}

	public void begin(DeploymentSession session) {
		this.session = session;
		addOrder(BEGIN);
		joinedTime = System.currentTimeMillis();
		joined = true;
	}
	
	public void process(String arg0, InputStream arg1)
			 {
		addOrder(PROCESS);
	}

	public void dropped(String arg0)  {
		addOrder(DROPPED);
		if (simulateExceptionOnDropped) {
			throw new RuntimeException("This resource processor doesn't manage it.");
		}
	}

	public void dropAllResources()  {
		addOrder(DROP_ALL_RESOURCES);	
	}

	public void prepare()  {
		addOrder(PREPARE);
		prepareTime = System.currentTimeMillis();
		if (simulateExceptionOnPrepare) {
			throw new RuntimeException("This resource processor cannot commit.");
		}
	}

	public void commit() {
		addOrder(COMMIT);
		commitedTime = System.currentTimeMillis();
		if (simulateExceptionOnCommit) {
			throw new RuntimeException("RuntimeException on commit.");
		}
	}

	public void rollback() {
		addOrder(ROLLBACK);
		rollbackCalled = true;
		rollbackTime = System.currentTimeMillis();
	}

	public void cancel() {
		addOrder(CANCEL);

	}

	private void addOrder(int methodMap) {
		if (count<order.length) {
			order[count]=methodMap;
		}
		count++;
	}
	
	public boolean isUninstallResourceOrdered() {
		if (order[0]==BEGIN && order[1]==DROPPED && order[2]==PREPARE && order[3]==COMMIT && order[4]==FINISH) {
			return true;
		}
		else {
			return false;
		}
		
	}
	
	public boolean isInstallUpdateOrdered() {
		if (order[0]==BEGIN && order[1]==PROCESS && order[2]==PREPARE && order[3]==COMMIT && order[4]==FINISH) {
			return true;
		} 
		else {
			return false;
		}
		
	}

	public boolean isUninstallOrdered() {
		if (order[0]==BEGIN && order[1]==DROP_ALL_RESOURCES && order[2]==PREPARE && order[3]==COMMIT && order[4]==FINISH) {
			return true;
		} 
		else {
			return false;
		}
	}
	
	public boolean exceptionAtDroppedOrdered() {
		if (order[0]==BEGIN && order[1]==DROPPED && order[2]==ROLLBACK && order[3]==FINISH) {
			return true;
		} 
		else {
			return false;
		}
	}
	
	public boolean exceptionAtPrepareOrdered() {
		if (order[0]==BEGIN && order[1]==PROCESS && order[2]==PREPARE && order[3]==ROLLBACK && order[4]==FINISH) {
			return true;
		} 
		else {
			return false;
		}
		
	}
	
	public void resetCount() {
		for (int i=0;i<order.length;i++) {
			order[i]=FINISH;
		}
		count=0;
	}
	
	public int getAction() {
		return 0;
//		return session.getDeploymentAction();
	}
	
	/**
	 * @return Returns the source.
	 */
	public DeploymentPackage getSource() {
		return session.getSourceDeploymentPackage();
	}
	
	/**
	 * @return Returns the target.
	 */
	public DeploymentPackage getTarget() {
		return session.getTargetDeploymentPackage();
	}

	/**
	 * @param simulateExceptionOnDropped The simulateExceptionOnDropped to set.
	 */
	public void setSimulateExceptionOnDropped(boolean simulateExceptionOnDropped) {
		this.simulateExceptionOnDropped = simulateExceptionOnDropped;
	}
	
	/**
	 * @param simulateExceptionOnPrepare The simulateExceptionOnPrepare to set.
	 */
	public void setSimulateExceptionOnPrepare(boolean simulateExceptionOnPrepare) {
		this.simulateExceptionOnPrepare = simulateExceptionOnPrepare;
	}

	public File getDataFile(Bundle bundle) {
		if (null != bundle)	{
			return session.getDataFile(bundle);
		} else {
			return null;
		}
	}

	public boolean joinedSession() {
		return joined;
	}

	public long sessionJoinTime() {
		return joinedTime;
	}
	
	public long sessionPrepareTime() {
		return prepareTime;
	}
	
	public long sessionCommitTime() {
		return commitedTime;
	}

	public void setSimulateExceptionOnCommit(boolean simulateExceptionOnCommit) {
		this.simulateExceptionOnCommit = simulateExceptionOnCommit;
		
	}

	public boolean rollbackCalled() {
		return rollbackCalled;
	}

	public long sessionRollbackTime() {
		return rollbackTime;
	}

	public boolean isUpdateRemoveResourceOrdered() {
		if (order[0]==BEGIN && order[1]==PROCESS && order[2]==DROPPED && 
				order[3]==PREPARE && order[4]==COMMIT && order[5]==FINISH) {
			return true;
		} 
		else {
			return false;
		}
	}

}
