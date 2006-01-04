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
 */

/* 
 * REVISION HISTORY:
 *
 * Date          Author(s)
 * CR            Headline
 * ============  ==============================================================
 * Feb 25, 2005  Andre Assad
 * 11            Implement DMT Use Cases 
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc2.tbc.Plugin.ExecPlugin;

import java.util.Date;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.MetaNode;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.service.dmt.spi.ExecPlugin;
import org.osgi.service.dmt.spi.ReadWriteDataSession;
import org.osgi.service.dmt.spi.ReadableDataSession;
import org.osgi.service.dmt.spi.TransactionalDataSession;
import org.osgi.test.cases.dmt.tc2.tbc.DmtTestControl;

/**
 * @author Andre Assad
 * 
 * A test implementation of a Plugin. This implementation validates the
 * DmtSession calls to a subtree handled by Plugin.
 * 
 */
public class TestExecPlugin implements DataPlugin, ExecPlugin, TransactionalDataSession {

	private DmtTestControl tbc;
	
    private static int createInteriorNodeCount;
    
    private static int createLeafNodeCount; 
    
    private static String nodeTitle;
    
    private static boolean exceptionAtCreateInteriorNode;
    
	private static boolean allUriIsExistent = false;

	public TestExecPlugin(DmtTestControl tbc) {
		this.tbc = tbc;

	}

	
	public ReadableDataSession openReadOnlySession(String[] sessionRoot, DmtSession session) throws DmtException {
		return this;
	}

	public ReadWriteDataSession openReadWriteSession(String[] sessionRoot, DmtSession session) throws DmtException {
	    return this;
	}

	public TransactionalDataSession openAtomicSession(String[] sessionRoot, DmtSession session) throws DmtException {
	    return this;
	}

	public void execute(DmtSession session, String[] nodePath, String correlator, String data) throws DmtException {
		
	}

	public void setNodeTitle(String[] nodeUri, String title) throws DmtException {
	}

	public void setNodeValue(String[] nodeUri, DmtData data) throws DmtException {
	}

	public void setDefaultNodeValue(String[] nodeUri) throws DmtException {
	}

	public void setNodeType(String[] nodeUri, String type) throws DmtException {

	}

	public void deleteNode(String[] nodeUri) throws DmtException {

	}

	public void createInteriorNode(String[] nodeUri, String type)
			throws DmtException {
        createInteriorNodeCount++;
        if (exceptionAtCreateInteriorNode) {
            throw new IllegalStateException();
        }
	}

	public void createLeafNode(String[] nodeUri, DmtData value, String mimeType)
			throws DmtException {
        createLeafNodeCount++;
	}

	public void copy(String[] nodeUri, String[] newNodeUri, boolean recursive)
			throws DmtException {

	}

	public void renameNode(String[] nodeUri, String newName) throws DmtException {

	}
	public void close() throws DmtException {

	}

	public boolean isNodeUri(String[] nodeUri) {
		String nodeName = tbc.mangleUri(nodeUri);
		if (allUriIsExistent 
		        || nodeName.equals(TestExecPluginActivator.ROOT)
				|| nodeName.equals(TestExecPluginActivator.INTERIOR_NODE)
				|| nodeName.equals(TestExecPluginActivator.INTERIOR_NODE_COPY)
				|| nodeName.equals(TestExecPluginActivator.INTERIOR_NODE2)
				|| nodeName.equals(TestExecPluginActivator.CHILD_INTERIOR_NODE)
				|| nodeName.equals(TestExecPluginActivator.LEAF_NODE)
				|| nodeName.equals(TestExecPluginActivator.INTERIOR_NODE_WITH_NULL_VALUES)) {

			return true;
		} else {
			return false;
		}
	}

	public DmtData getNodeValue(String[] nodeUri) throws DmtException {
		return null;
	}

	public String getNodeTitle(String[] nodeUri) throws DmtException {
		return nodeTitle;
	}

	public String getNodeType(String[] nodeUri) throws DmtException {
		return null;
	}

	public int getNodeVersion(String[] nodeUri) throws DmtException {
		return 0;
	}

	public Date getNodeTimestamp(String[] nodeUri) throws DmtException {
		return null;
	}

	public int getNodeSize(String[] nodeUri) throws DmtException {
		return 0;
	}

	public String[] getChildNodeNames(String[] nodeUri) throws DmtException {
		String nodeName = tbc.mangleUri(nodeUri);
		if (nodeName.equals(TestExecPluginActivator.INTERIOR_NODE_WITH_NULL_VALUES)) {
			return new String[] { TestExecPluginActivator.INTERIOR_NODE, null,
					TestExecPluginActivator.INTERIOR_NODE2, null };
		} else {
			return null;
		}

	}

	public MetaNode getMetaNode(String[] nodeUri) throws DmtException {
		String nodeName = tbc.mangleUri(nodeUri);
		if (nodeName.equals(TestExecPluginActivator.LEAF_NODE)
				|| nodeName.equals(TestExecPluginActivator.INEXISTENT_LEAF_NODE)
                | nodeName.equals(TestExecPluginActivator.INEXISTENT_INTERIOR_AND_LEAF_NODES)) {
			return new TestMetaNode(DmtData.FORMAT_INTEGER
					| DmtData.FORMAT_STRING,true);
		} else 	if (nodeName.equals(TestExecPluginActivator.INTERIOR_NODE_COPY)
				|| nodeName.equals(TestExecPluginActivator.INTERIOR_NODE2_COPY)) {
		    return new TestMetaNode(false);
		} else {
			return new TestMetaNode(true);
		}
	}

	
	public boolean isLeafNode(String[] nodeUri) throws DmtException {
		String nodeName = tbc.mangleUri(nodeUri);
		if (nodeName.equals(TestExecPluginActivator.LEAF_NODE)) {
			return true;
		} else {
			return false;
		}
	}

	public void nodeChanged(String[] nodeUri) throws DmtException {

	}

	public void commit() throws DmtException {
		
	}

	public void rollback() throws DmtException {
		
	}	

    public static void setAllUriIsExistent(boolean allUriIsExistent) {
        TestExecPlugin.allUriIsExistent = allUriIsExistent;
    }
    
    public static void resetCount() {
        createInteriorNodeCount=0;
        createLeafNodeCount=0;
    }

    public static int getCreateInteriorNodeCount() {
        return createInteriorNodeCount;
    }

    public static int getCreateLeafNodeCount() {
        return createLeafNodeCount;
    }

    public static void setExceptionAtCreateInteriorNode(
            boolean exceptionAtCreateInteriorNode) {
        TestExecPlugin.exceptionAtCreateInteriorNode = exceptionAtCreateInteriorNode;
    }

    public static void setDefaultNodeTitle(String nodeUri) {
        nodeTitle = nodeUri;
    }
    
}
