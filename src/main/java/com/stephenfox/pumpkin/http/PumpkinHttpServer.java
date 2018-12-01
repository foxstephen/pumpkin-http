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
import java.util.Arrays;
import java.util.EnumMap;
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

public class PumpkinHttpServer implements HttpServer {
  private static final Logger LOGGER = LoggerFactory.getLogger(PumpkinHttpServer.class);
  private static final int THREADS = 4;
  private final ExecutorService threadPool;
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
    this.resourceHandlers = new EnumMap<>(HttpMethod.class);
    this.threadPool = Executors.newCachedThreadPool(new PumpkinThreadFactory());
    this.sharedRequestQueue = new LinkedBlockingQueue<>();
    this.parseHandler();
  }

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

  // Parse all endpoints from the handler class.
  private void parseHandler() {
    try {
      final Constructor<?> constructor = handlerClass.getConstructor(null);
      this.instance = constructor.newInstance(null);
    } catch (NoSuchMethodException
        | IllegalAccessException
        | InstantiationException
        | InvocationTargetException e) {
      LOGGER.error("", e);
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

  private class PumpkinThreadFactory implements ThreadFactory {
    private static final String THREAD_PREFIX = "pumpkin-thread-";
    private int numberOfThreads = 0;

    @Override
    public Thread newThread(Runnable r) {
      return new Thread(r, THREAD_PREFIX + (++numberOfThreads));
    }
  }
}
