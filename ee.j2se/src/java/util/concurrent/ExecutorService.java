/*
 * Copyright (c) OSGi Alliance (2001, 2013). All Rights Reserved.
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

package java.util.concurrent;
public interface ExecutorService extends java.util.concurrent.Executor {
	boolean awaitTermination(long var0, java.util.concurrent.TimeUnit var1) throws java.lang.InterruptedException;
	<T> java.util.List<java.util.concurrent.Future<T>> invokeAll(java.util.Collection<? extends java.util.concurrent.Callable<T>> var0) throws java.lang.InterruptedException;
	<T> java.util.List<java.util.concurrent.Future<T>> invokeAll(java.util.Collection<? extends java.util.concurrent.Callable<T>> var0, long var1, java.util.concurrent.TimeUnit var2) throws java.lang.InterruptedException;
	<T> T invokeAny(java.util.Collection<? extends java.util.concurrent.Callable<T>> var0) throws java.lang.InterruptedException, java.util.concurrent.ExecutionException;
	<T> T invokeAny(java.util.Collection<? extends java.util.concurrent.Callable<T>> var0, long var1, java.util.concurrent.TimeUnit var2) throws java.lang.InterruptedException, java.util.concurrent.ExecutionException, java.util.concurrent.TimeoutException;
	boolean isShutdown();
	boolean isTerminated();
	void shutdown();
	java.util.List<java.lang.Runnable> shutdownNow();
	java.util.concurrent.Future<?> submit(java.lang.Runnable var0);
	<T> java.util.concurrent.Future<T> submit(java.lang.Runnable var0, T var1);
	<T> java.util.concurrent.Future<T> submit(java.util.concurrent.Callable<T> var0);
}

