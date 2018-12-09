package com.stephenfox.pumpkin.http;

import static com.stephenfox.pumpkin.http.Constants.CONTENT_TYPE;
import static com.stephenfox.pumpkin.http.Constants.IMAGE_X_ICON;
import static com.stephenfox.pumpkin.http.Constants.TEXT_CSS;
import static com.stephenfox.pumpkin.http.Constants.TEXT_HTML;
import static com.stephenfox.pumpkin.http.Constants.TEXT_JS;
import static com.stephenfox.pumpkin.http.Constants.TEXT_PLAIN;

import java.io.File;
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
    final File file = new File(httpRequest.getResource());
    final String fileName = file.getName();
    final String resourcePath = directory + fileName;

    try {
      final String resourceContents = readResource(resourcePath);
      final HttpHeaders httpHeaders = new PumpkinHttpHeaders();
      httpHeaders.set(CONTENT_TYPE, fileFormatHeader(fileName));
      HttpResponse.forRequest(httpRequest).setBody(resourceContents).setHeaders(httpHeaders).send();
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

  private static String fileFormatHeader(String filename) {
    // Probably not the best way of doing this.
    if (filename.contains(".css")) {
      return TEXT_CSS;
    } else if (filename.contains(".js")) {
      return TEXT_JS;
    } else if (filename.contains(".html")) {
      return TEXT_HTML;
    } else if (filename.contains(".ico")) {
      return IMAGE_X_ICON;
    } else {
      return TEXT_PLAIN;
    }
  }
}
