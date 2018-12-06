package com.stephenfox.pumpkin.http;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This handler just forwards requests to a method via reflection.
 *
 * @author Stephen Fox.
 */
class PumpkinForwardingHandler implements Handler {

  private static final Logger LOGGER = LoggerFactory.getLogger(PumpkinForwardingHandler.class);
  private final Object classToForwardTo;
  private final Method method;

  PumpkinForwardingHandler(Object classToForwardTo, Method method) {
    this.classToForwardTo = classToForwardTo;
    this.method = method;
  }

  @Override
  public void handle(HttpRequest httpRequest) {
    invokeHandler(httpRequest);
  }

  @Override
  public String path() {
    return null;
  }

  // Forwards the http request to a method via reflection.
  private void invokeHandler(HttpRequest request) {
    try {
      method.invoke(classToForwardTo, request);
    } catch (IllegalAccessException | InvocationTargetException e) {
      LOGGER.error(
          "An error occurred while attempting to invoke method for {}, returning 500",
          request.getResource());
      LOGGER.error("", e);
      HttpResponse.forRequest(request).setCode(500).send();
    }
  }
}
