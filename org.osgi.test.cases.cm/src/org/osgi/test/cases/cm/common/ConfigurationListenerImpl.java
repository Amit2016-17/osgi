/*
 * Copyright (c) OSGi Alliance (2005, 2020). All Rights Reserved.
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

package org.osgi.test.cases.cm.common;

import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.cm.ConfigurationListener;
import org.osgi.test.cases.cm.shared.Synchronizer;

/**
 * 
 * Simple <code>ConfigurationListener</code> implementation to be used in the
 * cm test case. This listener just copies the data from the events, so they can
 * be checked later by the test methods.
 * 
 * @author Jorge Mascena
 */
public class ConfigurationListenerImpl implements ConfigurationListener {

	private String[]			factoryPids;
	private String[]			pids;
	private int[]				types;
	private ServiceReference<ConfigurationAdmin>[]	references;
	private Synchronizer		synchronizer;
	private int					eventCount;
	public static final String	LISTENER_PID_SUFFIX	= "RFC0103";

	/**
	 * Creates <code>ConfigurationListenerImpl</code> to be used in the test
	 * case. Keeps data for just one event.
	 * 
	 * @param synchronizer the <code>Synchronizer</code> instance that
	 *        controls concurrency between the listener and the test execution.
	 */
	public ConfigurationListenerImpl(Synchronizer synchronizer) {
		this(synchronizer, 1);
	}

	/**
	 * Creates <code>ConfigurationListenerImpl</code> to be used in the test
	 * case. Keeps data for up to <code>eventCount</code> events.
	 * 
	 * @param synchronizer the synchronizer instance that controls concurrency
	 *        between the listener and the test execution.
	 * @param eventCount number of events expected by this listener.
	 */
	@SuppressWarnings("unchecked")
	public ConfigurationListenerImpl(Synchronizer synchronizer, int eventCount) {
		this.synchronizer = synchronizer;
		this.eventCount = 0;
		factoryPids = new String[eventCount];
		pids = new String[eventCount];
		types = new int[eventCount];
		references = new ServiceReference[eventCount];
	}

	/**
	 * Copies event data to be checked later. Assumes there's an empty slot to
	 * the event data. Filters out not expected events (generated by the
	 * framework).
	 * 
	 * @param event the broadcasted event.
	 * @see org.osgi.service.cm.ConfigurationListener#configurationEvent(org.osgi.service.cm.ConfigurationEvent)
	 */
	@Override
	public void configurationEvent(ConfigurationEvent event) {
		if ((event.getPid() != null && event.getPid().endsWith(
				LISTENER_PID_SUFFIX))
				|| (event.getFactoryPid() != null && event.getFactoryPid()
						.endsWith(LISTENER_PID_SUFFIX))) {
			synchronized (this) {
				factoryPids[eventCount] = event.getFactoryPid();
				pids[eventCount] = event.getPid();
				references[eventCount] = event.getReference();
				types[eventCount] = event.getType();
				eventCount++;
				synchronizer.signal();
			}
		}
	}

	/**
	 * Gets the Service Reference to the Configuration Admin that generated the
	 * first (or single) event.
	 * 
	 * @return the Service Reference from the first (or single) event
	 * @see org.osgi.service.cm.ConfigurationEvent
	 */
	public ServiceReference<ConfigurationAdmin> getReference() {
		return references[0];
	}

	/**
	 * Gets the Service Reference to the Configuration Admin that generated the
	 * <code>i</code> th event.
	 * 
	 * @param i the position of the event (from 1 to the constructor defined
	 *        <code>eventCount</code>)
	 * @return the Service Reference from the <code>i</code> th event
	 * @see org.osgi.service.cm.ConfigurationEvent
	 */
	public ServiceReference<ConfigurationAdmin> getReference(int i) {
		return references[i - 1];
	}

	/**
	 * Gets the factory pid associated with the first (or single) event.
	 * 
	 * @return Returns the factoryPid of the first (or single) event.
	 * @see org.osgi.service.cm.ConfigurationEvent
	 */
	public String getFactoryPid() {
		return factoryPids[0];
	}

	/**
	 * Gets the factory pid associated with the <code>i</code> th event.
	 * 
	 * @param i the position of the event (from 1 to the constructor defined
	 *        <code>eventCount</code>)
	 * @return Returns the factoryPid of the <code>i</code> th event.
	 * @see org.osgi.service.cm.ConfigurationEvent
	 */
	public String getFactoryPid(int i) {
		return factoryPids[i - 1];
	}

	/**
	 * Gets the pid associated with the first (or single) event.
	 * 
	 * @return Returns the pid of the first (or single) event.
	 * @see org.osgi.service.cm.ConfigurationEvent
	 */
	public String getPid() {
		return pids[0];
	}

	/**
	 * Gets the pid associated with the <code>i</code> th event.
	 * 
	 * @param i the position of the event (from 1 to the constructor defined
	 *        <code>eventCount</code>)
	 * @return Returns the pid of the <code>i</code> th event.
	 * @see org.osgi.service.cm.ConfigurationEvent
	 */
	public String getPid(int i) {
		return pids[i - 1];
	}

	/**
	 * Gets the type of the first (or single) event.
	 * 
	 * @return Returns the type of the first (or single) event.
	 * @see org.osgi.service.cm.ConfigurationEvent
	 */
	public int getType() {
		return types[0];
	}

	/**
	 * Gets the type of the <code>i</code> th event.
	 * 
	 * @param i the position of the event (from 1 to the constructor defined
	 *        <code>eventCount</code>)
	 * @return Returns the type of the <code>i</code> th event.
	 * @see org.osgi.service.cm.ConfigurationEvent
	 */
	public int getType(int i) {
		return types[i - 1];
	}
}
