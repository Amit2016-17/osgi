package org.osgi.test.cases.http.whiteboard.junit;

import java.io.IOException;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.servlet.AsyncContext;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.runtime.dto.FilterDTO;
import org.osgi.service.http.runtime.dto.RequestInfoDTO;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.test.cases.http.whiteboard.junit.mock.MockFilter;
import org.osgi.test.cases.http.whiteboard.junit.mock.MockServlet;

public class FilterTestCase extends BaseHttpWhiteboardTestCase {

	public void test_table_140_5_HTTP_WHITEBOARD_FILTER_ASYNC_SUPPORTED_validate() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_ASYNC_SUPPORTED, "true");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "/a");
		ServiceRegistration<Filter> sr = context.registerService(Filter.class, new MockFilter(), properties);
		serviceRegistrations.add(sr);

		FilterDTO filterDTO = getFilterDTOByName(HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME, "a");
		assertNotNull(filterDTO);
		assertTrue(filterDTO.asyncSupported);

		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_ASYNC_SUPPORTED, "false");
		sr.setProperties(properties);

		filterDTO = getFilterDTOByName(HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME, "a");
		assertNotNull(filterDTO);
		assertFalse(filterDTO.asyncSupported);

		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_ASYNC_SUPPORTED, 234l);
		sr.setProperties(properties);

		filterDTO = getFilterDTOByName(HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME, "a");
		assertNotNull(filterDTO);
		assertFalse(filterDTO.asyncSupported);

		properties.remove(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_ASYNC_SUPPORTED);
		sr.setProperties(properties);

		filterDTO = getFilterDTOByName(HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME, "a");
		assertNotNull(filterDTO);
		assertFalse(filterDTO.asyncSupported);
	}

	public void test_table_140_5_HTTP_WHITEBOARD_FILTER_DISPATCHER_async() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_ASYNC_SUPPORTED, "true");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_DISPATCHER, "ASYNC");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_NAME, "b");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "/b");
		ServiceRegistration<?> srA = context.registerService(Filter.class, new MockFilter().around("b"), properties);
		serviceRegistrations.add(srA);

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "b");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/b");
		ServiceRegistration<?> srB = context.registerService(Servlet.class, new MockServlet().content("a"), properties);
		serviceRegistrations.add(srB);

		final AtomicBoolean invoked = new AtomicBoolean(false);
		MockServlet mockServlet = new MockServlet() {

			final ExecutorService	executor	= Executors.newCachedThreadPool();

			protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
				doGetAsync(req.startAsync());
			}

			private void doGetAsync(final AsyncContext asyncContext) {
				executor.submit(new Callable<Void>() {
					public Void call() throws Exception {
						try {
							invoked.set(true);

							asyncContext.dispatch("/b");
						} finally {
							asyncContext.complete();
						}

						return null;
					}
				});
			}

		};

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED, "true");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		ServiceRegistration<?> srC = context.registerService(Servlet.class, mockServlet, properties);
		serviceRegistrations.add(srC);

		RequestInfoDTO requestInfoDTO = calculateRequestInfoDTO("/a");
		assertNotNull(requestInfoDTO);
		assertEquals(0, requestInfoDTO.filterDTOs.length);
		assertEquals(srC.getReference().getProperty(Constants.SERVICE_ID), requestInfoDTO.servletDTO.serviceId);

		assertEquals("bab", request("a"));
		assertTrue(invoked.get());
	}

	public void test_table_140_5_HTTP_WHITEBOARD_FILTER_DISPATCHER_request() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "/a");
		ServiceRegistration<?> srA = context.registerService(Filter.class, new MockFilter().around("b"), properties);
		serviceRegistrations.add(srA);

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		ServiceRegistration<?> srB = context.registerService(Servlet.class, new MockServlet().content("a"), properties);
		serviceRegistrations.add(srB);

		RequestInfoDTO requestInfoDTO = calculateRequestInfoDTO("/a");
		assertNotNull(requestInfoDTO);
		assertEquals(1, requestInfoDTO.filterDTOs.length);
		FilterDTO filterDTO = requestInfoDTO.filterDTOs[0];
		assertEquals(srA.getReference().getProperty(Constants.SERVICE_ID), filterDTO.serviceId);
		assertEquals(1, filterDTO.dispatcher.length);
		assertEquals("REQUEST", filterDTO.dispatcher[0]);

		assertEquals("bab", request("a"));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_DISPATCHER, "REQUEST");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "/a");
		srA.setProperties(properties);

		assertEquals("bab", request("a"));
	}

	public void test_table_140_5_HTTP_WHITEBOARD_FILTER_DISPATCHER_include() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_DISPATCHER, "INCLUDE");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "/a");
		ServiceRegistration<?> srA = context.registerService(Filter.class, new MockFilter().around("b"), properties);
		serviceRegistrations.add(srA);

		FilterDTO filterDTO = getFilterDTOByName(HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME, "a");
		assertNotNull(filterDTO);
		assertEquals(1, filterDTO.dispatcher.length);
		assertEquals("INCLUDE", filterDTO.dispatcher[0]);

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		ServiceRegistration<?> srB = context.registerService(Servlet.class, new MockServlet().content("a"), properties);
		serviceRegistrations.add(srB);

		RequestInfoDTO requestInfoDTO = calculateRequestInfoDTO("/a");
		assertNotNull(requestInfoDTO);
		assertEquals(0, requestInfoDTO.filterDTOs.length);
		assertEquals("a", request("a"));

		MockServlet mockServlet = new MockServlet() {

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				RequestDispatcher requestDispatcher = request.getRequestDispatcher("/a");

				requestDispatcher.include(request, response);
			}

		};

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "b");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/b");
		ServiceRegistration<?> srC = context.registerService(Servlet.class, mockServlet, properties);
		serviceRegistrations.add(srC);

		assertEquals("bab", request("b"));
	}

	public void test_table_140_5_HTTP_WHITEBOARD_FILTER_DISPATCHER_forward() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_DISPATCHER, "FORWARD");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "/a");
		ServiceRegistration<?> srA = context.registerService(Filter.class, new MockFilter().around("b"), properties);
		serviceRegistrations.add(srA);

		FilterDTO filterDTO = getFilterDTOByName(HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME, "a");
		assertNotNull(filterDTO);
		assertEquals(1, filterDTO.dispatcher.length);
		assertEquals("FORWARD", filterDTO.dispatcher[0]);

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		ServiceRegistration<?> srB = context.registerService(Servlet.class, new MockServlet().content("a"), properties);
		serviceRegistrations.add(srB);

		RequestInfoDTO requestInfoDTO = calculateRequestInfoDTO("/a");
		assertNotNull(requestInfoDTO);
		assertEquals(0, requestInfoDTO.filterDTOs.length);
		assertEquals("a", request("a"));

		MockServlet mockServlet = new MockServlet() {

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				RequestDispatcher requestDispatcher = request.getRequestDispatcher("/a");

				requestDispatcher.forward(request, response);
			}

		};

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "b");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/b");
		ServiceRegistration<?> srC = context.registerService(Servlet.class, mockServlet, properties);
		serviceRegistrations.add(srC);

		assertEquals("bab", request("b"));
	}

	public void test_table_140_5_HTTP_WHITEBOARD_FILTER_DISPATCHER_error() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_DISPATCHER, "ERROR");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_SERVLET, "a");
		ServiceRegistration<?> srA = context.registerService(Filter.class, new MockFilter().around("b"), properties);
		serviceRegistrations.add(srA);

		FilterDTO filterDTO = getFilterDTOByName(HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME, "a");
		assertNotNull(filterDTO);
		assertEquals(1, filterDTO.dispatcher.length);
		assertEquals("ERROR", filterDTO.dispatcher[0]);

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ERROR_PAGE, "4xx");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		ServiceRegistration<?> srB = context.registerService(Servlet.class, new MockServlet().content("a"), properties);
		serviceRegistrations.add(srB);

		RequestInfoDTO requestInfoDTO = calculateRequestInfoDTO("/a");
		assertNotNull(requestInfoDTO);
		assertEquals(0, requestInfoDTO.filterDTOs.length);
		assertEquals("a", request("a"));

		MockServlet mockServlet = new MockServlet() {

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			}

		};

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "b");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/b");
		ServiceRegistration<?> srC = context.registerService(Servlet.class, mockServlet, properties);
		serviceRegistrations.add(srC);

		assertEquals("bab", request("b"));
	}

	public void test_table_140_5_HTTP_WHITEBOARD_FILTER_DISPATCHER_multiple() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_DISPATCHER, new String[] {"REQUEST", "ERROR"});
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_SERVLET, "a");
		ServiceRegistration<?> srA = context.registerService(Filter.class, new MockFilter().around("b"), properties);
		serviceRegistrations.add(srA);

		FilterDTO filterDTO = getFilterDTOByName(HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME, "a");
		assertNotNull(filterDTO);
		assertEquals(2, filterDTO.dispatcher.length);
		Arrays.sort(filterDTO.dispatcher);
		assertTrue(Arrays.binarySearch(filterDTO.dispatcher, "ERROR") >= 0);
		assertTrue(Arrays.binarySearch(filterDTO.dispatcher, "REQUEST") >= 0);

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ERROR_PAGE, "4xx");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		ServiceRegistration<?> srB = context.registerService(Servlet.class, new MockServlet().content("a"), properties);
		serviceRegistrations.add(srB);

		RequestInfoDTO requestInfoDTO = calculateRequestInfoDTO("/a");
		assertNotNull(requestInfoDTO);
		assertEquals(1, requestInfoDTO.filterDTOs.length);
		assertEquals("bab", request("a"));

		MockServlet mockServlet = new MockServlet() {

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			}

		};

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "b");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/b");
		ServiceRegistration<?> srC = context.registerService(Servlet.class, mockServlet, properties);
		serviceRegistrations.add(srC);

		assertEquals("bab", request("b"));
	}

	public void test_table_140_5_HTTP_WHITEBOARD_FILTER_NAME() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		serviceRegistrations.add(context.registerService(Servlet.class, new MockServlet().content("a"), properties));

		MockFilter mockFilter = new MockFilter() {

			@Override
			public void init(FilterConfig config) throws ServletException {
				super.init(config);
				this.config = config;
			}

			@Override
			public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
				response.getWriter().write(config.getFilterName());
				chain.doFilter(request, response);
			}

			private FilterConfig config;

		};

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "/a");
		ServiceRegistration<?> srA = context.registerService(Filter.class, mockFilter, properties);
		serviceRegistrations.add(srA);

		RequestInfoDTO requestInfoDTO = calculateRequestInfoDTO("/a");
		assertNotNull(requestInfoDTO);
		assertEquals(1, requestInfoDTO.filterDTOs.length);
		assertEquals(mockFilter.getClass().getName(), requestInfoDTO.filterDTOs[0].name);
		assertEquals(mockFilter.getClass().getName() + "a", request("a"));

		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_NAME, "b");
		srA.setProperties(properties);

		requestInfoDTO = calculateRequestInfoDTO("/a");
		assertNotNull(requestInfoDTO);
		assertEquals(1, requestInfoDTO.filterDTOs.length);
		assertEquals("b", requestInfoDTO.filterDTOs[0].name);
		assertEquals("ba", request("a"));
	}

	public void test_table_140_5_HTTP_WHITEBOARD_SERVLET_PATTERN() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, new String[] {"", "/"});
		serviceRegistrations.add(context.registerService(Servlet.class, new MockServlet().content("a"), properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "/*");
		ServiceRegistration<?> srA = context.registerService(Filter.class, new MockFilter().around("b"), properties);
		serviceRegistrations.add(srA);

		RequestInfoDTO requestInfoDTO = calculateRequestInfoDTO("/a");
		assertNotNull(requestInfoDTO);
		assertEquals(1, requestInfoDTO.filterDTOs.length);
		assertEquals("bab", request("a"));
		assertEquals("bab", request("a.html"));
		assertEquals("bab", request("some/path/b.html"));
		assertEquals("a", request(""));

		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "");
		srA.setProperties(properties);

		requestInfoDTO = calculateRequestInfoDTO("/");
		assertNotNull(requestInfoDTO);
		assertEquals(1, requestInfoDTO.filterDTOs.length);
		assertEquals("a", request("a"));
		assertEquals("a", request("a.html"));
		assertEquals("a", request("some/path/b.html"));
		assertEquals("bab", request(""));

		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "*.html");
		srA.setProperties(properties);

		requestInfoDTO = calculateRequestInfoDTO("/a.html");
		assertEquals(1, requestInfoDTO.filterDTOs.length);
		assertEquals("a", request("a"));
		assertEquals("bab", request("a.html"));
		assertEquals("bab", request("some/path/b.html"));
		assertEquals("a", request(""));

		requestInfoDTO = calculateRequestInfoDTO("/a.html");
		assertEquals(1, requestInfoDTO.filterDTOs.length);
		assertEquals("bab", request("/a.html"));

		requestInfoDTO = calculateRequestInfoDTO("/some/path/a.html");
		assertEquals(1, requestInfoDTO.filterDTOs.length);
		assertEquals("bab", request("some/path/a.html"));
	}

}