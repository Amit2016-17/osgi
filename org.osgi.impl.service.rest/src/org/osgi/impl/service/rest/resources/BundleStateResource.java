/*
 * Copyright (c) OSGi Alliance (2013). All Rights Reserved.
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

package org.osgi.impl.service.rest.resources;

import org.osgi.framework.Bundle;
import org.osgi.impl.service.rest.PojoReflector;
import org.osgi.impl.service.rest.pojos.BundleStatePojo;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Put;

/**
 * The bundle state resource.
 * 
 * @author Jan S. Rellermeyer, IBM Research
 */
public class BundleStateResource extends AbstractOSGiResource<BundleStatePojo> {

	public BundleStateResource() {
		super(PojoReflector.getReflector(BundleStatePojo.class));
	}

	@Get("json|text")
	public Representation doGet(final Representation value,
			final Variant variant) {
		try {
			final Bundle bundle = getBundleFromKeys("bundleId",
					"bundleSymbolicName", "bundleVersion");
			if (bundle == null) {
				setStatus(Status.CLIENT_ERROR_NOT_FOUND);
				return null;
			}
			return getRepresentation(new BundleStatePojo(bundle.getState()),
					variant);
		} catch (final Exception e) {
			return ERROR(Status.SERVER_ERROR_INTERNAL, e, variant);
		}
	}

	@Put("json|text")
	public Representation doPut(final Representation value,
			final Variant variant) {
		try {
			final Bundle bundle = getBundleFromKeys("bundleId",
					"bundleSymbolicName", "bundleVersion");
			if (bundle == null) {
				setStatus(Status.CLIENT_ERROR_NOT_FOUND);
				return null;
			}
			final BundleStatePojo targetState = fromRepresentation(value,
					variant);

			if (targetState.getState() == Bundle.ACTIVE) {
				bundle.start();
				return getRepresentation(
						new BundleStatePojo(bundle.getState()), variant);
			} else if (targetState.getState() == Bundle.RESOLVED) {
				bundle.stop();
				return getRepresentation(
						new BundleStatePojo(bundle.getState()), variant);
			} else {
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "target state "
						+ targetState.getState() + " not supported");
				return ERROR;
			}
		} catch (final Exception e) {
			return ERROR(Status.SERVER_ERROR_INTERNAL, e, variant);
		}
	}

}