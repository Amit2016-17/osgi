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
 * Abr 14, 2005  Luiz Felipe Guimaraes
 * 1			 Implement Meg TCK
 * ============  ==============================================================
 */
package org.osgi.test.cases.dmt.main.tbc.Plugin;

import org.osgi.service.dmt.*;

public class TestPluginMetaNode implements DmtMetaNode {

	private boolean	 isLeaf;
    private int      format;

    // Interior nodes
    public TestPluginMetaNode() {
        this.isLeaf        = false;
        this.format        = DmtData.FORMAT_NODE;
    }
    
    // Leaf nodes
	public TestPluginMetaNode(int format) {
        this.isLeaf        = true;
        this.format        = format;
	}

	/* ---------------------------------------------------------------------- */

    public boolean can(int operation) {
        return true;
    }       	

    public boolean isLeaf() {
		return isLeaf;
	}

	public int getScope() {
		return DYNAMIC;
	}

	public String getDescription() {
		return "";
	}

	public int getMaxOccurrence() {
		return Integer.MAX_VALUE;
	}

	public boolean isZeroOccurrenceAllowed() {
		return true;
	}

	public DmtData getDefault() {
		return new DmtData("test");
	}

	public int getMax() {
		return Integer.MAX_VALUE;
	}

	public int getMin() {
		return Integer.MIN_VALUE;
	}

    public String[] getValidNames() {
        return null;
    }
    
	public DmtData[] getValidValues() {
		return null;
	}

	public int getFormat() {
		return format;
	}

    public String getNamePattern() {
        return null;
    }
    
	public String getPattern() {
		return null;
	}

	public String[] getMimeTypes() {
		return null;
	}

	public boolean isValidValue(DmtData value) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isValidName(String name) {
		// TODO Auto-generated method stub
		return false;
	}
}
