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
 * ===========  ==============================================================
 * 21/02/2005   Leonardo Barros
 * 14           Implement MEG TCK
 * ===========  ==============================================================
 * 22/03/2005   Alexandre Alves
 * 14			Implement MEG TCK
 * ===========  ==============================================================
 */

package org.osgi.test.cases.monitor.tbc;

import java.net.SocketPermission;

import org.osgi.framework.AdminPermission;
import org.osgi.framework.Bundle;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.PackagePermission;
import org.osgi.framework.ServicePermission;
import org.osgi.framework.ServiceReference;
import org.osgi.service.dmt.DmtAdmin;
import org.osgi.service.dmt.DmtAlertItem;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.event.TopicPermission;
import org.osgi.service.monitor.MonitorAdmin;
import org.osgi.service.monitor.MonitorListener;
import org.osgi.service.monitor.Monitorable;
import org.osgi.service.monitor.MonitoringJob;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.monitor.tbc.Activators.MonitorHandlerActivator;
import org.osgi.test.cases.monitor.tbc.Activators.RemoteAlertSenderActivator;
import org.osgi.test.cases.monitor.tbc.MonitorListener.Updated;
import org.osgi.test.cases.monitor.tbc.MonitorPermission.Implies;
import org.osgi.test.cases.monitor.tbc.MonitorPermission.MonitorPermission;
import org.osgi.test.cases.monitor.tbc.MonitorPermission.MonitorPermissionConstants;
import org.osgi.test.cases.monitor.tbc.Monitorable.Monitorables;
import org.osgi.test.cases.monitor.tbc.MonitoringJob.IsLocal;
import org.osgi.test.cases.monitor.tbc.MonitoringJob.RemoteAlertSender;
import org.osgi.test.cases.monitor.tbc.MonitoringJob.Stop;
import org.osgi.test.cases.monitor.tbc.StatusVariable.StatusVariable;
import org.osgi.test.cases.monitor.tbc.StatusVariable.StatusVariableConstants;
import org.osgi.test.cases.monitor.tbc.TreeStructure.TreeStructure;
import org.osgi.test.cases.util.DefaultTestBundleControl;

/**
 * @author Alexandre Santos
 *
 * Controls the execution of the test case.
 */
public class MonitorTestControl extends DefaultTestBundleControl {
	
	public static final String LONGID = "abcdefghjklmnoprstuvw";

	public final static String INVALID_ID = ";/?:@&=+$,";

	public final static String SV_MONITORABLEID1 = "cesar";

	public final static String SV_MONITORABLEID2 = "cesar2";

	public final static int MONITORABLE_LENGTH = 1;

	public final static int SV_LENGTH = 2;

	public final static String SV_NAME1 = "test0";

	public final static String SV_NAME2 = "test1";

	public final static String VALID_ID = "a23_-.";

	public final static String IDENTIFY_ID = "ID";

	public final static String DESCRIPTION = "description";

	public final static int INVALID_CM = -1;

	public final static int SV_INT_VALUE = 1;

	public final static float SV_FLOAT_VALUE = 0.0f;

	public final static boolean SV_BOOLEAN_VALUE = false;

	public final static String SV_STRING_VALUE = "value";

	public final static String INITIATOR = "initiator";

	public final static String[] SVS = { "cesar/test0", "cesar/test1" };

	public final static String[] SVS_NOT_SUPPORT_NOTIFICATION = { "cesar2/test0" };

	public final static String INEXISTENT_SVS = "cesar/tmp";

	public final static int SCHEDULE = 1;

	public final static int COUNT = 1;

	public final static int INVALID_SCHEDULE = -1;

	public final static int INVALID_COUNT = -1;

	public final static String INVALID_MONITORABLE_SV = "tmp";

	public static int EVENT_COUNT = 0;
	
	public static int SWITCH_EVENTS_COUNT = 0;
	
	public static final int TIMEOUT = 1000;
	public static final int SHORT_TIMEOUT = 2000;
	
	private int eventClassCode = -1;
	
	public static final String CONST_STATUSVARIABLE_NAME = "mon.statusvariable.name";
	public static final String CONST_STATUSVARIABLE_VALUE = "mon.statusvariable.value";
	public static final String CONST_MONITORABLE_PID = "mon.monitorable.pid";
	public static final String CONST_LISTENER_ID = "mon.listener.id";
	
	public static final String DMT_OSGI_MONITOR = "./OSGi/Monitor";
	
	public final static String DMT_URI_MONITORABLE1 = DMT_OSGI_MONITOR + "/"+SV_MONITORABLEID1;
	public final static String DMT_URI_MONITORABLE2 = DMT_OSGI_MONITOR + "/"+SV_MONITORABLEID2;
	
	public final static String MONITOR_XML_MONITORABLE1_SV1 = "x-oma-trap:"+SV_MONITORABLEID1+"/"+SV_NAME1;
		
	public final static String DMT_URI_MONITORABLE1_SV1 = DMT_URI_MONITORABLE1+"/"+SV_NAME1;
	public final static String DMT_URI_MONITORABLE1_SV2 = DMT_URI_MONITORABLE1+"/"+SV_NAME2;
	public final static String DMT_URI_MONITORABLE2_SV1 = DMT_URI_MONITORABLE2+"/"+SV_NAME1;
	public final static String DMT_URI_MONITORABLE2_SV2 = DMT_URI_MONITORABLE2+"/"+SV_NAME2;
	
	public static final String DMT_URI_MONITORABLE_SV1_TRAPID = DMT_URI_MONITORABLE1_SV1 + "/TrapID";
	public static final String DMT_URI_MONITORABLE_SV1_CM = DMT_URI_MONITORABLE1_SV1 + "/CM";
	public static final String DMT_URI_MONITORABLE_SV1_RESULTS = DMT_URI_MONITORABLE1_SV1 + "/Results";
	public static final String DMT_URI_MONITORABLE_SV1_SERVER = DMT_URI_MONITORABLE1_SV1 + "/Server";
	
	public static final String REMOTE_SERVER = "remoteServer";
	
	public final static String[] DMT_URI_MONITORABLE1_PROPERTIES = {
		DMT_URI_MONITORABLE_SV1_SERVER+"/"+REMOTE_SERVER,
		DMT_URI_MONITORABLE_SV1_SERVER+"/"+REMOTE_SERVER+"/Reporting/Type",
		DMT_URI_MONITORABLE_SV1_SERVER+"/"+REMOTE_SERVER+"/Reporting/Value",
		DMT_URI_MONITORABLE_SV1_SERVER+"/"+REMOTE_SERVER+"/Enabled",
		DMT_URI_MONITORABLE_SV1_SERVER+"/"+REMOTE_SERVER+"/ServerId",
		DMT_URI_MONITORABLE_SV1_SERVER+"/"+REMOTE_SERVER+"/Reporting",
		DMT_URI_MONITORABLE_SV1_SERVER+"/"+REMOTE_SERVER+"/TrapRef",
		DMT_URI_MONITORABLE_SV1_SERVER+"/"+REMOTE_SERVER+"/TrapRef/testId",
		DMT_URI_MONITORABLE_SV1_SERVER+"/"+REMOTE_SERVER+"/TrapRef/testId/TrapRefID"
	};
	
	private MonitorAdmin monitorAdmin;

	private MonitorListener monitorListener;

	private Monitorable monitorable;

	private PermissionAdmin permissionAdmin;
	
	private MonitorHandlerActivator monitorHandlerActivator;

	private DmtAdmin dmtAdmin;
	
	private String tb1Location;
	
	private TestInterface[] testInterfaces;
	
	private ServiceReference[] srvReferences;
	
	private String serverId = null;
	
	private String correlator = null;
	
	private DmtAlertItem[] alerts = null;
	
	private boolean receivedAlert = false;

	private boolean isMonitorablePid = false;
	private boolean isStatusVariableName = false;
	private boolean isStatusVariableValue = false;
	private boolean isListenerId = false;	
	
	private Bundle tb2 = null;
	private Bundle tb3 = null;
	
	private String statusVariableName = null;
	private String statusVariableValue = null;
	private String monitorablePid = null;
	private String listenerId = null;
	

	
	public void stopMonitorables() {
		try {		
			
			this.uninstallBundle(tb2);
			this.uninstallBundle(tb3);
			
			tb2 = null;
			tb3 = null;
			
		} catch (Exception e) {
			this.log("InvalidSyntaxException was raised. " + e.getClass().getName() );
		}		
	}
	
	public void installMonitorables() {
		try {
			
			tb3 = this.installBundle("tb3.jar");						
			
			ServiceReference[] refs = getContext().getServiceReferences(
					Monitorable.class.getName(), "(service.pid=" + SV_MONITORABLEID2 + ")");
			
			((TestingMonitorable) getContext().getService(refs[0])).setMonitorTestControlInterface(this);
			
			tb2 = this.installBundle("tb2.jar");			
			
			refs = getContext().getServiceReferences(
					Monitorable.class.getName(), "(service.pid=" + SV_MONITORABLEID1 + ")");
			
			monitorable = (Monitorable) getContext().getService(refs[0]);
			
			((TestingMonitorable) monitorable).setMonitorTestControlInterface(this);
			
			
		} catch (Exception e) {
			this.fail("Unexpected exception at prepare(installMonitorables). "
					+ e.getClass());
		}
	}
	
	private void installEventHandler() {
		try {
			monitorHandlerActivator = new MonitorHandlerActivator(this);
			monitorHandlerActivator.start(getContext());
		}
		catch (Exception e) {
			this.fail("Unexpected exception at prepare(installEventHandler). "
					+ e.getClass());			
		}		
	}
	
	private void installRemoteAlertSender() {
		try {
			RemoteAlertSenderActivator remoteAlertSenderActivator = new RemoteAlertSenderActivator(this);
			remoteAlertSenderActivator.start(getContext());
		}
		catch (Exception e) {
			this.fail("Unexpected exception at prepare(installRemoteAlertSender). "
					+ e.getClass());			
		}				
	}

	public void prepare() throws Exception {
			installMonitorables();
			installEventHandler();
			installRemoteAlertSender();
			
			permissionAdmin = (PermissionAdmin) getContext().getService(
					getContext().getServiceReference(
							PermissionAdmin.class.getName()));
			try {
				installBundle("tb1.jar");
			} catch (Exception e) {
				this.log("Unexpected exception at prepare when try to install tb1.jar.");
				throw new Exception(e.getMessage());
			}
			
			ServiceReference srvReference = getContext().getServiceReference(TB1Service.class.getName());
			tb1Location = srvReference.getBundle().getLocation();		
			TB1Service tcb1Service = (TB1Service) getContext().getService(srvReference);
			testInterfaces = tcb1Service.getTestClasses(this);
			
			
			monitorAdmin = (MonitorAdmin) getContext().getService(getContext().getServiceReference(MonitorAdmin.class.getName()));
			monitorListener = (MonitorListener) getContext().getService(
					getContext().getServiceReference(
							MonitorListener.class.getName()));
			
			dmtAdmin = (DmtAdmin) getContext().getService(getContext().getServiceReference(DmtAdmin.class.getName()));			

	}
	
	public void setLocalPermission(PermissionInfo[] permissions) {
		PermissionInfo[] defaults = new PermissionInfo[] {
				new PermissionInfo(PackagePermission.class.getName(), "*", "EXPORT, IMPORT"),	
				new PermissionInfo(ServicePermission.class.getName(), "*", "GET"),
				new PermissionInfo(AdminPermission.class.getName(), "*", "*"),
				new PermissionInfo(TopicPermission.class.getName(),"org/osgi/service/monitor/MonitorEvent",TopicPermission.PUBLISH),
				new PermissionInfo(SocketPermission.class.getName(), "*", "connect,resolve"),
				new PermissionInfo(ServicePermission.class.getName(), "*", ServicePermission.REGISTER),
		};
		int size = permissions.length + defaults.length;
		
		PermissionInfo[] permissao = new PermissionInfo[size];
		System.arraycopy(defaults, 0, permissao, 0, defaults.length);
		System.arraycopy(permissions, 0, permissao, defaults.length, permissions.length);

		getPermissionAdmin().setPermissions(getTb1Location(), permissao);
	}
	
	public void setLocalPermission(PermissionInfo permission) {
		PermissionInfo[] defaults = new PermissionInfo[] {
				new PermissionInfo(PackagePermission.class.getName(), "*", "EXPORT, IMPORT"),	
				new PermissionInfo(ServicePermission.class.getName(), "*", "GET"),
				new PermissionInfo(AdminPermission.class.getName(), "*", "*"),
				new PermissionInfo(TopicPermission.class.getName(),"org/osgi/service/monitor/MonitorEvent",TopicPermission.PUBLISH),
				new PermissionInfo(SocketPermission.class.getName(), "*", "connect,resolve"),
				new PermissionInfo(ServicePermission.class.getName(), "*", ServicePermission.REGISTER),
				permission
		};

		getPermissionAdmin().setPermissions(getTb1Location(), defaults);
	}
	
	public void setLocalPermission(String target, String action) {
		PermissionInfo[] defaults = new PermissionInfo[] {
				new PermissionInfo(PackagePermission.class.getName(), "*", "EXPORT, IMPORT"),	
				new PermissionInfo(ServicePermission.class.getName(), "*", "GET"),
				new PermissionInfo(AdminPermission.class.getName(), "*", "*"),
				new PermissionInfo(TopicPermission.class.getName(),"org/osgi/service/monitor/MonitorEvent",TopicPermission.PUBLISH),
				new PermissionInfo(SocketPermission.class.getName(), "*", "connect,resolve"),
				new PermissionInfo(ServicePermission.class.getName(), "*", ServicePermission.REGISTER),
				new PermissionInfo(org.osgi.service.monitor.MonitorPermission.class.getName(), target, action)
		};

		getPermissionAdmin().setPermissions(tb3.getLocation(), defaults);
	}	
	
	/*
	 * Returns Tb3 location
	 */	
	public String getTb3Location() {
		return tb3.getLocation();
	}
	

	/*
	 * Executes Tcs for constants
	 */
	public void testStatusVariableConstants() {
		new StatusVariableConstants(this).run();
	}

	/*
	 * Executes StatusVariable constructor
	 */
	public void testStatusVariable() {
		new StatusVariable(this).run();
	}

	/*
	 * Executes StatusVariable.equals
	 */
	public void testStatusVariableEquals() {
		new org.osgi.test.cases.monitor.tbc.StatusVariable.Equals(this).run();
	}

	/*
	 * Executes StatusVariable.ToString
	 */
	public void testStatusVariableToString() {
		new org.osgi.test.cases.monitor.tbc.StatusVariable.ToString(this).run();
	}

	/*
	 * Executes MonitorPermission and MonitorPermission.getActions test methods
	 */
	public void testMonitorPermission() {
		new MonitorPermission(this).run();
	}

	/*
	 * Executes MonitorPermissionPermission.equals test methods
	 */
	public void testMonitorPermissionEquals() {
		new org.osgi.test.cases.monitor.tbc.MonitorPermission.Equals(this)
				.run();
	}

	/*
	 * Executes MonitorPermissionPermission.equals test methods
	 */
	public void testMonitorPermissionHashCode() {
		new org.osgi.test.cases.monitor.tbc.MonitorPermission.HashCode(this)
				.run();
	}

	/*
	 * Executes MonitorPermission.implies test methods
	 */
	public void testMonitorPermissionImplies() {
		new Implies(this).run();
	}

	public void testMonitorPermissionConstants() {
		new MonitorPermissionConstants(this).run();
	}

	/*
	 * Executes MonitorAdmin.getStatusVariable test methods
	 */
	public void testMonitorAdminGetStatusVariable() {
		testInterfaces[3].run();
	}

	/*
	 * Executes MonitorAdmin.getStatusVariables test methods
	 */
	public void testMonitorAdminGetStatusVariables() {
		testInterfaces[5].run();
	}

	/*
	 * Executes MonitorAdmin.switchEvents test methods
	 */
	public void testMonitorAdminSwitchEvents() {
		testInterfaces[9].run();
	}

	/*
	 * Executes MonitorAdmin.resetStatusVariable test methods
	 */
	public void testMonitorAdminResetStatusVariable() {
		testInterfaces[6].run();
	}

	/*
	 * Executes MonitorAdmin.getStatusVariableNames test methods
	 */
	public void testMonitorAdminGetStatusVariableNames() {
		testInterfaces[4].run();
	}

	/*
	 * Executes MonitorAdmin.getDescription test methods
	 */
	public void testMonitorAdminGetDescription() {
		testInterfaces[0].run();
	}

	/*
	 * Executes MonitorAdmin.getMonitorableNames test methods
	 */
	public void testMonitorAdminGetMonitorableNames() {
		testInterfaces[1].run();
	}

	/*
	 * Executes MonitorAdmin.getRunningJobs test methods
	 */
	public void testMonitorAdminGetRunningJobs() {
		testInterfaces[2].run();
	}

	/*
	 * Executes MonitorAdmin.startJob test methods
	 */
	public void testMonitorAdminStartJob() {
		testInterfaces[7].run();		
	}

	/*
	 * Executes MonitorAdmin.startScheduledJob test methods
	 */
	public void testMonitorAdminStartScheduledJob() {
		testInterfaces[8].run();
	}

	/*
	 * Executes the MonitoringJobs.Stop tests
	 */
	public void testMonitoringJobsStop() {
		new Stop(this).run();
	}

	public void testMonitoringJobsIsLocal() {
		new IsLocal(this).run();
	}

	/*
	 * Executes the MonitorListener.Updated tests
	 */
	public void testMonitorListenerUpdated() {
		new Updated(
				this).run();
	}	

	/*
	 * Executes the TreeStructure tests
	 */
	public void testTreeStructure() {
		new TreeStructure(this).run();
	}
	
	/*
	 * Executes the RemoteStartJob tests
	 */
	public void testRemoteAlertSender() {
		new RemoteAlertSender(this).run();
	}	
	
	/*
	 * Executes the Monitorables tests
	 */
	public void testMonitorables() {
		new Monitorables(this).run();
	}		

	/**
	 * @return Returns the monitorAdmin.
	 */
	public MonitorAdmin getMonitorAdmin() {
		return monitorAdmin;
	}

	/**
	 * @return Returns the updateListener.
	 */
	public MonitorListener getMonitorListener() {
		return monitorListener;
	}

	/**
	 * @return Returns the monitorable.
	 */
	public Monitorable getMonitorable() {
		return monitorable;
	}

	public PermissionAdmin getPermissionAdmin() {
		return permissionAdmin;
	}

	public void unprepare() {
		try {
			this.stopMonitorables();
		} catch (Exception e) {
			this.fail("Unexpected exception at unprepare. " + e.getClass());
		}
	}

	/**
	 * @return Returns the dmtAdmin.
	 */
	public DmtAdmin getDmtAdmin() {
		return dmtAdmin;
	}
	
	/**
	 * Method to stop all running monitoring job.
	 */

	public void stopRunningJobs() {
		MonitoringJob[] mjs = this.getMonitorAdmin().getRunningJobs();
		if (mjs != null) {
			for (int i = 0; i < mjs.length; i++) {
				mjs[i].stop();
			}
		}

	}	
	/**
	 * @return Returns the tb1Location.
	 */
	public String getTb1Location() {
		return tb1Location;
	}
	
	public void closeSession(DmtSession session) {
		try {
			session.close();
		} catch (DmtException e) {
			log("#fail when closing the session.");
		}
	}
	
	/**
	 * @return Returns the correlator.
	 */
	public String getCorrelator() {
		return correlator;
	}
	/**
	 * @param correlator The correlator to set.
	 */
	public void setCorrelator(String correlator) {
		this.correlator = correlator;
	}
	/**
	 * @return Returns the receivedAlert.
	 */
	public boolean isReceivedAlert() {
		return receivedAlert;
	}
	/**
	 * @param receivedAlert The receivedAlert to set.
	 */
	public void setReceivedAlert(boolean receivedAlert) {
		this.receivedAlert = receivedAlert;
	}
	/**
	 * @return Returns the serverId.
	 */
	public String getServerId() {
		return serverId;
	}
	/**
	 * @param serverId The serverId to set.
	 */
	public void setServerId(String serverId) {
		this.serverId = serverId;
	}
	
	public void resetAlert() {
		serverId = null;
		correlator = null;
		receivedAlert = false;
		alerts = null;
		eventClassCode = -1;
	}
	
	/**
	 * @return Returns the isListenerId.
	 */
	public boolean isListenerId() {
		return isListenerId;
	}
	/**
	 * @param isListenerId The isListenerId to set.
	 */
	public void setListenerId(boolean isListenerId) {
		this.isListenerId = isListenerId;
	}
	/**
	 * @return Returns the isMonitorablePid.
	 */
	public boolean isMonitorablePid() {
		return isMonitorablePid;
	}
	/**
	 * @param isMonitorablePid The isMonitorablePid to set.
	 */
	public void setMonitorablePid(boolean isMonitorablePid) {
		this.isMonitorablePid = isMonitorablePid;
	}
	/**
	 * @return Returns the isStatusVariableName.
	 */
	public boolean isStatusVariableName() {
		return isStatusVariableName;
	}
	/**
	 * @param isStatusVariableName The isStatusVariableName to set.
	 */
	public void setStatusVariableName(boolean isStatusVariableName) {
		this.isStatusVariableName = isStatusVariableName;
	}
	/**
	 * @return Returns the isStatusVariableValue.
	 */
	public boolean isStatusVariableValue() {
		return isStatusVariableValue;
	}
	/**
	 * @param isStatusVariableValue The isStatusVariableValue to set.
	 */
	public void setStatusVariableValue(boolean isStatusVariableValue) {
		this.isStatusVariableValue = isStatusVariableValue;
	}
	
	public void resetEvent() {
		isMonitorablePid = false;
		isStatusVariableName = false;
		isStatusVariableValue = false;
		isListenerId = false;	
		statusVariableName = null;
		statusVariableValue = null;
		monitorablePid = null;
		listenerId = null;		
	}
	/**
	 * @return Returns the listenerId.
	 */
	public String getListenerId() {
		return listenerId;
	}
	/**
	 * @param listenerId The listenerId to set.
	 */
	public void setListenerId(String listenerId) {
		this.listenerId = listenerId;
	}
	/**
	 * @return Returns the monitorablePid.
	 */
	public String getMonitorablePid() {
		return monitorablePid;
	}
	/**
	 * @param monitorablePid The monitorablePid to set.
	 */
	public void setMonitorablePid(String monitorablePid) {
		this.monitorablePid = monitorablePid;
	}
	/**
	 * @return Returns the statusVariableName.
	 */
	public String getStatusVariableName() {
		return statusVariableName;
	}
	/**
	 * @param statusVariableName The statusVariableName to set.
	 */
	public void setStatusVariableName(String statusVariableName) {
		this.statusVariableName = statusVariableName;
	}
	/**
	 * @return Returns the statusVariableValue.
	 */
	public String getStatusVariableValue() {
		return statusVariableValue;
	}
	/**
	 * @param statusVariableValue The statusVariableValue to set.
	 */
	public void setStatusVariableValue(String statusVariableValue) {
		this.statusVariableValue = statusVariableValue;
	}
	
	
	public TestingMonitorable getMonitorableInterface() {
		try {
			ServiceReference[] rfs = getContext().getServiceReferences(Monitorable.class.getName(), "(service.pid="+MonitorTestControl.SV_MONITORABLEID1+")");
			return rfs == null ? null: (TestingMonitorable) getContext().getService(rfs[0]);
		} catch (InvalidSyntaxException e) {
			log("Error on TestingMonitorableInterface");
			return null;
		}
	}
	
	public void reinstallMonitorable1() {
		try {
			this.uninstallBundle(tb2);
			tb2 = this.installBundle("tb2.jar");
			ServiceReference[] refs = getContext().getServiceReferences(
					Monitorable.class.getName(), "(service.pid=" + SV_MONITORABLEID1 + ")");
			
			monitorable = (Monitorable) getContext().getService(refs[0]);			
			((TestingMonitorable) monitorable).setMonitorTestControlInterface(this);			
		} catch (Exception e) {
			log("Fail when we try to reinstall the monitorable");
		}
	}
	
	public void cleanUp(MonitoringJob job) {
		if (job != null) {
			job.stop();
		}
	}
	
	public void cleanUp(DmtSession session, String[] nodes) {
		for (int i=0; i<nodes.length; i++) {
			try {
				session.deleteNode(nodes[i]);
			} catch (DmtException e) {
				log("error deleting the node: " + nodes[i]);
			}
		}
		closeSession(session);
		
	}
	
	/**
	 * @return Returns the eventClassCode.
	 */
	public int getEventClassCode() {
		return eventClassCode;
	}
	/**
	 * @param eventClassCode The eventClassCode to set.
	 */
	public void setEventClassCode(int eventClassCode) {
		this.eventClassCode = eventClassCode;
	}
	/**
	 * @return Returns the alerts.
	 */
	public DmtAlertItem[] getAlerts() {
		return alerts;
	}
	/**
	 * @param alerts The alerts to set.
	 */
	public void setAlerts(DmtAlertItem[] alerts) {
		this.alerts = alerts;
	}
}
