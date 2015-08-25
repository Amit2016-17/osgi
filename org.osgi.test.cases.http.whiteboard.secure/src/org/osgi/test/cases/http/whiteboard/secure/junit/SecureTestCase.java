package org.osgi.test.cases.http.whiteboard.secure.junit;

import java.io.FilePermission;
import java.security.AccessControlContext;
import java.security.AccessControlException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import org.osgi.framework.AdminPermission;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServicePermission;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.runtime.HttpServiceRuntime;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

public class SecureTestCase extends BaseHttpWhiteboardTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		assertNotNull(System.getSecurityManager());
	}

	/*
	 * The Http Whiteboard implementation must be granted
	 * ServicePermission[interfaceName, GET] to retrieve the Http Whiteboard
	 * services from the service registry.
	 *
	 * Since a Resource may be associated with any service, the RI needs GET on
	 * interfaces '*'.
	 */
	public void testImplHasGETServicePermission() throws Exception {
		BundleContext context = getContext();

		ServiceReference<HttpServiceRuntime> serviceReference =
				context.getServiceReference(HttpServiceRuntime.class);

		Bundle bundle = serviceReference.getBundle();

		AccessControlContext acc = bundle.adapt(AccessControlContext.class);

		try {
			acc.checkPermission(new ServicePermission("*", ServicePermission.GET));
		} catch (AccessControlException ace) {
			fail();
		}
	}

	/*
	 * Bundles that need to introspect the state of the Http runtime will need
	 * ServicePermission[org.osgi.service.http.runtime.HttpServiceRuntime, GET]
	 * to obtain the HttpServiceRuntime service and access the DTO types.
	 */
	public void testHttpServiceRuntimeClientHasGETServicePermission_tb1() throws Exception {
		Bundle bundle = install("tb1.jar");

		try {
			bundle.start();

			AccessControlContext acc = bundle.adapt(AccessControlContext.class);

			acc.checkPermission(new ServicePermission(HttpServiceRuntime.class.getName(), ServicePermission.GET));
		} catch (AccessControlException ace) {
			fail();
		} finally {
			bundle.uninstall();
		}
	}

	/*
	 * Bundles that need to introspect the state of the Http runtime will need
	 * ServicePermission[org.osgi.service.http.runtime.HttpServiceRuntime, GET]
	 * to obtain the HttpServiceRuntime service and access the DTO types.
	 */
	public void testHttpServiceRuntimeClientHasNoGETServicePermission_tb2() throws Exception {
		Bundle bundle = install("tb2.jar");

		try {
			bundle.start();

			AccessControlContext acc = bundle.adapt(AccessControlContext.class);

			acc.checkPermission(new ServicePermission(HttpServiceRuntime.class.getName(), ServicePermission.GET));

			fail();
		} catch (AccessControlException ace) {
		} finally {
			bundle.uninstall();
		}
	}

	/*
	 * The Http Whiteboard implementation must be granted
	 * AdminPermission[*,RESOURCE] so that bundles may use the default
	 * ServletContextHelper implementation.
	 */
	public void testImplHasRESOURCEAdminPermission() throws Exception {
		BundleContext context = getContext();

		ServiceReference<HttpServiceRuntime> serviceReference =
				context.getServiceReference(HttpServiceRuntime.class);

		Bundle bundle = serviceReference.getBundle();

		AccessControlContext acc = bundle.adapt(AccessControlContext.class);

		try {
			acc.checkPermission(new AdminPermission("*", AdminPermission.RESOURCE));
		} catch (AccessControlException ace) {
			fail();
		}
	}

	/*
	 * The Http Whiteboard implementation must capture the AccessControlContext
	 * object of the bundle registering a ServletContextHelper service, and then
	 * use the captured AccessControlContext object when accessing resources
	 * returned by the ServletContextHelper service.
	 */
	public void testImplCapturesAccessControlContext() throws Exception {
		BundleContext context = getContext();

		ServiceReference<HttpServiceRuntime> serviceReference =
				context.getServiceReference(HttpServiceRuntime.class);

		Bundle bundle = serviceReference.getBundle();

		AccessControlContext acc = bundle.adapt(AccessControlContext.class);

		try {
			acc.checkPermission(new FilePermission("/etc/passwd", "read"));
		} catch (AccessControlException ace) {
			fail();
		}

		bundle = install("tb1.jar");

		try {
			bundle.start();

			Dictionary<String, Object> properties = new Hashtable<String, Object>();
			properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN, "/*");
			properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX, "/");
			serviceRegistrations.add(context.registerService(Object.class, new Object(), properties));

			Map<String, List<String>> result = request("/etc/passwd", null);

			assertEquals("500", result.get("responseCode").get(0));
		} finally {
			bundle.uninstall();
		}
	}

}
