package com.stephenfox.pumpkin.http;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PumpkinHttpRequestProcessor implements HttpRequestProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(PumpkinHttpRequestProcessor.class);
  private final BlockingQueue<Socket> requestQueue;
  private final Map<HttpMethod, Map<String, Method>> resourceHandlers;
  private final Object handlerInstance;

  PumpkinHttpRequestProcessor(
      BlockingQueue<Socket> requestQueue,
      Object handlerInstance,
      Map<HttpMethod, Map<String, Method>> resourceHandlers) {
    this.requestQueue = requestQueue;
    this.handlerInstance = handlerInstance;
    this.resourceHandlers = resourceHandlers;
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

          final Method method =
              resourceHandlers.get(request.getMethod()).get(request.getResource());
          if (method == null) {
            HttpResponse.response404(request).send();
          } else {
            invokeHandler(request, method);
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

  private void invokeHandler(HttpRequest request, Method method) {
    try {
      method.invoke(handlerInstance, request);
    } catch (IllegalAccessException | InvocationTargetException e) {
      LOGGER.error(
          "An error occurred while attempting to invoke method for {}", request.getResource());
      LOGGER.error("", e);
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
