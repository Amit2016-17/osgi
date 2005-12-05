/*
 * ============================================================================
 * (c) Copyright 2004 Nokia
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
package org.osgi.impl.service.deploymentadmin;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.MetaNode;

/**
 * DMT MetaNode implementation
 */
public class Metanode implements MetaNode {
    
    public static final boolean IS_LEAF = true;
    public static final boolean ZERO_OCC = true;

    // 0. bit = CMD_ADD, 1. bit = CMD_DELETE, etc. 
    private int       ops;
    private boolean   isLeaf;
    private int       scope;
    private String    description;
    private int       maxOccurrence;
    private boolean   zeroOccurrenceAllowed;
    private DmtData   def = null;
    private int       min = 0;
    private int       max = 0;
    private DmtData[] validValues;
    private int       format;
    
    public String toString() {
        return "[ Metanode ops: " + ops + 
               ", leaf: " + isLeaf + 
               ", scope: " + scope + 
               ", descr: " + description + 
               ", maxOcc: " + maxOccurrence + 
               ", zeroOcc: " + zeroOccurrenceAllowed + 
               ", def: " + def + 
               ", min: " + min + 
               ", max: " + max + 
               ", validVals: " + validValues + 
               ", format: " + format + "]";
    }
    
    public Metanode(int op,
            		boolean isLeaf,
            		int scope,
    				String description,
    				int maxOccurrence,
    				boolean zeroOccurrenceAllowed,
    				DmtData def,
    				int min,
    				int max,
    				DmtData[] validValues,
    				int format) 
    {
        orOperation(op);
        this.isLeaf = isLeaf;
        this.scope = scope;
        this.description = description;
        this.maxOccurrence = maxOccurrence;
        this.zeroOccurrenceAllowed = zeroOccurrenceAllowed;
        this.def = def;
        this.min = min;
        this.max = max;
        this.validValues = validValues;
        this.format = format;
    }
    
    public boolean can(int operation) {
        int ops = (1 << operation);
        int can = this.ops & ops;
        return can != 0;
    }
    
    public Metanode orOperation(int op) {
        ops = ops | (1 << op);
        return this;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public int getScope() {
        return scope;
    }

    public String getDescription() {
        return description;
    }

    public int getMaxOccurrence() {
        return maxOccurrence;
    }

    public boolean isZeroOccurrenceAllowed() {
        return zeroOccurrenceAllowed;
    }

    public DmtData getDefault() {
        return def;
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }

    public DmtData[] getValidValues() {
        return validValues;
    }

    public String[] getValidNames() {
        // TODO
        return null;
    }

    public int getFormat() {
        return format;
    }

    public String[] getMimeTypes() {
        // TODO
        return null;
    }

    public boolean isValidValue(DmtData value) {
        // TODO
        return true;
    }

    public boolean isValidName(String name) {
        // TODO
        return true;
    }

}
