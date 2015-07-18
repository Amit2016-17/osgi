/*
 * Copyright (c) OSGi Alliance (2014, 2015). All Rights Reserved.
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
package org.osgi.test.cases.http.whiteboard.junit;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.servlet.AsyncContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.runtime.dto.DTOConstants;
import org.osgi.service.http.runtime.dto.ErrorPageDTO;
import org.osgi.service.http.runtime.dto.FailedServletDTO;
import org.osgi.service.http.runtime.dto.RequestInfoDTO;
import org.osgi.service.http.runtime.dto.ServletDTO;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

public class HttpWhiteboardTestCase extends BaseHttpWhiteboardTestCase {

	@Override
	protected String[] getBundlePaths() {
		return new String[0];// {"/tb1.jar", "/tb2.jar"};
	}

	public void test_140_4_4to5() throws Exception {
		BundleContext context = getContext();

		final AtomicBoolean invoked = new AtomicBoolean(false);

		Servlet servlet = new HttpServlet() {

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response)
					throws ServletException, IOException {

				invoked.set(true);

				response.getWriter().write("a");
			}

		};

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, new String[] {"/a", "/b"});
		serviceRegistrations.add(context.registerService(Servlet.class, servlet, properties));

		assertEquals("a", request("a"));
		assertTrue(invoked.get());
		invoked.set(false);
		assertEquals("a", request("b"));
		assertTrue(invoked.get());
	}

	public void test_140_4_9() throws Exception {
		BundleContext context = getContext();

		final AtomicBoolean invoked = new AtomicBoolean(false);

		Servlet servlet = new HttpServlet() {

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response)
					throws ServletException, IOException {

				invoked.set(true);

				response.getWriter().write("a");
			}

		};

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/*");
		serviceRegistrations.add(context.registerService(Servlet.class, servlet, properties));

		assertEquals("a", request("a"));
		assertTrue(invoked.get());
		invoked.set(false);
		assertEquals("a", request("b.html"));
		assertTrue(invoked.get());
		invoked.set(false);
		assertEquals("a", request("some/path/b.html"));
		assertTrue(invoked.get());
		assertEquals("404", request("", null).get("responseCode").get(0));
	}

	public void test_140_4_10() throws Exception {
		BundleContext context = getContext();

		final AtomicBoolean invoked = new AtomicBoolean(false);

		Servlet servlet = new HttpServlet() {

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response)
					throws ServletException, IOException {

				invoked.set(true);

				response.getWriter().write("a");
			}

		};

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "*.html");
		serviceRegistrations.add(context.registerService(Servlet.class, servlet, properties));

		assertEquals("a", request("a.html"));
		assertTrue(invoked.get());
		invoked.set(false);
		assertEquals("a", request("b.html"));
		assertTrue(invoked.get());
		invoked.set(false);
		assertEquals("a", request("some/path/b.html"));
		assertTrue(invoked.get());
		assertEquals("404", request("a.xhtml", null).get("responseCode").get(0));
		assertEquals("404", request("some/path/a.xhtml", null).get("responseCode").get(0));
	}

	public void test_140_4_11to13() throws Exception {
		BundleContext context = getContext();

		final AtomicBoolean invoked = new AtomicBoolean(false);

		Servlet servlet = new HttpServlet() {

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response)
					throws ServletException, IOException {

				invoked.set(true);

				PrintWriter writer = response.getWriter();
				writer.write((request.getContextPath() == null) ? "" : request.getContextPath());
				writer.write(":");
				writer.write((request.getServletPath() == null) ? "" : request.getServletPath());
				writer.write(":");
				writer.write((request.getPathInfo() == null) ? "" : request.getPathInfo());
			}

		};

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "");
		serviceRegistrations.add(context.registerService(Servlet.class, servlet, properties));

		assertEquals("::/", request(""));
		assertTrue(invoked.get());
		assertEquals("404", request("a.xhtml", null).get("responseCode").get(0));
		assertEquals("404", request("some/path/a.xhtml", null).get("responseCode").get(0));
	}

	public void test_140_4_14to15() throws Exception {
		BundleContext context = getContext();

		final AtomicBoolean invoked = new AtomicBoolean(false);

		Servlet servlet = new HttpServlet() {

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response)
					throws ServletException, IOException {

				invoked.set(true);

				PrintWriter writer = response.getWriter();
				writer.write((request.getContextPath() == null) ? "" : request.getContextPath());
				writer.write(":");
				writer.write((request.getServletPath() == null) ? "" : request.getServletPath());
				writer.write(":");
				writer.write((request.getPathInfo() == null) ? "" : request.getPathInfo());
			}

		};

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/");
		serviceRegistrations.add(context.registerService(Servlet.class, servlet, properties));

		assertEquals(":/a.html:", request("a.html"));
		assertTrue(invoked.get());
		invoked.set(false);
		assertEquals(":/a.xhtml:", request("a.xhtml"));
		assertTrue(invoked.get());
		invoked.set(false);
		assertEquals(":/some/path/a.xhtml:", request("some/path/a.xhtml"));
		assertTrue(invoked.get());
	}

	public void test_140_4_16() throws Exception {
		BundleContext context = getContext();

		final AtomicBoolean invoked = new AtomicBoolean(false);

		Servlet servlet = new HttpServlet() {

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response)
					throws ServletException, IOException {

				invoked.set(true);

				PrintWriter writer = response.getWriter();
				writer.write((request.getContextPath() == null) ? "" : request.getContextPath());
				writer.write(":");
				writer.write((request.getServletPath() == null) ? "" : request.getServletPath());
				writer.write(":");
				writer.write((request.getPathInfo() == null) ? "" : request.getPathInfo());
			}

		};

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		serviceRegistrations.add(context.registerService(Servlet.class, servlet, properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/fee/fi/foo/fum");
		serviceRegistrations.add(context.registerService(Servlet.class, servlet, properties));

		assertEquals(":/a:", request("a"));
		assertTrue(invoked.get());
		invoked.set(false);
		assertEquals(":/fee/fi/foo/fum:", request("fee/fi/foo/fum"));
		assertTrue(invoked.get());
	}

	public void test_140_4_17to22() throws Exception {
		BundleContext context = getContext();

		class AServlet extends HttpServlet {

			public AServlet(String content) {
				this.content = content;
			}

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response)
					throws ServletException, IOException {

				response.getWriter().write(content);
			}

			private final String	content;

		}

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		ServiceRegistration<Servlet> srA = context.registerService(Servlet.class, new AServlet("a"), properties);
		serviceRegistrations.add(srA);

		assertEquals("a", request("a"));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "b");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		ServiceRegistration<Servlet> srB = context.registerService(Servlet.class, new AServlet("b"), properties);
		serviceRegistrations.add(srB);

		assertEquals("a", request("a"));

		FailedServletDTO failedServletDTO = getFailedServletDTOByName("b");

		assertNotNull(failedServletDTO);
		assertEquals(
				DTOConstants.FAILURE_REASON_SHADOWED_BY_OTHER_SERVICE,
				failedServletDTO.failureReason);
		assertEquals(
				srB.getReference().getProperty(Constants.SERVICE_ID),
				failedServletDTO.serviceId);

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "c");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		properties.put(Constants.SERVICE_RANKING, 1000);
		serviceRegistrations.add(context.registerService(Servlet.class, new AServlet("c"), properties));

		assertEquals("c", request("a"));

		failedServletDTO = getFailedServletDTOByName("a");

		assertNotNull(failedServletDTO);
		assertEquals(
				DTOConstants.FAILURE_REASON_SHADOWED_BY_OTHER_SERVICE,
				failedServletDTO.failureReason);
		assertEquals(
				srA.getReference().getProperty(Constants.SERVICE_ID),
				failedServletDTO.serviceId);
	}

	public void test_140_4_23to25() throws Exception {
		BundleContext context = getContext();

		class AServlet extends HttpServlet {
		}

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		ServiceRegistration<Servlet> srA = context.registerService(Servlet.class, new AServlet(), properties);
		serviceRegistrations.add(srA);

		ServletDTO servletDTO = getServletDTOByName(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME,
				AServlet.class.getName());

		assertNotNull(servletDTO);
		assertEquals(srA.getReference().getProperty(Constants.SERVICE_ID), servletDTO.serviceId);
	}

	public void test_140_4_26to31() throws Exception {
		BundleContext context = getContext();

		class AServlet extends HttpServlet {

			public AServlet(String content) {
				this.content = content;
			}

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response)
					throws ServletException, IOException {

				response.getWriter().write(content);
			}

			private final String	content;

		}

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		properties.put(Constants.SERVICE_RANKING, Integer.MAX_VALUE);
		ServiceRegistration<Servlet> srA = context.registerService(Servlet.class, new AServlet("a"), properties);
		serviceRegistrations.add(srA);

		RequestInfoDTO requestInfoDTO = calculateRequestInfoDTO("/a");

		assertNotNull(requestInfoDTO);
		assertNotNull(requestInfoDTO.servletDTO);
		assertEquals("a", requestInfoDTO.servletDTO.name);
		assertEquals(
				srA.getReference().getProperty(Constants.SERVICE_ID),
				requestInfoDTO.servletDTO.serviceId);
		assertEquals("a", request("a"));

		HttpService httpService = getHttpService();

		if (httpService == null) {
			return;
		}

		httpService.registerServlet("/a", new AServlet("b"), null, null);

		try {
			requestInfoDTO = calculateRequestInfoDTO("/a");

			assertNotNull(requestInfoDTO);
			assertNotNull(requestInfoDTO.servletDTO);
			assertFalse((Long) srA.getReference().getProperty(Constants.SERVICE_ID) == requestInfoDTO.servletDTO.serviceId);
			assertEquals("b", request("a"));
		} finally {
			httpService.unregister("/a");
		}
	}

	public void test_table_140_4_HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED_validate() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED, "blah");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		serviceRegistrations.add(context.registerService(Servlet.class, new HttpServlet() {}, properties));

		RequestInfoDTO requestInfoDTO = calculateRequestInfoDTO("/a");

		assertNotNull(requestInfoDTO);
		assertNotNull(requestInfoDTO.servletDTO);
		assertFalse(requestInfoDTO.servletDTO.asyncSupported);

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED, "true");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/b");
		serviceRegistrations.add(context.registerService(Servlet.class, new HttpServlet() {}, properties));

		requestInfoDTO = calculateRequestInfoDTO("/b");

		assertNotNull(requestInfoDTO);
		assertNotNull(requestInfoDTO.servletDTO);
		assertTrue(requestInfoDTO.servletDTO.asyncSupported);

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED, "false");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/c");
		serviceRegistrations.add(context.registerService(Servlet.class, new HttpServlet() {}, properties));

		requestInfoDTO = calculateRequestInfoDTO("/c");

		assertNotNull(requestInfoDTO);
		assertNotNull(requestInfoDTO.servletDTO);
		assertFalse(requestInfoDTO.servletDTO.asyncSupported);

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED, 234l);
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/d");
		serviceRegistrations.add(context.registerService(Servlet.class, new HttpServlet() {}, properties));

		requestInfoDTO = calculateRequestInfoDTO("/d");

		assertNotNull(requestInfoDTO);
		assertNotNull(requestInfoDTO.servletDTO);
		assertFalse(requestInfoDTO.servletDTO.asyncSupported);

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/e");
		serviceRegistrations.add(context.registerService(Servlet.class, new HttpServlet() {}, properties));

		requestInfoDTO = calculateRequestInfoDTO("/e");

		assertNotNull(requestInfoDTO);
		assertNotNull(requestInfoDTO.servletDTO);
		assertFalse(requestInfoDTO.servletDTO.asyncSupported);
	}

	public void test_table_140_4_HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED() throws Exception {
		BundleContext context = getContext();
		final AtomicBoolean invoked = new AtomicBoolean(false);

		class AServlet extends HttpServlet {

			final ExecutorService	executor	= Executors.newCachedThreadPool();

			protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
				doGetAsync(req.startAsync());
			}

			private void doGetAsync(final AsyncContext asyncContext) {
				executor.submit(new Callable<Void>() {
					public Void call() throws Exception {
						try {
							invoked.set(true);

							PrintWriter writer = asyncContext.getResponse().getWriter();

							writer.print("a");
						} finally {
							asyncContext.complete();
						}

						return null;
					}
				});
			}

		}

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED, "true");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		ServiceRegistration<Servlet> srA = context.registerService(Servlet.class, new AServlet(), properties);
		serviceRegistrations.add(srA);

		RequestInfoDTO requestInfoDTO = calculateRequestInfoDTO("/a");

		assertNotNull(requestInfoDTO);
		assertNotNull(requestInfoDTO.servletDTO);
		assertTrue(requestInfoDTO.servletDTO.asyncSupported);
		assertTrue((Long) srA.getReference().getProperty(Constants.SERVICE_ID) == requestInfoDTO.servletDTO.serviceId);
		assertEquals("a", requestInfoDTO.servletDTO.name);
		assertEquals("a", request("a"));
		assertTrue(invoked.get());
		invoked.set(false);

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED, "false");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "b");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/b");
		ServiceRegistration<Servlet> srB = context.registerService(Servlet.class, new AServlet(), properties);
		serviceRegistrations.add(srB);

		assertEquals("500", request("b", null).get("responseCode").get(0));
		assertFalse(invoked.get());
	}

	public void test_table_140_4_HTTP_WHITEBOARD_SERVLET_ERROR_PAGE_validate() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ERROR_PAGE, "400");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		ServiceRegistration<Servlet> srA = context.registerService(Servlet.class, new HttpServlet() {}, properties);
		serviceRegistrations.add(srA);

		ErrorPageDTO errorPageDTO = getErrorPageDTOByName(HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME, "a");
		assertNotNull(errorPageDTO);
		assertEquals(1, errorPageDTO.errorCodes.length);
		assertEquals(400, errorPageDTO.errorCodes[0]);
		assertEquals(0, errorPageDTO.exceptions.length);

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ERROR_PAGE, new String[] {"400", ServletException.class.getName()});
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		ServiceRegistration<Servlet> srB = context.registerService(Servlet.class, new HttpServlet() {}, properties);
		serviceRegistrations.add(srB);

		errorPageDTO = getErrorPageDTOByName(HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME, "a");
		assertNotNull(errorPageDTO);
		assertEquals(1, errorPageDTO.errorCodes.length);
		assertEquals(400, errorPageDTO.errorCodes[0]);
		assertEquals(0, errorPageDTO.exceptions.length);

		FailedServletDTO failedServletDTO = getFailedServletDTOByName("a");
		assertNotNull(failedServletDTO);
		assertEquals(DTOConstants.FAILURE_REASON_SHADOWED_BY_OTHER_SERVICE, failedServletDTO.failureReason);
		assertEquals(srB.getReference().getProperty(Constants.SERVICE_ID), failedServletDTO.serviceId);

		properties.put(Constants.SERVICE_RANKING, Integer.MAX_VALUE);
		srB.setProperties(properties);

		errorPageDTO = getErrorPageDTOByName(HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME, "a");
		assertNotNull(errorPageDTO);
		assertEquals(400, errorPageDTO.errorCodes[0]);
		assertEquals(ServletException.class.getName(), errorPageDTO.exceptions[0]);

		failedServletDTO = getFailedServletDTOByName("a");
		assertNotNull(failedServletDTO);
		assertEquals(DTOConstants.FAILURE_REASON_SHADOWED_BY_OTHER_SERVICE, failedServletDTO.failureReason);
		assertEquals(srA.getReference().getProperty(Constants.SERVICE_ID), failedServletDTO.serviceId);
	}

	public void test_table_140_4_HTTP_WHITEBOARD_SERVLET_ERROR_PAGE() throws Exception {
		BundleContext context = getContext();

		final AtomicBoolean invoked = new AtomicBoolean(false);

		class AServlet extends HttpServlet {

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "a");
			}

		}

		class BServlet extends HttpServlet {

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				invoked.set(true);
				String message = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
				response.getWriter().write((message == null) ? "" : message);
			}

		}

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		serviceRegistrations.add(context.registerService(Servlet.class, new AServlet(), properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "b");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ERROR_PAGE, new String[] {HttpServletResponse.SC_BAD_GATEWAY + "", HttpServletResponse.SC_FORBIDDEN + ""});
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/error");
		serviceRegistrations.add(context.registerService(Servlet.class, new BServlet(), properties));

		ServletDTO servletDTO = getServletDTOByName(HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME, "b");
		assertNotNull(servletDTO);

		ErrorPageDTO errorPageDTO = getErrorPageDTOByName(HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME, "b");
		assertNotNull(errorPageDTO);
		assertTrue(Arrays.binarySearch(errorPageDTO.errorCodes, HttpServletResponse.SC_BAD_GATEWAY) >= 0);

		Map<String, List<String>> response = request("a", null);
		assertEquals("a", response.get("responseBody").get(0));
		assertTrue(invoked.get());
		assertEquals(HttpServletResponse.SC_BAD_GATEWAY + "", response.get("responseCode").get(0));
	}

	public void test_table_140_4_HTTP_WHITEBOARD_SERVLET_ERROR_PAGE_4xx() throws Exception {
		BundleContext context = getContext();

		final AtomicBoolean invoked = new AtomicBoolean(false);

		class AServlet extends HttpServlet {

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				response.sendError(HttpServletResponse.SC_FORBIDDEN, "a");
			}

		}

		class BServlet extends HttpServlet {

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				invoked.set(true);
				String message = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
				response.getWriter().write((message == null) ? "" : message);
			}

		}

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		serviceRegistrations.add(context.registerService(Servlet.class, new AServlet(), properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "b");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ERROR_PAGE, "4xx");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/error");
		serviceRegistrations.add(context.registerService(Servlet.class, new BServlet(), properties));

		ServletDTO servletDTO = getServletDTOByName(HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME, "b");
		assertNotNull(servletDTO);
		ErrorPageDTO errorPageDTO = getErrorPageDTOByName(HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME, "b");
		assertNotNull(errorPageDTO);
		assertTrue(Arrays.binarySearch(errorPageDTO.errorCodes, HttpServletResponse.SC_FORBIDDEN) >= 0);

		Map<String, List<String>> response = request("a", null);
		assertEquals("a", response.get("responseBody").get(0));
		assertTrue(invoked.get());
		assertEquals(HttpServletResponse.SC_FORBIDDEN + "", response.get("responseCode").get(0));
	}

	public void test_table_140_4_HTTP_WHITEBOARD_SERVLET_ERROR_PAGE_5xx() throws Exception {
		BundleContext context = getContext();

		final AtomicBoolean invoked = new AtomicBoolean(false);

		class AServlet extends HttpServlet {

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "a");
			}

		}

		class BServlet extends HttpServlet {

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				invoked.set(true);
				String message = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
				response.getWriter().write((message == null) ? "" : message);
			}

		}

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		serviceRegistrations.add(context.registerService(Servlet.class, new AServlet(), properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "b");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ERROR_PAGE, "5xx");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/error");
		serviceRegistrations.add(context.registerService(Servlet.class, new BServlet(), properties));

		ServletDTO servletDTO = getServletDTOByName(HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME, "b");
		assertNotNull(servletDTO);
		ErrorPageDTO errorPageDTO = getErrorPageDTOByName(HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME, "b");
		assertNotNull(errorPageDTO);
		assertTrue(Arrays.binarySearch(errorPageDTO.errorCodes, HttpServletResponse.SC_BAD_GATEWAY) >= 0);

		Map<String, List<String>> response = request("a", null);
		assertEquals("a", response.get("responseBody").get(0));
		assertTrue(invoked.get());
		assertEquals(HttpServletResponse.SC_BAD_GATEWAY + "", response.get("responseCode").get(0));
	}

	public void test_table_140_4_HTTP_WHITEBOARD_SERVLET_ERROR_PAGE_exception() throws Exception {
		BundleContext context = getContext();

		final AtomicBoolean invoked = new AtomicBoolean(false);

		class AException extends ServletException {
		}

		class AServlet extends HttpServlet {

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				throw new AException();
			}

		}

		class BServlet extends HttpServlet {

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				invoked.set(true);
				String exception = (String) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION_TYPE);
				response.getWriter().write((exception == null) ? "" : exception);
			}

		}

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		serviceRegistrations.add(context.registerService(Servlet.class, new AServlet(), properties));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "b");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ERROR_PAGE, ServletException.class.getName());
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/error");
		serviceRegistrations.add(context.registerService(Servlet.class, new BServlet(), properties));

		ServletDTO servletDTO = getServletDTOByName(HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME, "b");
		assertNotNull(servletDTO);
		ErrorPageDTO errorPageDTO = getErrorPageDTOByName(HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME, "b");
		assertNotNull(errorPageDTO);
		assertTrue(Arrays.binarySearch(errorPageDTO.exceptions, ServletException.class.getName()) >= 0);

		Map<String, List<String>> response = request("a", null);
		assertEquals(AException.class.getName(), response.get("responseBody").get(0));
		assertTrue(invoked.get());
		assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR + "", response.get("responseCode").get(0));
	}

	public void test_table_140_4_HTTP_WHITEBOARD_SERVLET_NAME() throws Exception {
		BundleContext context = getContext();

		class AServlet extends HttpServlet {

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				response.getWriter().write(getServletName());
			}

		}

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		ServiceRegistration<Servlet> srA = context.registerService(Servlet.class, new AServlet(), properties);
		serviceRegistrations.add(srA);

		ServletDTO servletDTO = getServletDTOByName(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME,
				AServlet.class.getName());

		assertNotNull(servletDTO);
		assertEquals(srA.getReference().getProperty(Constants.SERVICE_ID), servletDTO.serviceId);
		assertEquals(AServlet.class.getName(), request("a"));
	}

	public void test_table_140_4_initParams() throws Exception {
		BundleContext context = getContext();

		class AServlet extends HttpServlet {

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				String initParameter = getServletConfig().getInitParameter(request.getParameter("p"));

				response.getWriter().write((initParameter == null) ? "" : initParameter);
			}

		}

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_INIT_PARAM_PREFIX + "param1", "value1");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_INIT_PARAM_PREFIX + "param2", "value2");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_INIT_PARAM_PREFIX + "param3", 345l);
		ServiceRegistration<Servlet> srA = context.registerService(Servlet.class, new AServlet(), properties);
		serviceRegistrations.add(srA);

		ServletDTO servletDTO = getServletDTOByName(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME,
				AServlet.class.getName());

		assertNotNull(servletDTO);
		assertTrue(servletDTO.initParams.containsKey("param1"));
		assertTrue(servletDTO.initParams.containsKey("param2"));
		assertFalse(servletDTO.initParams.containsKey("param3"));
		assertEquals(srA.getReference().getProperty(Constants.SERVICE_ID), servletDTO.serviceId);
		assertEquals("value1", request("a?p=param1"));
		assertEquals("value2", request("a?p=param2"));
		assertEquals("", request("a?p=param3"));
	}

	public void test_140_4_38to42() throws Exception {
		BundleContext context = getContext();
		final AtomicBoolean invoked = new AtomicBoolean(false);

		class AServlet extends HttpServlet {

			@Override
			public void destroy() {
				invoked.set(true);

				super.destroy();
			}

			@Override
			public void init(ServletConfig config) throws ServletException {
				invoked.set(true);

				super.init(config);
			}

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				response.getWriter().write("a");
			}

		}

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		ServiceRegistration<Servlet> srA = context.registerService(Servlet.class, new AServlet(), properties);

		assertEquals("a", request("a"));
		assertTrue(invoked.get());
		invoked.set(false);
		srA.unregister();
		assertTrue(invoked.get());
	}

	public void test_140_4_42to44() throws Exception {
		BundleContext context = getContext();
		final AtomicBoolean invoked = new AtomicBoolean(false);

		class AServlet extends HttpServlet {

			@Override
			public void init(ServletConfig config) throws ServletException {
				invoked.set(true);

				throw new ServletException();
			}

		}

		class BServlet extends HttpServlet {

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				response.getWriter().write("failed");
			}

		}

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		serviceRegistrations.add(context.registerService(Servlet.class, new AServlet(), properties));

		FailedServletDTO failedServletDTO = getFailedServletDTOByName("a");
		assertNotNull(failedServletDTO);
		assertEquals(DTOConstants.FAILURE_REASON_EXCEPTION_ON_INIT, failedServletDTO.failureReason);
		assertTrue(invoked.get());

		Map<String, List<String>> response = request("a", null);
		// Not sure what the appropriate behavior should be here!!! Should it be
		// 500?
		assertEquals("404", response.get("responseCode").get(0));

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		serviceRegistrations.add(context.registerService(Servlet.class, new BServlet(), properties));

		response = request("a", null);
		// Not sure what the appropriate behavior should be here!!! Should it be
		// 500?
		assertEquals("500", response.get("responseCode").get(0));
	}

}
