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

package java.lang.management;
public interface MemoryPoolMXBean extends java.lang.management.PlatformManagedObject {
	java.lang.management.MemoryUsage getCollectionUsage();
	long getCollectionUsageThreshold();
	long getCollectionUsageThresholdCount();
	java.lang.String[] getMemoryManagerNames();
	java.lang.String getName();
	java.lang.management.MemoryUsage getPeakUsage();
	java.lang.management.MemoryType getType();
	java.lang.management.MemoryUsage getUsage();
	long getUsageThreshold();
	long getUsageThresholdCount();
	boolean isCollectionUsageThresholdExceeded();
	boolean isCollectionUsageThresholdSupported();
	boolean isUsageThresholdExceeded();
	boolean isUsageThresholdSupported();
	boolean isValid();
	void resetPeakUsage();
	void setCollectionUsageThreshold(long var0);
	void setUsageThreshold(long var0);
}

