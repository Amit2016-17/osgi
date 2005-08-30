/*
 * ============================================================================
 * (c) Copyright 2005 Nokia
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Nokia and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */
package org.osgi.impl.service.policy;

import java.util.Date;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.dmt.DmtAdmin;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.spi.TransactionalDataSession;

/**
 *
 * dmt plugin base class for policy
 * 
 * @version $Revision$
 */
public abstract class AbstractPolicyPlugin implements TransactionalDataSession {
	/**
	 * the official root position in the management tree
	 */
	protected String ROOT;

	/**
	 * true, if something is changed, and needs to be written back to the system
	 */
	private boolean dirty;
	
	/**
	 * true, if session was opened in atomic mode
	 */
	boolean atomic;
	
	/**
	 * the dmt admin, needed for the mangle function
	 */
	private DmtAdmin dmtAdmin;
		
	public final String mangle(String nodename) {
		return dmtAdmin.mangle(nodename);
	}
	
	public AbstractPolicyPlugin(ComponentContext context) {
		ROOT = (String) context.getProperties().get("dataRootURIs");
		dmtAdmin = (DmtAdmin) context.locateService("dmtAdmin");
		dirty=false;
	}

	/**
	 * flips the dirty bit
	 * @param nodeUri
	 * @throws DmtException
	 */
	protected final void switchToWriteMode() {
		dirty=true;
	}
		
	/**
	 * 
	 */
	protected final String[] chopPath(String[]path) {
		String []ret = new String[path.length-5];
		System.arraycopy(path,5,ret,0,path.length-5);
		return ret;
	}
	
	protected final boolean isDirty() {
		return dirty;
	}
	
	/*
	 * methods that are not needed anywhere
	 */

	public final void setNodeTitle(String nodeUri[], String title) throws DmtException {
		throw new DmtException(nodeUri,DmtException.FEATURE_NOT_SUPPORTED,"");
	}

	public final void setNodeType(String nodeUri[], String type) throws DmtException {
		throw new DmtException(nodeUri,DmtException.FEATURE_NOT_SUPPORTED,"");
	}

	public void createLeafNode(String nodeUri[], DmtData value,String mimeType)
			throws DmtException {
		throw new DmtException(nodeUri,DmtException.FEATURE_NOT_SUPPORTED,"");
	}

	public final void copy(String nodeUri[], String newNodeUri[], boolean recursive)
			throws DmtException {
		throw new DmtException(nodeUri,DmtException.FEATURE_NOT_SUPPORTED,"");
	}

	public final void renameNode(String nodeUri[], String newName) throws DmtException {
		throw new DmtException(nodeUri,DmtException.FEATURE_NOT_SUPPORTED,"");
	}

	public final String getNodeTitle(String nodeUri[]) throws DmtException {
		throw new DmtException(nodeUri,DmtException.FEATURE_NOT_SUPPORTED,"");
	}

	public final String getNodeType(String nodeUri[]) throws DmtException {
		throw new DmtException(nodeUri,DmtException.FEATURE_NOT_SUPPORTED,"");
	}

	public final int getNodeVersion(String nodeUri[]) throws DmtException {
		throw new DmtException(nodeUri,DmtException.FEATURE_NOT_SUPPORTED,"");
	}

	public final Date getNodeTimestamp(String nodeUri[]) throws DmtException {
		throw new DmtException(nodeUri,DmtException.FEATURE_NOT_SUPPORTED,"");
	}

	public final int getNodeSize(String nodeUri[]) throws DmtException {
		throw new DmtException(nodeUri,DmtException.FEATURE_NOT_SUPPORTED,"");
	}

	public void nodeChanged(String nodeUri[]) throws DmtException {
		throw new DmtException(nodeUri,DmtException.FEATURE_NOT_SUPPORTED,"");
	}

}
