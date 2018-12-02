package com.stephenfox.pumpkin.http;

import java.util.Map;

public interface HttpHeaders {

  static HttpHeaders from(Map<String, String> map) {
    return new PumpkinHttpHeaders(map);
  }

  static HttpHeaders empty() {
    return PumpkinHttpHeaders.EMPTY;
  }

  String get(String header);

  HttpHeaders set(String header, String value);

  String format();
}
