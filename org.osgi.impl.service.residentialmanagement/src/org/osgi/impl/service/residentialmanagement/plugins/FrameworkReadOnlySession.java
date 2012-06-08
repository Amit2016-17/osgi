/*
 * Copyright (c) OSGi Alliance (2000-2011).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.impl.service.residentialmanagement.plugins;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.Date;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.startlevel.FrameworkStartLevel;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.MetaNode;
import org.osgi.service.dmt.Uri;
import org.osgi.service.dmt.spi.ReadableDataSession;
import org.osgi.service.dmt.DmtConstants;

/**
 * 
 * @author Shigekuni KONDO, NTT Corporation
 */
class FrameworkReadOnlySession implements ReadableDataSession,
		SynchronousBundleListener {
	protected BundleContext context;
	protected Hashtable bundlesTable = new Hashtable();
	protected Properties properties = null;
	protected boolean managedFlag = false;
	protected int signersInstanceId = 1;
	protected int entriesInstanceId = 1;
	protected int packageWiresInstanceId = 1;
	protected int bundleWiresInstanceId = 1;
	protected int hostWiresInstanceId = 1;
	protected int serviceWiresInstanceId = 1;
	protected int rootLength = 1;

	FrameworkReadOnlySession(FrameworkPlugin plugin, BundleContext context) {
		this.context = context;
		properties = (Properties) System.getProperties().clone();
		for (int i = 0; i < RMTConstants.LAUNCHING_PROPERTIES.length; i++) {
			if (context.getProperty(RMTConstants.LAUNCHING_PROPERTIES[i]) != null)
				properties.put(RMTConstants.LAUNCHING_PROPERTIES[i],
						context.getProperty(RMTConstants.LAUNCHING_PROPERTIES[i]));
		}
		String root = System.getProperty(RMTConstants.KEY_OF_RMT_ROOT_URI);
		if (root != null) {
			String[] rootArray = RMTUtil.pathToArrayUri(root + "/");
			rootLength = rootArray.length;
		}
	}

	public void nodeChanged(String[] nodePath) throws DmtException {
		// do nothing - the version and timestamp properties are not supported
	}

	public void close() throws DmtException {
		// no cleanup needs to be done when closing read-only session
	}
	
	public String[] getChildNodeNames(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);

		if (path.length == 1) {
			String[] children = new String[4];
			children[0] = RMTConstants.FRAMEWORKSTARTLEVEL;
			children[1] = RMTConstants.INITIALBUNDLESTARTLEVEL;
			children[2] = RMTConstants.BUNDLE;
			children[3] = RMTConstants.PROPERTY;
			return children;
		}

		if (path.length == 2) {
			if (path[1].equals(RMTConstants.PROPERTY)) {
				String[] children = new String[properties.size()];
				int i = 0;
				for (Enumeration keys = properties.keys(); keys
						.hasMoreElements(); i++) {
					children[i] = (String) keys.nextElement();
				}
				return children;
			}

			if (path[1].equals(RMTConstants.BUNDLE)) {
				String[] children = new String[bundlesTable.size()];
				int i = 0;
				for (Enumeration keys = bundlesTable.keys(); keys
						.hasMoreElements(); i++) {
					children[i] = (String) keys.nextElement();
				}
				return children;
			}
		}

		if (path.length == 3 && path[1].equals(RMTConstants.BUNDLE)) {
			if (this.bundlesTable.get(path[2]) != null) {
				BundleSubTree bs = (BundleSubTree) this.bundlesTable
						.get(path[2]);
				Node node = bs.getLocatonNode();
				return node.getChildNodeNames();
			}
		}

		if (path.length == 4 && path[1].equals(RMTConstants.BUNDLE)) {
			if (path[3].equals(RMTConstants.BUNDLETYPE)) {
				if (this.bundlesTable.get(path[2]) != null) {
					BundleSubTree bs = (BundleSubTree) this.bundlesTable
							.get(path[2]);
					if (bs.getBundleType() != null) {
						String[] type = new String[1];
						type[0] = "0";
						return type;
					} else {
						return new String[0];
					}
				}
			}
			if (path[3].equals(RMTConstants.HEADERS)) {
				if (this.bundlesTable.get(path[2]) != null) {
					BundleSubTree bs = (BundleSubTree) this.bundlesTable
							.get(path[2]);
					Dictionary headers = bs.getHeaders();
					String[] children = new String[headers.size()];
					Enumeration keys = headers.keys();
					for (int i = 0; keys.hasMoreElements(); i++) {
						children[i] = (String) keys.nextElement();
					}
					return children;
				}
			}
			if (path[3].equals(RMTConstants.ENTRIES)) {
				if (this.bundlesTable.get(path[2]) != null) {
					BundleSubTree bs = (BundleSubTree) this.bundlesTable
							.get(path[2]);
					return setListNodeNameForVector(bs.getEntries());
				}
			}
			if (path[3].equals(RMTConstants.SIGNERS)) {
				if (this.bundlesTable.get(path[2]) != null) {
					BundleSubTree bs = (BundleSubTree) this.bundlesTable
							.get(path[2]);
					return setListNodeNameForVector(bs.getSigners());
				}
			}
			if (path[3].equals(RMTConstants.WIRES)) {
				if (this.bundlesTable.get(path[2]) != null) {
					BundleSubTree bs = (BundleSubTree) this.bundlesTable
							.get(path[2]);
					return setListNodeNameForMap(bs.getWires());
				}
			}
		}

		if (path.length == 5) {
			if (path[3].equals(RMTConstants.ENTRIES)) {
				if (this.bundlesTable.get(path[2]) != null) {
					BundleSubTree bs = (BundleSubTree) this.bundlesTable
							.get(path[2]);
					Vector entries = bs.getEntries();
					try {
						entries.get(Integer.parseInt(path[4]));
						String[] children = new String[3];
						children[0] = RMTConstants.PATH;
						children[1] = RMTConstants.CONTENT;
						children[2] = RMTConstants.ENTRIESINSTANCEID;
						return children;
					} catch (ArrayIndexOutOfBoundsException ae) {
						String[] children = new String[0];
						return children;
					}
				}
			}
			if (path[3].equals(RMTConstants.SIGNERS)) {
				if (this.bundlesTable.get(path[2]) != null) {
					BundleSubTree bs = (BundleSubTree) this.bundlesTable
							.get(path[2]);
					Vector signers = bs.getSigners();
					try {
						signers.get(Integer.parseInt(path[4]));
						String[] children = new String[3];
						children[0] = RMTConstants.CERTIFICATECHAIN;
						children[1] = RMTConstants.ISTRUSTED;
						children[2] = RMTConstants.SIGNERSINSTANCEID;
						return children;
					} catch (ArrayIndexOutOfBoundsException ae) {
						String[] children = new String[0];
						return children;
					}
				}
			}
			if (path[3].equals(RMTConstants.WIRES)) {
				if (this.bundlesTable.get(path[2]) != null) {
					BundleSubTree bs = (BundleSubTree) this.bundlesTable
							.get(path[2]);
					Map wires = bs.getWires();
					Vector list = (Vector) wires.get(path[4]);
					String[] children = new String[list.size()];
					for (int i = 0; i < list.size(); i++) {
						children[i] = Integer.toString(i);
					}
					return children;
				}
			}
		}

		if (path.length == 6) {
			if (path[3].equals(RMTConstants.SIGNERS)) {
				if (this.bundlesTable.get(path[2]) != null) {
					BundleSubTree bs = (BundleSubTree) this.bundlesTable
							.get(path[2]);
					Vector signers = bs.getSigners();
					try {
						SignersSubtree ss = (SignersSubtree) signers
								.get(Integer.parseInt(path[4]));
						Vector chainList = ss.getCertifitateChainList();
						String[] children = new String[chainList.size()];
						for (int i = 0; i < chainList.size(); i++) {
							children[i] = Integer.toString(i);
						}
						return children;
					} catch (ArrayIndexOutOfBoundsException ae) {
						throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
						"The specified node does not exist in the framework object.");
					} catch (NumberFormatException ne) {
						throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
						"The specified node does not exist in the framework object.");
					}
				}
			}
			if (path[3].equals(RMTConstants.WIRES)) {
				if (this.bundlesTable.get(path[2]) != null) {
					BundleSubTree bs = (BundleSubTree) this.bundlesTable
							.get(path[2]);
					if (bs.getWires() != null) {
						Vector vec = (Vector) ((Map) bs.getWires())
								.get(path[4]);
						if (vec != null) {
							try {
								vec.get(Integer.parseInt(path[5]));
								String[] children = new String[6];
								children[0] = RMTConstants.NAMESPACE;
								children[1] = RMTConstants.PROVIDER;
								children[2] = RMTConstants.REQUIRER;
								children[3] = RMTConstants.WIRESINSTANCEID;
								children[4] = RMTConstants.REQUIREMENT;
								children[5] = RMTConstants.CAPABILITY;
								return children;
							} catch (ArrayIndexOutOfBoundsException ae) {
								throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
								"The specified node does not exist in the framework object.");
							} catch (NumberFormatException ne) {
								throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
								"The specified node does not exist in the framework object.");
							}
						}
					}
				}
			}
		}

		if (path.length == 7 && path[3].equals(RMTConstants.WIRES)) {
			if (this.bundlesTable.get(path[2]) != null) {
				BundleSubTree bs = (BundleSubTree) this.bundlesTable
						.get(path[2]);
				if (bs.getWires() != null) {
					Vector vec = (Vector) ((Map) bs.getWires()).get(path[4]);
					if (vec != null) {
						try {
							vec.get(Integer.parseInt(path[5]));
							if (path[6].equals(RMTConstants.REQUIREMENT)) {
								String[] children = new String[3];
								children[0] = RMTConstants.FILTER;
								children[1] = RMTConstants.REQUIREMENTDIRECTIVE;
								children[2] = RMTConstants.REQUIREMENTATTRIBUTE;
								return children;
							}
							if (path[6].equals(RMTConstants.CAPABILITY)) {
								String[] children = new String[2];
								children[0] = RMTConstants.CAPABILITYDIRECTIVE;
								children[1] = RMTConstants.CAPABILITYATTRIBUTE;
								return children;
							}
						} catch (ArrayIndexOutOfBoundsException ae) {
							throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
							"The specified node does not exist in the framework object.");
						}
					}
				}
			}
		}

		if (path.length == 8 && path[3].equals(RMTConstants.WIRES)) {
			if (this.bundlesTable.get(path[2]) != null) {
				BundleSubTree bs = (BundleSubTree) this.bundlesTable
						.get(path[2]);
				if (bs.getWires() != null) {
					Vector vec = (Vector) ((Map) bs.getWires()).get(path[4]);
					if (vec != null) {
						try {
							WiresSubtree ws = (WiresSubtree) vec.get(Integer
									.parseInt(path[5]));
							if (path[6].equals(RMTConstants.REQUIREMENT)
									&& path[7].equals(RMTConstants.REQUIREMENTDIRECTIVE)) {
								return setListNodeNameForMap(ws.getRequirementDirective());
							}
							if (path[6].equals(RMTConstants.REQUIREMENT)
									&& path[7].equals(RMTConstants.REQUIREMENTATTRIBUTE)) {
								return setListNodeNameForMap(ws.getRequirementAttribute());
							}
							if (path[6].equals(RMTConstants.CAPABILITY)
									&& path[7].equals(RMTConstants.CAPABILITYDIRECTIVE)) {
								return setListNodeNameForMap(ws.getCapabilityDirective());
							}
							if (path[6].equals(RMTConstants.CAPABILITY)
									&& path[7].equals(RMTConstants.CAPABILITYATTRIBUTE)) {
								return setListNodeNameForMap(ws.getCapabilityAttribute());
							}
						} catch (ArrayIndexOutOfBoundsException ae) {
							throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
							"The specified node does not exist in the framework object.");
						}
					}
				}
			}
		}
		throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
		"The specified node does not exist in the framework object.");
	}

	public MetaNode getMetaNode(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);

		if (path.length == 1) // ./OSGi/Framework
			return new FrameworkMetaNode("Framework Root node.",
					MetaNode.PERMANENT, !FrameworkMetaNode.CAN_ADD,
					!FrameworkMetaNode.CAN_DELETE,
					!FrameworkMetaNode.ALLOW_ZERO,
					!FrameworkMetaNode.ALLOW_INFINITE);

		if (path.length == 2) { // ./OSGi/Framework/...
			if (path[1].equals(RMTConstants.FRAMEWORKSTARTLEVEL))
				return new FrameworkMetaNode("StartLevel of Framework.",
						MetaNode.AUTOMATIC, !FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_INTEGER, null);

			if (path[1].equals(RMTConstants.INITIALBUNDLESTARTLEVEL))
				return new FrameworkMetaNode(
						"Initial Bundle StartLevel of Framework.",
						MetaNode.AUTOMATIC, !FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_INTEGER, null);

			if (path[1].equals(RMTConstants.PROPERTY))
				return new FrameworkMetaNode("The Framework Properties.",
						MetaNode.AUTOMATIC, !FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE);

			if (path[1].equals(RMTConstants.BUNDLE))
				return new FrameworkMetaNode("Bundle subtree.",
						MetaNode.AUTOMATIC, !FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE);
		}

		if (path.length == 3) {
			if (path[1].equals(RMTConstants.PROPERTY))
				return new FrameworkMetaNode(
						"The requested start level for the framework.",
						MetaNode.DYNAMIC, !FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						FrameworkMetaNode.ALLOW_ZERO,
						FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);

			if (path[1].equals(RMTConstants.BUNDLE))
				return new FrameworkMetaNode("The Map of Location.",
						MetaNode.DYNAMIC, FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						FrameworkMetaNode.ALLOW_ZERO,
						FrameworkMetaNode.ALLOW_INFINITE);
		}

		if (path.length == 4) {
			if (path[3].equals(RMTConstants.URL))
				return new FrameworkMetaNode("", MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);

			if (path[3].equals(RMTConstants.AUTOSTART))
				return new FrameworkMetaNode("", MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_BOOLEAN, null);

			if (path[3].equals(RMTConstants.FAULTTYPE))
				return new FrameworkMetaNode("", MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_INTEGER, null);

			if (path[3].equals(RMTConstants.FAULTMESSAGE))
				return new FrameworkMetaNode("", MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);

			if (path[3].equals(RMTConstants.BUNDLEID))
				return new FrameworkMetaNode("", MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE, DmtData.FORMAT_LONG,
						null);

			if (path[3].equals(RMTConstants.SYMBOLICNAME))
				return new FrameworkMetaNode("", MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);

			if (path[3].equals(RMTConstants.VERSION))
				return new FrameworkMetaNode("", MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);

			if (path[3].equals(RMTConstants.STATE))
				return new FrameworkMetaNode("", MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);

			if (path[3].equals(RMTConstants.REQUESTEDSTATE))
				return new FrameworkMetaNode("", MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);

			if (path[3].equals(RMTConstants.BUNDLETYPE))
				return new FrameworkMetaNode("", MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE);

			if (path[3].equals(RMTConstants.HEADERS))
				return new FrameworkMetaNode("", MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE);

			if (path[3].equals(RMTConstants.LASTMODIFIED))
				return new FrameworkMetaNode("", MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_DATE_TIME, null);

			if (path[3].equals(RMTConstants.LOCATION))
				return new FrameworkMetaNode("", MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);

			if (path[3].equals(RMTConstants.BUNDLESTARTLEVEL))
				return new FrameworkMetaNode("", MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_INTEGER, null);

			if (path[3].equals(RMTConstants.BUNDLEINSTANCEID))
				return new FrameworkMetaNode("", MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_INTEGER, null);

			if (path[3].equals(RMTConstants.ENTRIES))
				return new FrameworkMetaNode("", MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE);

			if (path[3].equals(RMTConstants.SIGNERS))
				return new FrameworkMetaNode("", MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE);

			if (path[3].equals(RMTConstants.WIRES))
				return new FrameworkMetaNode("", MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE);
		}
		if (path.length == 5) {
			if (path[3].equals(RMTConstants.BUNDLETYPE))
				return new FrameworkMetaNode("", MetaNode.DYNAMIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						FrameworkMetaNode.ALLOW_ZERO,
						FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);

			if (path[3].equals(RMTConstants.HEADERS))
				return new FrameworkMetaNode("", MetaNode.DYNAMIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						FrameworkMetaNode.ALLOW_ZERO,
						FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);

			if (path[3].equals(RMTConstants.ENTRIES))
				return new FrameworkMetaNode("", MetaNode.DYNAMIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						FrameworkMetaNode.ALLOW_ZERO,
						FrameworkMetaNode.ALLOW_INFINITE);

			if (path[3].equals(RMTConstants.SIGNERS))
				return new FrameworkMetaNode("", MetaNode.DYNAMIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						FrameworkMetaNode.ALLOW_ZERO,
						FrameworkMetaNode.ALLOW_INFINITE);

			if (path[3].equals(RMTConstants.WIRES))
				return new FrameworkMetaNode("", MetaNode.DYNAMIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						FrameworkMetaNode.ALLOW_ZERO,
						FrameworkMetaNode.ALLOW_INFINITE);
		}
		if (path.length == 6) {
			if (path[5].equals(RMTConstants.PATH))
				return new FrameworkMetaNode("", MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);

			if (path[5].equals(RMTConstants.CONTENT))
				return new FrameworkMetaNode("", MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_BINARY, null);

			if (path[5].equals(RMTConstants.ENTRIESINSTANCEID))
				return new FrameworkMetaNode("", MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_INTEGER, null);

			if (path[5].equals(RMTConstants.ISTRUSTED))
				return new FrameworkMetaNode("", MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_BOOLEAN, null);

			if (path[5].equals(RMTConstants.SIGNERSINSTANCEID))
				return new FrameworkMetaNode("", MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_INTEGER, null);

			if (path[5].equals(RMTConstants.CERTIFICATECHAIN))
				return new FrameworkMetaNode("", MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE);

			if (path[3].equals(RMTConstants.WIRES))
				return new FrameworkMetaNode("", MetaNode.DYNAMIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						FrameworkMetaNode.ALLOW_ZERO,
						FrameworkMetaNode.ALLOW_INFINITE);
		}
		if (path.length == 7) {
			if (path[5].equals(RMTConstants.CERTIFICATECHAIN))
				return new FrameworkMetaNode("", MetaNode.DYNAMIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						FrameworkMetaNode.ALLOW_ZERO,
						FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);

			if (path[6].equals(RMTConstants.NAMESPACE))
				return new FrameworkMetaNode("", MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);

			if (path[6].equals(RMTConstants.REQUIRER))
				return new FrameworkMetaNode("", MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);

			if (path[6].equals(RMTConstants.PROVIDER))
				return new FrameworkMetaNode("", MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);

			if (path[6].equals(RMTConstants.WIRESINSTANCEID))
				return new FrameworkMetaNode("", MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_INTEGER, null);

			if (path[6].equals(RMTConstants.REQUIREMENT))
				return new FrameworkMetaNode("", MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE);

			if (path[6].equals(RMTConstants.CAPABILITY))
				return new FrameworkMetaNode("", MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE);
		}
		if (path.length == 8) {
			if (path[6].equals(RMTConstants.REQUIREMENT)
					&& path[7].equals(RMTConstants.REQUIREMENTDIRECTIVE))
				return new FrameworkMetaNode("", MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE);

			if (path[6].equals(RMTConstants.REQUIREMENT)
					&& path[7].equals(RMTConstants.REQUIREMENTATTRIBUTE))
				return new FrameworkMetaNode("", MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE);

			if (path[6].equals(RMTConstants.REQUIREMENT) && path[7].equals(RMTConstants.FILTER))
				return new FrameworkMetaNode("", MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);

			if (path[6].equals(RMTConstants.CAPABILITY)
					&& path[7].equals(RMTConstants.CAPABILITYDIRECTIVE))
				return new FrameworkMetaNode("", MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE);

			if (path[6].equals(RMTConstants.CAPABILITY)
					&& path[7].equals(RMTConstants.CAPABILITYATTRIBUTE))
				return new FrameworkMetaNode("", MetaNode.AUTOMATIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE);
		}
		if (path.length == 9) {
			if (path[6].equals(RMTConstants.REQUIREMENT)
					&& path[7].equals(RMTConstants.REQUIREMENTDIRECTIVE))
				return new FrameworkMetaNode("", MetaNode.DYNAMIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						FrameworkMetaNode.ALLOW_ZERO,
						FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);

			if (path[6].equals(RMTConstants.REQUIREMENT)
					&& path[7].equals(RMTConstants.REQUIREMENTATTRIBUTE))
				return new FrameworkMetaNode("", MetaNode.DYNAMIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						FrameworkMetaNode.ALLOW_ZERO,
						FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);

			if (path[6].equals(RMTConstants.CAPABILITY)
					&& path[7].equals(RMTConstants.CAPABILITYDIRECTIVE))
				return new FrameworkMetaNode("", MetaNode.DYNAMIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						FrameworkMetaNode.ALLOW_ZERO,
						FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);

			if (path[6].equals(RMTConstants.CAPABILITY)
					&& path[7].equals(RMTConstants.CAPABILITYATTRIBUTE))
				return new FrameworkMetaNode("", MetaNode.DYNAMIC,
						!FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						FrameworkMetaNode.ALLOW_ZERO,
						FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);
		}
		throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
				"No such node defined in the Framework.");
	}

	public int getNodeSize(String[] nodePath) throws DmtException {
		return getNodeValue(nodePath).getSize();
	}

	public int getNodeVersion(String[] nodePath) throws DmtException {
		throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED,
				"Version property is not supported.");
	}

	public Date getNodeTimestamp(String[] nodePath) throws DmtException {
		throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED,
				"Timestamp property is not supported.");
	}

	public String getNodeTitle(String[] nodePath) throws DmtException {
		throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED,
				"Title property is not supported.");
	}

	public String getNodeType(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);
		if (path.length == 1)
			return RMTConstants.FRAMEWORK_NODE_TYPE;
		if (path.length == 2) {
			if (path[1].equals(RMTConstants.BUNDLE))
				return DmtConstants.DDF_MAP;
			if (path[1].equals(RMTConstants.PROPERTY))
				return DmtConstants.DDF_MAP;
		}
		if (path.length == 4 && path[1].equals(RMTConstants.BUNDLE)) {
			if (path[3].equals(RMTConstants.BUNDLETYPE))
				return DmtConstants.DDF_LIST;
			if (path[3].equals(RMTConstants.HEADERS))
				return DmtConstants.DDF_MAP;
			if (path[3].equals(RMTConstants.WIRES))
				return DmtConstants.DDF_MAP;
			if (path[3].equals(RMTConstants.SIGNERS))
				return DmtConstants.DDF_LIST;
			if (path[3].equals(RMTConstants.ENTRIES))
				return DmtConstants.DDF_LIST;
		}
		if (path.length == 6) {
			if (path[3].equals(RMTConstants.WIRES))
				return DmtConstants.DDF_LIST;
			if (path[5].equals(RMTConstants.CERTIFICATECHAIN))
				return DmtConstants.DDF_LIST;
		}
		if (path.length == 8) {
			if (path[6].equals(RMTConstants.REQUIREMENT)
					&& path[7].equals(RMTConstants.REQUIREMENTDIRECTIVE))
				return DmtConstants.DDF_MAP;
			if (path[6].equals(RMTConstants.REQUIREMENT)
					&& path[7].equals(RMTConstants.REQUIREMENTATTRIBUTE))
				return DmtConstants.DDF_MAP;
			if (path[6].equals(RMTConstants.CAPABILITY)
					&& path[7].equals(RMTConstants.CAPABILITYDIRECTIVE))
				return DmtConstants.DDF_MAP;
			if (path[6].equals(RMTConstants.CAPABILITY)
					&& path[7].equals(RMTConstants.CAPABILITYATTRIBUTE))
				return DmtConstants.DDF_MAP;
		}
		if (isLeafNode(nodePath))
			return FrameworkMetaNode.LEAF_MIME_TYPE;

		throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
				"The specified node does not exist in the framework object.");
	}

	public boolean isNodeUri(String[] nodePath) {
		String[] path = shapedPath(nodePath);

		if (path.length == 1)
			return true;

		if (path.length == 2) {
			if (path[1].equals(RMTConstants.FRAMEWORKSTARTLEVEL)
					|| path[1].equals(RMTConstants.INITIALBUNDLESTARTLEVEL)
					|| path[1].equals(RMTConstants.PROPERTY) || path[1].equals(RMTConstants.BUNDLE))
				return true;
		}

		if (path.length == 3) {
			if (path[1].equals(RMTConstants.PROPERTY)) {
				if (properties.get(path[2]) != null)
					return true;
			}
			if (path[1].equals(RMTConstants.BUNDLE)) {
				if (bundlesTable.get(path[2]) != null)
					return true;
			}
		}

		if (path.length == 4 && path[1].equals(RMTConstants.BUNDLE)) {
			if (this.bundlesTable.get(path[2]) != null) {
				BundleSubTree bs = (BundleSubTree) this.bundlesTable
						.get(path[2]);
				Node node = bs.getLocatonNode();
				if (node.findNode(new String[] { path[3] }) != null)
					return true;
			}
		}

		if (path.length == 5 && path[1].equals(RMTConstants.BUNDLE)) {
			if (this.bundlesTable.get(path[2]) != null) {
				BundleSubTree bs = (BundleSubTree) this.bundlesTable
						.get(path[2]);
				if (path[3].equals(RMTConstants.BUNDLETYPE)) {
					if (bs.getBundleType() != null)
						return true;
				}
				if (path[3].equals(RMTConstants.HEADERS)) {
					Dictionary headers = bs.getHeaders();
					if (headers.get(path[4]) != null)
						return true;
				}
				if (path[3].equals(RMTConstants.ENTRIES)) {
					Vector entries = bs.getEntries();
					try {
						entries.get(Integer.parseInt(path[4]));
						return true;
					} catch (ArrayIndexOutOfBoundsException ae) {
						return false;
					}
				}
				if (path[3].equals(RMTConstants.SIGNERS)) {
					Vector signers = bs.getSigners();
					try {
						signers.get(Integer.parseInt(path[4]));
						return true;
					} catch (ArrayIndexOutOfBoundsException ae) {
						return false;
					}
				}
				if (path[3].equals(RMTConstants.WIRES)) {
					Map wires = bs.getWires();
					if (wires.get(path[4]) != null)
						return true;
				}
			}
		}

		if (path.length == 6 && path[1].equals(RMTConstants.BUNDLE)) {
			if (this.bundlesTable.get(path[2]) != null) {
				BundleSubTree bs = (BundleSubTree) this.bundlesTable
						.get(path[2]);
				if (path[3].equals(RMTConstants.ENTRIES)) {
					Vector entries = bs.getEntries();
					try {
						entries.get(Integer.parseInt(path[4]));
						if (path[5].equals(RMTConstants.PATH) || path[5].equals(RMTConstants.CONTENT)
								|| path[5].equals(RMTConstants.ENTRIESINSTANCEID))
							return true;
					} catch (ArrayIndexOutOfBoundsException ae) {
						return false;
					}
				}
				if (path[3].equals(RMTConstants.SIGNERS)) {
					Vector signers = bs.getSigners();
					try {
						signers.get(Integer.parseInt(path[4]));
						if (path[5].equals(RMTConstants.ISTRUSTED)
								|| path[5].equals(RMTConstants.SIGNERSINSTANCEID)
								|| path[5].equals(RMTConstants.CERTIFICATECHAIN))
							return true;
					} catch (ArrayIndexOutOfBoundsException ae) {
						return false;
					}
				}
				if (path[3].equals(RMTConstants.WIRES)) {
					Map wires = bs.getWires();
					Vector vec = (Vector) wires.get(path[4]);
					if (vec != null) {
						try {
							vec.get(Integer.parseInt(path[5]));
						} catch (ArrayIndexOutOfBoundsException ae) {
							return false;
						}
						return true;
					}
				}
			}
		}

		if (path.length == 7 && path[1].equals(RMTConstants.BUNDLE)) {
			if (this.bundlesTable.get(path[2]) != null) {
				BundleSubTree bs = (BundleSubTree) this.bundlesTable
						.get(path[2]);
				if (path[3].equals(RMTConstants.SIGNERS)) {
					Vector signers = bs.getSigners();
					try {
						SignersSubtree ss = (SignersSubtree) signers
								.get(Integer.parseInt(path[4]));
						Vector list = ss.getCertifitateChainList();
						list.get(Integer.parseInt(path[6]));
						return true;
					} catch (ArrayIndexOutOfBoundsException ae) {
						return false;
					}
				}
				if (path[3].equals(RMTConstants.WIRES)) {
					Map wires = bs.getWires();
					Vector vec = (Vector) wires.get(path[4]);
					if (vec != null) {
						try {
							vec.get(Integer.parseInt(path[5]));
						} catch (ArrayIndexOutOfBoundsException ae) {
							return false;
						}
						if (path[6].equals(RMTConstants.NAMESPACE)
								|| path[6].equals(RMTConstants.REQUIREMENT)
								|| path[6].equals(RMTConstants.PROVIDER)
								|| path[6].equals(RMTConstants.REQUIRER)
								|| path[6].equals(RMTConstants.WIRESINSTANCEID)
								|| path[6].equals(RMTConstants.CAPABILITY))
							return true;
					}
				}
			}
		}

		if (path.length == 8 && path[1].equals(RMTConstants.BUNDLE)) {
			BundleSubTree bs = (BundleSubTree) this.bundlesTable.get(path[2]);
			if (path[3].equals(RMTConstants.WIRES)) {
				Map wires = bs.getWires();
				Vector vec = (Vector) wires.get(path[4]);
				if (vec != null) {
					try {
						vec.get(Integer.parseInt(path[5]));
					} catch (ArrayIndexOutOfBoundsException ae) {
						return false;
					}
					if (path[7].equals(RMTConstants.FILTER)
							|| path[7].equals(RMTConstants.REQUIREMENTDIRECTIVE)
							|| path[7].equals(RMTConstants.REQUIREMENTATTRIBUTE)
							|| path[7].equals(RMTConstants.CAPABILITYDIRECTIVE)
							|| path[7].equals(RMTConstants.CAPABILITYDIRECTIVE))
						return true;
				}
			}
		}

		if (path.length == 9 && path[1].equals(RMTConstants.BUNDLE)) {
			BundleSubTree bs = (BundleSubTree) this.bundlesTable.get(path[2]);
			if (path[3].equals(RMTConstants.WIRES)) {
				Map wires = bs.getWires();
				Vector vec = (Vector) wires.get(path[4]);
				WiresSubtree ws;
				if (vec != null) {
					try {
						ws = (WiresSubtree) vec.get(Integer.parseInt(path[5]));
					} catch (ArrayIndexOutOfBoundsException ae) {
						return false;
					}
					if (path[6].equals(RMTConstants.REQUIREMENT)
							&& path[7].equals(RMTConstants.REQUIREMENTDIRECTIVE)) {
						Map rd = ws.getRequirementDirective();
						return !rd.isEmpty();
					}
					if (path[6].equals(RMTConstants.REQUIREMENT)
							&& path[7].equals(RMTConstants.REQUIREMENTATTRIBUTE)) {
						Map ra = ws.getRequirementAttribute();
						return !ra.isEmpty();
					}
					if (path[6].equals(RMTConstants.CAPABILITY)
							&& path[7].equals(RMTConstants.CAPABILITYDIRECTIVE)) {
						Map cd = ws.getCapabilityDirective();
						return !cd.isEmpty();
					}
					if (path[6].equals(RMTConstants.CAPABILITY)
							&& path[7].equals(RMTConstants.CAPABILITYATTRIBUTE)) {
						Map ca = ws.getCapabilityAttribute();
						return !ca.isEmpty();
					}
				}
			}
		}

		return false;
	}

	public boolean isLeafNode(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);

		if (path.length == 1)
			return false;

		if (path.length == 2) {
			if (path[1].equals(RMTConstants.FRAMEWORKSTARTLEVEL)
					|| path[1].equals(RMTConstants.INITIALBUNDLESTARTLEVEL))
				return true;
		}

		if (path.length == 3) {
			if (path[1].equals(RMTConstants.PROPERTY))
				return true;
		}

		if (path.length == 4) {
			if (path[3].equals(RMTConstants.URL) || path[3].equals(RMTConstants.AUTOSTART)
					|| path[3].equals(RMTConstants.FAULTTYPE)
					|| path[3].equals(RMTConstants.FAULTMESSAGE) || path[3].equals(RMTConstants.BUNDLEID)
					|| path[3].equals(RMTConstants.SYMBOLICNAME) || path[3].equals(RMTConstants.VERSION)
					|| path[3].equals(RMTConstants.LOCATION) || path[3].equals(RMTConstants.STATE)
					|| path[3].equals(RMTConstants.REQUESTEDSTATE)
					|| path[3].equals(RMTConstants.LASTMODIFIED)
					|| path[3].equals(RMTConstants.BUNDLESTARTLEVEL)
					|| path[3].equals(RMTConstants.BUNDLEINSTANCEID))
				return true;
		}

		if (path.length == 5) {
			if (path[3].equals(RMTConstants.BUNDLETYPE) || path[3].equals(RMTConstants.HEADERS))
				return true;
		}

		if (path.length == 6) {
			if (path[5].equals(RMTConstants.ISTRUSTED) || path[5].equals(RMTConstants.SIGNERSINSTANCEID)
					|| path[5].equals(RMTConstants.PATH) || path[5].equals(RMTConstants.CONTENT)
					|| path[5].equals(RMTConstants.ENTRIESINSTANCEID))
				return true;
		}

		if (path.length == 7) {
			if (path[6].equals(RMTConstants.NAMESPACE) || path[6].equals(RMTConstants.PROVIDER)
					|| path[6].equals(RMTConstants.REQUIRER)
					|| path[6].equals(RMTConstants.WIRESINSTANCEID)
					|| path[5].equals(RMTConstants.CERTIFICATECHAIN))
				return true;
		}

		if (path.length == 8) {
			if (path[7].equals(RMTConstants.FILTER))
				return true;
		}
		if (path.length == 9) {
			if (path[7].equals(RMTConstants.REQUIREMENTDIRECTIVE)
					|| path[7].equals(RMTConstants.REQUIREMENTATTRIBUTE)
					|| path[7].equals(RMTConstants.CAPABILITYDIRECTIVE)
					|| path[7].equals(RMTConstants.CAPABILITYATTRIBUTE))
				return true;
		}
		return false;
	}

	public DmtData getNodeValue(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);
		if (path.length == 1)
			throw new DmtException(nodePath,
					DmtException.FEATURE_NOT_SUPPORTED,
					"The given path indicates an interior node.");

		if (path.length == 2) {
			Bundle sysBundle = context.getBundle(0);
			FrameworkStartLevel fs = (FrameworkStartLevel) sysBundle
					.adapt(FrameworkStartLevel.class);
			if (path[1].equals(RMTConstants.FRAMEWORKSTARTLEVEL)) {
				int st = fs.getStartLevel();
				return new DmtData(st);
			}
			if (path[1].equals(RMTConstants.INITIALBUNDLESTARTLEVEL)) {
				int ist = fs.getInitialBundleStartLevel();
				return new DmtData(ist);
			}
		}

		if (path.length == 3) {
			if (path[1].equals(RMTConstants.PROPERTY)) {
				String value = (String) properties.get(path[2]);
				if (value != null)
					return new DmtData(value);
			}
		}

		if (path.length == 4) {
			BundleSubTree bs = (BundleSubTree) this.bundlesTable.get(path[2]);
			if (bs != null
					&& bs.getLocatonNode().findNode(new String[] { path[3] }) != null) {
				if (path[3].equals(RMTConstants.URL))
					return new DmtData(bs.getURL());
				if (path[3].equals(RMTConstants.AUTOSTART))
					return new DmtData(bs.getAutoStart());
				if (path[3].equals(RMTConstants.FAULTTYPE))
					return new DmtData(bs.getFaultType());
				if (path[3].equals(RMTConstants.FAULTMESSAGE))
					return new DmtData(bs.getFaultMassage());
				if (path[3].equals(RMTConstants.BUNDLEID))
					return new DmtData(bs.getBundleId());
				if (path[3].equals(RMTConstants.SYMBOLICNAME))
					return new DmtData(bs.getSymbolicNmae());
				if (path[3].equals(RMTConstants.VERSION))
					return new DmtData(bs.getVersion());
				if (path[3].equals(RMTConstants.LOCATION))
					return new DmtData(bs.getLocation());
				if (path[3].equals(RMTConstants.STATE))
					return new DmtData(bs.getState());
				if (path[3].equals(RMTConstants.REQUESTEDSTATE))
					return new DmtData(bs.getRequestedState());
				if (path[3].equals(RMTConstants.LASTMODIFIED))
					return new DmtData(bs.getLastModified());
				if (path[3].equals(RMTConstants.BUNDLESTARTLEVEL))
					return new DmtData(bs.getStartLevel());
				if (path[3].equals(RMTConstants.BUNDLEINSTANCEID))
					return new DmtData(bs.getInstanceId());
			}
		}

		if (path.length == 5) {
			BundleSubTree bs = (BundleSubTree) this.bundlesTable.get(path[2]);
			if (bs != null
					&& bs.getLocatonNode().findNode(new String[] { path[3] }) != null) {
				if (path[3].equals(RMTConstants.BUNDLETYPE) && path[4].equals("0")) {
					return new DmtData(bs.getBundleType());
				}
				if (path[3].equals(RMTConstants.HEADERS)) {
					Dictionary dic = bs.getHeaders();
					String value = (String) dic.get(path[4]);
					if (value != null)
						return new DmtData(value);
				}
			}
		}

		if (path.length == 6) {
			BundleSubTree bs = (BundleSubTree) this.bundlesTable.get(path[2]);
			if (bs != null
					&& bs.getLocatonNode().findNode(new String[] { path[3] }) != null) {
				if (path[3].equals(RMTConstants.ENTRIES)) {
					Vector vec = (Vector) bs.getEntries();
					EntrySubtree es = (EntrySubtree) vec.get(Integer
							.parseInt(path[4]));
					if (path[5].equals(RMTConstants.PATH))
						return new DmtData(es.getPath());
					if (path[5].equals(RMTConstants.CONTENT))
						return new DmtData(es.getContent());
					if (path[5].equals(RMTConstants.ENTRIESINSTANCEID))
						return new DmtData(es.getInstanceId());
				}
				if (path[3].equals(RMTConstants.SIGNERS)) {
					Vector vec = (Vector) bs.getSigners();
					SignersSubtree ss = (SignersSubtree) vec.get(Integer
							.parseInt(path[4]));
					if (path[5].equals(RMTConstants.ISTRUSTED))
						return new DmtData(ss.isTrusted());
					if (path[5].equals(RMTConstants.SIGNERSINSTANCEID))
						return new DmtData(ss.getInstanceId());
				}
			}
		}

		if (path.length == 7) {
			BundleSubTree bs = (BundleSubTree) this.bundlesTable.get(path[2]);
			if (bs != null
					&& bs.getLocatonNode().findNode(new String[] { path[3] }) != null) {
				if (path[3].equals(RMTConstants.SIGNERS)) {
					Vector vec = (Vector) bs.getSigners();
					SignersSubtree ss = (SignersSubtree) vec.get(Integer
							.parseInt(path[4]));
					if (path[5].equals(RMTConstants.CERTIFICATECHAIN)) {
						Vector cvec = (Vector) ss.getCertifitateChainList();
						String name = (String) cvec.get(Integer
								.parseInt(path[6]));
						return new DmtData(name);
					}
				}
				if (path[3].equals(RMTConstants.WIRES)) {
					Map wires = bs.getWires();
					Vector vec = (Vector) wires.get(path[4]);
					WiresSubtree ws;
					if (vec != null) {
						try {
							ws = (WiresSubtree) vec.get(Integer
									.parseInt(path[5]));
						} catch (ArrayIndexOutOfBoundsException ae) {
							throw new DmtException(nodePath,
									DmtException.NODE_NOT_FOUND,
									"The specified leaf node does not exist in the framework object.");
						}
						if (path[6].equals(RMTConstants.NAMESPACE))
							return new DmtData(ws.getNameSpace());
						if (path[6].equals(RMTConstants.PROVIDER))
							return new DmtData(ws.getProvider());
						if (path[6].equals(RMTConstants.REQUIRER))
							return new DmtData(ws.getRequirer());
						if (path[6].equals(RMTConstants.WIRESINSTANCEID))
							return new DmtData(ws.getInstanceId());
					}
				}
			}
		}

		if (path.length == 8) {
			BundleSubTree bs = (BundleSubTree) this.bundlesTable.get(path[2]);
			if (bs != null
					&& bs.getLocatonNode().findNode(new String[] { path[3] }) != null) {
				if (path[3].equals(RMTConstants.WIRES)) {
					Map wires = bs.getWires();
					Vector vec = (Vector) wires.get(path[4]);
					WiresSubtree ws;
					if (vec != null) {
						try {
							ws = (WiresSubtree) vec.get(Integer
									.parseInt(path[5]));
						} catch (ArrayIndexOutOfBoundsException ae) {
							throw new DmtException(nodePath,
									DmtException.NODE_NOT_FOUND,
									"The specified leaf node does not exist in the framework object.");
						}
						if (path[6].equals(RMTConstants.REQUIREMENT)
								&& path[7].equals(RMTConstants.FILTER))
							return new DmtData(ws.getFilter());
					}
				}
			}
		}

		if (path.length == 9) {
			BundleSubTree bs = (BundleSubTree) this.bundlesTable.get(path[2]);
			if (bs != null
					&& bs.getLocatonNode().findNode(new String[] { path[3] }) != null) {
				if (path[3].equals(RMTConstants.WIRES)) {
					Map wires = bs.getWires();
					Vector vec = (Vector) wires.get(path[4]);
					WiresSubtree ws;
					if (vec != null) {
						try {
							ws = (WiresSubtree) vec.get(Integer
									.parseInt(path[5]));
						} catch (ArrayIndexOutOfBoundsException ae) {
							throw new DmtException(nodePath,
									DmtException.NODE_NOT_FOUND,
									"The specified leaf node does not exist in the framework object.");
						}

						if (path[6].equals(RMTConstants.REQUIREMENT)) {
							if (path[7].equals(RMTConstants.REQUIREMENTDIRECTIVE)) {
								Map rd = ws.getRequirementDirective();
								if (!rd.isEmpty())
									return new DmtData(rd.get(path[8])
											.toString());
							}
							if (path[7].equals(RMTConstants.REQUIREMENTATTRIBUTE)) {
								Map ra = ws.getRequirementAttribute();
								if (!ra.isEmpty())
									return new DmtData(ra.get(path[8])
											.toString());
							}
						}
						if (path[6].equals(RMTConstants.CAPABILITY)) {
							if (path[7].equals(RMTConstants.CAPABILITYDIRECTIVE)) {
								Map cd = ws.getCapabilityDirective();
								if (!cd.isEmpty())
									return new DmtData(cd.get(path[8])
											.toString());
							}
							if (path[7].equals(RMTConstants.CAPABILITYATTRIBUTE)) {
								Map ca = ws.getCapabilityAttribute();
								if (!ca.isEmpty()) {
									Object obj = ca.get(path[8]);
									if (obj instanceof Collection) {
										ArrayList list = new ArrayList(
												(Collection) obj);
										return new DmtData(list.toString());
									} else if (obj instanceof Object[]) {
										ArrayList list = new ArrayList(
												Arrays.asList((Object[]) obj));
										return new DmtData(list.toString());
									}
								}
								return new DmtData(ca.get(path[8]).toString());
							}
						}
					}
				}
			}
		}

		throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
				"The specified leaf node does not exist in the framework object.");
	}

	protected String[] shapedPath(String[] nodePath) {
		int size = nodePath.length;
		int srcPos = rootLength;
		int destPos = 0;
		int length = size - srcPos;
		String[] newPath = new String[length];
		System.arraycopy(nodePath, srcPos, newPath, destPos, length);
		return newPath;
	}
	
	private String[] setListNodeNameForMap(Map map){
		String[] children = new String[map.size()];
		Iterator it = map.keySet().iterator();
		for (int i = 0; it.hasNext(); i++) {
			children[i] = (String) it.next();
		}
		return children;
	}
	
	private String[] setListNodeNameForVector(Vector vec){
		String[] children = new String[vec.size()];
		for (int i = 0; i < vec.size(); i++) {
			children[i] = Integer.toString(i);
		}
		return children;
	}

	public void bundleChanged(BundleEvent event) {
		synchronized (this.bundlesTable){
			if (!this.managedFlag) {
				Bundle[] bundles = context.getBundles();
				for (int i = 0; i < bundles.length; i++) {
					String location = Uri.encode(bundles[i].getLocation());
					BundleSubTree bs = new BundleSubTree(bundles[i]);
					this.bundlesTable.put(location, bs);
				}
				this.managedFlag = true;
			}

			Bundle bundle = event.getBundle();
			if (event.getType() == BundleEvent.INSTALLED) {
				if (this.bundlesTable.get(Uri.encode(bundle.getLocation())) == null) {
					String location = Uri.encode(bundle.getLocation());
					BundleSubTree bs = new BundleSubTree(bundle);
					this.bundlesTable.put(location, bs);
					return;
				}
				BundleSubTree bs = (BundleSubTree) this.bundlesTable.get(bundle
						.getLocation());
				if (!bs.getCreateFlag()) {
					bs.createNodes(bundle);
				}
			} else if (event.getType() == BundleEvent.RESOLVED
					|| event.getType() == BundleEvent.STARTED) {
				String location = Uri.encode(bundle.getLocation());
				BundleSubTree bs = (BundleSubTree) this.bundlesTable.get(location);
				bs.createWires();
			} else if (event.getType() == BundleEvent.UNINSTALLED) {
				String location = Uri.encode(bundle.getLocation());
				this.bundlesTable.remove(location);
			}
		}
	}

	public void managedWires() {
		if (!this.bundlesTable.isEmpty()) {
			for (Enumeration keys = this.bundlesTable.keys(); keys
					.hasMoreElements();) {
				String location = (String) keys.nextElement();
				BundleSubTree bs = (BundleSubTree) this.bundlesTable
						.get(location);
				bs.createWires();
			}
		}
	}

	protected Map managedWires(Bundle bundle) {
		packageWiresInstanceId = 1;
		hostWiresInstanceId = 1;
		bundleWiresInstanceId = 1;
		serviceWiresInstanceId = 1;

		Map wires = new HashMap();

		Vector packageList = new Vector();
		Vector hostList = new Vector();
		Vector bundleList = new Vector();
		Vector serviceList = new Vector();

		BundleWiring wiring = (BundleWiring) bundle.adapt(BundleWiring.class);

		List packageRequiredWireList = wiring
				.getRequiredWires(BundleRevision.PACKAGE_NAMESPACE);
		packageList.addAll(createWiresSubtree(packageRequiredWireList,
				BundleRevision.PACKAGE_NAMESPACE));
		List packageProvidedWireList = wiring
				.getProvidedWires(BundleRevision.PACKAGE_NAMESPACE);
		packageList.addAll(createWiresSubtree(packageProvidedWireList,
				BundleRevision.PACKAGE_NAMESPACE));
		wires.put(BundleRevision.PACKAGE_NAMESPACE, packageList);

		List hostRequiredWireList = wiring
				.getRequiredWires(BundleRevision.HOST_NAMESPACE);
		hostList.addAll(createWiresSubtree(hostRequiredWireList,
				BundleRevision.HOST_NAMESPACE));
		List hostProvidedWireList = wiring
				.getProvidedWires(BundleRevision.HOST_NAMESPACE);
		hostList.addAll(createWiresSubtree(hostProvidedWireList,
				BundleRevision.HOST_NAMESPACE));
		wires.put(BundleRevision.HOST_NAMESPACE, hostList);

		List bundleRequiredWireList = wiring
				.getRequiredWires(BundleRevision.BUNDLE_NAMESPACE);
		bundleList.addAll(createWiresSubtree(bundleRequiredWireList,
				BundleRevision.BUNDLE_NAMESPACE));
		List bundleProvidedWireList = wiring
				.getProvidedWires(BundleRevision.BUNDLE_NAMESPACE);
		bundleList.addAll(createWiresSubtree(bundleProvidedWireList,
				BundleRevision.BUNDLE_NAMESPACE));
		wires.put(BundleRevision.BUNDLE_NAMESPACE, bundleList);

		serviceList = createServiceWiresSubtree(bundle);
		wires.put(RMTConstants.SERVICE_NAMESPACE, serviceList);

		return wires;
	}

	private Vector createServiceWiresSubtree(Bundle bundle) {
		int id = serviceWiresInstanceId;
		Vector list = new Vector();
		try {
			ServiceReference[] references = context.getAllServiceReferences(
					null, null);
			for (int i = 0; i < references.length; i++) {
				String registerBundleLocation = references[i].getBundle()
						.getLocation();
				String thisBundleLocation = bundle.getLocation();
				Map directive = new HashMap();
				Map capabilityAttribute = new HashMap();
				Map requirementAttribute = new HashMap();
				capabilityAttribute.put(RMTConstants.SERVICE_NAMESPACE, references[i]
						.getProperty(Constants.SERVICE_ID).toString());
				String[] keys = references[i].getPropertyKeys();
				for (int j = 0; j < keys.length; j++) {
					capabilityAttribute.put(keys[j],
							references[i].getProperty(keys[j]).toString());
				}

				Bundle[] usingBundle = references[i].getUsingBundles();
				String serviceId = references[i].getProperty(
						Constants.SERVICE_ID).toString();
				String filter = "(service.id=" + serviceId + ")";
				requirementAttribute.put("Filter", filter);
				if (registerBundleLocation.equals(thisBundleLocation)
						&& usingBundle != null) {
					for (int k = 0; k < usingBundle.length; k++) {
						WiresSubtree ws = new WiresSubtree(RMTConstants.SERVICE_NAMESPACE,
								usingBundle[k].getLocation(),
								thisBundleLocation, id++, directive,
								requirementAttribute, directive,
								capabilityAttribute, filter);
						list.add(ws);
					}
				} else {
					if (usingBundle != null) {
						for (int k = 0; k < usingBundle.length; k++) {
							String usingBundleLocation = usingBundle[k]
									.getLocation();
							if (usingBundleLocation.equals(thisBundleLocation)) {
								WiresSubtree ws = new WiresSubtree(
										RMTConstants.SERVICE_NAMESPACE, thisBundleLocation,
										registerBundleLocation, id++,
										directive, requirementAttribute,
										directive, capabilityAttribute, filter);
								list.add(ws);
							}
						}
					}
				}
			}
		} catch (InvalidSyntaxException e) {
		}
		return list;
	}

	private Vector createWiresSubtree(List list, String nameSpace) {

		int id = 0;
		Vector vec = new Vector();

		if (nameSpace.equals(BundleRevision.PACKAGE_NAMESPACE)) {
			id = packageWiresInstanceId;
		}
		if (nameSpace.equals(BundleRevision.HOST_NAMESPACE)) {
			id = hostWiresInstanceId;
		}
		if (nameSpace.equals(BundleRevision.BUNDLE_NAMESPACE)) {
			id = bundleWiresInstanceId;
		}
		Iterator it = list.iterator();
		for (int i = 0; it.hasNext(); i++) {
			BundleWire wire = (BundleWire) it.next();
			BundleCapability capability = wire.getCapability();
			BundleRequirement requirement = wire.getRequirement();
			String providerLocation = wire.getProviderWiring().getBundle()
					.getLocation();
			String requirerLocation = wire.getRequirerWiring().getBundle()
					.getLocation();
			WiresSubtree ws = new WiresSubtree(nameSpace, requirerLocation,
					providerLocation, id++, capability, requirement);
			vec.add(ws);
		}

		return vec;
	}

	protected class WiresSubtree {
		String nameSpace = null;
		String requirer = null;
		String provider = null;
		int instanceId = 0;
		Map requirementDirectives = null;
		Map capabilityDirectives = null;
		Map requirementAttributes = null;
		Map capabilityAttributes = null;
		String filter = "";

		WiresSubtree(String nameSpace, String require, String provider, int id,
				BundleCapability capability, BundleRequirement requirement) {
			this.nameSpace = nameSpace;
			this.requirer = require;
			this.provider = provider;
			this.instanceId = id;
			this.requirementDirectives = requirement.getDirectives();
			this.capabilityDirectives = capability.getDirectives();
			this.requirementAttributes = requirement.getAttributes();
			this.capabilityAttributes = capability.getAttributes();
		}

		WiresSubtree(String nameSpace, String require, String provider, int id,
				Map requirementDirectives, Map requirementAttributes,
				Map capabilityDirectives, Map capabilityAttributes,
				String filter) {
			this.nameSpace = nameSpace;
			this.requirer = require;
			this.provider = provider;
			this.instanceId = id;
			this.requirementDirectives = requirementDirectives;
			this.capabilityDirectives = capabilityDirectives;
			this.requirementAttributes = requirementAttributes;
			this.capabilityAttributes = capabilityAttributes;
			this.filter = filter;
		}

		protected String getNameSpace() {
			return this.nameSpace;
		}

		protected String getProvider() {
			return this.provider;
		}

		protected String getRequirer() {
			return this.requirer;
		}

		protected int getInstanceId() {
			return this.instanceId;
		}

		protected String getFilter() {
			return this.filter;
		}

		protected Map getRequirementDirective() {
			return this.requirementDirectives;
		}

		protected Map getRequirementAttribute() {
			return this.requirementAttributes;
		}

		protected Map getCapabilityDirective() {
			return this.capabilityDirectives;
		}

		protected Map getCapabilityAttribute() {
			return this.capabilityAttributes;
		}
	}

	protected class BundleSubTree {
		Bundle bundle = null;
		String location = null;
		String url = "";
		boolean autoStart = false;
		int type = -1;
		String message = "";
		String requestedState = "";
		Vector entries = null;
		Vector signers = null;
		Map wires = null;
		Node locationNode = null;
		int bundleStartLevelTmp = 0;
		boolean createFlag = false;

		BundleSubTree(String bundleLocation) {
			this.location = Uri.encode(bundleLocation);

			locationNode = new Node(this.location, null, true);
			locationNode.addNode(new Node(RMTConstants.URL, null, false));
			locationNode.addNode(new Node(RMTConstants.AUTOSTART, null, false));
			locationNode.addNode(new Node(RMTConstants.LOCATION, null, false));
			locationNode.addNode(new Node(RMTConstants.REQUESTEDSTATE, null, false));
			locationNode.addNode(new Node(RMTConstants.BUNDLESTARTLEVEL, null, false));
			locationNode.addNode(new Node(RMTConstants.BUNDLEINSTANCEID, null, false));
		}

		BundleSubTree(Bundle bundleObj) {
			this.bundle = bundleObj;
			this.location = Uri.encode(bundle.getLocation());
			this.createFlag = true;

			locationNode = new Node(this.location, null, true);
			locationNode.addNode(new Node(RMTConstants.URL, null, false));
			locationNode.addNode(new Node(RMTConstants.AUTOSTART, null, false));
			locationNode.addNode(new Node(RMTConstants.LOCATION, null, false));
			locationNode.addNode(new Node(RMTConstants.REQUESTEDSTATE, null, false));
			locationNode.addNode(new Node(RMTConstants.BUNDLESTARTLEVEL, null, false));
			locationNode.addNode(new Node(RMTConstants.BUNDLEINSTANCEID, null, false));

			locationNode.addNode(new Node(RMTConstants.BUNDLEID, null, false));
			locationNode.addNode(new Node(RMTConstants.SYMBOLICNAME, null, false));
			locationNode.addNode(new Node(RMTConstants.VERSION, null, false));
			locationNode.addNode(new Node(RMTConstants.STATE, null, false));
			locationNode.addNode(new Node(RMTConstants.LASTMODIFIED, null, false));
			locationNode.addNode(new Node(RMTConstants.FAULTTYPE, null, false));
			locationNode.addNode(new Node(RMTConstants.FAULTMESSAGE, null, false));

			locationNode.addNode(new Node(RMTConstants.BUNDLETYPE, null, true));
			locationNode.addNode(new Node(RMTConstants.HEADERS, null, true));
			locationNode.addNode(new Node(RMTConstants.ENTRIES, null, true));
			locationNode.addNode(new Node(RMTConstants.SIGNERS, null, true));
			locationNode.addNode(new Node(RMTConstants.WIRES, null, true));
			this.entries = managedEntries(null, this.bundle, "");
			this.signers = managedSigners(this.bundle);
		}

		protected void createNodes(Bundle bundle) {
			this.bundle = bundle;
			this.createFlag = true;
			locationNode.addNode(new Node(RMTConstants.BUNDLEID, null, false));
			locationNode.addNode(new Node(RMTConstants.SYMBOLICNAME, null, false));
			locationNode.addNode(new Node(RMTConstants.VERSION, null, false));
			locationNode.addNode(new Node(RMTConstants.STATE, null, false));
			locationNode.addNode(new Node(RMTConstants.LASTMODIFIED, null, false));
			locationNode.addNode(new Node(RMTConstants.FAULTTYPE, null, false));
			locationNode.addNode(new Node(RMTConstants.FAULTMESSAGE, null, false));

			locationNode.addNode(new Node(RMTConstants.BUNDLETYPE, null, true));
			locationNode.addNode(new Node(RMTConstants.HEADERS, null, true));
			locationNode.addNode(new Node(RMTConstants.ENTRIES, null, true));
			locationNode.addNode(new Node(RMTConstants.SIGNERS, null, true));
			locationNode.addNode(new Node(RMTConstants.WIRES, null, true));
			this.entries = managedEntries(null, this.bundle, "");
			this.signers = managedSigners(this.bundle);
		}

		protected boolean getCreateFlag() {
			return this.createFlag;
		}

		protected void createWires() {
			if (this.bundle.getState() <= 2)
				return;
			this.wires = managedWires(this.bundle);
		}

		protected Bundle getBundleObj() {
			return this.bundle;
		}

		protected Node getLocatonNode() {
			return this.locationNode;
		}

		protected void setURL(String url) {
			this.url = url;
		}

		protected String getURL() {
			return this.url;
		}

		protected void setAutoStart(boolean autoStart) {
			this.autoStart = autoStart;
		}

		protected boolean getAutoStart() {
			return this.autoStart;
		}

		protected void setFaultType(int type) {
			this.type = type;
		}

		protected int getFaultType() {
			return this.type;
		}

		protected void setFaultMassage(String message) {
			this.message = message;
		}

		protected String getFaultMassage() {
			return this.message;
		}

		protected long getBundleId() {
			return this.bundle.getBundleId();
		}

		protected String getSymbolicNmae() {
			return this.bundle.getSymbolicName();
		}

		protected String getVersion() {
			return this.bundle.getVersion().toString();
		}

		protected String getBundleType() {
			BundleRevision rev = (BundleRevision) this.bundle
					.adapt(BundleRevision.class);
			int bundleType = rev.getTypes();
			if (bundleType == 1)
				return RMTConstants.FRAGMENT;
			else
				return null;
		}

		protected Dictionary getHeaders() {
			if (this.bundle == null)
				return null;
			else
				return this.bundle.getHeaders();
		}

		protected String getLocation() {
			return Uri.decode(this.location);
		}

		protected String getState() {
			int state = this.bundle.getState();
			if (state == 2)
				return RMTConstants.INSTALLED;
			else if (state == 4)
				return RMTConstants.RESOLVED;
			else if (state == 8)
				return RMTConstants.STARTING;
			else if (state == 32)
				return RMTConstants.ACTIVE;
			else if (state == 16)
				return RMTConstants.STOPPING;
			else
				return null;
		}

		protected void setRequestedState(String state) {
			this.requestedState = state;
		}

		protected String getRequestedState() {
			return this.requestedState;
		}

		protected Date getLastModified() {
			return new Date(this.bundle.getLastModified());
		}

		protected int getStartLevel() {
			BundleStartLevel sl = (BundleStartLevel) this.bundle
					.adapt(BundleStartLevel.class);
			return sl.getStartLevel();
		}

		protected int getStartLevelTmp() {
			return this.bundleStartLevelTmp;
		}

		protected void setStartLevel(int stl) {
			this.bundleStartLevelTmp = stl;
			BundleStartLevel sl = (BundleStartLevel) this.bundle
					.adapt(BundleStartLevel.class);
			sl.setStartLevel(stl);
		}

		protected int getInstanceId() {
			long bundleId = this.bundle.getBundleId() + 1;
			return Integer.parseInt(Long.toString(bundleId));
		}

		protected Vector getEntries() {
			return entries;
		}

		protected Vector getSigners() {
			return signers;
		}

		protected Map getWires() {
			return wires;
		}
	}

	protected class Node {
		static final String INTERIOR = "Interiror";
		static final String LEAF = "leaf";
		private String name;
		private String type;
		private Vector children = new Vector();

		Node(String name, Node[] children, boolean nodeType) {
			this.name = name;
			if (children != null) {
				for (int i = 0; i < children.length; i++) {
					this.children.add(children[i]);
				}
			} else
				this.children = new Vector();
			if (nodeType)
				type = INTERIOR;
			else
				type = LEAF;
		}

		protected Node findNode(String[] path) {
			for (int i = 0; i < children.size(); i++) {
				if ((((Node) children.get(i)).getName()).equals(path[0])) {
					if (path.length == 1) {
						return (Node) children.get(i);
					} else {
						String[] nextpath = new String[path.length - 1];
						for (int x = 1; x < path.length; x++) {
							nextpath[x - 1] = path[x];
						}
						return ((Node) children.get(i)).findNode(nextpath);
					}
				}
			}
			return null;
		}

		protected String getName() {
			return name;
		}

		protected void addNode(Node add) {
			children.add(add);
		}

		protected void deleteNode(Node del) {
			children.remove(del);
		}

		protected Node[] getChildren() {
			Node[] nodes = new Node[children.size()];
			for (int i = 0; i < children.size(); i++) {
				nodes[i] = ((Node) children.get(i));
			}
			return nodes;
		}

		protected String[] getChildNodeNames() {
			String[] name = new String[children.size()];
			for (int i = 0; i < children.size(); i++) {
				Node node = ((Node) children.get(i));
				name[i] = node.getName();
			}
			return name;
		}

		protected String getType() {
			return type;
		}
	}

	protected Vector managedSigners(Bundle bundle) {
		Map signersAll = bundle.getSignerCertificates(Bundle.SIGNERS_ALL);
		Map signersTrusted = bundle
				.getSignerCertificates(Bundle.SIGNERS_TRUSTED);
		Vector certList = new Vector();
		Iterator it = signersAll.keySet().iterator();
		for (int i = 0; it.hasNext(); i++) {
			Vector certChainList = new Vector();
			X509Certificate cert = (X509Certificate) it.next();
			List certificateChane = (List) signersAll.get(cert);
			for (Iterator itCert = certificateChane.iterator(); itCert
					.hasNext();) {
				X509Certificate certs = (X509Certificate) itCert.next();
				certChainList.add(certs.getSubjectDN().getName());
			}
			if (signersTrusted.get(cert) != null) {
				SignersSubtree signersobj = new SignersSubtree(true,
						signersInstanceId, certChainList);
				signersInstanceId++;
				certList.add(signersobj);
			} else {
				SignersSubtree signersobj = new SignersSubtree(false,
						signersInstanceId, certChainList);
				signersInstanceId++;
				certList.add(signersobj);
			}
		}
		return certList;
	}

	protected class SignersSubtree {
		boolean trust = false;
		int id = 0;
		Vector certChainList = null;

		SignersSubtree(boolean trust, int id, Vector certChainList) {
			this.certChainList = certChainList;
			this.id = id;
			this.trust = trust;
		}

		protected int getInstanceId() {
			return id;
		}

		protected boolean isTrusted() {
			return trust;
		}

		protected Vector getCertifitateChainList() {
			return certChainList;
		}
	}

	protected Vector managedEntries(Vector entries, Bundle bundle, String p) {
		if (entries == null) {
			entries = new Vector();
		}
		Vector entryPathes = new Vector();
		bundleEntry(entryPathes, bundle, p);
		Iterator ite = entryPathes.iterator();
		while (ite.hasNext()) {
			String path = (String) ite.next();
			try {
				BufferedInputStream bis = new BufferedInputStream(bundle
						.getEntry(Uri.decode(path)).openStream());
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				int b = -1;
				while ((b = bis.read()) != -1)
					bos.write(b);
				EntrySubtree entriesObj = new EntrySubtree(path,
						bos.toByteArray(), entriesInstanceId);
				bis.close();
				bos.close();
				entriesInstanceId++;
				entries.add(entriesObj);
			} catch (IOException ioe) {
				continue;
			}
		}
		return entries;
	}

	private void bundleEntry(Vector entry, Bundle bundle, String p) {
		Enumeration pathes = bundle.getEntryPaths(p);
		while (pathes.hasMoreElements()) {
			String path = (String) pathes.nextElement();
			if (path.endsWith("/"))
				bundleEntry(entry, bundle, path);
			else {
				entry.add(Uri.encode(path));
			}
		}
	}

	protected class EntrySubtree {
		String path = null;
		byte[] content = null;
		int id = -1;

		EntrySubtree(String path, byte[] content, int id) {
			this.path = path;
			this.content = content;
			this.id = id;
		}

		protected String getPath() {
			return path;
		}

		protected byte[] getContent() {
			return content;
		}

		protected int getInstanceId() {
			return id;
		}
	}
}
