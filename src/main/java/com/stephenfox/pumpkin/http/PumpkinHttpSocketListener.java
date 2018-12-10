package com.stephenfox.pumpkin.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class loops forever waiting for incoming tcp connections via socket. This class acts as a
 * producer of incoming tcp connections by placing them into a shared request queue. The socket
 * object associated with the connection is placed into the queue for further processing.
 *
 * @author Stephen Fox.
 */
class PumpkinHttpSocketListener implements HttpSocketListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(PumpkinHttpSocketListener.class);
  private final int port;
  private final String host;
  private final Queue<Socket> requestQueue;

  /**
   * Creates a new listener.
   *
   * @param host The host to run on.
   * @param port The port to listen on.
   * @param requestQueue A queue where the incoming connections will be placed, this queue should be
   *     thread safe.
   */
  PumpkinHttpSocketListener(String host, int port, Queue<Socket> requestQueue) {
    this.host = host;
    this.port = port;
    this.requestQueue = requestQueue;
  }

  @Override
  public void listen() {
    try (final ServerSocket serverSocket = new ServerSocket(port)) {
      while (true) {
        final Socket socket = serverSocket.accept();
        requestQueue.add(socket);
      }
    } catch (IOException e) {
      LOGGER.error("", e);
    }
  }
}
