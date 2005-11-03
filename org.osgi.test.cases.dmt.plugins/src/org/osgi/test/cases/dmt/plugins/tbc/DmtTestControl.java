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
 * Jan 21, 2005  Andre Assad
 * 1             Implement MEG TCK
 * ============  ==============================================================
 * Feb 14, 2005  Alexandre Santos
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-002)
 * ============  ==============================================================
 * Mar 02, 2005  Andre Assad
 * 11            Implement DMT Use Cases 
 * ===========   ==============================================================
 * Mar 04, 2005  Alexandre Santos
 * 23            Updates due to changes in the DmtAcl API
 * ===========   ==============================================================
 */

package org.osgi.test.cases.dmt.plugins.tbc;
import org.osgi.framework.ServiceReference;
import org.osgi.service.dmt.DmtAdmin;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.plugins.tbc.DataPluginFactory.TestDataPluginActivator;
import org.osgi.test.cases.dmt.plugins.tbc.ExecPlugin.TestExecPluginActivator;
import org.osgi.test.cases.dmt.plugins.tbc.MetaNode.Can;
import org.osgi.test.cases.dmt.plugins.tbc.MetaNode.DmtMetaNodeConstants;
import org.osgi.test.cases.dmt.plugins.tbc.MetaNode.GetDefault;
import org.osgi.test.cases.dmt.plugins.tbc.MetaNode.GetDescription;
import org.osgi.test.cases.dmt.plugins.tbc.MetaNode.GetFormat;
import org.osgi.test.cases.dmt.plugins.tbc.MetaNode.GetMax;
import org.osgi.test.cases.dmt.plugins.tbc.MetaNode.GetMaxOccurrence;
import org.osgi.test.cases.dmt.plugins.tbc.MetaNode.GetMimeTypes;
import org.osgi.test.cases.dmt.plugins.tbc.MetaNode.GetMin;
import org.osgi.test.cases.dmt.plugins.tbc.MetaNode.GetScope;
import org.osgi.test.cases.dmt.plugins.tbc.MetaNode.GetValidNames;
import org.osgi.test.cases.dmt.plugins.tbc.MetaNode.GetValidValues;
import org.osgi.test.cases.dmt.plugins.tbc.MetaNode.IsLeaf;
import org.osgi.test.cases.dmt.plugins.tbc.MetaNode.IsValidName;
import org.osgi.test.cases.dmt.plugins.tbc.MetaNode.IsValidValue;
import org.osgi.test.cases.dmt.plugins.tbc.MetaNode.IsZeroOccurrenceAllowed;
import org.osgi.test.cases.dmt.plugins.tbc.MetaNode.TestMetaNodeDataPluginActivator;
import org.osgi.test.cases.dmt.plugins.tbc.MetaNode.MetaData.MetaData;
import org.osgi.test.cases.dmt.plugins.tbc.MetaNode.MetaData.TestPluginMetaDataActivator;
import org.osgi.test.cases.dmt.plugins.tbc.Others.OpenSession;
import org.osgi.test.cases.dmt.plugins.tbc.Others.OverlappingPlugins;
import org.osgi.test.cases.dmt.plugins.tbc.Others.UseCases;
import org.osgi.test.cases.dmt.plugins.tbc.Plugins.FatalExceptionDataPluginActivator;
import org.osgi.test.cases.dmt.plugins.tbc.Plugins.NewDataPluginActivator;
import org.osgi.test.cases.dmt.plugins.tbc.Plugins.OverlappingDataPluginActivator;
import org.osgi.test.cases.dmt.plugins.tbc.Plugins.OverlappingExecPluginActivator;
import org.osgi.test.cases.dmt.plugins.tbc.Plugins.OverlappingSubtreeDataPluginActivator;
import org.osgi.test.cases.dmt.plugins.tbc.Plugins.ToBeOverlappedDataPluginActivator;
import org.osgi.test.cases.dmt.plugins.tbc.TreeStructure.Configuration;
import org.osgi.test.cases.dmt.plugins.tbc.TreeStructure.Log;
import org.osgi.test.cases.util.DefaultTestBundleControl;

public class DmtTestControl extends DefaultTestBundleControl {

	private DmtAdmin dmtAdmin;

	private TestDataPluginActivator testDataPluginActivator;
	
	private TestExecPluginActivator testExecPluginActivator;
	
	private TestMetaNodeDataPluginActivator testMetaNodeDataPluginActivator;
	
	private OverlappingDataPluginActivator overlappingDataPluginActivator;
	
	private OverlappingExecPluginActivator overlappingExecPluginActivator;
	
	private OverlappingSubtreeDataPluginActivator overlappingSubtreeDataPluginActivator;
	
	private ToBeOverlappedDataPluginActivator toBeOverlappedDataPluginActivator;
	
	private NewDataPluginActivator newDataPluginActivator;
	
	private FatalExceptionDataPluginActivator fatalExceptionDataPluginActivator;
	
	private TestPluginMetaDataActivator testPluginMetaDataActivator; 
	
	public void prepare() {
		log("#before each run");
		ServiceReference dmtAdminReference = getContext().getServiceReference(DmtAdmin.class.getName());
		dmtAdmin = (DmtAdmin) getContext().getService(dmtAdminReference);
		registerTestPlugins();
		
	}


	public void registerTestPlugins() {
		try {
			testDataPluginActivator = new TestDataPluginActivator(this);
			testDataPluginActivator.start(getContext());
			
			//Tries to register an overlapping Plugin, DmtAdmin must ignore
			overlappingDataPluginActivator = new OverlappingDataPluginActivator();
			overlappingDataPluginActivator.start(getContext()); 

			//Tries to register a plugin that is part of the same subtree that the plugin above controls, DmtAdmin must ignore
			overlappingSubtreeDataPluginActivator = new OverlappingSubtreeDataPluginActivator();
			overlappingSubtreeDataPluginActivator.start(getContext());

			//Registers a DataPlugin to be overlapped by the ExecPlugin below
			toBeOverlappedDataPluginActivator = new ToBeOverlappedDataPluginActivator();
			toBeOverlappedDataPluginActivator.start(getContext());
			
			//Registers a ExecPlugin that overlaps the DataPlugin above 
			testExecPluginActivator = new TestExecPluginActivator(this);
			testExecPluginActivator.start(getContext());	

			//Tries to register an overlapping ExecPlugin, DmtAdmin must ignore
			overlappingExecPluginActivator = new OverlappingExecPluginActivator();
			overlappingExecPluginActivator.start(getContext());	

			//----------------------------------------------------------------------------------//
			//Plugin to the MetaNode tests
			testMetaNodeDataPluginActivator = new TestMetaNodeDataPluginActivator(this);
			testMetaNodeDataPluginActivator.start(getContext());

			//Plugin to metadata tests
			testPluginMetaDataActivator = new TestPluginMetaDataActivator(this);
			testPluginMetaDataActivator.start(getContext());
			//----------------------------------------------------------------------------------//
			
			//Plugin that throws a fatal exception
			fatalExceptionDataPluginActivator = new FatalExceptionDataPluginActivator(this);
			fatalExceptionDataPluginActivator.start(getContext());
			
			newDataPluginActivator = new NewDataPluginActivator(this);
			newDataPluginActivator.start(getContext());		
			

		} catch (Exception e) {
			log("#TestControl: Fail to register a TestPlugin");
		}
	}
	
	public void setState() {
		log("#before each method");
	}
	
	//OverlappingPlugins
	public void testOverlappingPlugins() {
		new OverlappingPlugins(this).run();
	}
	//DataPluginFactory methods
	public void testDataPluginFactoryConstants() {
		new org.osgi.test.cases.dmt.plugins.tbc.DataPluginFactory.TransactionalDataSession.PluginConstants(this).run();
	}
	public void testDataPluginFactoryClose() {
		new org.osgi.test.cases.dmt.plugins.tbc.DataPluginFactory.TransactionalDataSession.Close(this).run();
	}
	public void testDataPluginFactoryGetChildNodeNames() {
		new org.osgi.test.cases.dmt.plugins.tbc.DataPluginFactory.TransactionalDataSession.GetChildNodeNames(this).run();
	}

	public void testDataPluginFactoryGetMetaNode() {
		new org.osgi.test.cases.dmt.plugins.tbc.DataPluginFactory.TransactionalDataSession.GetMetaNode(this).run();
	}

	public void testDataPluginFactoryGetNodeSize() {
		new org.osgi.test.cases.dmt.plugins.tbc.DataPluginFactory.TransactionalDataSession.GetNodeSize(this).run();
	}

	public void testDataPluginFactoryGetNodeTimestamp() {
		new org.osgi.test.cases.dmt.plugins.tbc.DataPluginFactory.TransactionalDataSession.GetNodeTimestamp(this).run();
	}

	public void testDataPluginFactoryGetNodeTitle() {
		new org.osgi.test.cases.dmt.plugins.tbc.DataPluginFactory.TransactionalDataSession.GetNodeTitle(this).run();
	}

	public void testDataPluginFactoryGetNodeType() {
		new org.osgi.test.cases.dmt.plugins.tbc.DataPluginFactory.TransactionalDataSession.GetNodeType(this).run();
	}

	public void testDataPluginFactoryGetNodeValue() {
		new org.osgi.test.cases.dmt.plugins.tbc.DataPluginFactory.TransactionalDataSession.GetNodeValue(this).run();
	}

	public void testDataPluginFactoryGetNodeVersion() {
		new org.osgi.test.cases.dmt.plugins.tbc.DataPluginFactory.TransactionalDataSession.GetNodeVersion(this).run();
	}

	public void testDataPluginFactoryIsNodeUri() {
		new org.osgi.test.cases.dmt.plugins.tbc.DataPluginFactory.TransactionalDataSession.IsNodeUri(this).run();
	}

	public void testDataPluginFactoryIsLeafNode() {
		new org.osgi.test.cases.dmt.plugins.tbc.DataPluginFactory.TransactionalDataSession.IsLeafNode(this).run();
	}
	public void testDataPluginFactoryNodeChanged() {
		new org.osgi.test.cases.dmt.plugins.tbc.DataPluginFactory.TransactionalDataSession.NodeChanged(this).run();
	}	
	
	public void testDataPluginFactoryCommit() { 
		new org.osgi.test.cases.dmt.plugins.tbc.DataPluginFactory.TransactionalDataSession.Commit(this).run();
	}
	
	public void testDataPluginFactoryCopy() {
		new org.osgi.test.cases.dmt.plugins.tbc.DataPluginFactory.TransactionalDataSession.Copy(this).run();
	}
	
	public void testDataPluginFactoryCreateInteriorNode() {
		new org.osgi.test.cases.dmt.plugins.tbc.DataPluginFactory.TransactionalDataSession.CreateInteriorNode(this).run();
	}
	
	public void testDataPluginFactoryCreateLeafNode() {
		new org.osgi.test.cases.dmt.plugins.tbc.DataPluginFactory.TransactionalDataSession.CreateLeafNode(this).run();
	}
	
	public void testDataPluginFactoryDeleteNode() {
		new org.osgi.test.cases.dmt.plugins.tbc.DataPluginFactory.TransactionalDataSession.DeleteNode(this).run();
	}

	public void testDataPluginFactoryRenameNode() {
		new org.osgi.test.cases.dmt.plugins.tbc.DataPluginFactory.TransactionalDataSession.RenameNode(this).run();
	}

	public void testDataPluginFactoryRollback() {
		new org.osgi.test.cases.dmt.plugins.tbc.DataPluginFactory.TransactionalDataSession.Rollback(this).run();
	}

	public void testDataPluginFactorySetNodeTitle() {
		new org.osgi.test.cases.dmt.plugins.tbc.DataPluginFactory.TransactionalDataSession.SetNodeTitle(this).run();
	}
	
	public void testDataPluginFactorySetNodeType() {
		new org.osgi.test.cases.dmt.plugins.tbc.DataPluginFactory.TransactionalDataSession.SetNodeType(this).run();
	}

	public void testDataPluginFactorySetNodeValue() {
		new org.osgi.test.cases.dmt.plugins.tbc.DataPluginFactory.TransactionalDataSession.SetNodeValue(this).run();
	}
	
	
	//ExecPlugin Method
	public void testExecPluginExecute() {
		new org.osgi.test.cases.dmt.plugins.tbc.ExecPlugin.Execute(this).run();
	}
	
	
	// DmtMetaNode
	public void testDmtMetaNodeConstants() {
		new DmtMetaNodeConstants(this).run();
	}
	
	public void testDmtMetaNodeCan() {
		new Can(this).run();
	}

	public void testDmtMetaNodeGetDefault() {
		new GetDefault(this).run();
	}

	public void testDmtMetaNodeGetDescription() {
		new GetDescription(this).run();
	}

	public void testDmtMetaNodeGetFormat() {
		new GetFormat(this).run();
	}

	public void testDmtMetaNodeGetMax() {
		new GetMax(this).run();
	}

	public void testDmtMetaNodeGetMaxOccurrence() {
		new GetMaxOccurrence(this).run();
	}

	public void testDmtMetaNodeGetMimeTypes() {
		new GetMimeTypes(this).run();
	}

	public void testDmtMetaNodeGetMin() {
		new GetMin(this).run();
	}

	public void testDmtMetaNodeisValidName() {
		new IsValidName(this).run();
	}

	public void testDmtMetaNodeisValidValue() {
		new IsValidValue(this).run();
	}

	public void testDmtMetaNodeGetValidValues() {
		new GetValidValues(this).run();
	}

	public void testDmtMetaNodeGetValidNames() {
		new GetValidNames(this).run();
	}

	public void testDmtMetaNodeIsLeaf() {
		new IsLeaf(this).run();
	}

	public void testDmtMetaNodeGetScope() {
		new GetScope(this).run();
	}

	public void testDmtMetaNodeIsZeroOccurrenceAllowed() {
		new IsZeroOccurrenceAllowed(this).run();
	}
	
	//TreeStructure test cases
	public void testTreeStructureLog() {
		new Log(this).run();
	}

	public void testTreeStructureConfiguration() {
		new Configuration(this).run();
	}
	
	//Use cases test cases
	public void testUseCases() {
		new UseCases(this).run();
	}

	//Meta data test cases
	public void testMetaData() {
		new MetaData(this).run();
	}
	public void testConstraints() {
        //TODO Remove when test framework timeout is bigger than session creation timeout
//	    new Constraints(this).run();
	}		
    public void testOpenSession() {
        new OpenSession(this).run();
    }       
	/**
	 * Clean up after each method. Notice that during debugging many times the
	 * unsetState is never reached.
	 */
	public void unsetState() {
		log("#after each method");
	}

	/**
	 * Clean up after a run. Notice that during debugging many times the
	 * unprepare is never reached.
	 */
	public void unprepare() {
		log("#after each run");
	}

	/**
	 * @return Returns the factory.
	 */
	public DmtAdmin getDmtAdmin() {
		if (dmtAdmin != null)
			return dmtAdmin;
		else
			throw new NullPointerException("DmtAdmin factory is null");
	}

	/**
	 * It deletes all the nodes created during the execution of the test. It
	 * receives a String array containing all the node URIs.
	 */
	public void cleanUp(DmtSession session,String[] nodeUri) {
		if (session != null && session.getState() == DmtSession.STATE_OPEN) {
			if (nodeUri == null) {
				closeSession(session);
			} else {
				for (int i = 0; i < nodeUri.length; i++) {
					try {
						session.deleteNode(nodeUri[i]);
					} catch (Throwable e) {
						log("#Exception at cleanUp: "+e.getClass().getName() + " [Message: " +e.getMessage() +"]");
					}
				}
				closeSession(session);
			}
		}
	}
	public void cleanUp(DmtSession session,boolean cleanTemporary) {
	    closeSession(session);
		if (cleanTemporary) {
		    DmtConstants.TEMPORARY="";
		    DmtConstants.PARAMETER_2="";
		    DmtConstants.PARAMETER_3="";
	    }

	    
	}
	public void closeSession(DmtSession session) {
		if (null != session) {
			if (session.getState() == DmtSession.STATE_OPEN) {
				try {
					session.close();
				} catch (DmtException e) {
					log("#Exception closing the session: "+e.getClass().getName() + ". Message: [" +e.getMessage() +"]");
				}
			}
		}
	}	

	public FatalExceptionDataPluginActivator getFatalExceptionDataPluginActivator() {
		return fatalExceptionDataPluginActivator;
	}

	public String mangleUri(String[] nodeUri) {
		StringBuffer nodeNameBuffer= new StringBuffer();
		String nodeName="";
		if (nodeUri.length>0) {
			for (int i=0;i<nodeUri.length;i++) {
				nodeNameBuffer = nodeNameBuffer.append(getDmtAdmin().mangle(nodeUri[i]) + "/");
			}
			nodeName = nodeNameBuffer.substring(0,nodeNameBuffer.length()-1);
		}
		
		return nodeName;
	}

	public void cleanAcl(String nodeUri) {
		DmtSession session = null;
		try {
			session = getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeAcl(nodeUri,null);
		} catch (Exception e) {
			log("#Exception cleaning the acl from "+ nodeUri +" : "+e.getClass().getName() + "Message: [" +e.getMessage() +"]");
		} finally {
			closeSession(session);
		}
		
	}
	

}
