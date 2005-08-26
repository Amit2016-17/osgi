/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2004, 2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package org.osgi.service.dmt.spi;

import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;

/**
 * An implementation of this interface takes the responsibility of handling 
 * node execute requests requests in a subtree of the DMT.
 * <p>
 * In an OSGi environment such implementations should be registered at the OSGi
 * service registry specifying the list of root node URIs in a
 * <code>String</code> array in the <code>execRootURIs</code> registration
 * parameter (as defined by the {@link #EXEC_ROOT_URIS} constant).
 */
public interface ExecPlugin {
    /**
     * The registration parameter that specifies the subtrees handled by this
     * plugin. The parameter with this name must contain the array of root URIs
     * of all subtrees in which this plugin handles the execute operations.
     */
    String EXEC_ROOT_URIS = "execRootURIs";

    /**
     * Execute the given node with the given data. This operation
     * corresponds to the EXEC command in OMA DM.
     * <p>
     * The semantics of an execute operation and the data parameter it takes
     * depends on the definition of the managed object on which the command is
     * issued. Session information is given as it is needed for sending alerts
     * back from the plugin. If a correlation ID is specified, it should be used
     * as the <code>correlator</code> parameter for alerts sent in response to
     * this execute operation.
     * <p>
     * The <code>nodePath</code> parameter contains an array of path segments 
     * identifying the node to be executed in the subtree of this plugin. This
     * is an absolute path, so the first segment is always &quot;.&quot;. 
     * Special characters appear escaped in the segments.
     * 
     * @param session a reference to the session in which the operation was
     *        issued, must not be <code>null</code>
     * @param nodePath the absolute path of the node to be executed, must not be 
     *        <code>null</code>
     * @param correlator an identifier to associate this operation with any
     *        alerts sent in response to it, can be <code>null</code>
     * @param data the parameter of the execute operation, can be
     *        <code>null</code>
     * @throws DmtException with the following possible error codes:
     *         <ul>
     *         <li><code>NODE_NOT_FOUND</code> if the node does not exist and
     *         the plugin does not allow executing unexisting nodes
     *         <li><code>METADATA_MISMATCH</code> if the command failed because
     *         of meta-data restrictions
     *         <li><code>DATA_STORE_FAILURE</code> if an error occurred while
     *         accessing the data store
     *         <li><code>COMMAND_FAILED</code> if some unspecified error is 
     *         encountered while attempting to complete the command
     *         </ul>
     * @see DmtSession#execute(String, String)
     * @see DmtSession#execute(String, String, String)
     */
    void execute(DmtSession session, String[] nodePath, String correlator,
            String data) throws DmtException;
}
