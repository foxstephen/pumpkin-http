package com.stephenfox.pumpkin.http;

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
  private final Class<?> handlerClass;
  private Object instance;
  private Map<String, Method> resourceHandlers;

  PumpkinHttpServer(String host, int port, Class<?> handlerClass) {
    this.host = host;
    this.port = port;
    this.handlerClass = handlerClass;
    this.resourceHandlers = new HashMap<>();
    this.threadPool = Executors.newCachedThreadPool(new PumpkinThreadFactory());
    this.sharedRequestQueue = new LinkedBlockingQueue<>();
    this.parseHandler();
  }

  @Override
  public void start() {
    LOGGER.info("Starting with {} thread(s); Listening on {}:{}", THREADS, host, port);

    for (int i = 0; i < THREADS; i++) {
      final HttpRequestProcessor requestProcessor =
          new PumpkinHttpRequestProcessor(sharedRequestQueue, instance, resourceHandlers);
      threadPool.execute(requestProcessor::run);
    }

    final HttpSocketListener listener =
        new PumpkinHttpSocketListener(host, port, sharedRequestQueue);
    listener.listen();
  }

  // Parse all endpoints from the method class.
  private void parseHandler() {
    // TODO: This method needs to be refactored LOLOLOL.
    try {
      final Constructor<?> constructor = handlerClass.getConstructor(null);
      this.instance = constructor.newInstance(null);
    } catch (NoSuchMethodException
        | IllegalAccessException
        | InstantiationException
        | InvocationTargetException e) {
      LOGGER.error("", e);
    }

    final List<Class<? extends Annotation>> httpMethodAnnotations =
        Arrays.asList(
            Options.class,
            Get.class,
            Head.class,
            Post.class,
            Put.class,
            Delete.class,
            Trace.class,
            Connect.class);

    for (Class<? extends Annotation> httpMethodClass : httpMethodAnnotations) {
      final List<? extends Reflection.MethodAnnotationPair<? extends Annotation>> methods =
          Reflection.getMethodsWithAnnotation(httpMethodClass, handlerClass);

      for (Reflection.MethodAnnotationPair<? extends Annotation> methodAnnotationPair : methods) {
        final Method method = methodAnnotationPair.getMethod();
        final Annotation annotation = methodAnnotationPair.getAnnotation();

        if (annotation instanceof Options) {
          final String resource = ((Options) annotation).resource();
          resourceHandlers.put(resource, method);
        } else if (annotation instanceof Get) {
          final String resource = ((Get) annotation).resource();
          resourceHandlers.put(resource, method);
        } else if (annotation instanceof Head) {
          final String resource = ((Head) annotation).resource();
          resourceHandlers.put(resource, method);
        } else if (annotation instanceof Post) {
          final String resource = ((Post) annotation).resource();
          resourceHandlers.put(resource, method);
        } else if (annotation instanceof Put) {
          final String resource = ((Put) annotation).resource();
          resourceHandlers.put(resource, method);
        } else if (annotation instanceof Delete) {
          final String resource = ((Delete) annotation).resource();
          resourceHandlers.put(resource, method);
        } else if (annotation instanceof Trace) {
          final String resource = ((Trace) annotation).resource();
          resourceHandlers.put(resource, method);
        } else if (annotation instanceof Connect) {
          final String resource = ((Connect) annotation).resource();
          resourceHandlers.put(resource, method);
        } else {
          throw new IllegalArgumentException(
              "Unknown http method for annotation class" + annotation);
        }
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
