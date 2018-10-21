package com.stephenfox.pumpkin.http;

public interface HttpHeaders {
  String get(String header);

  HttpHeaders put(String header, String value);
}
