package com.stephenfox.pumpkin.http;

public interface HttpRequest {
  String getVersion();

  HttpHeaders getHeaders();

  HttpMethod getMethod();

  String getResource();

  String getBody();
}
