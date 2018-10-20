package com.stephenfox.pumpkin.http;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

public class PumpkinHttpServer implements HttpServer {

  private static final int THREADS = 1;
  private final ExecutorService threadPool;
  private final Queue<HttpRequest> requests;
  private final int port;
  private final String host;
  private final BlockingQueue<HttpRequest> sharedRequestQueue;

  public PumpkinHttpServer(String host, int port) {
    this.host = host;
    this.port = port;
    this.threadPool = Executors.newCachedThreadPool();
    this.requests = new ArrayDeque<>();
    this.sharedRequestQueue = new LinkedBlockingDeque<>();
  }

  public void start() {
    for (int i = 0; i < THREADS; i++) {
      final HttpSocketListener listener =
          new PumpkinHttpSocketListener(host, port, sharedRequestQueue);
      threadPool.submit(listener::listen);
    }

    System.out.println("Starting threads to listen on " + host + ":" + port);
    while (true) {
      try {
        final HttpRequest request = sharedRequestQueue.take();
        System.out.println("Received request " + request);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
