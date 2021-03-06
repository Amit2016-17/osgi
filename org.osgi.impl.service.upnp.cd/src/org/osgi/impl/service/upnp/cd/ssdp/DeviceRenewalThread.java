package org.osgi.impl.service.upnp.cd.ssdp;

import java.util.Enumeration;

// This class renewals the device for each 35 minutes. It invokes NOITY after expiration
// of device. By default for every device 35 minutes is expiration time.
public class DeviceRenewalThread extends Thread {
	private SSDPComponent	ssdpcomp;
	private boolean			flag	= true;
	private DeviceExporter	exporter;

	// This constructor constructs the DeviceRenewalThread.
	DeviceRenewalThread(SSDPComponent comp) {
		ssdpcomp = comp;
		exporter = new DeviceExporter();
	}

	// This methos checks contineously check the device created time. If the
	// created time expires
	// expires it will renewal device.
	@Override
	public void run() {
		while (flag) {
			try {
				for (Enumeration<DeviceDetails> enumeration = ssdpcomp.allDeviceDetails
						.elements(); enumeration
						.hasMoreElements();) {
					DeviceDetails device = enumeration.nextElement();
					if (device.getTime() <= System.currentTimeMillis()) {
						System.out.println("send renew notify message");
						exporter.sendDeviceForNotify(device);
					}
				}
				Thread.sleep(5000); //### added so this thread does not consume
				// all time - pkr
			}
			catch (Exception e) {
				// ignored
			}
		}
	}

	// This methos kills device renewal thread.
	public void killDeviceRenewalThread() {
		flag = false;
		interrupt();
	}
}
