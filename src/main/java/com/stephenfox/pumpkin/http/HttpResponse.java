package com.stephenfox.pumpkin.http;

public interface HttpResponse {

  static HttpResponse forRequest(HttpRequest httpRequest) {
    return new PumpkinHttpResponse(httpRequest);
  }

  static HttpResponse response404(HttpRequest httpRequest) {
    return forRequest(httpRequest).setCode(404);
  }

  static HttpResponse response400(HttpRequest httpRequest) {
    return forRequest(httpRequest).setCode(400);
  }

  static HttpResponse response500(HttpRequest httpRequest) {
    return forRequest(httpRequest).setCode(500);
  }

  HttpResponse setHeaders(HttpHeaders headers);

  HttpResponse setBody(String body);

  HttpResponse setBody(byte[] body);

  HttpResponse setCode(int code);

  void send();
}
