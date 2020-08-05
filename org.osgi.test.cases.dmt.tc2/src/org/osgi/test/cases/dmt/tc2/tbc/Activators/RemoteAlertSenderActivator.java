/*
 * Copyright (c) The Open Services Gateway Initiative (2004).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the Open Services Gateway Initiative
 * (OSGI) Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of OSGi). OSGi is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and OSGI DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL OSGI BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
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
 * Jul 12, 2005  Luiz Felipe Guimaraes
 * 118           Implement sendAlert
 * ============  ==============================================================
 */
package org.osgi.test.cases.dmt.tc2.tbc.Activators;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.dmt.notification.spi.RemoteAlertSender;
import org.osgi.test.cases.dmt.tc2.tbc.DmtConstants;


/**
 * @author Luiz Felipe Guimaraes
 *
 */
public class RemoteAlertSenderActivator implements BundleActivator {

	private ServiceRegistration<RemoteAlertSender>	servReg;
	
	
	public static final int RANKING = 5; 

	private RemoteAlertSenderImpl testRemoteAlertSenderImpl;

	public RemoteAlertSenderActivator() {
	}
	
	@Override
	public void start(BundleContext bc) throws Exception {
		// creating the service
		testRemoteAlertSenderImpl = new RemoteAlertSenderImpl();
		
		Dictionary<String,Object> ht = new Hashtable<>();
		ht.put("principals", new String[] { DmtConstants.PRINCIPAL, DmtConstants.PRINCIPAL_2 });
		ht.put(Constants.SERVICE_RANKING, Integer.valueOf(RemoteAlertSenderActivator.RANKING));	
		servReg = bc.registerService(RemoteAlertSender.class,
				testRemoteAlertSenderImpl, ht);
		System.out.println("RemoteAlertSender activated.");
	}

	@Override
	public void stop(BundleContext arg0) throws Exception {
		// unregistering the service
		servReg.unregister();
	}

}
