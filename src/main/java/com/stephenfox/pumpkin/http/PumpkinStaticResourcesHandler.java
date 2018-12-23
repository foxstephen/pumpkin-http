package com.stephenfox.pumpkin.http;

import static com.stephenfox.pumpkin.http.Constants.CONTENT_TYPE;
import static com.stephenfox.pumpkin.http.Constants.IMAGE_JPEG;
import static com.stephenfox.pumpkin.http.Constants.IMAGE_PNG;
import static com.stephenfox.pumpkin.http.Constants.IMAGE_X_ICON;
import static com.stephenfox.pumpkin.http.Constants.TEXT_CSS;
import static com.stephenfox.pumpkin.http.Constants.TEXT_HTML;
import static com.stephenfox.pumpkin.http.Constants.TEXT_JS;
import static com.stephenfox.pumpkin.http.Constants.TEXT_PLAIN;
import static com.stephenfox.pumpkin.http.FileUtils.readAllBytes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.NoSuchFileException;

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
      final byte[] resourceContents = readResource(resourcePath);
      final HttpHeaders httpHeaders = new PumpkinHttpHeaders();
      httpHeaders.set(CONTENT_TYPE, fileFormatHeader(fileName));
      HttpResponse.forRequest(httpRequest).setBody(resourceContents).setHeaders(httpHeaders).send();
    } catch (Exception e) {
      if (e instanceof NoSuchFileException) {
        LOGGER.info("File does not exist {}", resourcePath, e);
        HttpResponse.response404(httpRequest).send();
      } else {
        LOGGER.error("A problem occurred reading from {}", resourcePath, e);
        HttpResponse.response500(httpRequest).send();
      }
    }
  }

  @Override
  public String path() {
    return path;
  }

  private InputStream getResourceStream(String path) throws IOException {
    final InputStream inputStream = classLoader.getResourceAsStream(path);
    if (inputStream == null) {
      throw new IOException("Could not retrieve resource, resource url was null");
    }
    return inputStream;
  }

  private byte[] readResource(String path) throws IOException {
    final InputStream resourcesStream = getResourceStream(path);
    // This is a naive approach to just reading and returning
    // all the bytes for the file. This file could in fact be quite large
    // etc, which may warrant a different Handler
    // and assume that this handler only reads 'small' files.
    // To implement handling for much larger files shouldn't be
    // a massive undertaking.
    return readAllBytes(resourcesStream);
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
    } else if (filename.contains(".png")) {
      return IMAGE_PNG;
    } else if (filename.contains(".jpeg") || filename.contains(".jpg")) {
      return IMAGE_JPEG;
    } else {
      return TEXT_PLAIN;
    }
  }
}
