package com.stephenfox.pumpkin.http;

import java.io.IOException;
import java.io.OutputStream;

class PumpkinHttpResponse implements HttpResponse {
  private final OutputStream outputStream;
  private HttpHeaders headers;
  private String body;
  private int code;

  PumpkinHttpResponse(HttpRequest request) {
    this.outputStream = request.getConnection();
  }

  @Override
  public HttpResponse setBody(String body) {
    this.body = body;
    return this;
  }

  @Override
  public HttpResponse setHeaders(HttpHeaders headers) {
    this.headers = headers;
    return this;
  }

  @Override
  public HttpResponse setCode(int code) {
    this.code = code;
    return this;
  }

  @Override
  public void send() {
    try {
      outputStream.write(prepare());
      outputStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private byte[] prepare() {
    addDefaultHeaders();
    return ("HTTP/1.1 " + code + " " + reason(code) + "\r\n" + headers + "\r\n\r\n" + body)
        .getBytes();
  }

  private static String reason(int code) {
    if (code == 404) {
      return "Not found";
    } else if (code == 200) {
      return "OK";
    } else {
      return "No reason";
    }
  }

  private void addDefaultHeaders() {
    if (headers == null) {
      headers = new PumpkinHttpHeaders();
    }
    if (headers.get("Content-Length") == null) {
      if (body != null) {
        headers.put("Content-Length", String.valueOf(body.length()));
      } else {
        headers.put("Content-Length", String.valueOf(0));
      }
    }
  }
}
