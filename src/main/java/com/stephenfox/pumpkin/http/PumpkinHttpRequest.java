package com.stephenfox.pumpkin.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class PumpkinHttpRequest implements HttpRequest {
  private static final String METHOD = "method";
  private static final String RESOURCE = "resource";
  private static final String VERSION = "version";
  private static final String KEY = "key";
  private static final String VALUE = "value";
  private static final Pattern httpMethodResourceVersion =
      Pattern.compile("^(?<method>GET|POST)\\s(?<resource>\\/\\S*)\\s(?<version>HTTP\\/\\d.\\d)$");
  private static final Pattern httpHeader = Pattern.compile("(?<key>\\S+):\\s*(?<value>.+)");

  private final String version;
  private final HttpHeaders headers;
  private final HttpMethod method;
  private final String body;
  private final String resource;

  static PumpkinHttpRequest from(BufferedReader reader) {
    String body = null;
    String resource = null;
    HttpMethod method = null;
    HttpHeaders headers = new PumpkinHttpHeaders();
    String version = null;

    String line;
    try {
      line = reader.readLine();
      while (!line.isEmpty()) {
        final Matcher mrvMatcher = httpMethodResourceVersion.matcher(line);
        if (mrvMatcher.matches()) {
          method = HttpMethod.valueOf(mrvMatcher.group(METHOD));
          resource = mrvMatcher.group(RESOURCE);
          version = mrvMatcher.group(VERSION);
          line = reader.readLine();
          continue;
        }

        final Matcher headerMatcher = httpHeader.matcher(line);
        if (headerMatcher.matches()) {
          final String key = headerMatcher.group(KEY);
          final String value = headerMatcher.group(VALUE);
          headers.put(key, value);
        }
        line = reader.readLine();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return new PumpkinHttpRequest(version, method, headers, resource, body);
  }

  private PumpkinHttpRequest(
      String version, HttpMethod method, HttpHeaders headers, String resource, String body) {
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
  public String toString() {
    return "PumpkinHttpRequest{"
        + "version='"
        + version
        + '\''
        + ", headers="
        + headers
        + ", method="
        + method
        + ", body='"
        + body
        + '\''
        + ", resource='"
        + resource
        + '\''
        + '}';
  }
}
