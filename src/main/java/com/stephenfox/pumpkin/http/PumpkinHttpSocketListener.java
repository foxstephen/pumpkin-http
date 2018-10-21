package com.stephenfox.pumpkin.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
          final OutputStream outputStream = socket.getOutputStream();
          try {
            sharedRequestQueue.add(PumpkinHttpRequest.from(reader, outputStream));
          } catch (IllegalArgumentException e) {
            HttpResponse.response400(new BadRequest(outputStream));
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // TODO: this is ugly.
  private static class BadRequest implements HttpRequest {
    private final OutputStream outputStream;

    private BadRequest(OutputStream outputStream) {
      this.outputStream = outputStream;
    }

    @Override
    public String getVersion() {
      return null;
    }

    @Override
    public HttpHeaders getHeaders() {
      return null;
    }

    @Override
    public HttpMethod getMethod() {
      return null;
    }

    @Override
    public String getResource() {
      return null;
    }

    @Override
    public String getBody() {
      return null;
    }

    @Override
    public OutputStream getConnection() {
      return outputStream;
    }
  }
}
