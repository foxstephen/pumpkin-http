package com.stephenfox.pumpkin.http;

import com.stephenfox.pumpkin.http.method.Connect;
import com.stephenfox.pumpkin.http.method.Delete;
import com.stephenfox.pumpkin.http.method.Get;
import com.stephenfox.pumpkin.http.method.Head;
import com.stephenfox.pumpkin.http.method.Options;
import com.stephenfox.pumpkin.http.method.Post;
import com.stephenfox.pumpkin.http.method.Put;
import com.stephenfox.pumpkin.http.method.Trace;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

public enum HttpMethod {
  OPTIONS,
  GET,
  HEAD,
  POST,
  PUT,
  DELETE,
  TRACE,
  CONNECT;

  static final List<Class<? extends Annotation>> all =
      Arrays.asList(
          Options.class,
          Get.class,
          Head.class,
          Post.class,
          Put.class,
          Delete.class,
          Trace.class,
          Connect.class);
}
