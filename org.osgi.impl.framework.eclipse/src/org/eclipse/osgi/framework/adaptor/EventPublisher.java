/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.adaptor;

import org.osgi.framework.Bundle;

/**
 * The EventPublisher is used by FrameworkAdaptors to publish events to the
 * Framework.
 */
public interface EventPublisher {

	/**
	 * Publish a FrameworkEvent.
	 *
	 * @param type FrameworkEvent type.
	 * @param bundle Bundle related to FrameworkEvent.
	 * @param throwable Related exception or <tt>null</tt>.
	 * @see org.osgi.framework.FrameworkEvent
	 */
	public abstract void publishFrameworkEvent(int type, Bundle bundle, Throwable throwable);

}