/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
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

package org.osgi.test.cases.cu.tb1;

import org.osgi.service.cu.ControlUnitException;
import org.osgi.service.cu.admin.spi.CUAdminCallback;
import org.osgi.service.cu.admin.spi.ManagedControlUnit;

/**
 *
 * Represents information given by the tachometer.
 * 
 * The tachometer gives a raw output expressed in pulses/second.
 * It can be calibrated with a specific dpp (distance per pulse).
 * 
 * It offers one state variable: 
 * 	byte rawOutput (pulses/second)
 * 
 * and the following actions:
 * 	void calibrate (byte dpp);
 * 	byte getDpp();
 * 	boolean isDppValid();
 * 
 * @version $Revision$
 */
public class HipTacho implements ManagedControlUnit {
	private String id = "hip.tacho";
	private String type = "hip.tacho";
	private byte dpp;
	private byte rawOutput;
	private boolean dppValid;
	private CUAdminCallback adminCallback;

	/**
	 * Create a new HipTacho
	 */
	public HipTacho() {
	}

	/**
	 * @param adminCallback
	 * @see org.osgi.service.cu.admin.spi.ManagedControlUnit#setControlUnitCallback(org.osgi.service.cu.admin.spi.CUAdminCallback)
	 */
	public void setControlUnitCallback(CUAdminCallback adminCallback) {
		this.adminCallback = adminCallback;
	}

	/**
	 * @return
	 * @see org.osgi.service.cu.ControlUnit#getId()
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return
	 * @see org.osgi.service.cu.ControlUnit#getType()
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param varId
	 * @return
	 * @throws Exception
	 * @see org.osgi.service.cu.ControlUnit#queryStateVariable(java.lang.String)
	 */
	public Object queryStateVariable(String varId) throws ControlUnitException {
		// rawOutput
		if (varId == "hip.tacho.rawOutput") {
			rawOutput++;
			return new Byte(rawOutput);
		}
		else throw (new ControlUnitException(ControlUnitException.NO_SUCH_STATE_VARIABLE_ERROR));
	}

	/**
	 * @param actionId
	 * @param arguments
	 * @return
	 * @throws Exception
	 * @see org.osgi.service.cu.ControlUnit#invokeAction(java.lang.String, java.lang.Object)
	 */
	public Object invokeAction(String actionId, Object arguments)
			throws ControlUnitException {
		//void calibrate(byte dpp)
		if (actionId == "hip.tacho.calibrate") {
			if (arguments == null)
				throw (new ControlUnitException(ControlUnitException.ILLEGAL_ACTION_ARGUMENTS_ERROR));
			if (arguments instanceof Byte) {
				dpp = ((Byte)arguments).byteValue();
				dppValid = true;
				return null;
			}
			else throw (new ControlUnitException(ControlUnitException.ILLEGAL_ACTION_ARGUMENTS_ERROR));
		}
		
		//byte getDpp()
		else if (actionId == "hip.tacho.getDpp") {
			if (arguments != null)
				throw (new IllegalArgumentException());
			return (new Byte(dpp));
		}
		
		//boolean isDppValid()
		else if (actionId == "hip.tacho.isDppValid") {
			if (arguments != null)
				throw (new IllegalArgumentException());	
			return (new Boolean(dppValid));
		}
		else throw (new IllegalArgumentException());
	}
}
