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
 * Set 08, 2005  Alexandre Alves
 * 179           Implement Review Issues            
 * ===========   ==============================================================
 */
package br.org.cesar.bundles.tc1.rp4;

import java.io.InputStream;
import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.deploymentadmin.spi.DeploymentSession;
import org.osgi.service.deploymentadmin.spi.ResourceProcessor;
import org.osgi.test.cases.deploymentadmin.tc1.tb1.DeploymentAdmin.UninstallDeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentConstants;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.TestingRollbackCall;

/**
 * @author Alexandre Alves
 *
 */
public class ResourceProcessorImpl implements BundleActivator, TestingRollbackCall {

	private boolean called = false;
    private boolean prepareException = false;
    private ServiceRegistration sr;

	public void start(BundleContext bc) throws Exception {
		Dictionary props = new Hashtable();
		props.put("service.pid", DeploymentConstants.PID_RESOURCE_PROCESSOR4);
		
        sr = bc.registerService(ResourceProcessor.class.getName(), this, props);
		System.out.println("Resource Processor started.");
	}

	public void stop(BundleContext context) throws Exception {
		sr.unregister();
		
	}

    public void begin(DeploymentSession session) {
    }

    public void process(String name, InputStream stream)  {
        
    }

    public void dropped(String resource)  {
        
    }

    public void dropAllResources()  {
        throw new RuntimeException();
    }

    public void prepare()  {
        if (prepareException) {
            throw new RuntimeException();
        }
                
    }

    public void commit() {
        
    }

    public void rollback() {
        called = true;
        UninstallDeploymentPackage.ROLL_BACK_CALLED = true;
    }

    public void cancel() {
        
    }

    public boolean isRolbackCalled() {
        return called;
    }

    public void setPrepareException(boolean value) {
        this.prepareException = value;
    }
}
