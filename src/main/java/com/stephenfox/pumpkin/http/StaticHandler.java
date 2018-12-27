package com.stephenfox.pumpkin.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used for serving static resources, whether from the resources directory packaged in
 * a jar file or from a directory on the host system.
 *
 * @author Stephen Fox.
 */
public class StaticHandler implements Handler {

  private static final Logger LOGGER = LoggerFactory.getLogger(StaticHandler.class);
  // Based on how this object is constructed we dispatch the method calls to the correct handler.
  private Handler handler;

  public StaticHandler(String path, String directory, boolean filesystem) {
    if (filesystem) {
      this.handler = new StaticFileSystemHandler(path, directory);
    } else {
      this.handler = new StaticResourceHandler(path, directory);
    }
  }

  public StaticHandler(String path, String directory) {
    this(path, directory, false);
  }

  @Override
  public void handle(HttpRequest httpRequest) {
    this.handler.handle(httpRequest);
  }

  @Override
  public String path() {
    return this.handler.path();
  }
}
