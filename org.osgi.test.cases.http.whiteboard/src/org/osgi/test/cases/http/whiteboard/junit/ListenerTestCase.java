package org.osgi.test.cases.http.whiteboard.junit;

import java.io.IOException;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionIdListener;
import javax.servlet.http.HttpSessionListener;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.runtime.dto.DTOConstants;
import org.osgi.service.http.runtime.dto.FailedListenerDTO;
import org.osgi.service.http.runtime.dto.ListenerDTO;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.test.cases.http.whiteboard.junit.mock.MockSCL;
import org.osgi.test.cases.http.whiteboard.junit.mock.MockServlet;

public class ListenerTestCase extends BaseHttpWhiteboardTestCase {

	public void test_140_7_validation() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		ServiceRegistration<?> sr = context.registerService(
				ServletContextListener.class, new MockSCL(new AtomicReference<ServletContext>()), properties);
		serviceRegistrations.add(sr);

		ListenerDTO listenerDTO = getListenerDTOByServiceId(DEFAULT, getServiceId(sr));
		assertNotNull(listenerDTO);

		properties.remove(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER);
		sr.setProperties(properties);

		listenerDTO = getListenerDTOByServiceId(DEFAULT, getServiceId(sr));
		assertNull(listenerDTO);

		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "blah");
		sr.setProperties(properties);

		FailedListenerDTO failedListenerDTO = getFailedListenerDTOByServiceId(getServiceId(sr));
		assertNotNull(failedListenerDTO);
		assertEquals(DTOConstants.FAILURE_REASON_VALIDATION_FAILED, failedListenerDTO.failureReason);

		listenerDTO = getListenerDTOByServiceId(DEFAULT, getServiceId(sr));
		assertNull(listenerDTO);
	}

	public void test_140_7_ServletContextListener() throws Exception {
		BundleContext context = getContext();
		final AtomicBoolean invokedDestroy = new AtomicBoolean(false);
		final AtomicBoolean invokedInit = new AtomicBoolean(false);

		class AServletContextListener implements ServletContextListener {

			public void contextDestroyed(ServletContextEvent event) {
				invokedDestroy.set(true);
			}

			public void contextInitialized(ServletContextEvent event) {
				invokedInit.set(true);
			}

		}

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		ServiceRegistration<?> sr = context.registerService(ServletContextListener.class, new AServletContextListener(), properties);
		serviceRegistrations.add(sr);

		ListenerDTO listenerDTO = getListenerDTOByServiceId(DEFAULT, getServiceId(sr));
		assertNotNull(listenerDTO);
		assertTrue(invokedInit.get());
		assertFalse(invokedDestroy.get());
		invokedInit.set(false);

		properties.remove(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER);
		sr.setProperties(properties);

		assertTrue(invokedDestroy.get());
		assertFalse(invokedInit.get());
	}

	public void test_140_7_ServletContextAttributeListener() throws Exception {
		BundleContext context = getContext();
		final AtomicBoolean invokedAdded = new AtomicBoolean(false);
		final AtomicBoolean invokedRemoved = new AtomicBoolean(false);
		final AtomicBoolean invokedReplaced = new AtomicBoolean(false);

		class AServletContextAttributeListener implements ServletContextAttributeListener {

			public void attributeAdded(ServletContextAttributeEvent arg0) {
				invokedAdded.set(true);
			}

			public void attributeRemoved(ServletContextAttributeEvent arg0) {
				invokedRemoved.set(true);
			}

			public void attributeReplaced(ServletContextAttributeEvent arg0) {
				invokedReplaced.set(true);
			}

		}

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		ServiceRegistration<?> sr = context.registerService(ServletContextAttributeListener.class, new AServletContextAttributeListener(), properties);
		serviceRegistrations.add(sr);

		ListenerDTO listenerDTO = getListenerDTOByServiceId(DEFAULT, getServiceId(sr));
		assertNotNull(listenerDTO);

		class AServlet extends HttpServlet {

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				String set = request.getParameter("set");
				if (set != null) {
					getServletContext().setAttribute(set, new Date().toString());
					response.getWriter().write("set");
				}
				String rem = request.getParameter("rem");
				if (rem != null) {
					getServletContext().removeAttribute(rem);
					response.getWriter().write("rem");
				}
			}

		}

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		serviceRegistrations.add(context.registerService(Servlet.class, new AServlet(), properties));
		assertEquals("set", request("a?set=a"));
		assertTrue(invokedAdded.get());
		assertFalse(invokedRemoved.get());
		assertFalse(invokedReplaced.get());
		invokedAdded.set(false);

		assertEquals("set", request("a?set=a"));
		assertFalse(invokedRemoved.get());
		assertFalse(invokedAdded.get());
		assertTrue(invokedReplaced.get());
		invokedReplaced.set(false);

		assertEquals("rem", request("a?rem=a"));
		assertTrue(invokedRemoved.get());
		assertFalse(invokedAdded.get());
		assertFalse(invokedReplaced.get());
	}

	public void test_140_7_ServletRequestListener() throws Exception {
		BundleContext context = getContext();
		final AtomicBoolean invokedInit = new AtomicBoolean(false);
		final AtomicBoolean invokedDestroy = new AtomicBoolean(false);

		class AServletRequestListener implements ServletRequestListener {

			public void requestDestroyed(ServletRequestEvent arg0) {
				invokedDestroy.set(true);
			}

			public void requestInitialized(ServletRequestEvent arg0) {
				invokedInit.set(true);
			}

		}

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		ServiceRegistration<?> sr = context.registerService(ServletRequestListener.class, new AServletRequestListener(), properties);
		serviceRegistrations.add(sr);

		ListenerDTO listenerDTO = getListenerDTOByServiceId(DEFAULT, getServiceId(sr));
		assertNotNull(listenerDTO);

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		serviceRegistrations.add(context.registerService(Servlet.class, new MockServlet().content("a"), properties));

		assertEquals("a", request("a"));
		assertTrue(invokedInit.get());
		assertTrue(invokedDestroy.get());
	}

	public void test_140_7_ServletRequestAttributeListener() throws Exception {
		BundleContext context = getContext();
		final AtomicBoolean invokedAdded = new AtomicBoolean(false);
		final AtomicBoolean invokedRemoved = new AtomicBoolean(false);
		final AtomicBoolean invokedReplaced = new AtomicBoolean(false);

		class AServletRequestAttributeListener implements ServletRequestAttributeListener {

			public void attributeAdded(ServletRequestAttributeEvent arg0) {
				invokedAdded.set(true);
			}

			public void attributeRemoved(ServletRequestAttributeEvent arg0) {
				invokedRemoved.set(true);
			}

			public void attributeReplaced(ServletRequestAttributeEvent arg0) {
				invokedReplaced.set(true);
			}

		}

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		ServiceRegistration<?> sr = context.registerService(ServletRequestAttributeListener.class, new AServletRequestAttributeListener(), properties);
		serviceRegistrations.add(sr);

		ListenerDTO listenerDTO = getListenerDTOByServiceId(DEFAULT, getServiceId(sr));
		assertNotNull(listenerDTO);

		class AServlet extends HttpServlet {

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				request.setAttribute("a", new Date().toString());
				request.setAttribute("a", new Date().toString());
				request.removeAttribute("a");
				response.getWriter().write("a");
			}

		}

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		serviceRegistrations.add(context.registerService(Servlet.class, new AServlet(), properties));
		assertEquals("a", request("a"));
		assertTrue(invokedAdded.get());
		assertTrue(invokedRemoved.get());
		assertTrue(invokedReplaced.get());
	}

	public void test_140_7_HttpSessionListener() throws Exception {
		BundleContext context = getContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "a");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/");
		ServiceRegistration<ServletContextHelper> srA = context.registerService(ServletContextHelper.class, new ServletContextHelper() {}, properties);

		final AtomicBoolean invokedCreate = new AtomicBoolean(false);
		final AtomicBoolean invokedDestroy = new AtomicBoolean(false);

		class AHttpSessionListener implements HttpSessionListener {

			public void sessionCreated(HttpSessionEvent arg0) {
				invokedCreate.set(true);
			}

			public void sessionDestroyed(HttpSessionEvent arg0) {
				invokedDestroy.set(true);
			}

		}

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=a)");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		ServiceRegistration<?> srB = context.registerService(HttpSessionListener.class, new AHttpSessionListener(), properties);
		serviceRegistrations.add(srB);

		ListenerDTO listenerDTO = getListenerDTOByServiceId("a", getServiceId(srB));
		assertNotNull(listenerDTO);

		class AServlet extends HttpServlet {

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				HttpSession session = request.getSession(true);
				session.setMaxInactiveInterval(1);
				session.invalidate();
			}

		}

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=a)");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		serviceRegistrations.add(context.registerService(Servlet.class, new AServlet(), properties));

		assertEquals("", request("a"));
		assertTrue(invokedCreate.get());
		srA.unregister();
		assertTrue(invokedDestroy.get());
	}

	public void test_140_7_HttpSessionAttributeListener() throws Exception {
		BundleContext context = getContext();
		final AtomicBoolean invokedAdded = new AtomicBoolean(false);
		final AtomicBoolean invokedRemoved = new AtomicBoolean(false);
		final AtomicBoolean invokedReplaced = new AtomicBoolean(false);

		class AHttpSessionAttributeListener implements HttpSessionAttributeListener {

			public void attributeAdded(HttpSessionBindingEvent arg0) {
				invokedAdded.set(true);
			}

			public void attributeRemoved(HttpSessionBindingEvent arg0) {
				invokedRemoved.set(true);
			}

			public void attributeReplaced(HttpSessionBindingEvent arg0) {
				invokedReplaced.set(true);
			}

		}

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		ServiceRegistration<?> sr = context.registerService(HttpSessionAttributeListener.class, new AHttpSessionAttributeListener(), properties);
		serviceRegistrations.add(sr);

		ListenerDTO listenerDTO = getListenerDTOByServiceId(DEFAULT, getServiceId(sr));
		assertNotNull(listenerDTO);

		class AServlet extends HttpServlet {

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				HttpSession session = request.getSession();
				session.setAttribute("a", new Date().toString());
				session.setAttribute("a", new Date().toString());
				session.removeAttribute("a");
				response.getWriter().write("a");
			}

		}

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		serviceRegistrations.add(context.registerService(Servlet.class, new AServlet(), properties));
		assertEquals("a", request("a"));
		assertTrue(invokedAdded.get());
		assertTrue(invokedRemoved.get());
		assertTrue(invokedReplaced.get());
	}

	public void test_140_7_HttpSessionIdListener() throws Exception {
		BundleContext context = getContext();
		final AtomicBoolean invoked = new AtomicBoolean(false);

		class AHttpSessionIdListener implements HttpSessionIdListener {

			public void sessionIdChanged(HttpSessionEvent event, String previousId) {
				invoked.set(true);
			}

		}

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		ServiceRegistration<?> sr = context.registerService(HttpSessionIdListener.class, new AHttpSessionIdListener(), properties);
		serviceRegistrations.add(sr);

		ListenerDTO listenerDTO = getListenerDTOByServiceId(DEFAULT, getServiceId(sr));
		assertNotNull(listenerDTO);

		class AServlet extends HttpServlet {

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				request.getSession(true);
				request.changeSessionId();
				response.getWriter().write("a");
			}

		}

		properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/a");
		serviceRegistrations.add(context.registerService(Servlet.class, new AServlet(), properties));
		assertEquals("a", request("a"));
		assertTrue(invoked.get());
	}

}
