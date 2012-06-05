package org.osgi.impl.service.residentialmanagement.plugins;

import org.osgi.framework.BundleContext;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.spi.TransactionalDataSession;
import org.osgi.service.dmt.spi.ReadWriteDataSession;

public class LogReadWriteSession extends LogReadOnlySession implements ReadWriteDataSession, TransactionalDataSession{
	
	LogReadWriteSession(BundleContext context) {
		super(context);
	}

	public void close() throws DmtException {
		this.resetKeepLogEntry();
	}
	
	public void commit() throws DmtException {
	}

	public void rollback() throws DmtException {
	}

	public void copy(String[] nodePath, String[] newNodePath, boolean recursive)
			throws DmtException {
		throw new DmtException(nodePath, DmtException.COMMAND_FAILED,
		"This operation is not allowed in the log subtree.");
	}

	public void createInteriorNode(String[] nodePath, String type)
			throws DmtException {
		throw new DmtException(nodePath, DmtException.COMMAND_FAILED,
		"There is no interior node to be created in the log subtree.");
	}

	public void createLeafNode(String[] nodePath, DmtData value, String mimeType)
			throws DmtException {
		throw new DmtException(nodePath, DmtException.COMMAND_FAILED,
		"There is no leaf node to be created in the log subtree.");
	}

	public void deleteNode(String[] nodePath) throws DmtException {
		throw new DmtException(nodePath, DmtException.COMMAND_FAILED,
		"This operation is not allowed in the log subtree.");
	}

	public void renameNode(String[] nodePath, String newName)
			throws DmtException {
		throw new DmtException(nodePath, DmtException.COMMAND_FAILED,
		"This operation is not allowed in the log subtree.");
	}

	public void setNodeTitle(String[] nodePath, String title)
			throws DmtException {
		throw new DmtException(nodePath, DmtException.COMMAND_FAILED,
		"This operation is not allowed in the log subtree.");
	}

	public void setNodeType(String[] nodePath, String type) throws DmtException {
		throw new DmtException(nodePath, DmtException.COMMAND_FAILED,
		"This operation is not allowed in the log subtree.");
	}

	public void setNodeValue(String[] nodePath, DmtData data)
			throws DmtException {
		throw new DmtException(nodePath, DmtException.COMMAND_FAILED,
		"This operation is not allowed in the log subtree.");
	}
}
