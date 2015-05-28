
package org.osgi.impl.service.enocean.basedriver.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.impl.service.enocean.basedriver.EnOceanBaseDriver;
import org.osgi.impl.service.enocean.basedriver.radio.MessageSYS_EX;
import org.osgi.impl.service.enocean.utils.Logger;
import org.osgi.service.device.Constants;
import org.osgi.service.enocean.EnOceanDevice;
import org.osgi.service.enocean.EnOceanHandler;
import org.osgi.service.enocean.EnOceanMessage;
import org.osgi.service.enocean.EnOceanRPC;

/**
 * EnOcean device implementation.
 */
public class EnOceanDeviceImpl implements EnOceanDevice {

    private static final String TAG = "EnOceanDeviceImpl";

    private static final String ROLLING_CODE	= "ROLLING_CODE";
    private static final String LEARNING_MODE	= "LEARNING_MODE";
    private static final String ENCRYPTION_KEY 	= "ENCRYPTION_KEY";
    private static final String NAME 		= "NAME";
    private static final String PROFILE_NAME 	= "PROFILE_NAME";
    private static final String SECURITY_LEVEL 	= "SECURITY_LEVEL";

    private BundleContext bc;
    private ServiceRegistration sReg;
    private Properties props;
    private EnOceanMessage lastMessage;
    private EnOceanBaseDriver driver;
    private int chip_id;

    /**
     * An {@link EnOceanDeviceImpl} creation is directly related to its
     * registration within the framework. Such a Device should only be
     * registered after a proper teach-in procedure, so that the RORG, FIUNC and
     * TYPE are already known.
     * 
     * @param bundleContext
     * @param driver
     * @param uid
     * @param rorg
     */
    public EnOceanDeviceImpl(BundleContext bundleContext, EnOceanBaseDriver driver, int uid, int rorg) {
	this.bc = bundleContext;
	this.driver = driver;
	this.chip_id = uid;
	props = new Properties();
	props.put(Constants.DEVICE_CATEGORY, EnOceanDevice.DEVICE_CATEGORY);
	props.put(EnOceanDevice.CHIP_ID, String.valueOf(uid));
	props.put(EnOceanDevice.RORG, String.valueOf(rorg));
	sReg = this.bc.registerService(EnOceanDevice.class.getName(), this, props);
	Logger.d(TAG, "registering device : " + this);
	/* Initializations */
	lastMessage = null;
    }

    /**
     * @return the base driver's properties.
     */
    public Properties getServiceProperties() {
	return props;
    }

    /**
     * @param func
     * @param type
     * @param manuf
     */
    public void registerProfile(int func, int type, int manuf) {
	props.put(EnOceanDevice.FUNC, String.valueOf(func));
	props.put(EnOceanDevice.TYPE, String.valueOf(type));
	props.put(EnOceanDevice.MANUFACTURER, String.valueOf(manuf));
	sReg.setProperties(props);
    }

    public void setLearningMode(boolean learnMode) {
	props.put(LEARNING_MODE, String.valueOf(learnMode));
    }

    public int getRollingCode() {
	return getIntProperty(ROLLING_CODE, 0);
    }

    public void setRollingCode(int rollingCode) {
	props.put(ROLLING_CODE, String.valueOf(rollingCode));
    }

    public byte[] getEncryptionKey() {
	return (byte[]) props.get(ENCRYPTION_KEY);
    }

    public void setEncryptionKey(byte[] key) {
	props.put(ENCRYPTION_KEY, key);
    }

    public int[] getLearnedDevices() {
	return null;
    }

    /**
     * @return the name.
     */
    public String getName() {
	return props.getProperty(NAME);
    }

    /**
     * @param message
     * @param handler
     */
    public void send(EnOceanMessage message, EnOceanHandler handler) {
	// TODO Auto-generated method stub
    }

    /**
     * @return the las message as an EnOceanMessage.
     */
    public EnOceanMessage getLastMessage() {
	return lastMessage;
    }

    public Map getRPCs() {
	return null;
    }

    /**
     * @return the profile name.
     */
    public String getProfileName() {
	return props.getProperty(PROFILE_NAME);
    }

    public int getSecurityLevelFormat() {
	return getIntProperty(SECURITY_LEVEL, 0);
    }

    /**
     * @param name
     */
    public void setName(String name) {
	props.put(NAME, name);
    }

    /**
     * @param profileName
     */
    public void setProfileName(String profileName) {
	props.put(PROFILE_NAME, profileName);
    }

    /**
     * @param securityLevel
     */
    public void getSecurityLevelFormat(int securityLevel) {
	props.put(SECURITY_LEVEL, String.valueOf(securityLevel));
    }

    /**
     * @param rorg
     */
    public void setRorg(int rorg) {
	props.put(EnOceanDevice.RORG, String.valueOf(rorg));
    }

    public void setFunc(int func) {
	props.put(EnOceanDevice.FUNC, String.valueOf(func));
	sReg.setProperties(props);
    }

    public void setType(int type) {
	props.put(EnOceanDevice.TYPE, String.valueOf(type));
	sReg.setProperties(props);
    }

    /**
     * @param manuf
     */
    public void setManufacturer(int manuf) {
	props.put(EnOceanDevice.MANUFACTURER, String.valueOf(manuf));
	sReg.setProperties(props);
    }

    public int getChipId() {
	return chip_id;
    }

    public int getRorg() {
	return getIntProperty(EnOceanDevice.RORG);
    }

    public int getFunc() {
	return getIntProperty(EnOceanDevice.FUNC);
    }

    public int getType() {
	return getIntProperty(EnOceanDevice.TYPE);
    }

    public int getManufacturer() {
	return getIntProperty(EnOceanDevice.MANUFACTURER);
    }

    /**
     * @param msg
     */
    public void setLastMessage(EnOceanMessage msg) {
	lastMessage = msg;
    }

    /**
     * Safe function to get an integer property
     * 
     * @param key
     * @return the int-converted property, or -1
     */
    private final int getIntProperty(String key) {
	return getIntProperty(key, -1);
    }

    private final int getIntProperty(String key, int defaultValue) {
	try {
	    return Integer.parseInt(props.get(key).toString());
	} catch (Exception e) {
	    return defaultValue;
	}
    }

    public void invoke(EnOceanRPC rpc, EnOceanHandler handler) throws IllegalArgumentException {
	// Generate the SYS_EX message relative to the RPC
	MessageSYS_EX msg = new MessageSYS_EX(rpc);
	for (Iterator it = msg.getTelegrams().iterator(); it.hasNext();) {
	    byte[] telegram = (byte[]) it.next();
	    driver.send(telegram);
	}
    }

    public void remove() {
	try {
	    sReg.unregister();
	    Logger.e(TAG, "Unregistered device " + this);
	} catch (IllegalStateException e) {
	    Logger.e(TAG, "attempt to unregister a device twice : " + e.getMessage());
	}
    }

    public String toString() {
	return "<EnOceanDeviceImpl " + chip_id + ">" + props
		+ "</EnOceanDeviceImpl>";
    }

}
