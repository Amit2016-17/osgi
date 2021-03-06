package org.osgi.test.cases.xml.tb2;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

abstract class AbstractDocumentBuilderFactory extends DocumentBuilderFactory {
	private final boolean	m_validating;
	private final boolean	m_namespaceAware;

	protected AbstractDocumentBuilderFactory(boolean validating,
			boolean namespaceAware) {
		m_validating = validating;
		m_namespaceAware = namespaceAware;
	}

	public DocumentBuilder newDocumentBuilder()
			throws ParserConfigurationException {
		if (isValidating() && (m_validating == false)) {
			throw new ParserConfigurationException();
		}
		if (isNamespaceAware() && (m_namespaceAware == false)) {
			throw new ParserConfigurationException();
		}
		return null;
	}

	public Object getAttribute(String s) throws IllegalArgumentException {
		throw new IllegalArgumentException();
	}

	public void setAttribute(String s, Object o)
			throws IllegalArgumentException {
		throw new IllegalArgumentException();
	}

	public void setFeature(String name, boolean value)
			throws ParserConfigurationException {
		throw new ParserConfigurationException();
	}

	public boolean getFeature(String name) throws ParserConfigurationException {
		throw new ParserConfigurationException();
	}
}
