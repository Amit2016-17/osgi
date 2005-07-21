
package org.osgi.test.cases.permissionadmin.conditional.tbc;


import java.lang.reflect.Constructor;
import java.security.Permission;
import java.util.Enumeration;
import java.util.Vector;

import org.osgi.framework.AdminPermission;
import org.osgi.framework.Bundle;

import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.service.condpermadmin.ConditionalPermissionAdmin;
import org.osgi.service.condpermadmin.ConditionalPermissionInfo;

import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.test.cases.permissionadmin.conditional.testcond.TestCondition;


public class ConditionalUtility {

	private ConditionalTestControl 		testControl;
	private ConditionalTBCService  		tbc;
	private ConditionalPermTBCService	permTBC;
	private PermissionAdmin		   		  pAdmin;
	private ConditionalPermissionAdmin 	cpAdmin;
	private Bundle	 testBundle;
	private boolean	 hasPermissionsPerm;
	
	static String REQUIRED 					  = "RequiredPermission";
	static String CP_PERMISSION 			= "CPAdminPermission";
	static String P_PERMISSION 				= "PAdminPermission";
	static String REQUIRED_CP_PERMISSION 	= "RequiredIntersectCPAdminPermission";
	static String REQUIRED_P_PERMISSION 	= "RequiredIntersectPAdminPermission";
	
	static String VERSION_FLOOR				= "version_range_floor";
	static String VERSION_CEILING			= "version_range_ceiling";
	
	static String DN_S						    = "DNs";
	static String INAPPROPRIATE_DN_S	= "inappropriateDNs";
	static String SEPARATOR           = "\"";
	
	static String BUNDLE_VERSION 			= "Bundle-Version";

	public ConditionalUtility(ConditionalTestControl testControl, ConditionalTBCService  tbc, 
							  PermissionAdmin pAdmin, ConditionalPermissionAdmin cpAdmin, 
							  ConditionalPermTBCService	permTBC) {
		this.testControl = testControl;
		this.tbc = tbc;
		this.pAdmin = pAdmin;
		this.cpAdmin = cpAdmin;
		this.permTBC = permTBC;
	}
	
	public void setTestBunde(Bundle bundle, boolean hasPermissionsPerm) {
		this.testBundle = bundle;
		this.hasPermissionsPerm = hasPermissionsPerm;
	}
	
	
	ConditionInfo createTestCInfo(boolean postponed, boolean satisfied, boolean mutable, String name) {
    return new ConditionInfo(TestCondition.class.getName(), 
					new String[]{String.valueOf(postponed), 
								 String.valueOf(satisfied), 
								 String.valueOf(mutable),
                 name});
	}
	
	
	void createBadConditionInfo(String message, String encoded, Class exceptionClass) throws Exception {
		try {
			new ConditionInfo(encoded);
			testControl.fail(message + " did not result in a " + exceptionClass.getName());
		} catch (Exception e) {
			testControl.assertException(message, exceptionClass, e);
		}
	}

	void createBadConditionInfo(String message, String type, String[] args, Class exceptionClass) throws Exception {
		try {
			new ConditionInfo(type, args);
			testControl.fail(message + " did not result in a " + exceptionClass.getName());
		} catch (Exception e) {
			testControl.assertException(message, exceptionClass, e);
		}
	}

	void allowed(AdminPermission permission) {
    String message = "allowed permission " + permToString(permission);
		try {
			checkPermission(permission);
			testControl.pass(message);
		} catch (Throwable e) {
			testControl.fail(message + " but " + e.getClass().getName() + " was thrown");
		}
	}
	
	void notAllowed(AdminPermission permission, Class wanted) {
    String message = "not allowed " + permToString(permission);
		try {
			checkPermission(permission);
      //testControl.pass("FAIL>>>>" + message);
			testControl.failException(message, wanted);
		} catch (Throwable e) {
      if (!e.getClass().equals(wanted.getClass()) && (wanted.isInstance(e))) {
        testControl.pass(message + " and [" + e.getClass() + "] was thrown");
      } else {
        testControl.assertException(message, wanted, e);
      }
		}
	}
  
  private String permToString(AdminPermission permission) {
    return "permission [" + permission.getName() + ", " + permission.getActions() + "]";
  }
  
  private String cpInfoToString(ConditionalPermissionInfo cpInfo) {
    StringBuffer result = new StringBuffer("ConditionalPermissionInfo: ");
    ConditionInfo[] condInfos = cpInfo.getConditionInfos();
    result.append("\n       with conditional infos:");
    for (int i = 0; i < condInfos.length; i++) {
      result.append("\n         ");
      result.append(condInfos[i]);      
    }
    PermissionInfo[] permInfos = cpInfo.getPermissionInfos();
    result.append("\n       with permission infos:");
    for (int i = 0; i < permInfos.length; i++) {
      result.append("\n         ");
      result.append(permInfos[i]);      
    }
    return result.toString();
  }
	
	private void checkPermission(AdminPermission permission) throws Throwable {
		Permission p = getPermission(permission, testBundle);
		if (hasPermissionsPerm) {
			permTBC.checkPermission(p);
		} else {
			tbc.checkPermission(p);
		}
	}
	
  Vector getWildcardString(String value) {
    Vector result = new Vector();
    result.addElement(value);
    int beginIndex = 0;
    if (value.startsWith("http://")) {
      beginIndex = "http://".length();
    }
    int index = value.indexOf("/", beginIndex);
    int lastIndex;
    
    while (index != -1) {
      lastIndex = index + 1;
      result.addElement(value.substring(0, lastIndex) + "-");
      index = value.indexOf("/", lastIndex); 
    }               
    return result;
}

	
	// Returns vector whose values are version-ranges, where version belong 
	Vector getCorrectVersionRanges(String version, String floor, String ceiling) {
		Vector result = new Vector();
		
		result.addElement(new String("\"" + floor + "\""));
		result.addElement(new String("\"" + version + "\""));
		
		result.addElement(new String("\"[" + floor + "," + ceiling + "\"]"));
		result.addElement(new String("\"[" + floor + "," + ceiling + "\")"));
		result.addElement(new String("\"(" + floor + "," + ceiling + "\"]"));
		result.addElement(new String("\"(" + floor + "," + ceiling + "\")"));

		result.addElement(new String("\"[" + version + "," + ceiling + "\"]"));
		result.addElement(new String("\"[" + version + "," + ceiling + "\")"));

		result.addElement(new String("\"[" + floor + "," + version + "\"]"));
		result.addElement(new String("\"(" + floor + "," + version + "\"]"));

		
		return result;
	}
	
	// Returns vector whose values are version-ranges, where version not belong 
	Vector getNotCorrectVersionRanges(String version, String floor, String ceiling) {
		Vector result = new Vector();
		
		result.addElement(new String("\"" + ceiling + "\""));

		result.addElement(new String("\"(" + version + "," + ceiling + "\"]"));
		result.addElement(new String("\"(" + version + "," + ceiling + "\")"));

		result.addElement(new String("\"[" + floor + "," + version + "\")"));
		result.addElement(new String("\"(" + floor + "," + version + "\")"));

		
		return result;
	}
	
	AdminPermission getPermission(String resourceName) throws Exception {
		PermissionInfo pi = new PermissionInfo(ConditionResource.getString(resourceName)); 

		return getPermission(pi);
	}
	
	AdminPermission getPermission(PermissionInfo pi) throws Exception {
		String actions = pi.getActions();
		String name = pi.getName();
		String type = pi.getType();
		
		Class pClass = Class.forName(type);
		Constructor constructor = pClass.getConstructor(new Class[]{
				String.class, String.class});
		AdminPermission permission = (AdminPermission)constructor.newInstance(
				new Object[] {name, actions});

		return permission;
	}
	
	void setPermissionsByPermissionAdmin(String bundleLocation, Permission permission) {
		PermissionInfo pInfo = new PermissionInfo(permission.getClass().getName(), 
									permission.getName(), permission.getActions());
		pAdmin.setPermissions(bundleLocation, new PermissionInfo[]{pInfo});
	}
	
	ConditionalPermissionInfo setPermissionsByCPermissionAdmin(ConditionInfo[] cInfos, Permission[] permissions) {
    PermissionInfo[] permInfos = null;
    if (permissions != null) {
      permInfos = new PermissionInfo[permissions.length];
      for (int i = 0; i < permissions.length; i++) {
        Permission perm = permissions[i];
        permInfos[i] = new PermissionInfo(perm.getClass().getName(), perm.getName(), perm.getActions());
      }
    }
		return cpAdmin.addConditionalPermissionInfo(cInfos, permInfos);
	}
	
	void testPermissions(ConditionInfo[] conditions, Permission permission, AdminPermission[] allowedPermission, AdminPermission[] notAllowedPermission) {
    testPermissions(conditions, new Permission[]{permission}, allowedPermission, notAllowedPermission);
	}
  
  void testPermissions(ConditionInfo[] conditions, Permission[] permissions, AdminPermission[] allowedPermission, AdminPermission[] notAllowedPermission) {
    ConditionalPermissionInfo cpInfo = setPermissionsByCPermissionAdmin(conditions, permissions);
    testControl.trace("Test " + cpInfoToString(cpInfo));
    testControl.trace("Test for allowed permissions:");
    for (int i = 0; i < allowedPermission.length; i++) {
      allowed(allowedPermission[i]);
    }
    testControl.trace("Test for not allowed permissions:");
    for (int k = 0; k < notAllowedPermission.length; k++) {
      notAllowed(notAllowedPermission[k], SecurityException.class);
    }
    cpInfo.delete();
  }
  
  void testPermissions(ConditionInfo[] conditions, Permission[] permissions,
      AdminPermission[] allowedPermission, AdminPermission[] notAllowedPermission, String[] order) {
    ConditionalPermissionInfo cpInfo = setPermissionsByCPermissionAdmin(conditions, permissions);
    testControl.trace("Test " + cpInfoToString(cpInfo));
    boolean testForAllowed = allowedPermission.length>0;
    testControl.trace("Test for allowed permissions:");
    for (int i = 0; i < allowedPermission.length; i++) {
      allowed(allowedPermission[i]);
    }
    //it doesn't work because loadClass method in RI works with the system class loader only
    //when it works with other class loader, uncomment all 'testForAllowed' blocks here
    //if (testForAllowed) testEqualArrays(order, TestCondition.getSatisfOrder());
    testControl.trace("Test for not allowed permissions:");
    for (int k = 0; k < notAllowedPermission.length; k++) {
      notAllowed(notAllowedPermission[k], SecurityException.class);
    }
    //if (testForAllowed) {
    //  //alredy order is tested, so only clear the vector
    //  TestCondition.satisfOrder.removeAllElements();
    //} else {
    //  //order not tested yet, test it here
    //  testEqualArrays(order, TestCondition.getSatisfOrder());
    //}
    cpInfo.delete();
    
  }
  
  void deletePermissions(ConditionInfo[] conditions, Permission permission) {
    ConditionalPermissionInfo cpInfo = setPermissionsByCPermissionAdmin(conditions, new Permission[] {permission});
    testControl.pass("New permissions are set: " + cpInfoToString(cpInfo));
    Enumeration infos = cpAdmin.getConditionalPermissionInfos();
    testControl.pass("All set conditional permissions are: ");
    while (infos.hasMoreElements()) {
      testControl.pass(cpInfoToString((ConditionalPermissionInfo)infos.nextElement()));
    }
    cpInfo.delete();
    testControl.pass("ConditionalPermissionInfo deleted");
    int cpCounter = 0;
    while (infos.hasMoreElements()) {
      cpCounter++;
      testControl.pass(cpInfoToString((ConditionalPermissionInfo)infos.nextElement()));
    }
    testControl.assertEquals("After delete there have to be no conditional permissions: ", 0, cpCounter);
  }
  
  private void testEqualArrays(String[] array1, String[] array2) {
    if (array1 != null && array2 != null) {
      testControl.assertEquals("The number of checked TestCondionions: ", array1.length, array2.length);
      for (int i=0; i<array1.length; i++) {
        testControl.assertEquals("checked condition: ", array1[i], array2[i]);
      }
    } else {
      testControl.fail("Not correct check of TestConditions.");
    }
  }
	
	Vector createWildcardDNs(String value) {
		Vector result = new Vector();
		String semicolon = ";";
		String asterisk = "*";		
		
		int lastIndex = 0;
		int semicolonIndex = value.indexOf(semicolon);
		String element;
		String rdns;
		String prefix;
		String suffix;
		while (semicolonIndex != -1) {
			prefix = value.substring(0, lastIndex);
			element = value.substring(lastIndex, semicolonIndex);
			suffix = value.substring(semicolonIndex);
			
			//result.addElement(prefix + asterisk + suffix);
			result.addAll(addVector(createWildcardRDNs(element), prefix, suffix));
			
			lastIndex = semicolonIndex + 1;
			semicolonIndex = value.indexOf(semicolon, lastIndex);
		}
		
		if (lastIndex > 0) {
			prefix = value.substring(0, lastIndex);
			element = value.substring(lastIndex);
			
			//result.addElement(prefix + asterisk);
			result.addAll(addVector(createWildcardRDNs(element), prefix, ""));
		} else {
			result.addAll(createWildcardRDNs(value));
		}
		
		return result;
	}
	
	private Vector addVector(Vector vec, String prefix, String suffix) {
		Vector result = new Vector();
		for (int i = 0; i < vec.size(); ++i) {
			result.addElement(prefix + (String)vec.elementAt(i) + suffix);
		}
		
		return result;
	}
	
	
	private Vector createWildcardRDNs(String value) {
		Vector result = new Vector();
		String comma = ",";
		//String semicolon = ";";
		String equal = "=";
		String space = " ";
		String asterisk = "*";
		
		int lastIndex = 0;
		int commaIndex = value.indexOf(comma, lastIndex); 
		int equalIndex = value.indexOf(equal, lastIndex);
		
		while (commaIndex != -1) {
			result.addElement(asterisk + value.substring(commaIndex));
			if ((equalIndex != -1) && (equalIndex < commaIndex)) {
				result.addElement(value.substring(0, equalIndex + 1) + asterisk + 
								  value.substring(commaIndex));
			}
			lastIndex = commaIndex + 1;
			commaIndex = value.indexOf(comma, lastIndex); 
			equalIndex = value.indexOf(equal, lastIndex);
		}
		
		if (lastIndex > 0) {
			result.addElement(asterisk);
		}
		if (equalIndex > 0) {
			result.addElement(value.substring(0, equalIndex + 1) + asterisk);
		}

		
		return result;
	}
	
	private Permission getPermission(AdminPermission permission, Bundle bundle) {
		return new AdminPermission(bundle, permission.getActions());
	}
	
	
}
