package com.stephenfox.pumpkin.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PumpkinHttpRequest implements HttpRequest {
  private static final Pattern httpHeader = Pattern.compile("(?<key>\\S+):\\s*(?<value>.+)");
  private static final Logger LOGGER = LoggerFactory.getLogger(PumpkinHttpRequest.class);
  private final String version;
  private final HttpHeaders headers;
  private final HttpMethod method;
  private final String body;
  private final String resource;
  private final OutputStream outputStream;

  static PumpkinHttpRequest from(BufferedReader reader, OutputStream outputStream) {
    String body = null;
    String resource = null;
    HttpMethod method = null;
    // TODO: This inits an empty HashMap for each request even if there's no headers.
    HttpHeaders headers = new PumpkinHttpHeaders();
    String version = null;

    try {
      // Parse the request line.
      final String requestLine = reader.readLine();
      if (requestLine == null || requestLine.isEmpty()) {
        throw new InvalidHttpRequest("Invalid request line");
      }
      final String[] parsedRequestLine = requestLine.split(" "); // TODO: precompile regex?
      if (parsedRequestLine.length != 3) {
        throw new IllegalArgumentException("Invalid request line");
      }
      method = HttpMethod.valueOf(parsedRequestLine[0]);
      resource = parsedRequestLine[1];
      version = parsedRequestLine[2];

      // Parse the headers.
      // TODO: This assumes header always present?
      String header = reader.readLine();
      while (header.length() > 0) {
        final Matcher matcher = httpHeader.matcher(header);
        if (matcher.matches()) {
          headers.set(matcher.group("key"), matcher.group("value"));
        }

        header = reader.readLine();
      }

      // Read the body specified by Content-Length
      final String cl = headers.get("Content-Length");
      if (cl != null) {
        final int contentLength = Integer.parseInt(cl);
        final StringBuilder bodyBuilder = new StringBuilder();

        for (int i = 0; i < contentLength; i++) {
          bodyBuilder.append((char) reader.read());
        }

        body = bodyBuilder.toString();
      }

    } catch (IOException e) {
      LOGGER.error("", e);
    }

    return new PumpkinHttpRequest(outputStream, version, method, headers, resource, body);
  }

  private PumpkinHttpRequest(
      OutputStream outputStream,
      String version,
      HttpMethod method,
      HttpHeaders headers,
      String resource,
      String body) {
    this.outputStream = outputStream;
    this.version = version;
    this.method = method;
    this.headers = headers;
    this.resource = resource;
    this.body = body;
  }

  @Override
  public String getVersion() {
    return version;
  }

  @Override
  public HttpHeaders getHeaders() {
    return headers;
  }

  @Override
  public HttpMethod getMethod() {
    return method;
  }

  @Override
  public String getResource() {
    return resource;
  }

  @Override
  public String getBody() {
    return body;
  }

  @Override
  public OutputStream getConnection() {
    return outputStream;
  }

  @Override
  public String toString() {
    return "PumpkinHttpRequest{"
        + "\n\tversion='"
        + version
        + "',"
        + "\n\theaders="
        + headers
        + ",\n\tmethod="
        + method
        + ",\n\tbody='"
        + body
        + '\''
        + ",\n\tresource='"
        + resource
        + '\''
        + "\n}";
  }
}
