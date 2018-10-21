package com.stephenfox.pumpkin.http;

import static com.stephenfox.pumpkin.http.HttpMethod.CONNECT;
import static com.stephenfox.pumpkin.http.HttpMethod.DELETE;
import static com.stephenfox.pumpkin.http.HttpMethod.GET;
import static com.stephenfox.pumpkin.http.HttpMethod.HEAD;
import static com.stephenfox.pumpkin.http.HttpMethod.OPTIONS;
import static com.stephenfox.pumpkin.http.HttpMethod.POST;
import static com.stephenfox.pumpkin.http.HttpMethod.PUT;
import static com.stephenfox.pumpkin.http.HttpMethod.TRACE;

import com.stephenfox.pumpkin.http.handler.Connect;
import com.stephenfox.pumpkin.http.handler.Delete;
import com.stephenfox.pumpkin.http.handler.Get;
import com.stephenfox.pumpkin.http.handler.Head;
import com.stephenfox.pumpkin.http.handler.Options;
import com.stephenfox.pumpkin.http.handler.Post;
import com.stephenfox.pumpkin.http.handler.Put;
import com.stephenfox.pumpkin.http.handler.Trace;
import com.stephenfox.pumpkin.http.reflection.Reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PumpkinHttpServer implements HttpServer {
  private static final Logger LOGGER = LoggerFactory.getLogger(PumpkinHttpServer.class);
  private static final int THREADS = 1;
  private final ExecutorService threadPool;
  private final Queue<HttpRequest> requests;
  private final int port;
  private final String host;
  private final BlockingQueue<HttpRequest> sharedRequestQueue;
  private final Class<?> handlerClass;
  private Object instance;
  private Map<HttpMethod, Map<String, Method>> resourceHandlers;

  public PumpkinHttpServer(String host, int port, Class<?> handlerClass) {
    this.host = host;
    this.port = port;
    this.handlerClass = handlerClass;
    this.resourceHandlers = new HashMap<>();
    this.threadPool = Executors.newCachedThreadPool();
    this.requests = new ArrayDeque<>();
    this.sharedRequestQueue = new LinkedBlockingDeque<>();
    this.parseHandler();
  }

  public void start() {
    for (int i = 0; i < THREADS; i++) {
      final HttpSocketListener listener =
          new PumpkinHttpSocketListener(host, port, sharedRequestQueue);
      threadPool.submit(listener::listen);
    }
    LOGGER.info("Starting {} thread(s) to listen on {}:{}", THREADS, host, port);

    while (true) {
      try {
        final HttpRequest request = sharedRequestQueue.take();
        LOGGER.debug("Received request " + request);

        final Method method = resourceHandlers.get(request.getMethod()).get(request.getResource());
        if (method == null) {
          HttpResponse.response404(request).send();
        } else {
          method.invoke(instance, request);
        }

      } catch (InterruptedException | IllegalAccessException | InvocationTargetException e) {
        e.printStackTrace();
      }
    }
  }

  private void parseHandler() {
    try {
      final Constructor<?> constructor = handlerClass.getConstructor(null);
      this.instance = constructor.newInstance(null);
    } catch (NoSuchMethodException
        | IllegalAccessException
        | InstantiationException
        | InvocationTargetException e) {
      e.printStackTrace();
    }

    for (HttpMethod httpMethod : HttpMethod.values()) {
      resourceHandlers.put(httpMethod, new HashMap<>());
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
      final Optional<? extends Reflection.MethodAnnotationPair<? extends Annotation>>
          methodAnnotation = Reflection.getMethodAnnotation(httpMethodClass, handlerClass);

      if (methodAnnotation.isPresent()) {
        final Reflection.MethodAnnotationPair<?> getMethodAnnotationPair = methodAnnotation.get();
        final Method method = getMethodAnnotationPair.getMethod();
        final Annotation annotation = getMethodAnnotationPair.getAnnotation();

        if (annotation instanceof Options) {
          resourceHandlers.get(OPTIONS).put(((Options) annotation).resource(), method);
        } else if (annotation instanceof Get) {
          resourceHandlers.get(GET).put(((Get) annotation).resource(), method);
        } else if (annotation instanceof Head) {
          resourceHandlers.get(HEAD).put(((Head) annotation).resource(), method);
        } else if (annotation instanceof Post) {
          resourceHandlers.get(POST).put(((Post) annotation).resource(), method);
        } else if (annotation instanceof Put) {
          resourceHandlers.get(PUT).put(((Put) annotation).resource(), method);
        } else if (annotation instanceof Delete) {
          resourceHandlers.get(DELETE).put(((Delete) annotation).resource(), method);
        } else if (annotation instanceof Trace) {
          resourceHandlers.get(TRACE).put(((Trace) annotation).resource(), method);
        } else if (annotation instanceof Connect) {
          resourceHandlers.get(CONNECT).put(((Connect) annotation).resource(), method);
        } else {
          throw new IllegalArgumentException(
              "Unknown http method for annotation class" + annotation);
        }
      }
    }
  }
}
