/*
 * Copyright (c) OSGi Alliance (2014). All Rights Reserved.
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

package org.osgi.test.cases.promise.junit;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import org.osgi.util.function.Function;
import org.osgi.util.function.Predicate;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.FailedPromisesException;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

public class PromiseTest extends TestCase {
	public static final long	WAIT_TIME	= 2L;
	static Timer				timer		= new Timer();

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
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
	 * Fail with cause t.
	 * 
	 * @param t Cause of the failure.
	 */
	public static void fail(Throwable t) {
		AssertionFailedError e = new AssertionFailedError(t.getMessage());
		e.initCause(t);
		throw e;
	}

	public void testPromiseSuccess1() throws Exception {
		final Deferred<String> d = new Deferred<String>();
		final CountDownLatch latch = new CountDownLatch(1);
		final Promise<String> p = d.getPromise();
		p.onResolve(new Runnable() {
			public void run() {
				latch.countDown();
			}
		});
		assertFalse("callback ran before resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
		assertFalse("callback ran before resolved", p.isDone());
		String value = new String("value");
		d.resolve(value);
		assertTrue("callback did not run after resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
		assertTrue("callback did not run after resolved", p.isDone());
		assertNull("wrong failure", p.getFailure());
		assertSame("wrong value", value, p.getValue());
	}

	public void testPromiseSuccess2() throws Exception {
		final Deferred<Integer> d = new Deferred<Integer>();
		final CountDownLatch latch = new CountDownLatch(1);
		final Promise<Integer> p = d.getPromise();
		Promise<Number> p2 = p.<Number, Long> then(new Success<Number, Long>() {
			public Promise<Long> call(Promise<Number> resolved) throws Exception {
				latch.countDown();
				return Promises.newResolvedPromise(new Long(resolved.getValue().longValue()));
			}
		});
		assertFalse("callback ran before resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
		assertFalse("callback ran before resolved", p.isDone());
		assertFalse("callback ran before resolved", p2.isDone());
		Integer value = new Integer(15);
		d.resolve(value);
		assertTrue("callback did not run after resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
		assertTrue("callback did not run after resolved", p.isDone());
		assertTrue("callback did not run after resolved", p2.isDone());
		assertNull("wrong failure", p.getFailure());
		assertNull("wrong failure", p2.getFailure());
		assertSame("wrong value", value, p.getValue());
		assertEquals("wrong value", value.intValue(), p2.getValue().intValue());
	}

	public void testPromiseSuccess3() throws Exception {
		final Deferred<Integer> d = new Deferred<Integer>();
		final CountDownLatch latch1 = new CountDownLatch(1);
		final CountDownLatch latch2 = new CountDownLatch(1);
		final Promise<Integer> p = d.getPromise();
		Promise<Number> p2 = p.<Number, Long> then(new Success<Number, Long>() {
			public Promise<Long> call(Promise<Number> resolved) throws Exception {
				final Promise<Long> returned = Promises.newResolvedPromise(new Long(resolved.getValue().longValue()));
				returned.onResolve(new Runnable() {
					public void run() {
						latch1.countDown();
					}
				});
				return returned;
			}
		}, new Failure() {
			public void fail(Promise<?> resolved) throws Exception {
				latch2.countDown();
			}
		});
		assertFalse("callback ran before resolved", latch1.await(WAIT_TIME, TimeUnit.SECONDS));
		assertFalse("callback ran before resolved", latch2.await(WAIT_TIME, TimeUnit.SECONDS));
		assertFalse("callback ran before resolved", p.isDone());
		assertFalse("callback ran before resolved", p2.isDone());
		Integer value = new Integer(15);
		d.resolve(value);
		assertTrue("callback did not run after resolved", latch1.await(WAIT_TIME, TimeUnit.SECONDS));
		assertFalse("callback ran before resolved", latch2.await(WAIT_TIME, TimeUnit.SECONDS));
		assertTrue("callback did not run after resolved", p.isDone());
		assertTrue("callback did not run after resolved", p2.isDone());
		assertEquals("wrong value", value.intValue(), p2.getValue().intValue());
		assertNull("wrong failure", p2.getFailure());
		assertNull("wrong failure", p.getFailure());
		assertSame("wrong value", value, p.getValue());
	}

	public void testPromiseSuccess4() throws Exception {
		final Deferred<Integer> d1 = new Deferred<Integer>();
		final Deferred<String> d2 = new Deferred<String>();
		final AtomicReference<String> result = new AtomicReference<String>();
		final CountDownLatch latch = new CountDownLatch(1);
		final Promise<Integer> p = d1.getPromise();
		Promise<String> p2 = p.then(new Success<Number, String>() {
			public Promise<String> call(Promise<Number> resolved) throws Exception {
				result.set(resolved.getValue().toString());
				Promise<String> returned = d2.getPromise();
				returned.onResolve(new Runnable() {
					public void run() {
						latch.countDown();
					}
				});
				return returned;
			}
		}, new Failure() {
			public void fail(Promise<?> resolved) throws Exception {
			}
		});
		assertFalse("callback ran before resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
		assertFalse("callback ran before resolved", p.isDone());
		assertFalse("callback ran before resolved", p2.isDone());
		Integer value = new Integer(15);
		d1.resolve(value);
		assertFalse("callback ran before resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
		assertTrue("callback did not run after resolved", p.isDone());
		assertFalse("callback ran before resolved", p2.isDone());
		d2.resolve(result.get());
		assertTrue("callback did not run after resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
		assertTrue("callback did not run after resolved", p.isDone());
		assertTrue("callback did not run after resolved", p2.isDone());
		assertSame("wrong value", result.get(), p2.getValue());
		assertNull("wrong failure", p2.getFailure());
		assertNull("wrong failure", p.getFailure());
		assertSame("wrong value", value, p.getValue());
	}

	public void testPromiseFail1() throws Exception {
		final Deferred<String> d = new Deferred<String>();
		final CountDownLatch latch = new CountDownLatch(1);
		final Promise<String> p = d.getPromise();
		p.onResolve(new Runnable() {
			public void run() {
				latch.countDown();
			}
		});
		assertFalse("callback ran before resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
		assertFalse("callback ran before resolved", p.isDone());
		Throwable failure = new RuntimeException();
		d.fail(failure);
		assertTrue("callback did not run after resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
		assertTrue("callback did not run after resolved", p.isDone());
		assertSame("wrong failure", failure, p.getFailure());
		try {
			p.getValue();
			fail("getValue failed to throw InvocationTargetException");
		} catch (InvocationTargetException e) {
			assertSame("wrong failure", failure, e.getCause());
		}
	}

	public void testPromiseFail2() throws Exception {
		final Deferred<String> d = new Deferred<String>();
		final CountDownLatch latch = new CountDownLatch(1);
		final Promise<String> p = d.getPromise();
		p.then(null, new Failure() {
			public void fail(Promise<?> resolved) throws Exception {
				latch.countDown();
			}
		});
		assertFalse("callback ran before resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
		assertFalse("callback ran before resolved", p.isDone());
		Throwable failure = new RuntimeException();
		d.fail(failure);
		assertTrue("callback did not run after resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
		assertTrue("callback did not run after resolved", p.isDone());
		assertSame("wrong failure", failure, p.getFailure());
		try {
			p.getValue();
			fail("getValue failed to throw InvocationTargetException");
		} catch (InvocationTargetException e) {
			assertSame("wrong failure", failure, e.getCause());
		}
	}

	/**
	 * Test if we can get the failures when there is a chain. The idea is that
	 * you only specify the failure callback on the last
	 * {@link Promise#then(Success,Failure)} method. Any failures will bubble
	 * up.
	 * 
	 * @throws Exception
	 */
	public void testFailureChain() throws Exception {
		Deferred<String> d = new Deferred<String>();
		final Promise<String> p1 = d.getPromise();
		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicInteger callbackCallCount = new AtomicInteger(0);

		Success<String, String> doubler = new Success<String, String>() {
			public Promise<String> call(Promise<String> promise) throws Exception {
				callbackCallCount.incrementAndGet();
				return Promises.newResolvedPromise(promise.getValue() + promise.getValue());
			}
		};
		final Promise<String> p2 = p1.<String, String> then(doubler).<String, String> then(doubler).then(doubler);

		p2.onResolve(new Runnable() {
			public void run() {
				try {
					if (p2.getFailure() != null)
						latch.countDown();
				} catch (Exception e) {
					fail("unexpected exception", e);
				}
			}
		});

		Exception failure = new Exception("Y");
		assertFalse("callback ran before resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
		assertFalse("callback ran before resolved", p2.isDone());
		d.fail(failure);
		assertTrue("callback did not run after resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
		assertTrue("callback did not run after resolved", p2.isDone());

		assertSame("wrong exception", failure, p2.getFailure());
		try {
			p2.getValue();
			fail("getValue failed to throw InvocationTargetException");
		} catch (InvocationTargetException e) {
			assertSame("wrong failure", failure, e.getCause());
		}
		assertEquals("wrong number of callbacks called", 0, callbackCallCount.get());
	}

	public void testSuccessChain() throws Exception {
		Deferred<String> d = new Deferred<String>();
		final Promise<String> p1 = d.getPromise();
		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicInteger successCallbackCallCount = new AtomicInteger(0);
		final AtomicInteger failureCallbackCallCount = new AtomicInteger(0);

		Success<String, String> doubler = new Success<String, String>() {
			public Promise<String> call(Promise<String> promise) throws Exception {
				successCallbackCallCount.incrementAndGet();
				return Promises.newResolvedPromise(promise.getValue() + promise.getValue());
			}
		};
		Failure wrapper = new Failure() {
			public void fail(Promise<?> promise) throws Exception {
				failureCallbackCallCount.incrementAndGet();
				throw new Exception(promise.getFailure());
			}
		};
		final Promise<String> p2 = p1.<String, String> then(doubler, wrapper).<String, String> then(doubler, wrapper).then(doubler, wrapper);

		p2.onResolve(new Runnable() {
			public void run() {
				try {
					if (p2.getFailure() == null)
						latch.countDown();
				} catch (Exception e) {
					fail("unexpected exception", e);
				}
			}
		});

		assertFalse("callback ran before resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
		assertFalse("callback ran before resolved", p2.isDone());
		String value = new String("Y");
		d.resolve(value);
		assertTrue("callback did not run after resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
		assertTrue("callback did not run after resolved", p2.isDone());

		assertEquals("wrong value", "YYYYYYYY", p2.getValue());
		assertNull("wrong failure", p2.getFailure());
		assertEquals("wrong number of success callbacks called", 3, successCallbackCallCount.get());
		assertEquals("wrong number of failure callbacks called", 0, failureCallbackCallCount.get());
	}

	public void testExceptionOverride() throws Exception {
		Deferred<String> d = new Deferred<String>();
		final Promise<String> p1 = d.getPromise();
		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicInteger successCallbackCallCount = new AtomicInteger(0);
		final AtomicInteger failureCallbackCallCount = new AtomicInteger(0);

		Success<String, String> doubler = new Success<String, String>() {
			public Promise<String> call(Promise<String> promise) throws Exception {
				successCallbackCallCount.incrementAndGet();
				return Promises.newResolvedPromise(promise.getValue() + promise.getValue());
			}
		};
		Failure wrapper = new Failure() {
			public void fail(Promise<?> promise) throws Exception {
				failureCallbackCallCount.incrementAndGet();
				throw new Exception(promise.getFailure());
			}
		};
		final Promise<String> p2 = p1.<String, String> then(doubler, wrapper).<String, String> then(doubler, wrapper).then(doubler, wrapper);

		p2.onResolve(new Runnable() {
			public void run() {
				try {
					if (p2.getFailure() != null)
						latch.countDown();
				} catch (Exception e) {
					fail("unexpected exception", e);
				}
			}
		});

		Exception failure = new Exception("Y");
		assertFalse("callback ran before resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
		assertFalse("callback ran before resolved", p2.isDone());
		d.fail(failure);
		assertTrue("callback did not run after resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
		assertTrue("callback did not run after resolved", p2.isDone());

		assertEquals("wrong number of success callbacks called", 0, successCallbackCallCount.get());
		assertEquals("wrong number of failure callbacks called", 3, failureCallbackCallCount.get());
		assertSame("wrong exception", failure, p2.getFailure().getCause().getCause().getCause());
		try {
			p2.getValue();
			fail("getValue failed to throw InvocationTargetException");
		} catch (InvocationTargetException e) {
			assertSame("wrong failure", failure, e.getCause().getCause().getCause().getCause());
		}
	}

	/**
	 * Check if a promise can be called after it has already called back. This
	 * is a common use case for promises. I.e. you create a promise and whenever
	 * someone needs the value he uses 'then' instead of directly getting the
	 * value. Does take some getting used to.
	 */
	public void testRepeat() throws Exception {
		Deferred<String> d = new Deferred<String>();
		Promise<String> p1 = d.getPromise();
		String value = new String("10");
		d.resolve(value);
		assertTrue("promise not resolved", p1.isDone());

		final CountDownLatch latch1 = new CountDownLatch(1);
		p1.onResolve(new Runnable() {
			public void run() {
				latch1.countDown();
			}
		});
		assertTrue("callback did not run after resolved", latch1.await(WAIT_TIME, TimeUnit.SECONDS));

		final CountDownLatch latch2 = new CountDownLatch(1);
		p1.onResolve(new Runnable() {
			public void run() {
				latch2.countDown();
			}
		});
		assertTrue("callback did not run after resolved", latch2.await(WAIT_TIME, TimeUnit.SECONDS));

		final CountDownLatch latch3 = new CountDownLatch(1);
		Promise<Integer> p2 = p1.then(new Success<String, Integer>() {
			public Promise<Integer> call(Promise<String> promise) throws Exception {
				latch3.countDown();
				return Promises.newResolvedPromise(Integer.valueOf(promise.getValue()));
			}
		});
		assertTrue("callback did not run after resolved", latch3.await(WAIT_TIME, TimeUnit.SECONDS));

		assertEquals("wrong value", 10, p2.getValue().intValue());
		assertNull("wrong failure", p2.getFailure());
	}

	/**
	 * Test the basic chaining functionality.
	 */
	public void testThen() throws Exception {
		Deferred<String> d = new Deferred<String>();
		Promise<String> p1 = d.getPromise();
		final CountDownLatch latch = new CountDownLatch(1);
		Promise<Number> p2 = p1.<Number, Integer> then(new Success<Object, Integer>() {
			public Promise<Integer> call(final Promise<Object> promise)
					throws Exception {
				latch.countDown();
				final Deferred<Integer> n = new Deferred<Integer>();
				timer.schedule(new TimerTask() {
					public void run() {
						try {
							n.resolve(Integer.valueOf(promise.getValue().toString()));
						} catch (Exception e) {
							n.fail(e);
						}
					}
				}, 500);
				return n.getPromise();
			}
		});

		assertFalse("callback ran before resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
		assertFalse(p1.isDone());
		assertFalse(p2.isDone());

		String value = new String("20");
		d.resolve(value);
		assertTrue("callback did not run after resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
		assertTrue(p1.isDone());
		assertFalse(p2.isDone());

		assertEquals(20, p2.getValue().intValue());
		assertNull("wrong failure", p2.getFailure());
	}

	public void testValueInterrupted() throws Exception {
		final Deferred<String> d = new Deferred<String>();
		Promise<String> p = d.getPromise();
		final Thread thread = Thread.currentThread();
		assertFalse(p.isDone());
		timer.schedule(new TimerTask() {
			public void run() {
				thread.interrupt();
				timer.schedule(new TimerTask() {
					public void run() {
						d.resolve("failsafe");
					}
				}, 1000);
			}
		}, 500);
		try {
			p.getValue();
			fail("failed to throw InterruptedException");
		} catch (InterruptedException e) {
			// expected
		}
	}

	public void testFailureInterrupted() throws Exception {
		final Deferred<String> d = new Deferred<String>();
		Promise<String> p = d.getPromise();
		final Thread thread = Thread.currentThread();
		assertFalse(p.isDone());
		timer.schedule(new TimerTask() {
			public void run() {
				thread.interrupt();
				timer.schedule(new TimerTask() {
					public void run() {
						d.resolve("failsafe");
					}
				}, 1000);
			}
		}, 500);
		try {
			p.getFailure();
			fail("failed to throw InterruptedException");
		} catch (InterruptedException e) {
			// expected
		}
	}

	public void testNullCallback() throws Exception {
		Deferred<String> d = new Deferred<String>();
		Promise<String> p = d.getPromise();
		try {
			p.onResolve(null);
			fail("failed to error on null callback");
		} catch (NullPointerException e) {
			// expected
		}
	}

	public void testFailNull() throws Exception {
		Deferred<String> d = new Deferred<String>();
		try {
			d.fail(null);
			fail("failed to error on null failure");
		} catch (NullPointerException e) {
			// expected
		}
	}

	public void testMultiResolve() throws Exception {
		final Deferred<String> d = new Deferred<String>();
		Promise<String> p = d.getPromise();
		final AtomicBoolean fail = new AtomicBoolean(false);
		assertFalse(p.isDone());
		p.onResolve(new Runnable() {
			public void run() {
				try {
					d.resolve("onResolve");
					fail.set(true);
				} catch (IllegalStateException e) {
					// expected
				}
			}
		});
		d.resolve("first");
		assertTrue(p.isDone());
		assertFalse("failed to error on callback resolve", fail.get());
		try {
			d.resolve("second");
			fail("failed to error on second resolve");
		} catch (IllegalStateException e) {
			// expected
		}
		try {
			d.fail(new Exception("second"));
			fail("failed to error on fail after resolve");
		} catch (IllegalStateException e) {
			// expected
		}
	}

	public void testMultiFail() throws Exception {
		final Deferred<String> d = new Deferred<String>();
		Promise<String> p = d.getPromise();
		final AtomicBoolean fail = new AtomicBoolean(false);
		assertFalse(p.isDone());
		p.onResolve(new Runnable() {
			public void run() {
				try {
					d.fail(new Exception("onResolve"));
					fail.set(true);
				} catch (IllegalStateException e) {
					// expected
				}
			}
		});
		d.fail(new Exception("first"));
		assertTrue(p.isDone());
		assertFalse("failed to error on callback fail", fail.get());
		try {
			d.fail(new Exception("second"));
			fail("failed to error on second fail");
		} catch (IllegalStateException e) {
			// expected
		}
		try {
			d.resolve("second");
			fail("failed to error on resolve after fail");
		} catch (IllegalStateException e) {
			// expected
		}
	}

	public void testCallbackException1() throws Exception {
		final int size = 20;
		final Deferred<String> d = new Deferred<String>();
		final Promise<String> p = d.getPromise();
		final CountDownLatch latch = new CountDownLatch(size);
		final AtomicInteger count = new AtomicInteger(0);
		Random random = new Random();
		int next = random.nextInt(size);
		final int runtimeFail = next;
		do {
			next = random.nextInt(size);
		} while (next == runtimeFail);
		final int errorFail = next;
		for (int i = 0; i < size; i++) {
			p.onResolve(new Runnable() {
				public void run() {
					final int callback = count.getAndIncrement();
					latch.countDown();
					if (callback == runtimeFail) {
						throw new RuntimeException("bad callback " + callback);
					} else if (callback == errorFail) {
						throw new Error("bad callback " + callback);
					}
				}
			});
		}
		assertFalse("should not be resolved", p.isDone());
		assertEquals("some callbacks called", size, latch.getCount());
		final String result = "value";
		d.resolve(result);
		assertTrue("should be resolved", p.isDone());
		assertNull("wrong failure", p.getFailure());
		assertSame("wrong value", result, p.getValue());
		assertTrue("all callbacks did not run after resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
	}

	public void testCallbackException2() throws Exception {
		final Deferred<String> d = new Deferred<String>();
		final Promise<String> p = d.getPromise();
		assertFalse("should not be resolved", p.isDone());
		Throwable failure = new RuntimeException();
		d.fail(failure);
		p.onResolve(new Runnable() {
			public void run() {
				throw new Error("bad callback upon onResolve");
			}
		});
		assertTrue("should be resolved", p.isDone());
		assertSame("wrong failure", failure, p.getFailure());
		try {
			p.getValue();
			fail("getValue failed to throw InvocationTargetException");
		} catch (InvocationTargetException e) {
			assertSame("wrong failure", failure, e.getCause());
		}
	}

	public void testNewResolvedPromise() throws Exception {
		final CountDownLatch latch1 = new CountDownLatch(1);
		final CountDownLatch latch2 = new CountDownLatch(1);
		String value1 = new String("value");
		final Promise<String> p1 = Promises.newResolvedPromise(value1);
		final Promise<String> p2 = Promises.newResolvedPromise((String) null);
		assertTrue("promise not resolved", p1.isDone());
		assertTrue("promise not resolved", p2.isDone());
		p1.onResolve(new Runnable() {
			public void run() {
				latch1.countDown();
			}
		});
		p2.onResolve(new Runnable() {
			public void run() {
				latch2.countDown();
			}
		});
		assertTrue("callback did not run", latch1.await(WAIT_TIME, TimeUnit.SECONDS));
		assertTrue("callback did not run", latch2.await(WAIT_TIME, TimeUnit.SECONDS));
		assertNull("wrong failure", p1.getFailure());
		assertSame("wrong value", value1, p1.getValue());
		assertNull("wrong failure", p2.getFailure());
		assertNull("wrong value", p2.getValue());
	}

	public void testNewFailedPromise() throws Exception {
		final CountDownLatch latch = new CountDownLatch(1);
		Throwable failure = new Exception("value");
		final Promise<String> p = Promises.newFailedPromise(failure);
		assertTrue("promise not resolved", p.isDone());
		p.onResolve(new Runnable() {
			public void run() {
				latch.countDown();
			}
		});
		assertTrue("callback did not run", latch.await(WAIT_TIME, TimeUnit.SECONDS));
		assertSame("wrong failure", failure, p.getFailure());
		try {
			p.getValue();
			fail("getValue failed to throw InvocationTargetException");
		} catch (InvocationTargetException e) {
			assertSame("wrong failure", failure, e.getCause());
		}
		try {
			Promises.newFailedPromise(null);
			fail("expected NullPointerException");
		} catch (NullPointerException e) {
			// expected
		}
	}

	public void testLatchPromiseSuccess1() throws Exception {
		final Deferred<Integer> d1 = new Deferred<Integer>();
		final Promise<Integer> p1 = d1.getPromise();
		final CountDownLatch latch1 = new CountDownLatch(1);
		p1.onResolve(new Runnable() {
			public void run() {
				latch1.countDown();
			}
		});
		final Deferred<Long> d2 = new Deferred<Long>();
		final Promise<Long> p2 = d2.getPromise();
		final CountDownLatch latch2 = new CountDownLatch(1);
		p2.onResolve(new Runnable() {
			public void run() {
				latch2.countDown();
			}
		});
		final Promise<Void> latched = Promises.newLatchPromise(p1, p2);
		final CountDownLatch latch = new CountDownLatch(1);
		latched.onResolve(new Runnable() {
			public void run() {
				latch.countDown();
			}
		});
		assertFalse("p1 resolved", p1.isDone());
		assertFalse("p2 resolved", p2.isDone());
		assertFalse("latched resolved", latched.isDone());
		Integer value1 = new Integer(12);
		d1.resolve(value1);
		assertTrue("p1 callback did not run after resolved", latch1.await(WAIT_TIME, TimeUnit.SECONDS));
		assertTrue("p1 not resolved", p1.isDone());
		assertNull("p1 wrong failure", p1.getFailure());
		assertSame("p1 wrong value", value1, p1.getValue());
		assertFalse("p2 resolved", p2.isDone());
		assertFalse("latched resolved", latched.isDone());
		Long value2 = new Long(24);
		d2.resolve(value2);
		assertTrue("p2 callback did not run after resolved", latch2.await(WAIT_TIME, TimeUnit.SECONDS));
		assertTrue("p2 not resolved", p2.isDone());
		assertNull("p2 wrong failure", p2.getFailure());
		assertEquals("p2 wrong value", value2, p2.getValue());
		assertTrue("latched callback did not run after resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
		assertTrue("latched not resolved", latched.isDone());
		assertNull("latched wrong failure", latched.getFailure());
		assertNull("latched wrong value", latched.getValue());
	}

	public void testLatchPromiseSuccess2() throws Exception {
		final Deferred<String> d1 = new Deferred<String>();
		final Promise<String> p1 = d1.getPromise();
		final CountDownLatch latch1 = new CountDownLatch(1);
		p1.onResolve(new Runnable() {
			public void run() {
				latch1.countDown();
			}
		});
		final Deferred<String> d2 = new Deferred<String>();
		final Promise<String> p2 = d2.getPromise();
		final CountDownLatch latch2 = new CountDownLatch(1);
		p2.onResolve(new Runnable() {
			public void run() {
				latch2.countDown();
			}
		});
		Collection<Promise<String>> promises = new ArrayList<Promise<String>>();
		promises.add(p1);
		promises.add(p2);
		final Promise<Void> latched = Promises.newLatchPromise(promises);
		final CountDownLatch latch = new CountDownLatch(1);
		latched.onResolve(new Runnable() {
			public void run() {
				latch.countDown();
			}
		});
		assertFalse("p1 resolved", p1.isDone());
		assertFalse("p2 resolved", p2.isDone());
		assertFalse("latched resolved", latched.isDone());
		String value1 = new String("12");
		d1.resolve(value1);
		assertTrue("p1 callback did not run after resolved", latch1.await(WAIT_TIME, TimeUnit.SECONDS));
		assertTrue("p1 not resolved", p1.isDone());
		assertNull("p1 wrong failure", p1.getFailure());
		assertSame("p1 wrong value", value1, p1.getValue());
		assertFalse("p2 resolved", p2.isDone());
		assertFalse("latched resolved", latched.isDone());
		String value2 = new String("12");
		d2.resolve(value2);
		assertTrue("p2 callback did not run after resolved", latch2.await(WAIT_TIME, TimeUnit.SECONDS));
		assertTrue("p2 not resolved", p2.isDone());
		assertNull("p2 wrong failure", p2.getFailure());
		assertSame("p2 wrong value", value2, p2.getValue());
		assertTrue("latched callback did not run after resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
		assertTrue("latched not resolved", latched.isDone());
		assertNull("latched wrong failure", latched.getFailure());
		assertNull("latched wrong value", latched.getValue());
	}

	public void testLatchPromiseFail1() throws Exception {
		final Deferred<Integer> d1 = new Deferred<Integer>();
		final Promise<Integer> p1 = d1.getPromise();
		final CountDownLatch latch1 = new CountDownLatch(1);
		p1.onResolve(new Runnable() {
			public void run() {
				latch1.countDown();
			}
		});
		final Deferred<Long> d2 = new Deferred<Long>();
		final Promise<Long> p2 = d2.getPromise();
		final CountDownLatch latch2 = new CountDownLatch(1);
		p2.onResolve(new Runnable() {
			public void run() {
				latch2.countDown();
			}
		});
		final Deferred<Long> d3 = new Deferred<Long>();
		final Promise<Long> p3 = d3.getPromise();
		final CountDownLatch latch3 = new CountDownLatch(1);
		p2.onResolve(new Runnable() {
			public void run() {
				latch3.countDown();
			}
		});
		final Promise<Void> latched = Promises.newLatchPromise(p1, p2, p3);
		final CountDownLatch latch = new CountDownLatch(1);
		latched.onResolve(new Runnable() {
			public void run() {
				latch.countDown();
			}
		});
		assertFalse("p1 resolved", p1.isDone());
		assertFalse("p2 resolved", p2.isDone());
		assertFalse("p3 resolved", p3.isDone());
		assertFalse("latched resolved", latched.isDone());
		Throwable f1 = new Exception("fail1");
		d1.fail(f1);
		assertTrue("p1 callback did not run after resolved", latch1.await(WAIT_TIME, TimeUnit.SECONDS));
		assertTrue("p1 not resolved", p1.isDone());
		assertSame("p1 wrong failure", f1, p1.getFailure());
		try {
			p1.getValue();
			fail("p1 getValue failed to throw InvocationTargetException");
		} catch (InvocationTargetException e) {
			assertSame("wrong failure", f1, e.getCause());
		}
		assertFalse("p2 resolved", p2.isDone());
		assertFalse("latched resolved", latched.isDone());
		Long value2 = new Long(24);
		d2.resolve(value2);
		assertTrue("p2 callback did not run after resolved", latch2.await(WAIT_TIME, TimeUnit.SECONDS));
		assertTrue("p2 not resolved", p2.isDone());
		assertNull("p2 wrong failure", p2.getFailure());
		assertSame("p2 wrong value", value2, p2.getValue());
		assertFalse("latched resolved", latched.isDone());
		Throwable f3 = new Exception("fail3");
		d3.fail(f3);
		assertTrue("p3 callback did not run after resolved", latch3.await(WAIT_TIME, TimeUnit.SECONDS));
		assertTrue("p3 not resolved", p3.isDone());
		assertSame("p3 wrong failure", f3, p3.getFailure());
		try {
			p3.getValue();
			fail("p3 getValue failed to throw InvocationTargetException");
		} catch (InvocationTargetException e) {
			assertSame("wrong failure", f3, e.getCause());
		}
		assertTrue("latched callback did not run after resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
		assertTrue("latched not resolved", latched.isDone());
		FailedPromisesException fail = (FailedPromisesException) latched.getFailure();
		assertNotNull("latched wrong failure", fail);
		Collection<Promise<?>> failedPromises = fail.getFailedPromises();
		assertEquals("latched error contains wrong number of failed promise", 2, failedPromises.size());
		assertTrue("latched error doesn't contain failed promise p1", failedPromises.contains(p1));
		assertTrue("latched error doesn't contain failed promise p3", failedPromises.contains(p3));
		try {
			latched.getValue();
			fail("latched getValue failed to throw InvocationTargetException");
		} catch (InvocationTargetException e) {
			assertSame("wrong failure", fail, e.getCause());
		}
	}

	public void testLatchPromiseEmpty1() throws Exception {
		Collection<Promise<String>> promises = Collections.emptyList();
		final Promise<Void> latched = Promises.newLatchPromise(promises);
		assertTrue("latched not resolved", latched.isDone());
		final CountDownLatch latch = new CountDownLatch(1);
		latched.onResolve(new Runnable() {
			public void run() {
				latch.countDown();
			}
		});
		assertTrue("latched callback did not run after resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
		assertNull("latched wrong failure", latched.getFailure());
		assertNull("latched wrong value", latched.getValue());
	}

	public void testLatchPromiseEmpty2() throws Exception {
		final Promise<Void> latched = Promises.newLatchPromise();
		assertTrue("latched not resolved", latched.isDone());
		final CountDownLatch latch = new CountDownLatch(1);
		latched.onResolve(new Runnable() {
			public void run() {
				latch.countDown();
			}
		});
		assertTrue("latched callback did not run after resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
		assertNull("latched wrong failure", latched.getFailure());
		assertNull("latched wrong value", latched.getValue());
	}

	public void testLatchPromiseNull() throws Exception {
		try {
			Promises.newLatchPromise((Promise<?>[]) null);
			fail("expected NullPointerException");
		} catch (NullPointerException e) {
			// expected
		}
		try {
			Promises.newLatchPromise((Collection<Promise<Object>>) null);
			fail("expected NullPointerException");
		} catch (NullPointerException e) {
			// expected
		}
	}

	public void testResolveWithSuccess() throws Exception {
		final Deferred<Integer> d1 = new Deferred<Integer>();
		final CountDownLatch latch1 = new CountDownLatch(1);
		final Promise<Integer> p1 = d1.getPromise();
		p1.onResolve(new Runnable() {
			public void run() {
				latch1.countDown();
			}
		});
		final Deferred<Number> d2 = new Deferred<Number>();
		final CountDownLatch latch2 = new CountDownLatch(1);
		final Promise<Number> p2 = d2.getPromise();
		p2.onResolve(new Runnable() {
			public void run() {
				latch2.countDown();
			}
		});
		final Promise<Void> p3 = d2.resolveWith(p1);
		final CountDownLatch latch3 = new CountDownLatch(1);
		p3.onResolve(new Runnable() {
			public void run() {
				latch3.countDown();
			}
		});
		assertFalse("callback ran before resolved", latch1.await(WAIT_TIME, TimeUnit.SECONDS));
		assertFalse("callback ran before resolved", p1.isDone());
		assertFalse("callback ran before resolved", latch2.await(WAIT_TIME, TimeUnit.SECONDS));
		assertFalse("callback ran before resolved", p2.isDone());
		assertFalse("callback ran before resolved", latch3.await(WAIT_TIME, TimeUnit.SECONDS));
		assertFalse("callback ran before resolved", p3.isDone());

		Integer value = Integer.valueOf(42);
		d1.resolve(value);
		assertTrue("callback did not run after resolved", latch1.await(WAIT_TIME, TimeUnit.SECONDS));
		assertTrue("callback did not run after resolved", p1.isDone());
		assertTrue("callback did not run after resolved", latch2.await(WAIT_TIME, TimeUnit.SECONDS));
		assertTrue("callback did not run after resolved", p2.isDone());
		assertTrue("callback did not run after resolved", latch3.await(WAIT_TIME, TimeUnit.SECONDS));
		assertTrue("callback did not run after resolved", p3.isDone());
		assertNull("wrong failure", p1.getFailure());
		assertSame("wrong value", value, p1.getValue());
		assertNull("wrong failure", p2.getFailure());
		assertSame("wrong value", value, p2.getValue());
		assertNull("wrong failure", p3.getFailure());
		assertNull("wrong value", p3.getValue());
	}

	public void testResolveWithFailure() throws Exception {
		final Deferred<Integer> d1 = new Deferred<Integer>();
		final CountDownLatch latch1 = new CountDownLatch(1);
		final Promise<Integer> p1 = d1.getPromise();
		p1.onResolve(new Runnable() {
			public void run() {
				latch1.countDown();
			}
		});
		final Deferred<Number> d2 = new Deferred<Number>();
		final CountDownLatch latch2 = new CountDownLatch(1);
		final Promise<Number> p2 = d2.getPromise();
		p2.onResolve(new Runnable() {
			public void run() {
				latch2.countDown();
			}
		});
		final Promise<Void> p3 = d2.resolveWith(p1);
		final CountDownLatch latch3 = new CountDownLatch(1);
		p3.onResolve(new Runnable() {
			public void run() {
				latch3.countDown();
			}
		});
		assertFalse("callback ran before resolved", latch1.await(WAIT_TIME, TimeUnit.SECONDS));
		assertFalse("callback ran before resolved", p1.isDone());
		assertFalse("callback ran before resolved", latch2.await(WAIT_TIME, TimeUnit.SECONDS));
		assertFalse("callback ran before resolved", p2.isDone());
		assertFalse("callback ran before resolved", latch3.await(WAIT_TIME, TimeUnit.SECONDS));
		assertFalse("callback ran before resolved", p3.isDone());

		Throwable failure = new RuntimeException();
		d1.fail(failure);
		assertTrue("callback did not run after resolved", latch1.await(WAIT_TIME, TimeUnit.SECONDS));
		assertTrue("callback did not run after resolved", p1.isDone());
		assertTrue("callback did not run after resolved", latch2.await(WAIT_TIME, TimeUnit.SECONDS));
		assertTrue("callback did not run after resolved", p2.isDone());
		assertTrue("callback did not run after resolved", latch3.await(WAIT_TIME, TimeUnit.SECONDS));
		assertTrue("callback did not run after resolved", p3.isDone());
		assertSame("wrong failure", failure, p1.getFailure());
		try {
			p1.getValue();
			fail("getValue failed to throw InvocationTargetException");
		} catch (InvocationTargetException e) {
			assertSame("wrong failure", failure, e.getCause());
		}
		assertSame("wrong failure", failure, p2.getFailure());
		try {
			p2.getValue();
			fail("getValue failed to throw InvocationTargetException");
		} catch (InvocationTargetException e) {
			assertSame("wrong failure", failure, e.getCause());
		}
		assertNull("wrong failure", p3.getFailure());
		assertNull("wrong value", p3.getValue());
	}

	public void testResolveWithAlready1() throws Exception {
		final Deferred<Integer> d1 = new Deferred<Integer>();
		final CountDownLatch latch1 = new CountDownLatch(1);
		final Promise<Integer> p1 = d1.getPromise();
		p1.onResolve(new Runnable() {
			public void run() {
				latch1.countDown();
			}
		});
		final Deferred<Number> d2 = new Deferred<Number>();
		final CountDownLatch latch2 = new CountDownLatch(1);
		final Promise<Number> p2 = d2.getPromise();
		p2.onResolve(new Runnable() {
			public void run() {
				latch2.countDown();
			}
		});
		final Promise<Void> p3 = d2.resolveWith(p1);
		final CountDownLatch latch3 = new CountDownLatch(1);
		p3.onResolve(new Runnable() {
			public void run() {
				latch3.countDown();
			}
		});
		assertFalse("callback ran before resolved", latch1.await(WAIT_TIME, TimeUnit.SECONDS));
		assertFalse("callback ran before resolved", p1.isDone());
		assertFalse("callback ran before resolved", latch2.await(WAIT_TIME, TimeUnit.SECONDS));
		assertFalse("callback ran before resolved", p2.isDone());
		assertFalse("callback ran before resolved", latch3.await(WAIT_TIME, TimeUnit.SECONDS));
		assertFalse("callback ran before resolved", p3.isDone());

		Integer value = Integer.valueOf(42);
		d2.resolve(value);
		assertTrue("callback did not run after resolved", latch2.await(WAIT_TIME, TimeUnit.SECONDS));
		assertTrue("callback did not run after resolved", p2.isDone());
		assertFalse("callback ran before resolved", latch3.await(WAIT_TIME, TimeUnit.SECONDS));
		assertFalse("callback ran before resolved", p3.isDone());
		assertNull("wrong failure", p2.getFailure());
		assertSame("wrong value", value, p2.getValue());

		Throwable failure = new RuntimeException();
		d1.fail(failure);
		assertTrue("callback did not run after resolved", latch1.await(WAIT_TIME, TimeUnit.SECONDS));
		assertTrue("callback did not run after resolved", p1.isDone());
		assertTrue("callback did not run after resolved", latch3.await(WAIT_TIME, TimeUnit.SECONDS));
		assertTrue("callback did not run after resolved", p3.isDone());
		assertSame("wrong failure", failure, p1.getFailure());
		try {
			p1.getValue();
			fail("getValue failed to throw InvocationTargetException");
		} catch (InvocationTargetException e) {
			assertSame("wrong failure", failure, e.getCause());
		}
		assertTrue("wrong failure", p3.getFailure() instanceof IllegalStateException);
		try {
			p3.getValue();
			fail("getValue failed to throw InvocationTargetException");
		} catch (InvocationTargetException e) {
			assertTrue("wrong failure", e.getCause() instanceof IllegalStateException);
		}
	}

	public void testResolveWithAlready2() throws Exception {
		Integer value = Integer.valueOf(42);
		final Promise<Integer> p1 = Promises.newResolvedPromise(value);
		final Deferred<Number> d2 = new Deferred<Number>();
		d2.resolve(value);

		final Promise<Void> p3 = d2.resolveWith(p1);
		assertTrue("not resolved", p3.isDone());
		assertTrue("wrong failure", p3.getFailure() instanceof IllegalStateException);
		try {
			p3.getValue();
			fail("getValue failed to throw InvocationTargetException");
		} catch (InvocationTargetException e) {
			assertTrue("wrong failure", e.getCause() instanceof IllegalStateException);
		}
	}

	public void testResolveWithAlready3() throws Exception {
		Integer value = Integer.valueOf(42);
		Throwable failure = new RuntimeException();
		final Promise<Integer> p1 = Promises.newResolvedPromise(value);
		final Deferred<Number> d2 = new Deferred<Number>();
		d2.fail(failure);

		final Promise<Void> p3 = d2.resolveWith(p1);
		assertTrue("not resolved", p3.isDone());
		assertTrue("wrong failure", p3.getFailure() instanceof IllegalStateException);
		try {
			p3.getValue();
			fail("getValue failed to throw InvocationTargetException");
		} catch (InvocationTargetException e) {
			assertTrue("wrong failure", e.getCause() instanceof IllegalStateException);
		}
	}

	public void testResolveWithAlready4() throws Exception {
		Integer value = Integer.valueOf(42);
		final Promise<Integer> p1 = Promises.newResolvedPromise(value);
		final Deferred<Number> d2 = new Deferred<Number>();
		final Promise<Number> p2 = d2.getPromise();
		final Promise<Void> p3 = d2.resolveWith(p1);
		assertTrue("not resolved", p2.isDone());
		assertTrue("not resolved", p3.isDone());
		assertNull("wrong failure", p2.getFailure());
		assertSame("wrong value", value, p2.getValue());
		assertNull("wrong failure", p3.getFailure());
		assertNull("wrong value", p3.getValue());
	}

	public void testResolveWithAlready5() throws Exception {
		Throwable failure = new RuntimeException();
		final Promise<Integer> p1 = Promises.newFailedPromise(failure);
		final Deferred<Number> d2 = new Deferred<Number>();
		final Promise<Number> p2 = d2.getPromise();
		final Promise<Void> p3 = d2.resolveWith(p1);
		assertTrue("not resolved", p2.isDone());
		assertTrue("not resolved", p3.isDone());
		assertSame("wrong failure", failure, p2.getFailure());
		try {
			p2.getValue();
			fail("getValue failed to throw InvocationTargetException");
		} catch (InvocationTargetException e) {
			assertSame("wrong failure", failure, e.getCause());
		}
		assertNull("wrong failure", p3.getFailure());
		assertNull("wrong value", p3.getValue());
	}

	public void testResolveWithNull() throws Exception {
		Deferred<String> d = new Deferred<String>();
		try {
			d.resolveWith(null);
			fail("failed to error on null promise");
		} catch (NullPointerException e) {
			// expected
		}
	}

	public void testFilter() throws Exception {
		String value1 = new String("value");
		String value3 = new String("");
		Promise<String> p1 = Promises.newResolvedPromise(value1);
		final CountDownLatch latch1 = new CountDownLatch(1);
		Promise<String> p2 = p1.filter(new Predicate<String>() {
			public boolean test(String t) {
				latch1.countDown();
				return t.length() > 0;
			}
		});
		final CountDownLatch latch2 = new CountDownLatch(1);
		Promise<String> p4 = p1.filter(new Predicate<String>() {
			public boolean test(String t) {
				latch2.countDown();
				return t.length() == 0;
			}
		});
		Promise<String> p3 = Promises.newResolvedPromise(value3);
		final CountDownLatch latch3 = new CountDownLatch(1);
		Promise<String> p5 = p3.filter(new Predicate<String>() {
			public boolean test(String t) {
				latch3.countDown();
				return t.length() > 0;
			}
		});

		assertTrue(p1.isDone());
		assertTrue(p2.isDone());
		assertTrue(latch1.await(WAIT_TIME, TimeUnit.SECONDS));
		assertTrue(p3.isDone());
		assertTrue(p4.isDone());
		assertTrue(latch2.await(WAIT_TIME, TimeUnit.SECONDS));
		assertTrue(p5.isDone());
		assertTrue(latch3.await(WAIT_TIME, TimeUnit.SECONDS));

		assertSame("wrong value", value1, p1.getValue());
		assertNull("wrong failure", p1.getFailure());
		assertSame("wrong value", value1, p2.getValue());
		assertNull("wrong failure", p2.getFailure());
		assertSame("wrong value", value3, p3.getValue());
		assertNull("wrong failure", p3.getFailure());
		Throwable f4 = p4.getFailure();
		assertNotNull("wrong failure", f4);
		try {
			p4.getValue();
			fail("p4 getValue failed to throw InvocationTargetException");
		} catch (InvocationTargetException e) {
			assertSame("wrong failure", f4, e.getCause());
		}
		Throwable f5 = p5.getFailure();
		assertNotNull("wrong failure", f5);
		assertTrue(f5 instanceof NoSuchElementException);
		try {
			p5.getValue();
			fail("p5 getValue failed to throw InvocationTargetException");
		} catch (InvocationTargetException e) {
			assertSame("wrong failure", f5, e.getCause());
		}
	}

	public void testFilterException() throws Exception {
		String value1 = new String("value");
		final Error failure = new Error("fail");
		Promise<String> p1 = Promises.newResolvedPromise(value1);
		final CountDownLatch latch1 = new CountDownLatch(1);
		final CountDownLatch latch2 = new CountDownLatch(1);
		Promise<String> p2 = p1.filter(new Predicate<String>() {
			public boolean test(String t) {
				latch1.countDown();
				throw failure;
			}
		}).filter(new Predicate<String>() {
			public boolean test(String t) {
				latch2.countDown();
				return t.length() > 0;
			}
		});

		assertTrue(p1.isDone());
		assertTrue(p2.isDone());
		assertTrue(latch1.await(WAIT_TIME, TimeUnit.SECONDS));
		assertFalse(latch2.await(WAIT_TIME, TimeUnit.SECONDS));

		assertSame("wrong value", value1, p1.getValue());
		assertNull("wrong failure", p1.getFailure());
		assertSame("wrong failure", failure, p2.getFailure());
		try {
			p2.getValue();
			fail("p2 getValue failed to throw InvocationTargetException");
		} catch (InvocationTargetException e) {
			assertSame("wrong failure", failure, e.getCause());
		}
	}

	public void testFilterFailed() throws Exception {
		final Error failure = new Error("fail");
		Promise<String> p1 = Promises.newFailedPromise(failure);
		final CountDownLatch latch1 = new CountDownLatch(1);
		Promise<String> p2 = p1.filter(new Predicate<String>() {
			public boolean test(String t) {
				latch1.countDown();
				return t.length() > 0;
			}
		});

		assertTrue(p1.isDone());
		assertTrue(p2.isDone());
		assertFalse(latch1.await(WAIT_TIME, TimeUnit.SECONDS));

		assertSame("wrong failure", failure, p2.getFailure());
		try {
			p2.getValue();
			fail("p2 getValue failed to throw InvocationTargetException");
		} catch (InvocationTargetException e) {
			assertSame("wrong failure", failure, e.getCause());
		}
	}

	public void testFilterNull() throws Exception {
		String value1 = new String("value");
		Promise<String> p1 = Promises.newResolvedPromise(value1);
		try {
			p1.filter(null);
			fail("failed to error on null predicate");
		} catch (NullPointerException e) {
			// expected
		}
	}

	public void testMap() throws Exception {
		Integer value1 = new Integer(42);
		Promise<Integer> p1 = Promises.newResolvedPromise(value1);
		final CountDownLatch latch1 = new CountDownLatch(1);
		final CountDownLatch latch2 = new CountDownLatch(1);
		Promise<String> p2 = p1.<Number, Long> map(new Function<Number, Long>() {
			public Long apply(Number t) {
				latch1.countDown();
				return new Long(t.longValue());
			}
		}).map(new Function<Number, String>() {
			public String apply(Number t) {
				latch2.countDown();
				return t.toString();
			}
		});
		assertTrue(p1.isDone());
		assertTrue(p2.isDone());
		assertTrue(latch1.await(WAIT_TIME, TimeUnit.SECONDS));
		assertTrue(latch2.await(WAIT_TIME, TimeUnit.SECONDS));

		assertEquals("wrong value", value1.toString(), p2.getValue());
		assertNull("wrong failure", p2.getFailure());
	}

	public void testMapException() throws Exception {
		Integer value1 = new Integer(42);
		final Error failure = new Error("fail");
		Promise<Integer> p1 = Promises.newResolvedPromise(value1);
		final CountDownLatch latch1 = new CountDownLatch(1);
		final CountDownLatch latch2 = new CountDownLatch(1);
		Promise<String> p2 = p1.<Number, Long> map(new Function<Number, Long>() {
			public Long apply(Number t) {
				latch1.countDown();
				throw failure;
			}
		}).map(new Function<Number, String>() {
			public String apply(Number t) {
				latch2.countDown();
				return t.toString();
			}
		});
		assertTrue(p1.isDone());
		assertTrue(p2.isDone());
		assertTrue(latch1.await(WAIT_TIME, TimeUnit.SECONDS));
		assertFalse(latch2.await(WAIT_TIME, TimeUnit.SECONDS));

		assertSame("wrong failure", failure, p2.getFailure());
		try {
			p2.getValue();
			fail("p2 getValue failed to throw InvocationTargetException");
		} catch (InvocationTargetException e) {
			assertSame("wrong failure", failure, e.getCause());
		}
	}

	public void testMapNull() throws Exception {
		String value1 = new String("value");
		Promise<String> p1 = Promises.newResolvedPromise(value1);
		try {
			p1.map(null);
			fail("failed to error on null function");
		} catch (NullPointerException e) {
			// expected
		}
	}

	public void testFlatMap() throws Exception {
		Integer value1 = new Integer(42);
		Promise<Integer> p1 = Promises.newResolvedPromise(value1);
		final CountDownLatch latch1 = new CountDownLatch(1);
		final CountDownLatch latch2 = new CountDownLatch(1);
		Promise<String> p2 = p1.<Number, Long> flatMap(new Function<Number, Promise<Long>>() {
			public Promise<Long> apply(Number t) {
				latch1.countDown();
				return Promises.newResolvedPromise(new Long(t.longValue()));
			}
		}).flatMap(new Function<Number, Promise<String>>() {
			public Promise<String> apply(Number t) {
				latch2.countDown();
				return Promises.newResolvedPromise(t.toString());
			}
		});
		assertTrue(p1.isDone());
		assertTrue(p2.isDone());
		assertTrue(latch1.await(WAIT_TIME, TimeUnit.SECONDS));
		assertTrue(latch2.await(WAIT_TIME, TimeUnit.SECONDS));

		assertEquals("wrong value", value1.toString(), p2.getValue());
		assertNull("wrong failure", p2.getFailure());
	}

	public void testFlatMapException() throws Exception {
		Integer value1 = new Integer(42);
		final Error failure = new Error("fail");
		Promise<Integer> p1 = Promises.newResolvedPromise(value1);
		final CountDownLatch latch1 = new CountDownLatch(1);
		final CountDownLatch latch2 = new CountDownLatch(1);
		Promise<String> p2 = p1.<Number, Long> flatMap(new Function<Number, Promise<Long>>() {
			public Promise<Long> apply(Number t) {
				latch1.countDown();
				throw failure;
			}
		}).flatMap(new Function<Number, Promise<String>>() {
			public Promise<String> apply(Number t) {
				latch2.countDown();
				return Promises.newResolvedPromise(t.toString());
			}
		});
		assertTrue(p1.isDone());
		assertTrue(p2.isDone());
		assertTrue(latch1.await(WAIT_TIME, TimeUnit.SECONDS));
		assertFalse(latch2.await(WAIT_TIME, TimeUnit.SECONDS));

		assertSame("wrong failure", failure, p2.getFailure());
		try {
			p2.getValue();
			fail("p2 getValue failed to throw InvocationTargetException");
		} catch (InvocationTargetException e) {
			assertSame("wrong failure", failure, e.getCause());
		}
	}

	public void testFlatMapNull() throws Exception {
		String value1 = new String("value");
		Promise<String> p1 = Promises.newResolvedPromise(value1);
		try {
			p1.flatMap(null);
			fail("failed to error on null function");
		} catch (NullPointerException e) {
			// expected
		}
	}

	public void testRecoverNoFailure() throws Exception {
		final Integer value1 = new Integer(42);
		final Long value2 = new Long(43);
		final Promise<Number> p1 = Promises.newResolvedPromise((Number) value1);
		final CountDownLatch latch1 = new CountDownLatch(1);
		final Promise<Number> p2 = p1.recover(new Function<Promise<?>, Long>() {
			public Long apply(Promise<?> t) {
				latch1.countDown();
				return value2;
			}
		});
		assertTrue(p1.isDone());
		assertTrue(p2.isDone());
		assertFalse(latch1.await(WAIT_TIME, TimeUnit.SECONDS));

		assertSame("wrong value", value1, p2.getValue());
		assertNull("wrong failure", p2.getFailure());
	}

	public void testRecoverFailure() throws Exception {
		final Throwable failure = new Error("fail");
		final Long value2 = new Long(43);
		final Promise<Number> p1 = Promises.newFailedPromise(failure);
		final CountDownLatch latch1 = new CountDownLatch(1);
		final Promise<Number> p2 = p1.recover(new Function<Promise<?>, Long>() {
			public Long apply(Promise<?> t) {
				latch1.countDown();
				try {
					assertSame(failure, t.getFailure());
				} catch (InterruptedException e) {
					fail(e);
				}
				return value2;
			}
		});
		assertTrue(p1.isDone());
		assertTrue(p2.isDone());
		assertTrue(latch1.await(WAIT_TIME, TimeUnit.SECONDS));

		assertSame("wrong value", value2, p2.getValue());
		assertNull("wrong failure", p2.getFailure());
	}

	public void testRecoverFailureNull() throws Exception {
		final Throwable failure = new Error("fail");
		final Promise<Number> p1 = Promises.newFailedPromise(failure);
		final CountDownLatch latch1 = new CountDownLatch(1);
		final Promise<Number> p2 = p1.recover(new Function<Promise<?>, Long>() {
			public Long apply(Promise<?> t) {
				latch1.countDown();
				try {
					assertSame(failure, t.getFailure());
				} catch (InterruptedException e) {
					fail(e);
				}
				return null;
			}
		});
		assertTrue(p1.isDone());
		assertTrue(p2.isDone());
		assertTrue(latch1.await(WAIT_TIME, TimeUnit.SECONDS));

		assertSame("wrong failure", failure, p2.getFailure());
		try {
			p2.getValue();
			fail("p2 getValue failed to throw InvocationTargetException");
		} catch (InvocationTargetException e) {
			assertSame("wrong failure", failure, e.getCause());
		}
	}

	public void testRecoverFailureException() throws Exception {
		final Throwable failure1 = new Error("fail1");
		final Error failure2 = new Error("fail2");
		final Promise<Number> p1 = Promises.newFailedPromise(failure1);
		final CountDownLatch latch1 = new CountDownLatch(1);
		final Promise<Number> p2 = p1.recover(new Function<Promise<?>, Long>() {
			public Long apply(Promise<?> t) {
				latch1.countDown();
				try {
					assertSame(failure1, t.getFailure());
				} catch (InterruptedException e) {
					fail(e);
				}
				throw failure2;
			}
		});
		assertTrue(p1.isDone());
		assertTrue(p2.isDone());
		assertTrue(latch1.await(WAIT_TIME, TimeUnit.SECONDS));

		assertSame("wrong failure", failure2, p2.getFailure());
		try {
			p2.getValue();
			fail("p2 getValue failed to throw InvocationTargetException");
		} catch (InvocationTargetException e) {
			assertSame("wrong failure", failure2, e.getCause());
		}
	}

	public void testRecoverNull() throws Exception {
		String value1 = new String("value");
		Promise<String> p1 = Promises.newResolvedPromise(value1);
		try {
			p1.recover(null);
			fail("failed to error on null function");
		} catch (NullPointerException e) {
			// expected
		}
	}

	public void testRecoverWithNoFailure() throws Exception {
		final Integer value1 = new Integer(42);
		final Long value2 = new Long(43);
		final Promise<Number> p1 = Promises.newResolvedPromise((Number) value1);
		final CountDownLatch latch1 = new CountDownLatch(1);
		final Promise<Number> p2 = p1.recoverWith(new Function<Promise<?>, Promise<Long>>() {
			public Promise<Long> apply(Promise<?> t) {
				latch1.countDown();
				return Promises.newResolvedPromise(value2);
			}
		});
		assertTrue(p1.isDone());
		assertTrue(p2.isDone());
		assertFalse(latch1.await(WAIT_TIME, TimeUnit.SECONDS));

		assertSame("wrong value", value1, p2.getValue());
		assertNull("wrong failure", p2.getFailure());
	}

	public void testRecoverWithFailure() throws Exception {
		final Throwable failure = new Error("fail");
		final Long value2 = new Long(43);
		final Promise<Number> p1 = Promises.newFailedPromise(failure);
		final CountDownLatch latch1 = new CountDownLatch(1);
		final Promise<Number> p2 = p1.recoverWith(new Function<Promise<?>, Promise<Long>>() {
			public Promise<Long> apply(Promise<?> t) {
				latch1.countDown();
				try {
					assertSame(failure, t.getFailure());
				} catch (InterruptedException e) {
					fail(e);
				}
				return Promises.newResolvedPromise(value2);
			}
		});
		assertTrue(p1.isDone());
		assertTrue(p2.isDone());
		assertTrue(latch1.await(WAIT_TIME, TimeUnit.SECONDS));

		assertSame("wrong value", value2, p2.getValue());
		assertNull("wrong failure", p2.getFailure());
	}

	public void testRecoverWithFailureNull() throws Exception {
		final Throwable failure = new Error("fail");
		final Promise<Number> p1 = Promises.newFailedPromise(failure);
		final CountDownLatch latch1 = new CountDownLatch(1);
		final Promise<Number> p2 = p1.recoverWith(new Function<Promise<?>, Promise<Long>>() {
			public Promise<Long> apply(Promise<?> t) {
				latch1.countDown();
				try {
					assertSame(failure, t.getFailure());
				} catch (InterruptedException e) {
					fail(e);
				}
				return null;
			}
		});
		assertTrue(p1.isDone());
		assertTrue(p2.isDone());
		assertTrue(latch1.await(WAIT_TIME, TimeUnit.SECONDS));

		assertSame("wrong failure", failure, p2.getFailure());
		try {
			p2.getValue();
			fail("p2 getValue failed to throw InvocationTargetException");
		} catch (InvocationTargetException e) {
			assertSame("wrong failure", failure, e.getCause());
		}
	}

	public void testRecoverWithFailureException() throws Exception {
		final Throwable failure1 = new Error("fail1");
		final Error failure2 = new Error("fail2");
		final Promise<Number> p1 = Promises.newFailedPromise(failure1);
		final CountDownLatch latch1 = new CountDownLatch(1);
		final Promise<Number> p2 = p1.recoverWith(new Function<Promise<?>, Promise<Long>>() {
			public Promise<Long> apply(Promise<?> t) {
				latch1.countDown();
				try {
					assertSame(failure1, t.getFailure());
				} catch (InterruptedException e) {
					fail(e);
				}
				throw failure2;
			}
		});
		assertTrue(p1.isDone());
		assertTrue(p2.isDone());
		assertTrue(latch1.await(WAIT_TIME, TimeUnit.SECONDS));

		assertSame("wrong failure", failure2, p2.getFailure());
		try {
			p2.getValue();
			fail("p2 getValue failed to throw InvocationTargetException");
		} catch (InvocationTargetException e) {
			assertSame("wrong failure", failure2, e.getCause());
		}
	}

	public void testRecoverWithNull() throws Exception {
		String value1 = new String("value");
		Promise<String> p1 = Promises.newResolvedPromise(value1);
		try {
			p1.recoverWith(null);
			fail("failed to error on null function");
		} catch (NullPointerException e) {
			// expected
		}
	}

}
