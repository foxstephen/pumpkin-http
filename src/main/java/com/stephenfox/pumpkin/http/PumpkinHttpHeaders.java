package com.stephenfox.pumpkin.http;

import java.util.HashMap;
import java.util.Map;

public class PumpkinHttpHeaders implements HttpHeaders {

  private final Map<String, String> headersMap;

  PumpkinHttpHeaders() {
    this.headersMap = new HashMap<>();
  }

  @Override
  public String get(String header) {
    return headersMap.get(header);
  }

  @Override
  public HttpHeaders set(String header, String value) {
    headersMap.put(header, value);
    return this;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    for (Map.Entry<String, String> header : headersMap.entrySet()) {
      builder.append(header.getKey());
      builder.append(":");
      builder.append(header.getValue());
      builder.append("\r\n");
    }

    return builder.toString();
  }
}
