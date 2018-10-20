package com.stephenfox.pumpkin;

import com.stephenfox.pumpkin.http.HttpServer;
import com.stephenfox.pumpkin.http.PumpkinHttpServer;

public class Pumpkin {
  public static HttpServer httpServer() {
    return new PumpkinHttpServer("127.0.0.1", 8080);
  }

  public static void main(String[] args) {
    Pumpkin.httpServer().start();
  }
}
