/*
 * Copyright (c) OSGi Alliance (2004, 2016). All Rights Reserved.
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

package org.osgi.test.cases.condpermadmin.tb1;

import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.cases.condpermadmin.service.ConditionalDomTBCService;
import org.osgi.test.cases.condpermadmin.service.ConditionalPermTBCService;
import org.osgi.test.cases.condpermadmin.service.ConditionalTBCService;


public class Activator implements BundleActivator, ConditionalTBCService {
	
  private BundleContext context;
	private ServiceRegistration<ConditionalTBCService>	reg;

  public void start(BundleContext context) throws Exception {
    this.context = context;
		reg = context.registerService(ConditionalTBCService.class, this, null);
	}
	
	public void stop(BundleContext context) throws Exception {
    reg.unregister();
	}
  
  public void checkPermission(final Permission permission) 
    throws SecurityException {
    try {
			AccessController
					.doPrivileged(new PrivilegedExceptionAction<Void>() {
						public Void run() throws SecurityException {
          SecurityManager security = System.getSecurityManager();
          security.checkPermission(permission);
          return null;
        }
      });
    } catch (PrivilegedActionException e) {
      throw (SecurityException) e.getException();
    }
  }

  public void checkStack(Permission permission) throws SecurityException {
		ServiceReference<ConditionalPermTBCService> ref = context
				.getServiceReference(ConditionalPermTBCService.class);
    ConditionalPermTBCService service = context.getService(ref);
    service.checkStack(permission);
    context.ungetService(ref);
  }
 
  public void checkStack2(Permission permission) throws SecurityException {
		ServiceReference<ConditionalDomTBCService> ref = context
				.getServiceReference(ConditionalDomTBCService.class);
	    ConditionalDomTBCService service = context.getService(ref);
	    service.checkStack(permission);
	    context.ungetService(ref);
  }
}
