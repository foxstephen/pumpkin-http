package com.stephenfox.pumpkin.http.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;

public class Reflection {

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

  public static <T extends Annotation> Optional<MethodAnnotationPair<T>> getMethodAnnotation(
      Class<T> annotationClass, Class<?> clazz) {

    for (Method m : clazz.getDeclaredMethods()) {
      final T annotation = m.getAnnotation(annotationClass);

      if (annotation != null) {
        return Optional.of(new MethodAnnotationPair<>(m, annotation));
      }
    }
    return Optional.empty();
  }
}
