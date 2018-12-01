package com.stephenfox.pumpkin.http;

public interface HttpHeaders {
  String get(String header);

  HttpHeaders set(String header, String value);

  String format();
}
