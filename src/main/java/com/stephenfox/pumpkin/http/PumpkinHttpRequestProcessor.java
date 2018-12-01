package com.stephenfox.pumpkin.http;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PumpkinHttpRequestProcessor implements HttpRequestProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(PumpkinHttpRequestProcessor.class);
  private final BlockingQueue<HttpRequest> requestQueue;
  private final Map<HttpMethod, Map<String, Method>> resourceHandlers;
  private final Object handlerInstance;

  PumpkinHttpRequestProcessor(
      BlockingQueue<HttpRequest> requestQueue,
      Object handlerInstance,
      Map<HttpMethod, Map<String, Method>> resourceHandlers) {
    this.requestQueue = requestQueue;
    this.handlerInstance = handlerInstance;
    this.resourceHandlers = resourceHandlers;
  }

  @Override
  public void run() {
    try {
      while (true) {
        final HttpRequest request = requestQueue.take();
        LOGGER.debug("Received request {}", request);
        final Method method = resourceHandlers.get(request.getMethod()).get(request.getResource());
        if (method == null) {
          HttpResponse.response404(request).send();
        } else {
          invokeHandler(request, method);
        }
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      LOGGER.error("", e);
    }
  }

  private void invokeHandler(HttpRequest request, Method method) {
    try {
      method.invoke(handlerInstance, request);
    } catch (IllegalAccessException | InvocationTargetException e) {
      LOGGER.error(
          "An error occurred while attempting to invoke handler for {}", request.getResource());
      LOGGER.error("", e);
    }
  }
}
