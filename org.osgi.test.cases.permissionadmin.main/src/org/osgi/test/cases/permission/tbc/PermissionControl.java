/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2000-2001).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.test.cases.permission.tbc;

import java.io.*;

import org.osgi.framework.*;
import org.osgi.service.permissionadmin.*;
import org.osgi.test.cases.util.*;

public class PermissionControl extends DefaultTestBundleControl {

    PermissionAdmin permissionAdmin;
    PermissionInfo[] defaultPermissions;

/*    public String[] getMethods() {
        return new String[] {
            "testPermissionInfo"
        };
    }
*/

    public boolean checkPrerequisites() {
        return serviceAvailable(PermissionAdmin.class) &&
               securityNeeded(true);
    }

    public void prepare() throws Exception {
        /* Get the PermissionAdmin service */
        permissionAdmin = (PermissionAdmin) getRegistry().getService(PermissionAdmin.class);
        
        /* Give this bundle all permissions */
		permissionAdmin.setPermissions((getContext().getBundle()).getLocation(), 
		    new PermissionInfo[] { new PermissionInfo("java.security.AllPermission", "", "") } );				
    }

    public void unprepare() throws Exception {
    }

    public void setState() throws Exception {
    }

    public void clearState() throws Exception {
    }

    /*****  Test methods  *****/

    public void testBasicPermissionInfo() throws Exception {

        /* Construct a permissioninfo from a permission class */
        PermissionInfo permInfo = new PermissionInfo(FilePermission.class.getName(),
                "<<ALL FILES>>", "read");

        assertEquals("Actions", "read", permInfo.getActions());
        assertEquals("Name", "<<ALL FILES>>", permInfo.getName());
        assertEquals("Type", "java.io.FilePermission", permInfo.getType());
        
        /* Check the output of getEncoded() */
        assertEquals("getEncoded", "(java.io.FilePermission \"<<ALL FILES>>\" \"read\")", permInfo.getEncoded());
        
        /* toString() should be identical with getEncoded() */
        assertEquals("toString", permInfo.getEncoded(), permInfo.toString());

    }

    public void testEqual() throws Exception {
        /* Construct an identical permissionInfo and check equal */
        PermissionInfo permInfo = new PermissionInfo(FilePermission.class.getName(),
                "<<ALL FILES>>", "read");

        PermissionInfo identicalPermInfo = new PermissionInfo(
                FilePermission.class.getName(), "<<ALL FILES>>", "read");

        assertEquals("identical", permInfo, identicalPermInfo);
        assertEquals("identical hashcodes", permInfo.hashCode(), identicalPermInfo.hashCode());

        /* Construct another permissionInfo, not identical and check equal */
        PermissionInfo unIdenticalPermInfo = new PermissionInfo(
                FilePermission.class.getName(), "<<ALL FILES>>", "write");

        /* QUESTION: Is this test relevant? Should there be an assert for 
         notEqual? */
        assertTrue("not identical", !permInfo.equals(unIdenticalPermInfo));
    }

    public void testEncodedPermission() throws Exception {
        /* Check if permissioninfos created from encoded are identical to the original */
        PermissionInfo permInfo = new PermissionInfo(FilePermission.class.getName(),
                "<<ALL FILES>>", "read");

        String stringEncodedPermInfo = permInfo.getEncoded();

        PermissionInfo stringPermInfo = new PermissionInfo(stringEncodedPermInfo);

        /* Construct a normal permission info */
        PermissionInfo identicalPermInfo = new PermissionInfo(
                FilePermission.class.getName(), "<<ALL FILES>>", "read");

        assertEquals("constructed from a string",
            stringPermInfo, identicalPermInfo);

        assertEquals("toString", stringEncodedPermInfo, permInfo.toString());
    }

    /**
     * Tests various illegal ways to construct PermissionInfo objects with
     * an encoded string. WARNING! Many of these tests checks things
     * that are not well defined in the current spec!
     */
    public void testExceptionalPermissionInfo() throws Exception {
        
        /* !!! This is not explicitly specified in the spec */
        createBadPermissionInfo("null argument", 
            null,
            NullPointerException.class);

        createBadPermissionInfo("Empty argument", 
            "",
            IllegalArgumentException.class);

        createBadPermissionInfo("Only parenthesis", 
            "()",
            IllegalArgumentException.class);

        createBadPermissionInfo("Missing open parenthesis", 
            "java.io.FilePermission \"<<ALL FILES>>\" \"read\")",
            IllegalArgumentException.class);

        createBadPermissionInfo("Missing closing parentheses", 
            "(java.io.FilePermission \"<<ALL FILES>>\" \"read\"",
            IllegalArgumentException.class);

        createBadPermissionInfo("Quotes on type", 
            "\"java.io.FilePermission\" \"<<ALL FILES>>\" \"read\")",
            IllegalArgumentException.class);

        createBadPermissionInfo("Open quote on type", 
            "\"java.io.FilePermission \"<<ALL FILES>>\" \"read\")",
            IllegalArgumentException.class);

        createBadPermissionInfo("Closing quote on type", 
            "java.io.FilePermission\" \"<<ALL FILES>>\" \"read\")",
            IllegalArgumentException.class);

        createBadPermissionInfo("Missing open name quote", 
            "(java.io.FilePermission <<ALL FILES>>\" \"read\")",
            IllegalArgumentException.class);

        createBadPermissionInfo("Missing close name quote", 
            "(java.io.FilePermission \"<<ALL FILES>> \"read\")",
            IllegalArgumentException.class);

        createBadPermissionInfo("Missing open action quote", 
            "(java.io.FilePermission \"<<ALL FILES>>\" read\")",
            IllegalArgumentException.class);

        createBadPermissionInfo("Missing close action quote", 
            "(java.io.FilePermission \"<<ALL FILES>>\" \"read)",
            IllegalArgumentException.class);

        createBadPermissionInfo("Too many arguments", 
            "(java.io.FilePermission \"<<ALL FILES>>\" \"read\" \"write\")",
            IllegalArgumentException.class);

        /* This is problematic. The comma seems to be associated with the type */
//        createBadPermissionInfo("Comma separation between type and name", 
//            "(java.io.FilePermission, \"<<ALL FILES>>\" \"read\")",
//            IllegalArgumentException.class);

        createBadPermissionInfo("Comma separation between name and action", 
            "(java.io.FilePermission \"<<ALL FILES>>\", \"read\")",
            IllegalArgumentException.class);

        createBadPermissionInfo("Too much whitespace between type and name", 
            "(java.io.FilePermission  \"<<ALL FILES>>\" \"read\")",
            IllegalArgumentException.class);

        createBadPermissionInfo("Too much whitespace between name and action", 
            "(java.io.FilePermission \"<<ALL FILES>>\"  \"read\")",
            IllegalArgumentException.class);

        createBadPermissionInfo("Leading whitespace", 
            " (java.io.FilePermission \"<<ALL FILES>>\" \"read\")",
            IllegalArgumentException.class);

        createBadPermissionInfo("Whitespace after opening parenthesis", 
            "( java.io.FilePermission \"<<ALL FILES>>\" \"read\")",
            IllegalArgumentException.class);

        createBadPermissionInfo("Trailing whitespace", 
            "(java.io.FilePermission \"<<ALL FILES>>\" \"read\") ",
            IllegalArgumentException.class);

        createBadPermissionInfo("Whitespace before closing parenthesis", 
            "(java.io.FilePermission \"<<ALL FILES>>\" \"read\" )",
            IllegalArgumentException.class);
    }

    /**
     * Tests various variations of the arguments when constructing 
     * PermissionInfo objects. WARNING! Many of these tests checks things
     * that are not well defined in the current spec!
     */
    public void testPermissionInfo() throws Exception {
        PermissionInfo info = null;
 
        /* Test constructor with null type */
        createBadPermissionInfo("null arguments", null, null, null, 
                                NullPointerException.class);
        createBadPermissionInfo("null arguments", null, "<<ALL FILES>>", null, 
                                NullPointerException.class);
        createBadPermissionInfo("null arguments", null, "<<ALL FILES>>", "read", 
                                NullPointerException.class);
        createBadPermissionInfo("null arguments", null, null, "read", 
                                NullPointerException.class);
        
        /* Test constructor with null name and actions arguments */
        info = new PermissionInfo("java.io.FilePermission", null, null);
        assertEquals("type", "java.io.FilePermission", info.getType());
        assertNull("no name", info.getName());
        assertNull("no actions", info.getActions());
        assertEquals("encoded", "(java.io.FilePermission)", info.getEncoded());
        assertEquals("toString", "(java.io.FilePermission)", info.toString());
        pass("hashcode " + info.hashCode());

        /* Test constructor with null actions argument */
        info = new PermissionInfo("java.io.FilePermission", "<<ALL FILES>>", null);
        assertEquals("type", "java.io.FilePermission", info.getType());
        assertEquals("name", "<<ALL FILES>>", info.getName());
        assertNull("no actions", info.getActions());
        assertEquals("encoded", "(java.io.FilePermission \"<<ALL FILES>>\")", info.getEncoded());
        assertEquals("toString", "(java.io.FilePermission \"<<ALL FILES>>\")", info.toString());
        pass("hashcode " + info.hashCode());

        /* Test constructor with missing name argument */
        createBadPermissionInfo("missing name", "java.io.FilePermission", null, "read", 
                                IllegalArgumentException.class);

        /* Test constructor with no null arguments */
        info = new PermissionInfo("java.io.FilePermission", "<<ALL FILES>>", "read");
        assertEquals("type", "java.io.FilePermission", info.getType());
        assertEquals("name", "<<ALL FILES>>", info.getName());
        assertEquals("actions", "read", info.getActions());
        assertEquals("encoded", "(java.io.FilePermission \"<<ALL FILES>>\" \"read\")", info.getEncoded());
        assertEquals("toString", "(java.io.FilePermission \"<<ALL FILES>>\" \"read\")", info.toString());
        pass("hashcode " + info.hashCode());
    }
    

    /**
     * Makes sure that Bundle 0 (system bundle) is the one that registered
     * the PermissionAdmin service. 
     */
    public void testBundleZero() throws Exception {
        ServiceReference serviceRef = getContext().getServiceReference(
                PermissionAdmin.class.getName());
        Bundle bundle = serviceRef.getBundle();

        assertEquals("Bundle that registered PermissionAdmin", 0L, bundle.getBundleId());
    }


    /**
     * thereCanBeonlyOne. Test that there is only one permission admin
     */
    public void testThereCanBeOnlyOne() throws Exception {

        /* Get the references to all permissions admins */
        ServiceReference[] serviceRefs =
            getContext().getServiceReferences(PermissionAdmin.class.getName(), null);

        assertEquals("only one PermissionAdmin", 1, serviceRefs.length);
    }


    /**	
     */
    public void testImplicitPermissions() throws Throwable {

        PermissionInfo[] permInfoArrayDefault = {
                new PermissionInfo("java.security.AllPermission", "", "")
            };

        invokeWithPermissions("implicitPermissionsTest", permInfoArrayDefault);
    }
    
    public void implicitPermissionsTest() throws Exception {
        getContext().getDataFile("nicke.txt");
    }



    /*****  Helper methods  *****/

    /**
     * Creates a PermissionInfo with a bad encoded string and checks
     * that it throws a correct exception.
     */
    private void createBadPermissionInfo(String message, String encoded, Class exceptionClass) 
            throws Exception {
        try {
            new PermissionInfo(encoded);
            fail(message + " did not result in a " + exceptionClass.getName());
        }
        catch(Exception e) {
            assertException(message, exceptionClass, e);
        }
    }
    
    /**
     * Creates a PermissionInfo with a bad encoded string and checks
     * that it throws a correct exception.
     */
    private void createBadPermissionInfo(String message, String type, String name,
            String actions, Class exceptionClass) throws Exception {
        try {
            new PermissionInfo(type, name, actions);
            fail(message + " did not result in a " + exceptionClass.getName());
        }
        catch(Exception e) {
            assertException(message, exceptionClass, e);
        }
    }


    /**
     * Returns <code>true</code> if there is a SecurityManager installed,
     * returns <code>false</code> otherwise.
     */
    private boolean securityManagerExists() {
        return(System.getSecurityManager() != null);
    }

}

