package com.stephenfox.pumpkin.http;

import java.io.InputStream;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PumpkinStaticResourcesHandler implements Handler {

  private static final Logger LOGGER = LoggerFactory.getLogger(PumpkinStaticResourcesHandler.class);
  private final String directory;
  private final ClassLoader classLoader;
  private final String path;

  public PumpkinStaticResourcesHandler(String path, String directory) {
    this.path = path;
    this.directory = directory;
    this.classLoader = getClass().getClassLoader();
  }

  @Override
  public void handle(HttpRequest httpRequest) {
    final String resourcePath = httpRequest.getResource();
    try {
      final String resourceContents = readResource(resourcePath.substring(1));
      HttpResponse.forRequest(httpRequest).setBody(resourceContents).send();
    } catch (Exception e) {
      LOGGER.warn("A problem occurred reading from {}", resourcePath, e);
      HttpResponse.response500(httpRequest).send();
    }
  }

  @Override
  public String path() {
    return path;
  }

  private InputStream getResource(String path) {
    final InputStream resourceAsStream = classLoader.getResourceAsStream(path);
    if (resourceAsStream == null) {
      throw new RuntimeException("Could not retrieve resource");
    }
    return resourceAsStream;
  }

  private String readResource(String path) {
    final InputStream resource = getResource(path);

    final StringBuilder contents = new StringBuilder();
    try (Scanner scanner = new Scanner(resource)) {
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        contents.append(line).append("\n");
      }
      return contents.toString();
    }
  }
}
