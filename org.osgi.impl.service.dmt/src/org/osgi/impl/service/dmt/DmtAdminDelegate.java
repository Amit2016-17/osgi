/*
 * ============================================================================
 * (c) Copyright 2004 Nokia
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Nokia and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */
package org.osgi.impl.service.dmt;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.osgi.service.log.LogService;

import info.dmtree.DmtAdmin;
import info.dmtree.DmtEventListener;
import info.dmtree.DmtException;
import info.dmtree.DmtSession;
import info.dmtree.security.DmtPermission;
import info.dmtree.security.DmtPrincipalPermission;

/*
 * Dmt Admin service provider class. An instance of this is returned by
 * DmtServiceFactory for each bundle using the DmtAdmin service. Calls to the
 * getSession methods are forwarded to a singleton DmtAdminCore instance. This
 * class provides support for event listener registration, and event delivery to
 * the registered listeners.
 */
public class DmtAdminDelegate implements DmtAdmin {

    private DmtAdminCore dmtAdmin;
    private Context context;
    
    private Hashtable listeners;
    private boolean active; 

    DmtAdminDelegate(DmtAdminCore dmtAdmin, Context context) {
        this.dmtAdmin = dmtAdmin;
        this.context = context;
        
        listeners = new Hashtable();
        active = true;
    }
    
    public DmtSession getSession(String subtreeUri) throws DmtException {
        checkState();
        return dmtAdmin.getSession(subtreeUri);
    }

    public DmtSession getSession(String subtreeUri, int lockMode)
            throws DmtException {
        checkState();
        return dmtAdmin.getSession(subtreeUri, lockMode);
    }

    public DmtSession getSession(String principal, String subtreeUri,
            int lockMode) throws DmtException {
        checkState();
        return dmtAdmin.getSession(principal, subtreeUri, lockMode);
    }

    // TODO add DmtException for invalid rootUri, and maybe also instead of null rootUri?
    // TODO add IllegalArgumentException for invalid bits in the type bitmask
    public void addEventListener(int type, String rootUri, 
            DmtEventListener listener) {
        checkState();
        internalAddEventListener(null, type, rootUri, listener);
    }

    // TODO add DmtException for invalid rootUri, and maybe also instead of null rootUri?
    // TODO add IllegalArgumentException for invalid bits in the type bitmask
    public void addEventListener(String principal, int type, String rootUri,
            DmtEventListener listener) {
        checkState();

        if(principal == null)
            throw new NullPointerException("Remote principal parameter is null.");

        internalAddEventListener(principal, type, rootUri, listener);
    }
    
    public void removeEventListener(DmtEventListener listener) {
        checkState();

        if(listener == null)
            throw new NullPointerException("Listener object parameter is null.");
        
        listeners.remove(listener);
    }
    
    void dispatchEvent(final DmtEventCore event) {
        if(!active)
            return;
        
        Iterator i = listeners.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            EventListenerProperties properties = (EventListenerProperties) 
                    entry.getValue();
            final DmtEventListener listener = (DmtEventListener) entry.getKey();
            final String principal = properties.principal; // can be null
            
            if((event.getType() & properties.type) != 0 &&
                    event.containsNodeUnderRoot(properties.root)) {
                try {
                    AccessController.doPrivileged(new PrivilegedAction() {
                        public Object run() {
                            listener.changeOccurred(new DmtEventImpl(event,
                                    principal));
                            return null;
                        }
                    });
                } catch(RuntimeException e) {
                    context.log(LogService.LOG_WARNING, "DmtEventListener " +
                            "instance registered with DmtAdmin threw a " +
                            "RuntimeException while in a callback.", e);
                    listeners.remove(listener);
                }
            }
        }
    }
    
    void close() {
        active = false;
    }
    
    private void internalAddEventListener(String principal, int type, 
            String rootUri, DmtEventListener listener) {
        
        if(rootUri == null)
            throw new NullPointerException("Listener root URI parameter is null.");
        
        if(listener == null)
            throw new NullPointerException("Listener object parameter is null.");

        Node root;
        try {
            root = Node.validateAndNormalizeUri(rootUri);
        } catch(DmtException e) {
            // TODO get rid of exception transformation if/when the addEventListener methods can throw the appropriate DmtExceptions
            throw new IllegalArgumentException("Invalid root URI parameter: " + e);
        }

        SecurityManager sm = System.getSecurityManager();
        if(sm != null) {
            // In the remote case, check whether the caller has the permissions
            // to act on behalf of the given principal. In the local case, check
            // whether the caller has GET DmtPermission on the root node.
            // This is mainly to ensure that completely untrusted applications
            // cannot register listeners (e.g. for DOS attacks), as the actual 
            // event contents are protected by permissions anyway.
            if(principal != null)
                sm.checkPermission(new DmtPrincipalPermission(principal));
            else
                sm.checkPermission(new DmtPermission(root.getUri(),
                        DmtPermission.GET));
        }
        
        listeners.put(listener, new EventListenerProperties(principal, type,
                root));
    }
    
    private void checkState() {
        if(!active)
            throw new IllegalStateException("The service can no longer be " +
                    "used, as it has been released by the caller.");
    }

    private static class EventListenerProperties {
        String principal;
        int type;
        Node root;
        
        private EventListenerProperties(String principal, int type, Node root) {
            this.principal = principal;
            this.type = type;
            this.root = root;
        }
    }
}
