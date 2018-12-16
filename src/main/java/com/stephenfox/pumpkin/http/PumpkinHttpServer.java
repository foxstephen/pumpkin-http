package com.stephenfox.pumpkin.http;

import static com.stephenfox.pumpkin.http.Constants.PUMPKIN;

import com.stephenfox.pumpkin.http.method.Connect;
import com.stephenfox.pumpkin.http.method.Delete;
import com.stephenfox.pumpkin.http.method.Get;
import com.stephenfox.pumpkin.http.method.Head;
import com.stephenfox.pumpkin.http.method.Options;
import com.stephenfox.pumpkin.http.method.Post;
import com.stephenfox.pumpkin.http.method.Put;
import com.stephenfox.pumpkin.http.method.Trace;
import com.stephenfox.pumpkin.http.reflection.Reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PumpkinHttpServer implements HttpServer {
  private static final Logger LOGGER = LoggerFactory.getLogger(PumpkinHttpServer.class);
  private static final int THREADS = 4;
  private final ExecutorService threadPool;
  private final int port;
  private final String host;
  private final BlockingQueue<Socket> sharedRequestQueue;
  private Class<?> classToForwardRequestsTo;
  private Object instanceToForwardRequestsTo;
  private PathMapper<Handler> handlers;

  PumpkinHttpServer(String host, int port, Class<?> classToForwardRequestsTo) {
    this(host, port);
    this.classToForwardRequestsTo = classToForwardRequestsTo;
  }

  PumpkinHttpServer(String host, int port, Object instanceToForwardRequestsTo) {
    this(host, port);
    this.instanceToForwardRequestsTo = instanceToForwardRequestsTo;
  }

  private PumpkinHttpServer(String host, int port) {
    this.host = host;
    this.port = port;
    this.handlers = new PathMapper<>();
    this.threadPool = Executors.newCachedThreadPool(new PumpkinThreadFactory());
    this.sharedRequestQueue = new LinkedBlockingQueue<>();
  }

  @Override
  public void start() {
    this.parseHttpMethodMappings();
    System.out.println(PUMPKIN);
    LOGGER.info("Starting with {} thread(s); Listening on {}:{}", THREADS, host, port);

    for (int i = 0; i < THREADS; i++) {
      final HttpRequestProcessor requestProcessor =
          new PumpkinHttpRequestProcessor(sharedRequestQueue, handlers);
      threadPool.execute(requestProcessor::run);
    }

    final HttpSocketListener listener =
        new PumpkinHttpSocketListener(host, port, sharedRequestQueue);
    listener.listen();
  }

  @Override
  public HttpServer addHandler(Handler handler) {
    this.handlers.put(handler.path(), handler);
    return this;
  }

  private void parseHttpMethodMappings() {
    createInstanceToForwardTo();

    for (Class<? extends Annotation> httpMethodAnnotation : HttpMethod.all) {
      final List<? extends Reflection.MethodAnnotationPair<? extends Annotation>> methods =
          Reflection.getMethodsWithAnnotation(
              httpMethodAnnotation, instanceToForwardRequestsTo.getClass());

      for (Reflection.MethodAnnotationPair<? extends Annotation> methodAnnotationPair : methods) {
        final Method method = methodAnnotationPair.getMethod();
        final Annotation annotation = methodAnnotationPair.getAnnotation();

        final Handler handler = new PumpkinForwardingHandler(instanceToForwardRequestsTo, method);
        if (annotation instanceof Options) {
          final String resource = ((Options) annotation).resource();
          handlers.put(resource, handler);
        } else if (annotation instanceof Get) {
          final String resource = ((Get) annotation).resource();
          handlers.put(resource, handler);
        } else if (annotation instanceof Head) {
          final String resource = ((Head) annotation).resource();
          handlers.put(resource, handler);
        } else if (annotation instanceof Post) {
          final String resource = ((Post) annotation).resource();
          handlers.put(resource, handler);
        } else if (annotation instanceof Put) {
          final String resource = ((Put) annotation).resource();
          handlers.put(resource, handler);
        } else if (annotation instanceof Delete) {
          final String resource = ((Delete) annotation).resource();
          handlers.put(resource, handler);
        } else if (annotation instanceof Trace) {
          final String resource = ((Trace) annotation).resource();
          handlers.put(resource, handler);
        } else if (annotation instanceof Connect) {
          final String resource = ((Connect) annotation).resource();
          handlers.put(resource, handler);
        } else {
          throw new IllegalArgumentException(
              "Unknown http method for annotation class" + annotation);
        }
      }
    }
  }

  private void createInstanceToForwardTo() {
    if (instanceToForwardRequestsTo == null) {
      try {
        final Constructor<?> constructor = classToForwardRequestsTo.getConstructor(null);
        this.instanceToForwardRequestsTo = constructor.newInstance(null);
      } catch (NoSuchMethodException
          | IllegalAccessException
          | InstantiationException
          | InvocationTargetException e) {
        LOGGER.error("", e);
        throw new RuntimeException(e);
      }
    }
  }

  private class PumpkinThreadFactory implements ThreadFactory {
    private static final String THREAD_PREFIX = "pumpkin-thread-";
    private int numberOfThreads = 0;

    @Override
    public Thread newThread(Runnable r) {
      return new Thread(r, THREAD_PREFIX + (++numberOfThreads));
    }
  }
}
