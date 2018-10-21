package com.stephenfox.pumpkin.http;

import java.io.OutputStream;

public interface HttpRequest {
  String getVersion();

  HttpHeaders getHeaders();

  HttpMethod getMethod();

  String getResource();

  String getBody();

  OutputStream getConnection();
}
