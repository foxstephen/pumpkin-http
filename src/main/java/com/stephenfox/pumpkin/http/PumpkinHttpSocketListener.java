package com.stephenfox.pumpkin.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;

class PumpkinHttpSocketListener implements HttpSocketListener {

  private final int port;
  private final String host;
  private final Queue<HttpRequest> sharedRequestQueue;

  PumpkinHttpSocketListener(String host, int port, Queue<HttpRequest> sharedRequestQueue) {
    this.host = host;
    this.port = port;
    this.sharedRequestQueue = sharedRequestQueue;
  }

  @Override
  public void listen() {
    try {
      final ServerSocket serverSocket = new ServerSocket(port);

      while (true) {
        try {
          final Socket socket = serverSocket.accept();
          final BufferedReader reader =
              new BufferedReader(new InputStreamReader(socket.getInputStream()));
          sharedRequestQueue.add(PumpkinHttpRequest.from(reader));
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
