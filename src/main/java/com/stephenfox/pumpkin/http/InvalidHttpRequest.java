package com.stephenfox.pumpkin.http;

class InvalidHttpRequest extends RuntimeException {
  InvalidHttpRequest(String message) {
    super(message);
  }
}
