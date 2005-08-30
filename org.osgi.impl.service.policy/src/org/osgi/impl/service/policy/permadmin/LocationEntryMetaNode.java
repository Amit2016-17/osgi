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
package org.osgi.impl.service.policy.permadmin;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.MetaNode;

/**
 *
 * Meta node for nodes right under root
 * 
 * @version $Revision$
 */
public final class LocationEntryMetaNode implements MetaNode {
	public boolean can(int operation) { return (operation==CMD_DELETE)||(operation==CMD_ADD)||(operation==CMD_GET); }
	public boolean isLeaf() { return false;	}
	public int getScope() { return DYNAMIC; }
	public String getDescription() { return "permissions for a given location"; }
	public int getMaxOccurrence() {	return Integer.MAX_VALUE; }
	public boolean isZeroOccurrenceAllowed() { return true; }
	public DmtData getDefault() { return null; }
	public double getMax() { return Double.MAX_VALUE; }
	public double getMin() { return Double.MIN_VALUE;	}
	public DmtData[] getValidValues() { return null; }
	public int getFormat() { return DmtData.FORMAT_NODE; }
	public String[] getMimeTypes() { return null; }
	public String getReferredURI() { return null; }
	public String[] getDependentURIs() { return null; }
	public String[] getChildURIs() { return null; }
	public String[] getValidNames() { return null; }
	public boolean isValidValue(DmtData value) { return true; }
	public boolean isValidName(String name) { return true; }
}
