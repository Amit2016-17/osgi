/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2004). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and OSGi Alliance DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.service.webapplication;

import java.util.Dictionary;

/**
 * A WebApplication service is registered by a Web Application Bundle to
 * advertise its web application to the Web Container. It does this by
 * registering an object implementing this service with the framework,
 * under the name of this interface.
 * 
 * @version $Revision$
 */
public interface WebApplication {
	/**
	 * The version of the WebApplication API used during translation
	 * time. This value should be used by WAB translation tools to set the
	 * service property WEBAPP_VERSION. The value "1.0.0" is currently the only
	 * valid value.
	 */
	public static final String	API_VERSION				= "1.0.0";													//$NON-NLS-1$
	/**
	 * The fully qualified name of this interface -
	 * "org.osgi.service.webapplication.WebApplication". This is the
	 * service name to use to register the WebApplication with the framework.
	 */
	public static final String	WEBAPP_SERVICE			= "org.osgi.service.webapplication.WebApplication";	//$NON-NLS-1$
	/**
	 * The service property (named "webapp.context") for the Web Application
	 * context root. This service property must be set when registering a
	 * WebApplication with the framework. A WebApplication service registered
	 * without this service property set must be ignored by the Web Container
	 * bundle. The value of this property must be of type String and must be a
	 * legal web application context.
	 */
	public static final String	WEBAPP_CONTEXT			= "webapp.context";										//$NON-NLS-1$
	/**
	 * The service property (named "webapp.exclude.defaults") for a Web
	 * Application that wants to exclude the default settings from the Web
	 * Container. The value of this property must be of type String. The value
	 * of the property must be equal to, ignoring case, "true" if default
	 * settings are to be excluded.
	 */
	public static final String	WEBAPP_EXCLUDE_DEFAULTS	= "webapp.exclude.defaults";								//$NON-NLS-1$
	/**
	 * The service property (named "webapp.type") for specifying the Web
	 * Application type. This property is optional and is used to tag the web
	 * application for additional processing by other cooperating services and
	 * containers. The value of this property must be of type String.
	 */
	public static final String	WEBAPP_TYPE				= "webapp.type";											//$NON-NLS-1$
	/**
	 * The service property (named "webapp.api.version") for specifying the Web
	 * Application API version used at WAB translation time. The value of this
	 * property must be of type String and is should be set to the value of
	 * API_VERSION during translation time.
	 */
	public static final String	WEBAPP_API_VERSION		= "webapp.api.version";									//$NON-NLS-1$

	/**
	 * Returns a <code>Dictionary</code> of attributes that will be available
	 * to the Web Application Bundle's servlets as ServletContext attributes.
	 * These attributes may be accessed by a servlet in the Web Application
	 * Bundle using the following call:
	 * <p>
	 * getServletContext().getAttribute(key);
	 * <p>
	 * Where <code>key</code> is a key in the Dictionary.
	 * 
	 * @return A Dictionary of attributes that is used to initialize the
	 *         attributes of the Web Application Bundle's ServletContext, null
	 *         may be returned if no attributes exist. The Dictionary returned
	 *         is used by the Web Container once during the deployment of the
	 *         Web Application to get a list of attributes to set in the Web
	 *         Application's ServletContext.
	 */
	public Dictionary getServletContextAttributes();

	/**
	 * This method must be called by the Web Container to report any exception
	 * that occurs during the deployment of this WebApplication service.
	 * 
	 * @param e The Exception that occurred during deployment.
	 */
	public void deploymentException(Exception e);
}