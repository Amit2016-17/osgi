/*
 * Project Title : Wire Admin Test Case
 * Author        : Neviana Ducheva
 * Company       : ProSyst
 */
package org.osgi.test.cases.wireadmin.tb1;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.wireadmin.Consumer;
import org.osgi.service.wireadmin.Envelope;
import org.osgi.service.wireadmin.Wire;
import org.osgi.service.wireadmin.WireConstants;

/**
 * 
 * 
 * @author Neviana Ducheva
 * @version
 * @since
 */
public class ConsumerImpl implements BundleActivator, Consumer {

	@Override
	public void start(BundleContext context) {
		Hashtable<String,Object> p = new Hashtable<>();
		p.put(WireConstants.WIREADMIN_CONSUMER_FLAVORS, new Class[] {
				String.class, Integer.class, Envelope.class});
		p.put(Constants.SERVICE_PID, "consumer.ConsumerImplA");
		context.registerService(Consumer.class.getName(), this, p);
	}

	@Override
	public void stop(BundleContext context) {
		// service unregistered by framework
	}

	/**
	 * 
	 * 
	 * @param wires
	 */
	@Override
	public void producersConnected(Wire[] wires) {
		// empty
	}

	/**
	 * 
	 * 
	 * @param wire
	 * @param value
	 */
	@Override
	public void updated(Wire wire, Object value) {
		// empty
	}
}