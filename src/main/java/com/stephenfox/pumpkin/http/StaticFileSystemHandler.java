package com.stephenfox.pumpkin.http;

import static com.stephenfox.pumpkin.http.FileUtil.readAllBytes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class StaticFileSystemHandler implements Handler {
  private static final Logger LOGGER = LoggerFactory.getLogger(StaticFileSystemHandler.class);
  private final String path;
  private final String directory;

  StaticFileSystemHandler(String path, String directory) {
    this.path = path;
    this.directory = directory;
  }

  @Override
  public void handle(HttpRequest httpRequest) {
    final String filename = new File(httpRequest.getResource()).getName();
    final File file = new File(directory + filename);
    try {
      final byte[] fileContents = readFile(file);
      HttpResponse.forRequest(httpRequest).setBody(fileContents).send();
    } catch (IOException e) {
      if (e instanceof FileNotFoundException) {
        LOGGER.info("File does not exist {}", file.getAbsolutePath(), e);
        HttpResponse.response404(httpRequest).send();
      } else {
        LOGGER.error("A problem occurred reading from {}", file.getAbsolutePath(), e);
        HttpResponse.response500(httpRequest).send();
      }
    }
  }

  @Override
  public String path() {
    return path;
  }

  private byte[] readFile(File file) throws IOException {
    return readAllBytes(new FileInputStream(file));
  }
}
