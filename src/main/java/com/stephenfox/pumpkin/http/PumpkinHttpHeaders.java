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
  public void put(String header, String value) {
    headersMap.put(header, value);
  }
}
