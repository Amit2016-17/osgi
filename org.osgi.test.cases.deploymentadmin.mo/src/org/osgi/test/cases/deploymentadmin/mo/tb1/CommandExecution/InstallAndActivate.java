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
 * ===========  ===============================================================
 * Jul 1, 2005  Leonardo Barros
 * 97           [MEGTCK][DEPLOYMENT MO] Implement Deployment Management Objects 
 * ===========  ===============================================================
 * Jul 4, 2005  Andre Assad
 * 97           [MEGTCK][DEPLOYMENT MO] Implement Deployment Management Objects
 * ===========  ===============================================================
 * Jul 25, 2005 Eduardo Oliveira
 * 147          [MEGTCK][DEPLOYMENT MO] Implement Deployment Management Objects
 * ===========  ===============================================================
 */
package org.osgi.test.cases.deploymentadmin.mo.tb1.CommandExecution;

import java.io.File;
import java.util.Iterator;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.deploymentadmin.spi.ResourceProcessor;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.deploymentadmin.mo.tbc.DeploymentmoConstants;
import org.osgi.test.cases.deploymentadmin.mo.tbc.DeploymentmoTestControl;
import org.osgi.test.cases.deploymentadmin.mo.tbc.SessionWorker;
import org.osgi.test.cases.deploymentadmin.mo.tbc.TestInterface;
import org.osgi.test.cases.deploymentadmin.mo.tbc.util.BundleListenerImpl;
import org.osgi.test.cases.deploymentadmin.mo.tbc.util.MessagesConstants;
import org.osgi.test.cases.deploymentadmin.mo.tbc.util.TestingArtifact;
import org.osgi.test.cases.deploymentadmin.mo.tbc.util.TestingBlockingResourceProcessor;
import org.osgi.test.cases.deploymentadmin.mo.tbc.util.TestingDeploymentPackage;

/**
 * @author Andre Assad
 * 
 * This Test Class Validates the implementation of Command Execution -
 * InstallAndActivate, according to MEG reference documentation.
 */
public class InstallAndActivate implements TestInterface {

	private DeploymentmoTestControl tbc;
	
    private SessionWorker worker1;
    
	public InstallAndActivate(DeploymentmoTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testInstallAndActivate001();
		testInstallAndActivate002();
		testInstallAndActivate003();
		testInstallAndActivate004();
		testInstallAndActivate005();
		testInstallAndActivate006();
		testInstallAndActivate007();
		testInstallAndActivate008();
		testInstallAndActivate009();
		testInstallAndActivate010();
		testInstallAndActivate011();
        testInstallAndActivate012();
        testInstallAndActivate013();
        testInstallAndActivate014();
        testInstallAndActivate015();
        testInstallAndActivate016();
        testInstallAndActivate017();
        testInstallAndActivate018();
        testInstallAndActivate019();
        testInstallAndActivate020();
        testInstallAndActivate021();
        
	}

	/**
	 * This test asserts if InstallAndActivate command is executed correctly
	 * returning a result code 200 - Successful. It also tests if the specified
	 * node values at Deployed subtree are set.
	 * 
	 * @spec 3.6.5.2 InstallAndActivate Command
	 */

	private void testInstallAndActivate001() {
		tbc.log("#testInstallAndActivate001");
		DmtSession session = null;
		String nodeId = "";
        //Backups the artifact and, after the execute method removes, moves it again to the delivered area 
        String archiveName =DeploymentmoConstants.MAP_CODE_TO_ARTIFACT[DeploymentmoConstants.SIMPLE_DP] +".dp";
        File fileSrc = DeploymentmoTestControl.copyArtifact(archiveName);
		File fileDestiny = DeploymentmoTestControl.getFile(archiveName);
		
		try {
			
			session = tbc.getDmtAdmin().getSession(
					DeploymentmoConstants.PRINCIPAL,
					DeploymentmoConstants.OSGI_ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);

			String[] initialChildren = session.getChildNodeNames(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);


			 synchronized (tbc) {
				session.execute(DeploymentmoConstants.SIMPLE_DP_DELIVERED_OPERATIONS_INSTALLANDACTIVATE, null);
                 tbc.wait(DeploymentmoConstants.TIMEOUT);
             }

             String[] finalChildren= session.getChildNodeNames(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);
             tbc.assertTrue("Asserts that the at Deployed subtree was created",initialChildren.length+1==finalChildren.length);
             
             nodeId = DeploymentmoTestControl.getNodeId(initialChildren,finalChildren);
             tbc.assertAlertValues(DeploymentmoConstants.ALERT_TYPE_INSTALLANDACTIVATE,
                 DeploymentmoConstants.getDeployedNodeId(nodeId),
                 new DmtData(200));
             
			tbc.assertTrue(
					"Asserting if the node was removed from DMT structure after being installed",
					!session.isNodeUri(DeploymentmoConstants.SIMPLE_DP_DELIVERED));
			
            assertSimpleDpSubtree(session,nodeId);
			
		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		} finally {
        	if (!nodeId.equals("")) {
        		tbc.executeRemoveNode(session,DeploymentmoConstants.getDeployedOperationsRemove(nodeId));
        	}
			DeploymentmoTestControl.renameFileForced(fileSrc, fileDestiny);
		    tbc.closeSession(session);
			
		}
	}
	
	
	/**
	 * This test asserts that InstallAndActivate command updates the subtree when 
	 * a sub-tree under the $/Deployment/Inventory/Deployed node with the same ID node 
	 * already exist.
	 * 
	 * @spec 3.6.5.2 InstallAndActivate Command
	 */

	private void testInstallAndActivate002() {
		tbc.log("#testInstallAndActivate002");
		DmtSession session = null;
		String nodeId = "";
        //Backups the artifact and, after the execute method removes, moves it again to the delivered area 
        String archiveName =DeploymentmoConstants.MAP_CODE_TO_ARTIFACT[DeploymentmoConstants.SIMPLE_DP] +".dp";
        File fileSrc = DeploymentmoTestControl.copyArtifact(archiveName);
        File fileDestiny =DeploymentmoTestControl.getFile(archiveName);
		
        String archiveName2 =DeploymentmoConstants.MAP_CODE_TO_ARTIFACT[DeploymentmoConstants.SIMPLE_FIX_PACK_DP] +".dp";
        File fileSrc2 = DeploymentmoTestControl.copyArtifact(archiveName2);
		File fileDestiny2 =DeploymentmoTestControl.getFile(archiveName2);
		try {
			
			session = tbc.getDmtAdmin().getSession(
					DeploymentmoConstants.PRINCIPAL,
					DeploymentmoConstants.OSGI_ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);

			//Installs 'simple.dp'
			String[] initialChildren = session.getChildNodeNames(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);
			synchronized (tbc) {
				session.execute(DeploymentmoConstants.SIMPLE_DP_DELIVERED_OPERATIONS_INSTALLANDACTIVATE, null);
                tbc.wait(DeploymentmoConstants.TIMEOUT);
             }
			String[] finalChildren= session.getChildNodeNames(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);
			tbc.assertTrue("Asserts that the at Deployed subtree was created",initialChildren.length+1==finalChildren.length);
			nodeId = DeploymentmoTestControl.getNodeId(initialChildren,finalChildren);
			
            //Renames simple_fix_pack.dp to simple.dp, so the artifact id remains the same 
 			File file = DeploymentmoTestControl.getFile(archiveName2);
             tbc.assertTrue("Asserts that the file could be renamed",DeploymentmoTestControl.renameFileForced(file, fileDestiny));
             
             tbc.resetCommandValues();
             //Installs 'simple.dp' (which has the content of simple_fix_pack.dp)
             initialChildren = finalChildren;
			 synchronized (tbc) {
				session.execute(DeploymentmoConstants.getDeliveredOperationsInstallAndActivate(DeploymentmoConstants.SIMPLE_DP), null);
                tbc.wait(DeploymentmoConstants.TIMEOUT);
             }

			 finalChildren= session.getChildNodeNames(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);
			 
			 tbc.assertTrue("Asserts that the at Deployed subtree was not created (it is an update)",initialChildren.length==finalChildren.length);
             tbc.assertAlertValues(DeploymentmoConstants.ALERT_TYPE_INSTALLANDACTIVATE,
                 DeploymentmoConstants.getDeployedNodeId(nodeId),
                 new DmtData(200));

			tbc.assertTrue(
					"Asserting if the node was removed from DMT structure after being installed",
					!session.isNodeUri(DeploymentmoConstants.SIMPLE_DP_DELIVERED));

            //Deployment Package "simple.dp" (which has the content of simple_fix_pack.dp)


			tbc.assertEquals(
							"Asserting the node EnvType",
							DeploymentmoConstants.ENVTYPE,
							session.getNodeValue(DeploymentmoConstants.getDeployedEnvType(nodeId)).toString());
			
			String manifest = session.getNodeValue(DeploymentmoConstants.getDeployedExtManifest(nodeId)).toString().trim();
			boolean passed = true;
			Iterator iterator = DeploymentmoConstants.simpleFixPackManifest.iterator();
			while (iterator.hasNext()) {
				String element = (String)iterator.next();
				if (manifest.indexOf(element) < 0) {
					passed=false;
				}
			}
			
			tbc.assertTrue("Asserting the content of the Deployment Package manifest",passed);

			tbc.assertEquals(
							"Asserting the package type",
							DeploymentmoConstants.OSGI_DP,
							session.getNodeValue(DeploymentmoConstants.getDeployedExtPackageType(nodeId)).getInt());
			
			String[] signerChildren = session.getChildNodeNames(DeploymentmoConstants.getDeployedExtSigners(nodeId));
			
			if (signerChildren.length<=0) {
				tbc.fail("./OSGi/Deployment/Inventory/Deployed/[node_id]/Ext/Signers/[signer] does not exist");
			}
			String signer = signerChildren[0];
			
			tbc.assertEquals("Asserting the signer of the deployment package",
					DeploymentmoConstants.SIMPLE_DP_SIGNER,
					session.getNodeValue(DeploymentmoConstants.getDeployedExtSignersSignerId(nodeId, signer)).toString());

			
			//Bundle "bundles.tb1"
			Bundle bundle1 = tbc.getBundle(DeploymentmoConstants.SIMPLE_FIX_PACK_BUNDLE1_SYMBNAME);
			tbc.assertNotNull("The bundle was installed in the framework", bundle1);
			String bundleId = bundle1.getBundleId() + "";
			
			tbc.assertTrue("Asserting that the bundle id is the same as the specified",
					session.isNodeUri(DeploymentmoConstants.getDeployedExtBundlesBundleId(nodeId, bundleId)));
			
			tbc.assertEquals("Asserting bundle's state",
					DeploymentmoConstants.SIMPLE_FIX_PACK_BUNDLE1_STATE,
					session.getNodeValue(DeploymentmoConstants.getDeployedExtBundlesState(nodeId, bundleId)).getInt());
			
			tbc.assertTrue("Asserting that the manifest of the first bundle is the same as the specified",
					session.getNodeValue(DeploymentmoConstants.getDeployedExtBundlesManifest(nodeId, bundleId)).toString().
					indexOf(DeploymentmoConstants.SIMPLE_FIX_PACK_BUNDLE1_MANIFEST) > -1);
			
			tbc.assertEquals("Asserting that the location of the first bundle is the same as the specified",
					DeploymentmoConstants.SIMPLE_FIX_PACK_BUNDLE1_LOCATION,
					session.getNodeValue(DeploymentmoConstants.getDeployedExtBundlesLocation(nodeId, bundleId)).toString());

			//Bundle "bundles.tb2"
			Bundle bundle2 = tbc.getBundle(DeploymentmoConstants.SIMPLE_DP_BUNDLE2_SYMBNAME);
			tbc.assertNull("Asserts that the bundle 2 was removed from the framework", bundle2);

			String[] children = session.getChildNodeNames(DeploymentmoConstants.getDeployedExtBundles(nodeId));
			tbc.assertTrue("Asserting that there is only one bundle node in DMT",children.length==1);

		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		} finally {
        	if (!nodeId.equals("")) {
        		tbc.executeRemoveNode(session,DeploymentmoConstants.getDeployedOperationsRemove(nodeId));
        	}
        	DeploymentmoTestControl.renameFileForced(fileSrc, fileDestiny);
        	DeploymentmoTestControl.renameFileForced(fileSrc2, fileDestiny2);
		    tbc.closeSession(session);
			
		}
	}

	
	/**
     * This test asserts that a bundle can be downloaded and successfully installed
     * (result code 200)
     *
     * @spec 3.6.5.2 InstallAndActivate Command
     */
    private void testInstallAndActivate003() {
    	tbc.log("#testInstallAndActivate003");
		DmtSession session = null;
		String nodeId = "";
        //Backups the artifact and, after the execute method removes, moves it again to the delivered area 
        String archiveName =DeploymentmoConstants.MAP_CODE_TO_ARTIFACT[DeploymentmoConstants.SIMPLE_BUNDLE];
        File fileSrc = DeploymentmoTestControl.copyArtifact(archiveName);
        File fileDestiny =DeploymentmoTestControl.getFile(archiveName);
        
		
		try {
			
			session = tbc.getDmtAdmin().getSession(
					DeploymentmoConstants.PRINCIPAL,
					DeploymentmoConstants.OSGI_ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);


			String[] initialChildren = session.getChildNodeNames(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);
			synchronized (tbc) {
				session.execute(DeploymentmoConstants.getDeliveredOperationsInstallAndActivate(DeploymentmoConstants.SIMPLE_BUNDLE), null);
                tbc.wait(DeploymentmoConstants.TIMEOUT);
             }
			String[] finalChildren= session.getChildNodeNames(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);
			nodeId = DeploymentmoTestControl.getNodeId(initialChildren,finalChildren);
			
             tbc.assertAlertValues(DeploymentmoConstants.ALERT_TYPE_INSTALLANDACTIVATE,
                 DeploymentmoConstants.getDeployedNodeId(nodeId),
                 new DmtData(200));

			

		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		} finally {
        	if (!nodeId.equals("")) {
        		tbc.executeRemoveNode(session,DeploymentmoConstants.getDeployedOperationsRemove(nodeId));
        	}
        	DeploymentmoTestControl.renameFileForced(fileSrc, fileDestiny);
		    tbc.closeSession(session);
			
		}
	}
    
    /**
     * This test asserts that the result code is 250 if the installation was successful 
     * but with bundle start warning.
     *
     * @spec 3.6.5.2 InstallAndActivate Command
     */
    private void testInstallAndActivate004() {
    	tbc.log("#testInstallAndActivate004");
        assertResultCode(DeploymentmoConstants.BUNDLE_THROWS_EXCEPTION_DP, 250);
		DmtSession session = null;
		String nodeId = "";
        //Backups the artifact and, after the execute method removes, moves it again to the delivered area 
        String archiveName =DeploymentmoConstants.MAP_CODE_TO_ARTIFACT[DeploymentmoConstants.BUNDLE_THROWS_EXCEPTION_DP] + ".dp";
        File fileSrc = DeploymentmoTestControl.copyArtifact(archiveName);
        File fileDestiny =DeploymentmoTestControl.getFile(archiveName);
        
		
		try {
			
			session = tbc.getDmtAdmin().getSession(
					DeploymentmoConstants.PRINCIPAL,
					DeploymentmoConstants.OSGI_ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);


			String[] initialChildren = session.getChildNodeNames(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);
			synchronized (tbc) {
				session.execute(DeploymentmoConstants.getDeliveredOperationsInstallAndActivate(DeploymentmoConstants.BUNDLE_THROWS_EXCEPTION_DP), null);
                tbc.wait(DeploymentmoConstants.TIMEOUT);
             }
			String[] finalChildren= session.getChildNodeNames(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);
			nodeId = DeploymentmoTestControl.getNodeId(initialChildren,finalChildren);
			
             tbc.assertAlertValues(DeploymentmoConstants.ALERT_TYPE_INSTALLANDACTIVATE,
                 DeploymentmoConstants.getDeployedNodeId(nodeId),
                 new DmtData(250));

			

		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		} finally {
        	if (!nodeId.equals("")) {
        		tbc.executeRemoveNode(session,DeploymentmoConstants.getDeployedOperationsRemove(nodeId));
        	}
        	DeploymentmoTestControl.renameFileForced(fileSrc, fileDestiny);
		    tbc.closeSession(session);
			
		}
    }
    /**
     * This test asserts the result code is 401 if the user cancelled the download.
     *
     * @spec 3.6.5.2 InstallAndActivate Command
     */
    private void testInstallAndActivate005() {
        tbc.log("#testInstallAndActivate005");
        tbc.log("#Test case is only valid if the user cancelled the download");
        assertResultCode(DeploymentmoConstants.SIMPLE_DP, 401);
    }

    /**
     * This test asserts the result code is 404 if the content is not acceptable
     *
     * @spec 3.6.5.2 InstallAndActivate Command
     */
    private void testInstallAndActivate006() {
    	tbc.log("#testInstallAndActivate006");
        assertResultCode(DeploymentmoConstants.NOT_ACCEPTABLE_CONTENT, 404);
    }
    
    
    /**
     * This test asserts the result code is 450 if the manifest is not the first file in the stream
     *
     * @spec 3.6.5.2 InstallAndActivate Command
     */
    private void testInstallAndActivate007() {
        tbc.log("#testInstallAndActivate007");
        assertResultCode(DeploymentmoConstants.MANIFEST_NOT_1ST_FILE, 450);
    }
    
    /**
     * This test asserts the result code is 451 if the dp is missing a header.
     *
     * @spec 3.6.5.2 InstallAndActivate Command
     */
    private void testInstallAndActivate008() {
        tbc.log("#testInstallAndActivate008");
        assertResultCode(DeploymentmoConstants.MISSING_NAME_HEADER_DP, 451);
    }
    
    /**
     * This test asserts the result code is 453 if the fixpack target is missing.
     *
     * @spec 3.6.5.2 InstallAndActivate Command
     */
    private void testInstallAndActivate009() {
        tbc.log("#testInstallAndActivate009");
        assertResultCode(DeploymentmoConstants.FIX_PACK_LOWER_RANGE_DP, 453);
    }
    /**
     * This test asserts the result code is 454 if a bundle in the Deployment
     * Package is marked as an absent bundle but there is no such bundle in the
     * target deployment package.
     *
     * @spec 3.6.5.2 InstallAndActivate Command
     */
    private void testInstallAndActivate010() {
    	tbc.log("#testInstallAndActivate010");
        assertResultCode(DeploymentmoConstants.RESOURCE_PROCESSOR_DP,DeploymentmoConstants.SIMPLE_NO_BUNDLE_DP,DeploymentmoConstants.MISSING_BUNDLE_FIX_PACK, 454);
    }
    /**
     * This test asserts the result code is 455 if a resource in the deployment
     * package is marked as an absent resource but there is no such resource in
     * the target deployment package.
     *
     * @spec 3.6.5.2 InstallAndActivate Command
     */
    private void testInstallAndActivate011() {
    	tbc.log("#testInstallAndActivate011");
        assertResultCode(DeploymentmoConstants.RESOURCE_PROCESSOR_DP,DeploymentmoConstants.SIMPLE_NO_RESOURCE_DP,DeploymentmoConstants.MISSING_RESOURCE_FIX_PACK, 455);
    }
    
    /**
     * This test asserts the result code is 456 if signature authentication failed.
     *
     * @spec 3.6.5.2 InstallAndActivate Command
     */
    private void testInstallAndActivate012() {
        tbc.log("#testInstallAndActivate012");
        assertResultCode(DeploymentmoConstants.SIMPLE_UNSIGNED_DP, 456);
    }
    
    /**
     * This test asserts the result code is 457 if the bundle symbolic name is not 
     * the same as defined by the deployment package manifest.
     *
     * @spec 3.6.5.2 InstallAndActivate Command
     */
    private void testInstallAndActivate013() {
        tbc.log("#testInstallAndActivate013");
        assertResultCode(DeploymentmoConstants.SYMB_NAME_DIFFERENT_FROM_MANIFEST_DP, 457);
    }
    
    /**
     * This test asserts the result code is 458 if the matched resource processor service 
     * is a customizer from another deployment package.
     *
     * @spec 3.6.5.2 InstallAndActivate Command
     */
    private void testInstallAndActivate014() {
        tbc.log("#testInstallAndActivate014");
        assertResultCode(DeploymentmoConstants.RESOURCE_PROCESSOR_CUSTOMIZER, DeploymentmoConstants.RP_FROM_OTHER_DP,458);
    }
    
    /**
     * This test asserts the result code is 459 (no such resource). A resource was passed to a 
     * matched resource processor but the resource processor can not manage this resource.
     *
     * @spec 3.6.5.2 InstallAndActivate Command
     */
    private void testInstallAndActivate015() {
    	tbc.log("#testInstallAndActivate015");
    	DmtSession session = null;
		String nodeId = "";
		String nodeId2 = "";
		try {
			session = tbc.getDmtAdmin().getSession(
					DeploymentmoConstants.PRINCIPAL,
					DeploymentmoConstants.OSGI_ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);

			String[] initialChildren = session
					.getChildNodeNames(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);

			synchronized (tbc) {
				session.execute(DeploymentmoConstants.getDeliveredOperationsInstallAndActivate(
						DeploymentmoConstants.RP_THROWS_NO_SUCH_RESOURCE),null);
				
				tbc.wait(DeploymentmoConstants.TIMEOUT);
			}
			String[] finalChildren = session.getChildNodeNames(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);
			tbc.assertTrue("Asserts that the at Deployed subtree was created",
					initialChildren.length + 1 == finalChildren.length);
			
			nodeId = DeploymentmoTestControl.getNodeId(initialChildren,finalChildren);

			initialChildren = finalChildren;
			synchronized (tbc) {
				session.execute(DeploymentmoConstants.getDeliveredOperationsInstallAndActivate(
						DeploymentmoConstants.DP_THROWS_RESOURCE_VIOLATION),null);
				
				tbc.wait(DeploymentmoConstants.TIMEOUT);
			}
			
			finalChildren = session.getChildNodeNames(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);
			
			tbc.assertTrue("Asserts that the at Deployed subtree was created",
					initialChildren.length + 1 == finalChildren.length);
			nodeId2 = DeploymentmoTestControl.getNodeId(initialChildren,
					finalChildren);

			synchronized (tbc) {
				// Uninstalls a resource associated with the resource processor
				// above, so the rp throws an exception at dropped.
				session.execute(DeploymentmoConstants.getDeployedOperationsRemove(nodeId2), null);
				tbc.wait(DeploymentmoConstants.TIMEOUT);
			}
			tbc.assertAlertValues(DeploymentmoConstants.ALERT_TYPE_DEPLOYED_REMOVE,DeploymentmoConstants.getDeployedNodeId(nodeId2), new DmtData(459));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			if (!nodeId.equals("")) {
				tbc.executeRemoveNode(session, DeploymentmoConstants
						.getDeployedOperationsRemove(nodeId));
			}
			if (!nodeId2.equals("")) {
				tbc.executeRemoveNode(session, DeploymentmoConstants
						.getDeployedOperationsRemove(nodeId2));
			}
			
		}
    }
   

    /**
	 * This test asserts the result code is 460 if a bundle with the same
	 * symbolic name already exists.
	 * 
	 * @spec 3.6.5.2 InstallAndActivate Command
	 */
    private void testInstallAndActivate016() {
    	tbc.log("#testInstallAndActivate016");
        assertResultCode(DeploymentmoConstants.SIMPLE_DP, DeploymentmoConstants.DP_CONTAINING_A_BUNDLE_FROM_OTHER_DP,460);
    }
    
    /**
     * This test asserts the result code is 461 if a side effect of any 
     * resource already exists.
     *
     * @spec 3.6.5.2 InstallAndActivate Command
     */
    private void testInstallAndActivate017() {
    	tbc.log("#testInstallAndActivate017");
        assertResultCode(DeploymentmoConstants.NON_CUSTOMIZER_RP,DeploymentmoConstants.SIMPLE_RESOURCE_DP, DeploymentmoConstants.DP_THROWS_RESOURCE_VIOLATION,461);
    }
    
    /**
     * This test asserts the result code is 462 if resource processor is not able to commit
     *
     * @spec 3.6.5.2 InstallAndActivate Command
     */
    private void testInstallAndActivate018() {
    	tbc.log("#testInstallAndActivate018");
        assertResultCode(DeploymentmoConstants.RP_NOT_ABLE_TO_COMMIT, 462);
    }
    
    /**
     * This test asserts the result code is 464 if the resource processor 
     * required by the package is not found.
     *
     * @spec 3.6.5.2 InstallAndActivate Command
     */
    private synchronized void testInstallAndActivate019() {
        tbc.log("#testInstallAndActivate019");
        assertResultCode(DeploymentmoConstants.SIMPLE_RESOURCE_DP, 464);
    }
    
    /**
     * This test asserts the result code is 465 if the Deployment Manager is
     * busy with other request, blocked deployment operation was not started,
     * because a time out encountered.
     * 
     * @spec 3.6.5.2 InstallAndActivate Command
     */
    private synchronized void testInstallAndActivate020() {
        tbc.log("#testInstallAndActivate020");
        TestingBlockingResourceProcessor testBlockRP = null;
        try {
            TestingArtifact artifact = tbc.getArtifact(DeploymentmoConstants.BLOCK_SESSION_RESOURCE_PROCESSOR);
            TestingDeploymentPackage testDP = artifact.getDeploymentPackage();
            
            worker1 = new SessionWorker(tbc,testDP);
            worker1.start();
            
            int count = 0;
            BundleListenerImpl listener = tbc.getListener();
            while ((count < DeploymentmoConstants.TIMEOUT) &&
                !((listener.getCurrentType() == BundleEvent.STARTED) && 
                (listener.getCurrentBundle().getSymbolicName().indexOf(DeploymentmoConstants.PID_RESOURCE_PROCESSOR3) != -1))) {
                count++;
                wait(1);
            }
            
            testBlockRP = (TestingBlockingResourceProcessor) tbc.getService(
                ResourceProcessor.class, "(service.pid=" + DeploymentmoConstants.PID_RESOURCE_PROCESSOR3 + ")");
            tbc.assertNotNull("Blocking Resource Processor was registered", testBlockRP);
            
            assertResultCode(DeploymentmoConstants.SIMPLE_DP, 465);
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            if (testBlockRP != null) {
                testBlockRP.setReleased(true);
            }
        }
    }
    
    /**
     * Asserts that the Deployed area also reflects deployment artifacts deployed directly via 
     * the Deployment Admin service or Framework API.
     *
     * @spec 3.6.4 Deployed
     */
    private void testInstallAndActivate021() {
    	tbc.log("#testInstallAndActivate021");
        TestingArtifact artifact = tbc.getArtifact(DeploymentmoConstants.SIMPLE_DP);
        TestingDeploymentPackage testDP = artifact.getDeploymentPackage();
		DeploymentPackage dp = null;
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
					DeploymentmoConstants.PRINCIPAL,
					DeploymentmoConstants.OSGI_ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			
			String[] initialChildren = session.getChildNodeNames(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);

			dp = tbc.installDeploymentPackage(DeploymentmoConstants.SERVER + "www/" +testDP.getFilename());
			

            String[] finalChildren= session.getChildNodeNames(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);
            tbc.assertTrue("Asserts that the node at Deployed subtree was created",initialChildren.length+1==finalChildren.length);
            
            String nodeId = DeploymentmoTestControl.getNodeId(initialChildren,finalChildren);
            assertSimpleDpSubtree(session, nodeId);
            
			} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp);
			tbc.closeSession(session);
		}
    }
	
    
	private void assertResultCode(int dpCode,int resultCode) {
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
					DeploymentmoConstants.PRINCIPAL,
					DeploymentmoConstants.OSGI_ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);

			 synchronized (tbc) {
				session.execute(DeploymentmoConstants.getDeliveredOperationsInstallAndActivate(dpCode), null);
                tbc.wait(DeploymentmoConstants.TIMEOUT);
             }

			 tbc.assertAlertValues(DeploymentmoConstants.ALERT_TYPE_INSTALLANDACTIVATE,
				 DeploymentmoConstants.getDeliveredNodeId(dpCode),
                 new DmtData(resultCode));


		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		} finally {
		    tbc.closeSession(session);
			
		}
	}
	
	private void assertResultCode(int firstDpCode,int secondDpCode,int resultCode) {
		DmtSession session = null;
		String nodeId="";
		
        //Backups the artifact and, after the execute method removes, moves it again to the delivered area 
        String archiveName =DeploymentmoConstants.MAP_CODE_TO_ARTIFACT[firstDpCode] +".dp";
        File fileSrc = DeploymentmoTestControl.copyArtifact(archiveName);
        File fileDestiny =DeploymentmoTestControl.getFile(archiveName);
		
		try {
			session = tbc.getDmtAdmin().getSession(
					DeploymentmoConstants.PRINCIPAL,
					DeploymentmoConstants.OSGI_ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);

			String[] initialChildren= session.getChildNodeNames(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);

			 synchronized (tbc) {
				session.execute(DeploymentmoConstants.getDeliveredOperationsInstallAndActivate(firstDpCode), null);
                tbc.wait(DeploymentmoConstants.TIMEOUT);
             }
             String[] finalChildren= session.getChildNodeNames(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);
             tbc.assertTrue("Asserts that the at Deployed subtree was created",initialChildren.length+1==finalChildren.length);
             
             nodeId = DeploymentmoTestControl.getNodeId(initialChildren,finalChildren);
             
			 synchronized (tbc) {
				 session.execute(DeploymentmoConstants.getDeliveredOperationsInstallAndActivate(secondDpCode), null);
                 tbc.wait(DeploymentmoConstants.TIMEOUT);
             }

             tbc.assertAlertValues(DeploymentmoConstants.ALERT_TYPE_INSTALLANDACTIVATE,
                 null,
                 new DmtData(resultCode));


		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		} finally {
			//The first node is successfully installed, so it needs to be cleaned
        	if (!nodeId.equals("")) {
        		tbc.executeRemoveNode(session,DeploymentmoConstants.getDeployedOperationsRemove(nodeId));
        	}
        	DeploymentmoTestControl.renameFileForced(fileSrc, fileDestiny);
		    tbc.closeSession(session);
			
		}
	}
	
	
	private void assertResultCode(int firstDpCode,int secondDpCode,int thirdDpCode,int resultCode) {
		DmtSession session = null;
		String nodeId="";
		
		//Backups the artifact and, after the execute method removes, moves it again to the delivered area 
        String archiveName =DeploymentmoConstants.MAP_CODE_TO_ARTIFACT[firstDpCode] +".dp";
        File fileSrc = DeploymentmoTestControl.copyArtifact(archiveName);
        File fileDestiny =DeploymentmoTestControl.getFile(archiveName);
		
		try {
			session = tbc.getDmtAdmin().getSession(
					DeploymentmoConstants.PRINCIPAL,
					DeploymentmoConstants.OSGI_ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);

			String[] initialChildren= session.getChildNodeNames(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);

			 synchronized (tbc) {
				session.execute(DeploymentmoConstants.getDeliveredOperationsInstallAndActivate(firstDpCode), null);
                tbc.wait(DeploymentmoConstants.TIMEOUT);
             }
             String[] finalChildren= session.getChildNodeNames(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);
             tbc.assertTrue("Asserts that the at Deployed subtree was created",initialChildren.length+1==finalChildren.length);
             
             nodeId = DeploymentmoTestControl.getNodeId(initialChildren,finalChildren);

             assertResultCode(secondDpCode,thirdDpCode,resultCode);
		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		} finally {
        	if (!nodeId.equals("")) {
        		tbc.executeRemoveNode(session,DeploymentmoConstants.getDeployedOperationsRemove(nodeId));
        	}
        	DeploymentmoTestControl.renameFileForced(fileSrc, fileDestiny);
		    tbc.closeSession(session);
			
		}
	}
	
	private void assertSimpleDpSubtree(DmtSession session, String nodeId) {
		try { 
			 //Deployment Package "simple.dp"
			tbc.assertEquals(
							"Asserting the node EnvType",
							DeploymentmoConstants.ENVTYPE,
							session.getNodeValue(DeploymentmoConstants.getDeployedEnvType(nodeId)).toString());
			
			String manifest = session.getNodeValue(DeploymentmoConstants.getDeployedExtManifest(nodeId)).toString().trim();
			boolean passed = true;
			Iterator iterator = DeploymentmoConstants.simpleDpManifest.iterator();
			while (iterator.hasNext()) {
				String element = (String)iterator.next();
				if (manifest.indexOf(element) < 0) {
					passed=false;
				}
			}
			
			tbc.assertTrue("Asserting the content of the Deployment Package manifest",passed);
									
	
			tbc.assertEquals(
							"Asserting the package type",
							DeploymentmoConstants.OSGI_DP,
							session.getNodeValue(DeploymentmoConstants.getDeployedExtPackageType(nodeId)).getInt());
			
			String[] signerChildren = session.getChildNodeNames(DeploymentmoConstants.getDeployedExtSigners(nodeId));
			
			if (signerChildren.length<=0) {
				tbc.fail("./OSGi/Deployment/Inventory/Deployed/[node_id]/Ext/Signers/[signer] does not exist");
			}
			String signer = signerChildren[0];
			
			tbc.assertEquals("Asserting the signer of the deployment package",
					DeploymentmoConstants.SIMPLE_DP_SIGNER,
					session.getNodeValue(DeploymentmoConstants.getDeployedExtSignersSignerId(nodeId, signer)).toString());
	
			
			//Bundle "bundles.tb1"
			Bundle bundle1 = tbc.getBundle(DeploymentmoConstants.SIMPLE_DP_BUNDLE1_SYMBNAME);
			tbc.assertNotNull("The bundle was installed in the framework", bundle1);
			String bundleId = bundle1.getBundleId() + "";
			
			tbc.assertTrue("Asserting that the bundle id is the same as the specified",
					session.isNodeUri(DeploymentmoConstants.getDeployedExtBundlesBundleId(nodeId, bundleId)));
	
			tbc.assertEquals("Asserting bundle's state",
					DeploymentmoConstants.SIMPLE_DP_BUNDLE1_STATE,
					session.getNodeValue(DeploymentmoConstants.getDeployedExtBundlesState(nodeId, bundleId)).getInt());
			
			tbc.assertTrue("Asserting that the manifest of the first bundle is the same as the specified",
					session.getNodeValue(DeploymentmoConstants.getDeployedExtBundlesManifest(nodeId, bundleId)).toString().indexOf(DeploymentmoConstants.SIMPLE_DP_BUNDLE1_MANIFEST) > -1);
			
			tbc.assertEquals("Asserting that the location of the first bundle is the same as the specified",
					DeploymentmoConstants.SIMPLE_DP_BUNDLE1_LOCATION,
					session.getNodeValue(DeploymentmoConstants.getDeployedExtBundlesLocation(nodeId, bundleId)).toString());
	
			//Bundle "bundles.tb2"
			Bundle bundle2 = tbc.getBundle(DeploymentmoConstants.SIMPLE_DP_BUNDLE2_SYMBNAME);
			tbc.assertNotNull("The bundle was installed in the framework", bundle2);
			bundleId = bundle2.getBundleId() + "";
			
			tbc.assertTrue("Asserting that the bundle id is the same as the specified",
					session.isNodeUri(DeploymentmoConstants.getDeployedExtBundlesBundleId(nodeId, bundleId)));
	
			tbc.assertEquals("Asserting bundle's state",
					DeploymentmoConstants.SIMPLE_DP_BUNDLE2_STATE,
					session.getNodeValue(DeploymentmoConstants.getDeployedExtBundlesState(nodeId, bundleId)).getInt());
			
			tbc.assertTrue("Asserting that the manifest of the second bundle is the same as the specified",
					session.getNodeValue(DeploymentmoConstants.getDeployedExtBundlesManifest(nodeId, bundleId)).toString().indexOf(DeploymentmoConstants.SIMPLE_DP_BUNDLE2_MANIFEST) > -1);
			
			tbc.assertEquals("Asserting that the location of the second bundle is the same as the specified",
					DeploymentmoConstants.SIMPLE_DP_BUNDLE2_LOCATION,
					session.getNodeValue(DeploymentmoConstants.getDeployedExtBundlesLocation(nodeId, bundleId)).toString());
			
			String[] children = session.getChildNodeNames(DeploymentmoConstants.getDeployedExtBundles(nodeId));
			
			tbc.assertTrue("Asserting that there is two bundle nodes in DMT",children.length==2);
		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}
}
