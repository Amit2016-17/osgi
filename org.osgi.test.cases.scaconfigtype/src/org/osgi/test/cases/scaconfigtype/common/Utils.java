/*
 * Copyright (c) OSGi Alliance (2008, 2010). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osgi.test.cases.scaconfigtype.common;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;

import junit.framework.Assert;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author <a href="mailto:david.savage@paremus.com">David Savage</a>
 *
 */
public class Utils {
	/**
	 * Creates the basic SCA attributes to export a service with the given sca binding name
	 * @param bindingName
	 * @return
	 */
	public static Hashtable getBasicSCAAttributes(String bindingName) {
		Hashtable<String, String> dictionary = new Hashtable<String, String>();
		dictionary.put(RemoteServiceConstants.SERVICE_EXPORTED_INTERFACES, "*");
		dictionary.put(RemoteServiceConstants.SERVICE_EXPORTED_CONFIGS, SCAConfigConstants.ORG_OSGI_SCA_CONFIG);
		dictionary.put(SCAConfigConstants.ORG_OSGI_SCA_BINDING, bindingName);
		return dictionary;
	}

	/**
	 * Creates a new value not previously contained in this list
	 * @param values
	 * @return
	 */
	public static String fabricateValue(List values) {
		Assert.assertFalse(values.isEmpty());
		
		String type = (String) values.get(0);
		do {
			type += "foo";
		}
		while ( values.contains( type ) );
		
		return type;

	}
	
	/**
	 * Converts the property value to a list of values from the following classes:
	 *   <ul>
	 *   <li>String space delimited</li>
	 *   <li>Collection</ul>
	 *   <li>String[]</ul>
	 * @param configProperty
	 * @return
	 */
	public static List propertyToList(Object configProperty) {
		List list = new ArrayList(); // collect all supported config types
		if (configProperty instanceof String) {
			// TODO verify if space delimiter is valid for all sca properties
			StringTokenizer st = new StringTokenizer((String)configProperty, " ");
			while (st.hasMoreTokens()) {
				list.add(st.nextToken());
			}
		} else if (configProperty instanceof Collection) {
			Collection col = (Collection) configProperty;
			list.addAll(col);
		} else { // assume String[]
			String[] arr = (String[])configProperty;
			if (arr != null) {
				for (int i = 0; i < arr.length; i++) {
					list.add(arr[i]);
				}
			}
		}
		
		return list;
	}

	/**
	 * Looks for a service that exports the specified key and returns the list of values
	 * associated with that key
	 * 
	 * @param ctx
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static List getServiceAdvert(BundleContext ctx, String key) throws Exception {
		Filter filter = ctx.createFilter("(" +
				key + "=*)");
		ServiceTracker dpTracker = new ServiceTracker(ctx, filter, null);
		dpTracker.open();
		
		List vals = Collections.EMPTY_LIST;
		
		Object dp = dpTracker.waitForService(TestConstants.SERVICE_TIMEOUT);

		if ( dp != null ) {
			ServiceReference dpReference = dpTracker.getServiceReference();

			if ( dpReference != null ) { 
				// collect all supported config types
				vals = propertyToList(dpReference.getProperty(key)); 				
				dpTracker.close();
				return vals;
			}			
		}
		
		dpTracker.close();		
		return vals;
	}

    private static class InvocationHandlerImpl implements InvocationHandler {
        private Object instance;

        public InvocationHandlerImpl(Object instance) {
            super();
            this.instance = instance;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Method m = instance.getClass().getMethod(method.getName(), method.getParameterTypes());
            try {
                return m.invoke(instance, args);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }

    }

    /**
     * Returns a string representation of the given bundle.
     *
     * @param b
     * @param verbose
     * @return
     */
    public static String bundleStatus(Bundle bundle, boolean verbose) {
        StringBuffer sb = new StringBuffer();
        sb.append(bundle.getBundleId()).append(" ").append(bundle.getSymbolicName());
        int s = bundle.getState();
        if ((s & Bundle.UNINSTALLED) != 0) {
            sb.append(" UNINSTALLED");
        }
        if ((s & Bundle.INSTALLED) != 0) {
            sb.append(" INSTALLED");
        }
        if ((s & Bundle.RESOLVED) != 0) {
            sb.append(" RESOLVED");
        }
        if ((s & Bundle.STARTING) != 0) {
            sb.append(" STARTING");
        }
        if ((s & Bundle.STOPPING) != 0) {
            sb.append(" STOPPING");
        }
        if ((s & Bundle.ACTIVE) != 0) {
            sb.append(" ACTIVE");
        }

        if (verbose) {
            sb.append(" ").append(bundle.getLocation());
            sb.append(" ").append(bundle.getHeaders());
        }
        return sb.toString();
    }

    /**
     * A utility to cast the object to the given interface. If the class for the object
     * is loaded by a different classloader, a proxy will be created.
     *
     * @param <T>
     * @param obj
     * @param cls
     * @return
     */
    public static <T> T cast(Object obj, Class<T> cls) {
        if (obj == null) {
            return null;
        }
        if (cls.isInstance(obj)) {
            return cls.cast(obj);
        } else {
            return cls.cast(Proxy.newProxyInstance(cls.getClassLoader(),
                                                   new Class<?>[] {cls},
                                                   new InvocationHandlerImpl(obj)));
        }
    }
}
