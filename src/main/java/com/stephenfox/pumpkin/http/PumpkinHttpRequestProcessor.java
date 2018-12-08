package com.stephenfox.pumpkin.http;

import java.net.Socket;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PumpkinHttpRequestProcessor implements HttpRequestProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(PumpkinHttpRequestProcessor.class);
  private final BlockingQueue<Socket> requestQueue;
  private final Map<String, Handler> handlers;

  PumpkinHttpRequestProcessor(BlockingQueue<Socket> requestQueue, Map<String, Handler> handlers) {
    this.requestQueue = requestQueue;
    this.handlers = handlers;
  }

  @Override
  public void run() {
    Socket socket;
    try {
      while (true) {
        socket = requestQueue.take();
        final PumpkinHttpRequest request = parseRequest(socket);

        if (request != null) {
          if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Received request {}", request);
          }

          final Handler handler = handlers.get(request.getResource());
          if (handler == null) {
            LOGGER.warn("No handler found for resource {}", request.getResource());
            HttpResponse.response404(request).send();
          } else {
            handler.handle(request);
          }
        } else {
          HttpResponse.response400(new BadRequest(socket)).send();
        }
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      LOGGER.error("", e);
    }
  }

  private PumpkinHttpRequest parseRequest(Socket socket) {
    try {
      return PumpkinHttpRequest.from(socket);
    } catch (InvalidHttpRequest e) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Could not parse http request", e);
      }
      return null;
    }
  }

  // TODO: this is ugly.
  private static class BadRequest implements HttpRequest {

    private final Socket socket;

    private BadRequest(Socket socket) {
      this.socket = socket;
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
    public Socket getSocket() {
      return socket;
    }
  }
}
