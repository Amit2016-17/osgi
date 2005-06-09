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
package org.osgi.meg.demo.remote.gui;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.tree.TreeNode;

import org.osgi.meg.demo.remote.*;

public class TreeNodeImpl implements TreeNode {

    private String       label;
    private TreeNodeImpl parent;
    private Commander    commander;
    
    public TreeNodeImpl(String label, TreeNodeImpl parent, Commander commander) {
        this.label = label;
        this.parent = parent;
        this.commander = commander;
    }
    
    public boolean equals(Object o) {
    	if (!(o instanceof TreeNodeImpl))
            return false;
        TreeNodeImpl other = (TreeNodeImpl) o;
        return uri().equals(other.uri());
    }
    
    public int hashCode() {
    	return uri().hashCode();
    }
    
    public String getLabel() {
    	return label;
    }
    
    public String toString() {
    	return label;
    }
    
    private String uri = null;
    
    public String uri() {
        if (null != uri)
            return uri;
        
        if (null == parent) {
            uri = label;
            return uri;
        }
        uri = parent.uri() + "/" + label;
        
        return uri;
    }
        
    ///////////////////////////////////////////////////////////////////////
    
	public int getChildCount() {
        String[] sarr;
		try {
			sarr = Splitter.split(commander.command("gc " + uri()).trim(), '/', 0);
		} catch (CommanderException e) {
            // TODO
			e.printStackTrace();
            return 0;
		}
		//System.out.println(">>>getChildCount> " + uri() + " " + sarr.length);
		return sarr.length;
	}

	public boolean getAllowsChildren() {
        boolean ret;
		try {
			ret = "true".equals(commander.command("il " + uri()).trim());
		} catch (CommanderException e) {
			// TODO
			e.printStackTrace();
            return false;
		}
		//System.out.println(">>>getAllowsChildren> " + uri() + " " + ret);
        return ret;
	}

	public boolean isLeaf() {
        boolean ret;
		try {
			ret = "true".equals(commander.command("il " + uri()).trim());
		} catch (CommanderException e) {
			// TODO
			e.printStackTrace();
            return false;
		}
		//System.out.println(">>>isLeaf> " + uri() + " " + ret);
		return ret;
	}

	public Enumeration children() {
		String s;
		try {
			s = commander.command("gc " + uri()).trim();
		} catch (CommanderException e) {
			// TODO
			e.printStackTrace();
            return new Vector().elements();
		}
		String[] sarr = Splitter.split(s, '/', 0);
        Arrays.sort(sarr);
        Vector v = new Vector();
        for (int i = 0; i < sarr.length; ++i) {
        	v.add(new TreeNodeImpl(sarr[i], this, commander));   
        }
        //System.out.println(">>>children> " + uri() + " " + v);
        return v.elements();
	}

	public TreeNode getParent() {
        //System.out.println(">>>getParent> " + uri() + " " + parent);
		return parent;
	}

	public TreeNode getChildAt(int childIndex) {
        String s;
		try {
			s = commander.command("gc " + uri()).trim();
		} catch (CommanderException e) {
			// TODO
			e.printStackTrace();
            return null;
		}
		String[] sarr = Splitter.split(s, '/', 0);
        Arrays.sort(sarr);
        if (childIndex >= sarr.length)
        	return null;
        // TODO factory pattern
        TreeNodeImpl ret = new TreeNodeImpl(sarr[childIndex], this, commander);
        //System.out.println(">>>getChildAt> " + uri() + " index: " + childIndex + " " + ret);        
        return ret;
	}

	public int getIndex(TreeNode node) {
		String s;
		try {
			s = commander.command("gc " + uri()).trim();
		} catch (CommanderException e) {
			// TODO
			e.printStackTrace();
            return -1;
		}
		String[] sarr = Splitter.split(s, '/', 0);
        Arrays.sort(sarr);
        int ret = -1;
        for (int i = 0; i < sarr.length; ++i) {
        	if (((TreeNodeImpl) node).getLabel().equals(sarr[i])) {
                ret = i;
                break;
            }
        }
        //System.out.println(">>>getIndex> uri:" + uri() + " node: " + node + " " + ret);
        return ret;
	}

	///////////////////////////////////////////////////////////////////////////
    
	public String getNodeValue() throws CommanderException {
        if (isLeaf())
        	return commander.command("gv " + uri());
        else
            return "N/A";
	}

	public String getNodeAcl() throws CommanderException {
        return commander.command("ga " + uri());
	}
    
    public String getEffectiveNodeAcl() throws CommanderException {
        return commander.command("gea " + uri());
    }

	public String getMetaNode() throws CommanderException {
        return commander.command("gm " + uri());
	}

}
