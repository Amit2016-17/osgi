/*
 * Copyright (c) OSGi Alliance (2009). All Rights Reserved.
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

package org.osgi.test.support;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.wiring.FrameworkWiring;

public abstract class OSGiTestCase extends TestCase {
	private volatile BundleContext	context;

	/**
	 * This method is called by the JUnit runner for OSGi, and gives us a Bundle
	 * Context.
	 */
	public void setBundleContext(BundleContext context) {
		this.context = context;
	}

	/**
	 * Returns the current Bundle Context
	 */
	public BundleContext getContext() {
		BundleContext c = context;
		if (c == null) {
			fail("No Bundle context set, are you running in OSGi Test mode?");
		}
		return c;
	}

	/**
	 * Fail with cause t.
	 * 
	 * @param message Failure message.
	 * @param t Cause of the failure.
	 */
	public static void fail(String message, Throwable t) {
		AssertionFailedError e = new AssertionFailedError(message + ": "
				+ t.getMessage());
		e.initCause(t);
		throw e;
	}

	/**
	 * Assert a constant from class has a specific value.
	 * 
	 * @param expected Expected value.
	 * @param fieldName Constant field name.
	 * @param fieldClass Class containing constant.
	 */
	public static void assertConstant(Object expected, String fieldName,
			Class< ? > fieldClass) {
		try {
			Field f = fieldClass.getField(fieldName);
			assertTrue(Modifier.isPublic(f.getModifiers()));
			assertTrue(Modifier.isStatic(f.getModifiers()));
			assertTrue(Modifier.isFinal(f.getModifiers()));
			assertEquals(fieldName, expected, f.get(null));
		}
		catch (NoSuchFieldException e) {
			fail("missing field: " + fieldName, e);
		}
		catch (IllegalAccessException e) {
			fail("bad field: " + fieldName, e);
		}
	}
	
	public static <T> void assertEquals(String message,
			Comparator<T> comparator, List<T> expected, List<T> actual) {
		if (expected.size() != actual.size()) {
			failNotEquals(message, expected, actual);
		}

		for (int i = 0, l = expected.size(); i < l; i++) {
			assertEquals(message, comparator, expected.get(i), actual.get(i));
		}
	}

	public static <T> void assertEquals(String message,
			Comparator<T> comparator, T expected, T actual) {
		if (comparator.compare(expected, actual) == 0) {
			return;
		}
		failNotEquals(message, expected, actual);
	}

	/**
	 * Installs a resource within the test bundle as a bundle
	 * @param bundle a path to an entry that contains a bundle to install
	 * @return The installed bundle
	 * @throws BundleException if an error occurred while installing the bundle
	 * @throws IOException if an error occurred while reading the bundle content
	 */
	public Bundle install(String bundle) throws BundleException, IOException {
		URL entry = getContext().getBundle().getEntry(bundle);
		assertNotNull("Can not find bundle: " + bundle, entry);
		return getContext().installBundle(bundle, entry.openStream());
	}

	/**
	 * Refreshes the collection of bundle synchronously.
	 * @param bundles The bundles to be refreshed, or {@code null} to refresh
	 *        the removal pending bundles.
	 */
	public void synchronousRefreshBundles(Collection<Bundle> bundles) {
		FrameworkWiring fwkWiring = getContext().getBundle(0).adapt(FrameworkWiring.class);
		assertNotNull("Framework wiring is null.", fwkWiring);
		final boolean[] done = new boolean[] {false};
		FrameworkListener listener = new FrameworkListener() {
			public void frameworkEvent(FrameworkEvent event) {
				synchronized (done) {
					if (event.getType() == FrameworkEvent.PACKAGES_REFRESHED) {
						done[0] = true;
						done.notify();
					}
				}
			}
		};
		fwkWiring.refreshBundles(bundles, listener);
		synchronized (done) {
			if (!done[0])
				try {
					done.wait(OSGiTestCaseProperties.getTimeout()
							* OSGiTestCaseProperties.getScaling());
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					fail("Unexepected interruption.", e);
				}
			if (!done[0])
				fail("Timed out waiting for refresh bundles to finish.");
		}
	}
}
