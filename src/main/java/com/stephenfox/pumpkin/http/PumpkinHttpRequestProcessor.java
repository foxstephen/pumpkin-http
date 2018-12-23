package com.stephenfox.pumpkin.http;

import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PumpkinHttpRequestProcessor implements HttpRequestProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(PumpkinHttpRequestProcessor.class);
  private final BlockingQueue<Socket> requestQueue;
  private final PathMapper<Handler> handlers;

  PumpkinHttpRequestProcessor(BlockingQueue<Socket> requestQueue, PathMapper<Handler> handlers) {
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

          final PathMapper.Entry<Handler> handlerForPath = handlers.get(request.getResource());
          if (handlerForPath == null) {
            LOGGER.warn("No handler found for resource {}", request.getResource());
            HttpResponse.response404(request).send();
          } else {
            invokeHandler(socket, request, handlerForPath);
          }
        } else {
          HttpResponse.response400(new InternalRequest(socket)).send();
        }
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      LOGGER.error("", e);
    }
  }

  private static void invokeHandler(
      Socket socket, PumpkinHttpRequest request, PathMapper.Entry<Handler> handlerForPath) {
    try {
      final Handler handler = handlerForPath.getValue();
      request.setPathParams(handlerForPath.pathParams());
      handler.handle(request);
    } catch (Exception e) {
      LOGGER.trace("Exception occurred during handling, returning 500");
      HttpResponse.response500(new InternalRequest(socket)).send();
    }
  }

  private PumpkinHttpRequest parseRequest(Socket socket) {
    try {
      return PumpkinHttpRequest.from(socket);
    } catch (InvalidHttpRequestException e) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Could not parse http request", e);
      }
      return null;
    }
  }

  // TODO: this is ugly.
  private static class InternalRequest implements HttpRequest {

    private final Socket socket;

    private InternalRequest(Socket socket) {
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

    @Override
    public String getPathParam(String name) {
      return null;
    }
  }
}
