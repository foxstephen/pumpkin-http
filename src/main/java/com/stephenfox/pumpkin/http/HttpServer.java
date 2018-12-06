package com.stephenfox.pumpkin.http;

public interface HttpServer {

  void start();

  HttpServer addHandler(Handler handler);
}
