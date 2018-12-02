package com.stephenfox.pumpkin.http;

class InvalidHttpRequest extends Exception {
  InvalidHttpRequest(String message) {
    super(message);
  }
}
