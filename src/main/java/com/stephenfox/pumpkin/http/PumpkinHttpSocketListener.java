package com.stephenfox.pumpkin.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PumpkinHttpSocketListener implements HttpSocketListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(PumpkinHttpSocketListener.class);
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
    try (final ServerSocket serverSocket = new ServerSocket(port)) {
      while (true) {
        final Socket socket = serverSocket.accept();
        final BufferedReader reader =
            new BufferedReader(new InputStreamReader(socket.getInputStream()));
        final OutputStream outputStream = socket.getOutputStream();
        parseRequest(outputStream, reader);
      }
    } catch (IOException e) {
      LOGGER.error("", e);
    }
  }

  private void parseRequest(OutputStream outputStream, BufferedReader reader) {
    try {
      sharedRequestQueue.add(PumpkinHttpRequest.from(reader, outputStream));
    } catch (InvalidHttpRequest e) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("An error occurred parsing http request, responding with 400", e);
      }
      HttpResponse.response400(new BadRequest(outputStream)).send();
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
