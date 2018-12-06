package com.stephenfox.pumpkin.http;

/**
 * Handler can be used for intercepting and handling requests before they reach their target, as
 * middleware as such.
 *
 * @author Stephen Fox.
 */
public interface Handler {

  /**
   * A method to forward the {@code HttpRequest} to.
   *
   * @param httpRequest The http request.
   */
  void handle(HttpRequest httpRequest);

  /**
   * The path for which this handler is mapped. For example if this handle is mapped to
   * `/api/resource/foo` all requests matching that path will be forwarded here.
   *
   * @return The path.
   */
  String path();
}
