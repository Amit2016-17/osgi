/*
 * Project Title : Wire Admin Test Case
 * Author        : Neviana Ducheva
 * Company       : ProSyst
 */
package org.osgi.test.cases.wireadmin.tb6;

import java.util.Hashtable;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.wireadmin.*;

/**
 * 
 * 
 * @author Neviana Ducheva
 * @version
 * @since
 */
public class ProducerImpl implements BundleActivator, Producer {
	ServiceRegistration	regProducer;
	BundleContext		context;

	public ProducerImpl() {
	}

	public void start(BundleContext context) {
		this.context = context;
		Hashtable p = new Hashtable();
		p
				.put(
						org.osgi.service.wireadmin.WireConstants.WIREADMIN_PRODUCER_FLAVORS,
						new Class[] {String.class, Integer.class,
								Envelope.class});
		p.put(org.osgi.framework.Constants.SERVICE_PID,
				"producer.ProducerImplC");
		p
				.put(
						org.osgi.service.wireadmin.WireConstants.WIREADMIN_PRODUCER_SCOPE,
						new String[] {"*"});
		regProducer = context.registerService(Producer.class.getName(),
				new ProducerImpl(), p);
	}

	public void stop(BundleContext context) {
		try {
			if (regProducer != null) {
				regProducer.unregister();
				regProducer = null;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * 
	 * @param wires
	 */
	public void consumersConnected(Wire[] wires) {
	}

	/**
	 * 
	 * 
	 * @param wire
	 * @return
	 */
	public Object polled(Wire wire) {
		return "";
	}
}