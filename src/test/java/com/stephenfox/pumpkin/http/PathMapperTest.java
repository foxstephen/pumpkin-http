package com.stephenfox.pumpkin.http;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PathMapperTest {

  @Test
  public void test() {
    final PathMapper<Object> pathMapper = new PathMapper<>();
    final Object value = new Object();
    pathMapper.put("/api/{foo}/fizz/{bar}", value);
    final PathMapper.Entry entry = pathMapper.get("/api/fooValue/fizz/barValue");

    assertEquals("fooValue", entry.pathParams().get("foo"));
    assertEquals("barValue", entry.pathParams().get("bar"));

    assertEquals(value, entry.getValue());
  }
}
