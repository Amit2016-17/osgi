package org.osgi.test.cases.upnp.tbc.device;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;

import org.osgi.test.cases.upnp.tbc.UPnPConstants;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.test.support.sleep.Sleep;

/**
 * 
 * 
 */
public class DiscoveryServer {
	private final MulticastSocket	msocket;
	private final InetAddress		address;
	private final List<DiscoveryClient>	senders;

	public DiscoveryServer() throws Exception {
		msocket = new MulticastSocket(UPnPConstants.UPnPMCPort);
		address = InetAddress.getByName(UPnPConstants.UPnPMCAddress);
		msocket.joinGroup(address);
		senders = new ArrayList<>();
	}

	public void registerSender(DiscoveryClient client) {
		DefaultTestBundleControl.log("Starting Discovery messages sender");
		synchronized (senders) {
			senders.add(client);
			Thread th = new Thread(client, "MSG Sender Client");
			th.start();
		}
	}

	public void unregisterSender(DiscoveryClient client) {
		client.stop();
		synchronized (senders) {
			senders.remove(client);
		}
	}

	public void send(DatagramPacket p) {
		if (msocket != null && p != null) {
			try {
				for (int i = 0; i < UPnPConstants.UDP_SEND_COUNT; i++) {
					msocket.send(p);
					Sleep.sleep(100);
				}
			}
			catch (IOException er) {
				// ignored
			} catch (InterruptedException e) {
				// ignored
			}
		}
	}

	public void finish() {
		try {
			msocket.leaveGroup(address);
			msocket.close();
		}
		catch (Exception er) {
			// ignored
		}
	}
}
