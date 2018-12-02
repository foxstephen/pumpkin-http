package com.stephenfox.pumpkin.http;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class PumpkinHttpHeaders implements HttpHeaders {

  private final Map<String, String> headersMap;

  static final HttpHeaders EMPTY = new PumpkinHttpHeaders(Collections.emptyMap());

  PumpkinHttpHeaders(Map<String, String> headersMap) {
    this.headersMap = new HashMap<>(headersMap.size());
    for (Map.Entry<String, String> entry : headersMap.entrySet()) {
      set(entry.getKey(), entry.getValue());
    }
  }

  PumpkinHttpHeaders() {
    this.headersMap = new HashMap<>();
  }

  @Override
  public String get(String header) {
    return headersMap.get(header);
  }

  @Override
  public HttpHeaders set(String header, String value) {
    headersMap.put(header.trim(), value.trim());
    return this;
  }

  @Override
  public String format() {
    final StringBuilder builder = new StringBuilder();
    for (Map.Entry<String, String> header : headersMap.entrySet()) {
      builder.append(header.getKey());
      builder.append(":");
      builder.append(header.getValue());
      builder.append("\r\n");
    }
    return builder.toString();
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    for (Map.Entry<String, String> entry : headersMap.entrySet()) {
      builder.append("\n\t\t");
      builder.append(entry);
    }
    return builder.toString();
  }
}
