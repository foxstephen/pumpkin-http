package com.stephenfox.pumpkin.http;

import java.net.Socket;

public interface HttpRequest {
  String getVersion();

  HttpHeaders getHeaders();

  HttpMethod getMethod();

  String getResource();

  String getBody();

  // TODO: Probably not a good idea to expose this here as anyone can muck with it.
  Socket getSocket();
}
