/*
 * Copyright (c) OSGi Alliance (2004, 2013). All Rights Reserved.
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

package org.osgi.test.cases.event.secure.tb1;

import java.security.AccessController;
import java.security.PrivilegedAction;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.test.cases.event.secure.service.SenderService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * A bundle that registers a SenderService service.
 *
 * @author $Id$
 */
public class Activator implements BundleActivator, SenderService {

	private ServiceRegistration<SenderService>	senderServiceReg;
	ServiceTracker<EventAdmin, EventAdmin>		eventAdminTracker;

	public void start(BundleContext context) throws Exception {
		eventAdminTracker = new ServiceTracker<EventAdmin, EventAdmin>(context, EventAdmin.class, null);
		eventAdminTracker.open();
		senderServiceReg = context.registerService(SenderService.class, this, null);
	}

	public void stop(BundleContext context) throws Exception {
		senderServiceReg.unregister();
		eventAdminTracker.close();
	}

	public void postEvent(final Event event) {
		AccessController.doPrivileged(new PrivilegedAction<Void>() {
			public Void run() {
				eventAdminTracker.getService().postEvent(event);
				return null;
			}
		});
	}

	public void sendEvent(final Event event) {
		AccessController.doPrivileged(new PrivilegedAction<Void>() {
			public Void run() {
				eventAdminTracker.getService().sendEvent(event);
				return null;
			}
		});
	}
}
