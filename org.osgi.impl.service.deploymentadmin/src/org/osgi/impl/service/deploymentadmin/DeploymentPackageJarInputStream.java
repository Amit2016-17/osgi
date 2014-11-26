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
package org.osgi.impl.service.deploymentadmin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.security.PrivilegedAction;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipException;
import org.osgi.service.deploymentadmin.DeploymentException;

/**
 * DeploymentPackageJarInputStream class wraps the JarInputStream implementation
 * to override its behaviour according to the needs of the deployment packages
 * (DPs).
 */
public class DeploymentPackageJarInputStream {

	/**
	 * Extends the JarEntry functionality according to the needs of the
	 * deployment packages (DPs). It is able to recognise bundles, resources and
	 * their missing variations.
	 */
	public static class Entry extends JarEntry {
		private Attributes				attrs;
		private ByteArrayOutputStream	buffer;
		private boolean isL10N;

		private Entry(JarEntry je, ByteArrayOutputStream buffer, String locPath)
				throws IOException {
			super(je);
			if (null == buffer)
				throw new IllegalArgumentException(
						"The 'buffer' parameter cannot be null");
			attrs = je.getAttributes();
			isL10N = DeploymentPackageJarInputStream.isL10N(locPath, je.getName());
			this.buffer = buffer;
		}

		private Entry(String name, Attributes attrs, String locPath) {
			super(name);
			isL10N = DeploymentPackageJarInputStream.isL10N(locPath, name);
			this.attrs = attrs;
		}

		private static boolean isMissing(Attributes attrs) {
			if (null == attrs)
				return false;
			String miss = attrs.getValue(DAConstants.MISSING);
			if (null != miss)
				return Boolean.valueOf(miss).booleanValue();
			return false;
		}

		private static boolean isBundle(Attributes attrs) {
			if (null == attrs)
				return false;
			String symbName = attrs.getValue(DAConstants.BUNDLE_SYMBOLIC_NAME);
			String version = attrs.getValue(DAConstants.BUNDLE_VERSION);
			return null != symbName && null != version;
		}

		public InputStream getInputStream() throws IOException {
			return new ByteArrayInputStream(buffer.toByteArray());
		}

		public boolean isBundle() {
			return isBundle(attrs);
		}

		public boolean isCustomizerBundle() {
			String cust = attrs.getValue(DAConstants.CUSTOMIZER);
			boolean isCust = Boolean.valueOf(cust).booleanValue();
			return isBundle() && isCust;
		}

		public boolean isResource() {
			return !isBundle() && !isL10N();
		}

		public boolean isL10N() {
			return isL10N;
		}
		
		public boolean isMissing() {
			return isMissing(attrs);
		}

		public Attributes getAttributes() {
			return attrs;
		}

		/*
		 * Return list of Certificate[]-s. One list element is one certificate
		 * chain.
		 */
		private List getCertificateChains() {
			List ret = new LinkedList();
			Certificate[] certs = getCertificates();

			if (null == certs || certs.length == 0)
				return ret;

			int i = 0;
			while (i < certs.length) {
				ArrayList e = new ArrayList();
				java.security.cert.X509Certificate cPrev = null;
				java.security.cert.X509Certificate cAct = (java.security.cert.X509Certificate) certs[i];
				while (cPrev == null
						|| cPrev.getIssuerDN().equals(cAct.getSubjectDN())) {
					e.add(cAct);
					cPrev = cAct;
					++i;
					if (i >= certs.length)
						break;
					cAct = (java.security.cert.X509Certificate) certs[i];
				}
				ret.add(e.toArray(new java.security.cert.X509Certificate[] {}));
			}

			return ret;
		}

		/*
		 * Returns a the list of cerificate chains. One chain is a String[].
		 */
		public List getCertificateChainStringArrays() {
			List l = getCertificateChains();
			List list = new LinkedList();
			for (Iterator iter = l.iterator(); iter.hasNext();) {
				java.security.cert.X509Certificate[] cs = (java.security.cert.X509Certificate[]) iter.next();
				List buffer = new Vector();
				for (int i = 0; i < cs.length; i++)
					buffer.add(cs[i].getSubjectDN().toString());
				list.add(buffer.toArray(new String[] {}));
			}
			return list;
		}

		/**
		 * There is a bug in Java 5 that always returns null for
		 * getCertificates. So if we do not get any certificates, we try to get
		 * them through the code signers. Because this is in Java 5 or later
		 * only, we have to use reflecton. Oh yeah, we also require it to be
		 * done in a secure block because for some reason, you need permission
		 * to call public method.
		 */
		public Certificate[] getCertificates() {
			Certificate[] certs = super.getCertificates();
			if (certs != null)
				return certs;

			// If no certs, try reflection to get the signers
			final Object entry = this;
			return (Certificate[]) java.security.AccessController
					.doPrivileged(new PrivilegedAction() {
						public Object run() {
							try {
								List certificates = new ArrayList();
								Object signers[] = (Object[]) call(entry,
										"getCodeSigners");
								if (signers != null) {
									for (int i = 0; i < signers.length; i++) {
										Object certpath = call(signers[i],
												"getSignerCertPath");
										List signerCertificates = (List) call(
												certpath, "getCertificates");
										Certificate cert = (Certificate) signerCertificates
												.get(0);
										if (cert != null)
											certificates.add(cert);
									}
								}
								return certificates
										.toArray(new Certificate[certificates
												.size()]);
							}
							catch (Throwable t) {
								t.printStackTrace();
								return null;
							}
						}
					});
		}

		/**
		 * Try to find the public class where this is defined.
		 * 
		 * @param target
		 * @param name
		 * @return
		 * @throws Exception
		 */
		Object call(Object target, String name) throws Exception {
            Method m = target.getClass().getMethod(name);
            return m.invoke(target);
		}
	}

	// Are used to check right file order
	private static final int				FT_INITIAL				= -1;
	private static final int				FT_MANIFEST				= 0;
	private static final int				FT_SIGNATURE			= 1;
	private static final int				FT_L10N					= 2;
	private static final int				FT_BUNDLE				= 3;
	private static final int				FT_RESOURCE				= 4;

	// Is used to check right file order
	private int								lastFileType			= FT_INITIAL;

	private JarInputStream					jis;
	private DeploymentPackageResourceBundle	dprb					= new DeploymentPackageResourceBundle();

	private Manifest						manifest;
	// these Entries must precede resource entries in the subsequent nextEntry()
	// calls
	private LinkedList						missingBundleEntries	= new LinkedList();

	// this will contains the cerificates if there are any
	private Entry							firstEntry;

	private Entry							actEntry;
	private JarEntry						actJarEntry;

	private boolean							fixPack;
	private String							locPath;

	public DeploymentPackageJarInputStream(InputStream is) throws IOException,
			DeploymentException {
		this.jis = new JarInputStream(new DeploymentInputStream(is));

		Manifest mf = getManifest();
		if (null == mf)
			throw new DeploymentException(DeploymentException.CODE_ORDER_ERROR,
					"META-INF/MANIFEST.MF is missing or not the first entry");
		manifest = (Manifest) mf.clone();

		locPath = manifest.getMainAttributes().getValue(DAConstants.LOC_PATH);
		if (null == locPath)
			locPath = DAConstants.DEF_LOC_PATH;

		// these Entries must precede resource entries in the subsequent
		// nextEntry() calls
		for (Iterator iter = manifest.getEntries().keySet().iterator(); iter
				.hasNext();) {
			String name = (String) iter.next();
			Attributes as = manifest.getEntries().get(name);
			if (Entry.isBundle(as) && Entry.isMissing(as))
				missingBundleEntries.add(name);
		}

		fixPack = (null != manifest.getMainAttributes().getValue(
				DAConstants.DP_FIXPACK));

		// this skips the "uninterested" part of the stream (manifest, sign.
		// files, etc.)
		firstEntry = nextEntry();
	}

	public List getCertificateChains() {
		if (null != firstEntry)
			return firstEntry.getCertificateChains();
		return null;
	}

	public List getCertificateChainStringArrays() {
		if (null != firstEntry)
			return firstEntry.getCertificateChainStringArrays();

		return null;
	}

	/**
	 * Gives back the next Entry in the deployment package.
	 * 
	 * @return The next Entry or <code>null</code> if there is no more
	 *         entries.
	 * @throws IOException
	 * @throws DeploymentException
	 */
	public Entry nextEntry() throws IOException, DeploymentException {
		if (null != actEntry)
			return actEntry;

		if (null == actJarEntry)
			actJarEntry = getNextJarEntry();

		// these Entries must precede resource entries in the subsequent
		// nextEntry() calls
		if (null == actJarEntry || !Entry.isBundle(actJarEntry.getAttributes())) {
			// there are no more bundles or the stream ended
			if (!missingBundleEntries.isEmpty()) {
				// ... and there are missing entries
				String name = (String) missingBundleEntries.removeFirst();
				actEntry = new Entry(name,
						manifest.getEntries().remove(name), locPath);
				return actEntry;
			}
		}

		// The stream ended but we may have missing resources (in the manifest)
		if (null == actJarEntry) {
			Iterator it = manifest.getEntries().keySet().iterator();
			if (it.hasNext()) {
				String name = (String) it.next();
				if (!fixPack)
					throw new DeploymentException(
							DeploymentException.CODE_ORDER_ERROR,
							"There is no data in the stream for \"Name\"-section: "
									+ name);
				actEntry = new Entry(name,
						manifest.getEntries().get(name), locPath);

				// remove to ensure that the sequence of Entries ends
				it.remove();
			}
		}
		else {
			if (null == actJarEntry.getAttributes())
				throw new DeploymentException(
						DeploymentException.CODE_MISSING_HEADER,
						"There is no \"Name\"-section for JarEntry: "
								+ actJarEntry);

			ByteArrayOutputStream bos = readIntoBuffer();
			closeEntry();

			// We have opened a JarEntries so we have to close it
			// when nextEntry() is called next time
			actEntry = new Entry(actJarEntry, bos, locPath);

			if (actEntry.isL10N())
				dprb.addPropertyFile(actEntry.getName(), bos);
			
			// remove to ensure that the sequence of Entries ends
			manifest.getEntries().remove(actEntry.getName());

			actJarEntry = null;
		}
		return actEntry;
	}

	/*
	 * Skips uninterested JarEntries (directories, .sf files, etc.)
	 */
	private JarEntry getNextJarEntry() throws IOException, DeploymentException {
		do {
			try {
				actJarEntry = jis.getNextJarEntry();
			}
			catch (ZipException ze) {
				throw new DeploymentException(
						DeploymentException.CODE_NOT_A_JAR, "Bad jar file");
			}
			catch (SecurityException se) {
				// This could happen when a signed jar has been modified
				throw new DeploymentException(
						DeploymentException.CODE_SIGNING_ERROR,
						"Probably the DP is modified after signing! The error message is: "
								+ se.getMessage(), se);
			}
			checkFileOrder();
		} while (null != actJarEntry && isUninterested(actJarEntry));

		return actJarEntry;
	}

	private void checkFileOrder() throws DeploymentException, IOException {
		if (null != actJarEntry) {
			if (actJarEntry.isDirectory())
				return;
			int actFileType = getFileType(actJarEntry);
			if (actFileType < lastFileType)
				throw new DeploymentException(
						DeploymentException.CODE_ORDER_ERROR);
			lastFileType = actFileType;
		}
	}

	private int getFileType(JarEntry je) throws IOException {
		String name = je.getName().toLowerCase();
		if (name.startsWith("meta-inf/")
				&& (name.endsWith(".sf") || name.endsWith(".dsa")
						|| name.endsWith(".rsa") || name.endsWith(".rf")))
			return FT_SIGNATURE;
		else
			if (name.startsWith("meta-inf/manifest.mf"))
				return FT_MANIFEST;
			else
				if (isL10N(locPath, je.getName()))
					return FT_L10N;
		if (Entry.isBundle(je.getAttributes()))
			return FT_BUNDLE;
		return FT_RESOURCE;
	}

	private boolean isUninterested(JarEntry je) throws IOException {
		if (je.isDirectory())
			return true;
		if (je.getName().toLowerCase().startsWith("meta-inf/"))
			return true;
		return false;
	}

	private ByteArrayOutputStream readIntoBuffer() throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			byte[] data = new byte[0x1000];
			int i = jis.read(data);
			while (-1 != i) {
				bos.write(data, 0, i);
				i = jis.read(data);
			}
		}
		finally {
			if (null != bos)
				bos.close();
		}
		return bos;
	}

	public void closeEntry() throws IOException {
		// nextEntry() calls the JarInputStream.closeEntry() method
		actEntry = null;
	}

	public Manifest getManifest() {
		return jis.getManifest();
	}

	DeploymentPackageResourceBundle getResourceBundle() {
		return dprb;
	}

	private static boolean isL10N(String locPath, String l10n) {
		return l10n.startsWith(locPath) && l10n.toLowerCase().endsWith(".properties");
	}
	
}
