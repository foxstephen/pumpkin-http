package com.stephenfox.pumpkin.http;

public interface HttpHeaders {
  String get(String header);

  void put(String header, String value);
}
