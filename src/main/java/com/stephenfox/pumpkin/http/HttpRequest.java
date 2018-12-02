package com.stephenfox.pumpkin.http;

import java.net.Socket;

public interface HttpRequest {
  String getVersion();

  HttpHeaders getHeaders();

  HttpMethod getMethod();

  String getResource();

  String getBody();

  Socket getSocket();
}
