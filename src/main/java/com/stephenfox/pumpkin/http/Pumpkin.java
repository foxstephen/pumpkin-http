package com.stephenfox.pumpkin;

import com.stephenfox.pumpkin.http.HttpServer;
import com.stephenfox.pumpkin.http.PumpkinHttpServer;

public class Pumpkin {
  private Pumpkin() {}

  public static HttpServer httpServer(String host, int port, Class<?> handlerClass) {
    return new PumpkinHttpServer(host, port, handlerClass);
  }
}
