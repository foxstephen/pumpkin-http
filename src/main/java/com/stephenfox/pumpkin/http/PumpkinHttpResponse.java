package com.stephenfox.pumpkin.http;

import static com.stephenfox.pumpkin.http.Constants.CLOSE;
import static com.stephenfox.pumpkin.http.Constants.CONNECTION;
import static com.stephenfox.pumpkin.http.Constants.CONTENT_LENGTH;
import static com.stephenfox.pumpkin.http.Constants.INTERNAL_SERVER_ERROR;
import static com.stephenfox.pumpkin.http.Constants.NOT_FOUND;
import static com.stephenfox.pumpkin.http.Constants.NO_REASON;
import static com.stephenfox.pumpkin.http.Constants.OK;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PumpkinHttpResponse implements HttpResponse {
  private static final Logger LOGGER = LoggerFactory.getLogger(PumpkinHttpResponse.class);
  private OutputStream outputStream;
  private final Socket socket;
  private HttpHeaders headers;
  private String body;
  private int code;

  PumpkinHttpResponse(HttpRequest request) {
    this.socket = request.getSocket();
    try {
      this.outputStream = new DataOutputStream(request.getSocket().getOutputStream());
    } catch (IOException e) {
      LOGGER.error("", e);
    }
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
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Writing response to client");
      }

      outputStream.write(prepare());

      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Response written to client, will close connection");
      }
    } catch (IOException e) {
      LOGGER.error("An error occurred sending message to client", e);
    } finally {
      close();
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Connection closed");
      }
    }
  }

  private void close() {
    try {
      socket.close();
    } catch (IOException e) {
      LOGGER.error("Could not close socket", e);
    }
  }

  private byte[] prepare() {
    addDefaultHeaders();
    if (code == 0) {
      code = 200;
    }
    return ("HTTP/1.1 " + code + " " + reason(code) + "\r\n" + headers.format() + "\r\n" + body)
        .getBytes();
  }

  private static String reason(int code) {
    if (code == 404) {
      return NOT_FOUND;
    } else if (code == 200) {
      return OK;
    } else if (code == 500) {
      return INTERNAL_SERVER_ERROR;
    } else {
      return NO_REASON;
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

    headers.set(CONNECTION, CLOSE);
  }
}
