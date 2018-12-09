package com.stephenfox.pumpkin.http;

class InvalidHttpRequestException extends Exception {
  InvalidHttpRequestException(String message) {
    super(message);
  }
}
