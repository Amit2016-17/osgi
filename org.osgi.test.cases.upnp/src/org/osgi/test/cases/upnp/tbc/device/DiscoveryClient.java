package org.osgi.test.cases.upnp.tbc.device;

import java.net.*;

/**
 *
 *
 * @Marian Dichev
 * @m_dichev@prosyst.bg
 * @1.0
 * @since
 */
public abstract class DiscoveryClient implements Runnable {

  private DatagramPacket[] packet;
  private DiscoveryServer server;
  public boolean running = true;

  public DiscoveryClient (DiscoveryServer server) {
    this.server = server;
  }

  public abstract DatagramPacket[] getAliveDiscoveries();

  public abstract DatagramPacket[] getByeDiscoveries();

  public abstract long getTimeout();

  public abstract void request();

  public void run() {
    try {
    while (running) {
      server.logger.log ("Creating alive discovery messages");
      packet = getAliveDiscoveries();
      int len = packet.length;

      if (getTimeout() <= 0) {
        break;
      }

      server.logger.log ("Sending alive discovery messages");
      for (int i = 0; i < len; i++) {
        server.send ( packet[i] );
      }

      if (getTimeout() <= 0) {
        break;
      }

      try {
        synchronized (server) {
          server.wait(getTimeout());
        }
      } catch (InterruptedException er) {
        er.printStackTrace();
      }
    }
///    server.logger.log ("Taking all bye discoveries and sending");
    packet = getByeDiscoveries();
    for (int j = 0; j < packet.length; j++) {
      server.send(packet[j]);
    }
    } catch (Exception er) {
      er.printStackTrace();
    }
  }

}