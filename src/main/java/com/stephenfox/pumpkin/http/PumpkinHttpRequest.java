package com.stephenfox.pumpkin.http;

import static com.stephenfox.pumpkin.http.Constants.CONTENT_LENGTH;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PumpkinHttpRequest implements HttpRequest {
  private static final Logger LOGGER = LoggerFactory.getLogger(PumpkinHttpRequest.class);
  private final String version;
  private final HttpHeaders headers;
  private final HttpMethod method;
  private final String body;
  private final String resource;
  private final Socket socket;
  private Map<String, String> pathParams;

  static PumpkinHttpRequest from(Socket socket) throws InvalidHttpRequestException {
    String body = null;
    String resource = null;
    HttpMethod method = null;
    String version = null;
    HttpHeaders headers = null;

    try {
      final BufferedReader reader =
          new BufferedReader(new InputStreamReader(socket.getInputStream()));

      // Parse the request line.
      final String[] parsedRequestLine = parseRequestLine(reader);
      method = HttpMethod.valueOf(parsedRequestLine[0]);
      resource = parsedRequestLine[1];
      version = parsedRequestLine[2];

      // Parse the headers.
      headers = parseHeaders(reader);

      // Parse the body.
      body = parseBody(headers, reader);

    } catch (IOException e) {
      LOGGER.error("", e);
    }
    return new PumpkinHttpRequest(socket, version, method, headers, resource, body);
  }

  private static String[] parseRequestLine(BufferedReader reader)
      throws IOException, InvalidHttpRequestException {
    // Parse the request line.
    final String requestLine = reader.readLine();
    if (requestLine == null || requestLine.isEmpty()) {
      throw new InvalidHttpRequestException("Invalid request line - was empty");
    }
    final String[] parsedRequestLine = requestLine.split(" ");
    if (parsedRequestLine.length != 3) {
      throw new InvalidHttpRequestException(
          "Invalid request line " + Arrays.toString(parsedRequestLine));
    }
    return parsedRequestLine;
  }

  private static String parseBody(HttpHeaders headers, BufferedReader reader) throws IOException {
    String body = null;
    // Read the body specified by Content-Length, this also assumes content-length is correct
    // case.
    final String cl = headers.get(CONTENT_LENGTH);
    if (cl != null) {
      final int contentLength = Integer.parseInt(cl);
      final StringBuilder bodyBuilder = new StringBuilder();

      for (int i = 0; i < contentLength; i++) {
        bodyBuilder.append((char) reader.read());
      }

      body = bodyBuilder.toString();
    }
    return body;
  }

  private static HttpHeaders parseHeaders(BufferedReader reader) throws IOException {
    // TODO: This assumes header always present?
    String header = reader.readLine();
    Map<String, String> headersMap = null;

    while (header.length() > 0) {
      final String[] headerPair = header.split(":");
      if (headerPair.length == 2) {
        if (headersMap == null) {
          headersMap = new HashMap<>();
        }
        headersMap.put(headerPair[0], headerPair[1]);
      }

      header = reader.readLine();
    }
    return headersMap == null ? HttpHeaders.empty() : HttpHeaders.from(headersMap);
  }

  private PumpkinHttpRequest(
      Socket socket,
      String version,
      HttpMethod method,
      HttpHeaders headers,
      String resource,
      String body) {
    this.socket = socket;
    this.version = version;
    this.method = method;
    this.headers = headers;
    this.resource = resource;
    this.body = body;
  }

  void setPathParams(Map<String, String> pathParams) {
    this.pathParams = pathParams;
  }

  @Override
  public String getPathParam(String name) {
    if (pathParams != null) {
      return pathParams.get(name);
    }
    return null;
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
  public Socket getSocket() {
    return socket;
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
