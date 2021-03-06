/*
 * Copyright (c) OSGi Alliance (2015). All Rights Reserved.
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


package org.osgi.test.cases.component.tb24;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.test.cases.component.service.BaseService;
import org.osgi.test.cases.component.service.MultipleFieldTestService;
import org.osgi.test.cases.component.service.TestIdentitySet;
import org.osgi.test.cases.component.service.TestList;
import org.osgi.test.cases.component.service.TestSet;

public class DynamicMultipleFieldReceiver extends AbstractFieldReceiver
		implements MultipleFieldTestService<BaseService> {
	private volatile Collection<BaseService>									collectionService;
	private volatile List<BaseService>											listService;
	private final Set<BaseService>												updateService;
	private volatile Collection<ServiceReference<BaseService>>					collectionReference;
	private volatile List<ServiceReference<BaseService>>						listReference;
	private volatile Collection<ServiceReference<BaseService>>					updateReference;
	private volatile Collection<ComponentServiceObjects<BaseService>>			collectionServiceObjects;
	private volatile List<ComponentServiceObjects<BaseService>>					listServiceObjects;
	private volatile List<ComponentServiceObjects<BaseService>>					updateServiceObjects;
	private volatile Collection<Map<String, Object>>							collectionProperties;
	private volatile List<Map<String, Object>>									listProperties;
	private final Set<Map<String, Object>>										updateProperties;
	private volatile Collection<Map.Entry<Map<String, Object>, BaseService>>	collectionTuple;
	private volatile List<Map.Entry<Map<String, Object>, BaseService>>			listTuple;
	private final Set<Map.Entry<Map<String, Object>, BaseService>>				updateTuple;
	
	public DynamicMultipleFieldReceiver() {
		collectionService = new TestList<BaseService>();
		listService = new TestList<BaseService>();
		updateService = new TestSet<BaseService>();

		collectionReference = new TestList<ServiceReference<BaseService>>();
		listReference = new TestList<ServiceReference<BaseService>>();
		updateReference = null;

		collectionServiceObjects = new TestList<ComponentServiceObjects<BaseService>>();
		listServiceObjects = new TestList<ComponentServiceObjects<BaseService>>();
		updateServiceObjects = null;

		collectionProperties = new TestList<Map<String, Object>>();
		listProperties = new TestList<Map<String, Object>>();
		updateProperties = new TestIdentitySet<Map<String, Object>>();

		collectionTuple = new TestList<Map.Entry<Map<String, Object>, BaseService>>();
		listTuple = new TestList<Map.Entry<Map<String, Object>, BaseService>>();
		updateTuple = new TestIdentitySet<Map.Entry<Map<String, Object>, BaseService>>();
	}

	public Collection<BaseService> getCollectionService() {
		return collectionService;
	}

	public List<BaseService> getListService() {
		return listService;
	}

	public Collection<BaseService> getCollectionSubtypeService() {
		return updateService;
	}

	public Collection<ServiceReference<BaseService>> getCollectionReference() {
		return collectionReference;
	}

	public List<ServiceReference<BaseService>> getListReference() {
		return listReference;
	}

	public Collection<ServiceReference<BaseService>> getCollectionSubtypeReference() {
		return updateReference;
	}

	public Collection<ComponentServiceObjects<BaseService>> getCollectionServiceObjects() {
		return collectionServiceObjects;
	}

	public List<ComponentServiceObjects<BaseService>> getListServiceObjects() {
		return listServiceObjects;
	}

	public Collection<ComponentServiceObjects<BaseService>> getCollectionSubtypeServiceObjects() {
		return updateServiceObjects;
	}

	public Collection<Map<String, Object>> getCollectionProperties() {
		return collectionProperties;
	}

	public List<Map<String, Object>> getListProperties() {
		return listProperties;
	}

	public Collection<Map<String, Object>> getCollectionSubtypeProperties() {
		return updateProperties;
	}

	public Collection<Entry<Map<String, Object>, BaseService>> getCollectionTuple() {
		return collectionTuple;
	}

	public List<Entry<Map<String, Object>, BaseService>> getListTuple() {
		return listTuple;
	}

	public Collection<Entry<Map<String, Object>, BaseService>> getCollectionSubtypeTuple() {
		return updateTuple;
	}
}
