package com.stephenfox.pumpkin.http;

import static com.stephenfox.pumpkin.http.Constants.CONNECTION;
import static com.stephenfox.pumpkin.http.Constants.CONTENT_LENGTH;

import java.io.DataOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PumpkinHttpResponse implements HttpResponse {
  private static final Logger LOGGER = LoggerFactory.getLogger(PumpkinHttpResponse.class);
  private final DataOutputStream outputStream;
  private HttpHeaders headers;
  private String body;
  private int code;

  PumpkinHttpResponse(HttpRequest request) {
    this.outputStream = new DataOutputStream(request.getConnection());
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
      outputStream.writeBytes(prepare());
      outputStream.close();
    } catch (IOException e) {
      LOGGER.error("", e);
    }
  }

  private String prepare() {
    addDefaultHeaders();
    return ("HTTP/1.1 " + code + " " + reason(code) + "\r\n" + headers.format() + "\r\n" + body);
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
    if (headers.get(CONTENT_LENGTH) == null) {
      if (body != null) {
        headers.set(CONTENT_LENGTH, String.valueOf(body.length()));
      } else {
        headers.set(CONTENT_LENGTH, String.valueOf(0));
      }
    }

    headers.set(CONNECTION, "close");
  }
}
