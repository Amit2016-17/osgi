package org.osgi.test.cases.http.whiteboard.junit;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Manifest;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.runtime.HttpServiceRuntime;
import org.osgi.service.http.runtime.dto.ErrorPageDTO;
import org.osgi.service.http.runtime.dto.FailedErrorPageDTO;
import org.osgi.service.http.runtime.dto.FailedFilterDTO;
import org.osgi.service.http.runtime.dto.FailedResourceDTO;
import org.osgi.service.http.runtime.dto.FailedServletContextDTO;
import org.osgi.service.http.runtime.dto.FailedServletDTO;
import org.osgi.service.http.runtime.dto.FilterDTO;
import org.osgi.service.http.runtime.dto.ListenerDTO;
import org.osgi.service.http.runtime.dto.RequestInfoDTO;
import org.osgi.service.http.runtime.dto.ResourceDTO;
import org.osgi.service.http.runtime.dto.ServletContextDTO;
import org.osgi.service.http.runtime.dto.ServletDTO;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.test.support.OSGiTestCase;

public abstract class BaseHttpWhiteboardTestCase extends OSGiTestCase {

	public static final String	DEFAULT	= HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME;

	protected RequestInfoDTO calculateRequestInfoDTO(String string) {
		HttpServiceRuntime httpServiceRuntime = getHttpServiceRuntime();

		return httpServiceRuntime.calculateRequestInfoDTO(string);
	}

	protected String drainInputStream(InputStream is) throws IOException {
		byte[] bytes = new byte[1024];

		StringBuffer buffer = new StringBuffer(1024);

		int length;
		while ((length = is.read(bytes)) != -1) {
			String chunk = new String(bytes, 0, length);
			buffer.append(chunk);
		}
		return buffer.toString().trim();
	}

	protected String[] getBundlePaths() {
		return new String[0];
	}

	protected ErrorPageDTO getErrorPageDTOByName(String context, String name) {
		ServletContextDTO servletContextDTO = getServletContextDTOByName(context);

		if (servletContextDTO == null) {
			return null;
		}

		for (ErrorPageDTO errorPageDTO : servletContextDTO.errorPageDTOs) {
			if (name.equals(errorPageDTO.name)) {
				return errorPageDTO;
			}
		}

		return null;
	}

	protected FailedErrorPageDTO getFailedErrorPageDTOByName(String name) {
		for (FailedErrorPageDTO failedErrorPageDTO : getFailedErrorPageDTOs()) {
			if (name.equals(failedErrorPageDTO.name)) {
				return failedErrorPageDTO;
			}
		}

		return null;
	}

	protected FailedErrorPageDTO[] getFailedErrorPageDTOs() {
		HttpServiceRuntime httpServiceRuntime = getHttpServiceRuntime();

		return httpServiceRuntime.getRuntimeDTO().failedErrorPageDTOs;
	}

	protected FailedFilterDTO getFailedFilterDTOByName(String name) {
		for (FailedFilterDTO failedFilterDTO : getFailedFilterDTOs()) {
			if (name.equals(failedFilterDTO.name)) {
				return failedFilterDTO;
			}
		}

		return null;
	}

	protected FailedFilterDTO[] getFailedFilterDTOs() {
		return getHttpServiceRuntime().getRuntimeDTO().failedFilterDTOs;
	}

	protected FailedResourceDTO getFailedResourceDTOByServiceId(long serviceId) {
		for (FailedResourceDTO failedResourceDTO : getFailedResourceDTOs()) {
			if (serviceId == failedResourceDTO.serviceId) {
				return failedResourceDTO;
			}
		}

		return null;
	}

	protected FailedResourceDTO[] getFailedResourceDTOs() {
		return getHttpServiceRuntime().getRuntimeDTO().failedResourceDTOs;
	}

	protected FailedServletContextDTO getFailedServletContextDTOByName(String name) {
		for (FailedServletContextDTO failedServletContextDTO : getFailedServletContextDTOs()) {
			if (name.equals(failedServletContextDTO.name)) {
				return failedServletContextDTO;
			}
		}

		return null;
	}

	protected FailedServletContextDTO[] getFailedServletContextDTOs() {
		return getHttpServiceRuntime().getRuntimeDTO().failedServletContextDTOs;
	}

	protected FailedServletDTO getFailedServletDTOByName(String name) {
		for (FailedServletDTO failedServletDTO : getFailedServletDTOs()) {
			if (name.equals(failedServletDTO.name)) {
				return failedServletDTO;
			}
		}

		return null;
	}

	protected FailedServletDTO[] getFailedServletDTOs() {
		HttpServiceRuntime httpServiceRuntime = getHttpServiceRuntime();

		return httpServiceRuntime.getRuntimeDTO().failedServletDTOs;
	}

	protected FilterDTO getFilterDTOByName(String contextName, String name) {
		ServletContextDTO servletContextDTO = getServletContextDTOByName(contextName);

		if (servletContextDTO == null) {
			return null;
		}

		for (FilterDTO filterDTO : servletContextDTO.filterDTOs) {
			if (name.equals(filterDTO.name)) {
				return filterDTO;
			}
		}

		return null;
	}

	protected HttpService getHttpService() {
		BundleContext context = getContext();

		ServiceReference<HttpService> serviceReference =
				context.getServiceReference(HttpService.class);

		assertNotNull(serviceReference);

		return context.getService(serviceReference);
	}

	protected HttpServiceRuntime getHttpServiceRuntime() {
		BundleContext context = getContext();

		ServiceReference<HttpServiceRuntime> serviceReference =
				context.getServiceReference(HttpServiceRuntime.class);

		assertNotNull(serviceReference);

		return context.getService(serviceReference);
	}

	protected ListenerDTO getListenerDTOByServiceId(String contextName, long serviceId) {
		ServletContextDTO servletContextDTO = getServletContextDTOByName(contextName);

		if (servletContextDTO == null) {
			return null;
		}

		for (ListenerDTO listenerDTO : servletContextDTO.listenerDTOs) {
			if (serviceId == listenerDTO.serviceId) {
				return listenerDTO;
			}
		}

		return null;
	}

	protected ResourceDTO getResourceDTOByServiceId(String contextName, long serviceId) {
		ServletContextDTO servletContextDTO = getServletContextDTOByName(contextName);

		if (servletContextDTO == null) {
			return null;
		}

		for (ResourceDTO resourceDTO : servletContextDTO.resourceDTOs) {
			if (serviceId == resourceDTO.serviceId) {
				return resourceDTO;
			}
		}

		return null;
	}

	protected long getServiceId(ServiceRegistration<?> sr) {
		return (Long) sr.getReference().getProperty(Constants.SERVICE_ID);
	}

	protected ServletContextDTO getServletContextDTOByName(String name) {
		for (ServletContextDTO servletContextDTO : getServletContextDTOs()) {
			if (name.equals(servletContextDTO.name)) {
				return servletContextDTO;
			}
		}

		return null;
	}

	protected ServletContextDTO[] getServletContextDTOs() {
		return getHttpServiceRuntime().getRuntimeDTO().servletContextDTOs;
	}

	protected ServletDTO getServletDTOByName(String context, String name) {
		ServletContextDTO servletContextDTO = getServletContextDTOByName(context);

		if (servletContextDTO == null) {
			return null;
		}

		for (ServletDTO servletDTO : servletContextDTO.servletDTOs) {
			if (name.equals(servletDTO.name)) {
				return servletDTO;
			}
		}

		return null;
	}

	protected URL getServerURL(String path) throws MalformedURLException {
		if (!path.startsWith("/")) {
			path = "/" + path;
		}

		return new URL(
				"http", getProperty("org.apache.felix.http.host"),
				getIntegerProperty("org.osgi.service.http.port", 8080), path);
	}

	protected String getSymbolicName(ClassLoader classLoader) throws IOException {
		InputStream inputStream = classLoader.getResourceAsStream(
				"META-INF/MANIFEST.MF");

		Manifest manifest = new Manifest(inputStream);

		return manifest.getMainAttributes().getValue(Constants.BUNDLE_SYMBOLICNAME);
	}

	protected void log(String message) {
		System.out.println(message);
	}

	protected String request(String path) throws InterruptedException, IOException {
		URL serverURL = getServerURL(path);

		log("Requesting: " + serverURL.toString()); //$NON-NLS-1$

		HttpURLConnection connection = (HttpURLConnection) serverURL.openConnection();

		connection.setInstanceFollowRedirects(false);
		connection.setConnectTimeout(150 * 1000);
		connection.setReadTimeout(150 * 1000);
		connection.connect();

		int responseCode = connection.getResponseCode();

		InputStream stream;

		for (int i = 0; i < 9; i++) {
			if (responseCode >= 400) {
				if (i < 8) {
					connection.connect();

					responseCode = connection.getResponseCode();

					Thread.sleep(100);
				}
				else {
					stream = connection.getErrorStream();
				}
			}
			else {
				stream = connection.getInputStream();
			}
		}

		if (responseCode >= 400) {
			stream = connection.getErrorStream();
		}
		else {
			stream = connection.getInputStream();
		}

		try {
			return drainInputStream(stream);
		} finally {
			stream.close();
		}
	}

	protected Map<String, List<String>> request(String path, Map<String, List<String>> headers) throws InterruptedException, IOException {
		URL serverURL = getServerURL(path);

		log("Requesting: " + serverURL.toString()); //$NON-NLS-1$

		HttpURLConnection connection = (HttpURLConnection) serverURL.openConnection();

		if (headers != null) {
			for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
				for (String entryValue : entry.getValue()) {
					connection.setRequestProperty(entry.getKey(), entryValue);
				}
			}
		}

		int responseCode = connection.getResponseCode();

		Map<String, List<String>> map = new HashMap<String, List<String>>(connection.getHeaderFields());
		map.put("responseCode", Collections.singletonList(String.valueOf(responseCode)));

		InputStream stream;

		if (responseCode >= 400) {
			stream = connection.getErrorStream();
		}
		else {
			stream = connection.getInputStream();
		}

		try {
			map.put("responseBody", Arrays.asList(drainInputStream(stream)));

			return map;
		} finally {
			stream.close();
		}
	}

	protected void setUp() throws Exception {
		for (String bundlePath : getBundlePaths()) {
			Bundle bundle = install(bundlePath);

			bundle.start();

			bundles.add(bundle);
		}
	}

	protected void tearDown() throws Exception {
		for (ServiceRegistration<?> serviceRegistration : serviceRegistrations) {
			serviceRegistration.unregister();
		}

		serviceRegistrations.clear();

		for (Bundle bundle : bundles) {
			bundle.uninstall();
		}

		bundles.clear();
	}

	protected List<Bundle> bundles = new ArrayList<Bundle>();
	protected Set<ServiceRegistration<?>> serviceRegistrations = new HashSet<ServiceRegistration<?>>();

}
