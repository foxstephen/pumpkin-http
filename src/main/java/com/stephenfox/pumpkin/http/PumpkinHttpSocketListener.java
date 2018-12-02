package com.stephenfox.pumpkin.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PumpkinHttpSocketListener implements HttpSocketListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(PumpkinHttpSocketListener.class);
  private final int port;
  private final String host;
  private final Queue<Socket> requestQueue;

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
