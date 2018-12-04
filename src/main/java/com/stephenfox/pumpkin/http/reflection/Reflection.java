package com.stephenfox.pumpkin.http.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Reflection {

  private Reflection() {}

  public static class MethodAnnotationPair<T extends Annotation> {
    private Method method;
    private T annotation;

    MethodAnnotationPair(Method method, T annotation) {
      this.method = method;
      this.annotation = annotation;
    }

    public Method getMethod() {
      return method;
    }

    public T getAnnotation() {
      return annotation;
    }
  }

  public static <T extends Annotation> List<MethodAnnotationPair<T>> getMethodsWithAnnotation(
      Class<T> annotationClass, Class<?> clazz) {

    final List<MethodAnnotationPair<T>> pairs = new ArrayList<>();
    for (Method m : clazz.getDeclaredMethods()) {
      final T annotation = m.getAnnotation(annotationClass);

      if (annotation != null) {
        pairs.add(new MethodAnnotationPair<>(m, annotation));
      }
    }
    return pairs;
  }
}
