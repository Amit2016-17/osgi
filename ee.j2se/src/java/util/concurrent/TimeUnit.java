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
public enum TimeUnit {
	DAYS { void dummy() {} },
	HOURS { void dummy() {} },
	MICROSECONDS { void dummy() {} },
	MILLISECONDS { void dummy() {} },
	MINUTES { void dummy() {} },
	NANOSECONDS { void dummy() {} },
	SECONDS { void dummy() {} };
	public long convert(long var0, java.util.concurrent.TimeUnit var1) { return 0l; }
	public void sleep(long var0) throws java.lang.InterruptedException { }
	public void timedJoin(java.lang.Thread var0, long var1) throws java.lang.InterruptedException { }
	public void timedWait(java.lang.Object var0, long var1) throws java.lang.InterruptedException { }
	public long toDays(long var0) { return 0l; }
	public long toHours(long var0) { return 0l; }
	public long toMicros(long var0) { return 0l; }
	public long toMillis(long var0) { return 0l; }
	public long toMinutes(long var0) { return 0l; }
	public long toNanos(long var0) { return 0l; }
	public long toSeconds(long var0) { return 0l; }
	abstract void dummy();
}

