package org.osgi.test.cases.http.whiteboard.tb2;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

public class HttpWhiteboardTestBundle2 implements BundleActivator {

	public void start(BundleContext context) throws Exception {
		Dictionary<String, Object> properties;

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "default");
		properties.put("test.property", "test.value");
		serviceRegistrations.add(
				context.registerService(
						ServletContextHelper.class, new ServletContextHelperFactory(),
						properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "sc1");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/sc1");
		serviceRegistrations.add(
				context.registerService(
						ServletContextHelper.class, new ServletContextHelperFactory(),
						properties));
	}

	public void stop(BundleContext context) throws Exception {
		for (ServiceRegistration<?> serviceRegistration : serviceRegistrations) {
			serviceRegistration.unregister();
		}

		serviceRegistrations.clear();
	}

	private List<ServiceRegistration<?>>	serviceRegistrations	= new CopyOnWriteArrayList<ServiceRegistration<?>>();

}