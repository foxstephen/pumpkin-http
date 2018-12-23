package com.stephenfox.pumpkin.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

class FileUtils {

  private static final int MAX_BUFFER_SIZE = Integer.MAX_VALUE - 8;
  private static final int DEFAULT_BUFFER_SIZE = 8192;

  static byte[] readAllBytes(InputStream inputStream) throws IOException {
    byte[] buf = new byte[DEFAULT_BUFFER_SIZE];
    int capacity = buf.length;
    int nread = 0;
    int n;
    for (; ; ) {
      // read to EOF which may read more or less than initial buffer size
      while ((n = inputStream.read(buf, nread, capacity - nread)) > 0) nread += n;

      // if the last call to read returned -1, then we're done
      if (n < 0) break;

      // need to allocate a larger buffer
      if (capacity <= MAX_BUFFER_SIZE - capacity) {
        capacity = capacity << 1;
      } else {
        if (capacity == MAX_BUFFER_SIZE)
          throw new OutOfMemoryError("Required array size too large");
        capacity = MAX_BUFFER_SIZE;
      }
      buf = Arrays.copyOf(buf, capacity);
    }
    return (capacity == nread) ? buf : Arrays.copyOf(buf, nread);
  }
}
