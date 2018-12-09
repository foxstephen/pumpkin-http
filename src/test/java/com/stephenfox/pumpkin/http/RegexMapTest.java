package com.stephenfox.pumpkin.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class RegexMapTest {

  @Test
  public void testGet() {
    final RegexMap<String> regexMap = new RegexMap<>();
    regexMap.put("\\/app\\/\\d*\\/resources", "1");

    final String result = regexMap.get("/app/1/resources");
    assertEquals("1", result);
  }

  @Test
  public void testGetInvalidKey() {
    final RegexMap<String> regexMap = new RegexMap<>();
    regexMap.put("\\/app\\/\\d*\\/resources", "1");

    assertEquals("1", regexMap.get("/app/1/resources"));
    assertNull(regexMap.get("keyDontExist"));
  }

  @Test
  public void testIsEmpty() {
    final RegexMap<String> regexMap = new RegexMap<>();
    assertTrue(regexMap.isEmpty());
    regexMap.put("\\/app\\/\\d*\\/resources", "1");
    assertFalse(regexMap.isEmpty());
  }

  @Test
  public void testRemove() {
    final RegexMap<String> regexMap = new RegexMap<>();
    regexMap.put("\\S", "1");
    regexMap.remove("\\S");
    assertTrue(regexMap.isEmpty());

    assertNull(regexMap.remove("\\S"));
  }

  @Test
  public void testClear() {
    final RegexMap<String> regexMap = new RegexMap<>();
    final String regex1 = "f*";
    final String regex2 = "\\/app\\/\\d*\\/resources";
    final String regex3 = "[0-9]";

    regexMap.put(regex1, "");
    regexMap.put(regex2, "");
    regexMap.put(regex3, "");

    regexMap.clear();
    assertEquals(0, regexMap.size());
  }

  @Test
  public void testContainsKey() {
    final RegexMap<String> regexMap = new RegexMap<>();
    final String regex1 = "f*";
    final String regex2 = "\\/app\\/\\d*\\/resources";
    final String regex3 = "[0-9]";

    regexMap.put(regex1, "");
    regexMap.put(regex2, "");
    regexMap.put(regex3, "");

    assertTrue(regexMap.containsKey(regex1));
    assertTrue(regexMap.containsKey(regex2));
    assertTrue(regexMap.containsKey(regex3));
    assertFalse(regexMap.containsKey("\\W+"));
  }

  @Test
  public void testContainsValue() {
    final RegexMap<String> regexMap = new RegexMap<>();
    final String regex1 = "f*";
    final String regex2 = "\\/app\\/\\d*\\/resources";
    final String regex3 = "[0-9]";

    regexMap.put(regex1, "foo");
    regexMap.put(regex2, "bar");
    regexMap.put(regex3, "fizz");

    assertTrue(regexMap.containsValue("foo"));
    assertTrue(regexMap.containsValue("bar"));
    assertTrue(regexMap.containsValue("fizz"));
    assertFalse(regexMap.containsValue("steve"));
  }

  @Test
  public void testKeySet() {
    final RegexMap<String> regexMap = new RegexMap<>();
    final String regex1 = "f*";
    final String regex2 = "\\/app\\/\\d*\\/resources";
    final String regex3 = "[0-9]";

    regexMap.put(regex1, "foo");
    regexMap.put(regex2, "bar");
    regexMap.put(regex3, "fizz");

    assertEquals(3, regexMap.keySet().size());
    assertTrue(regexMap.keySet().contains(regex1));
    assertTrue(regexMap.keySet().contains(regex2));
    assertTrue(regexMap.keySet().contains(regex3));
  }

  @Test
  public void entrySet() {
    final RegexMap<String> regexMap = new RegexMap<>();
    final String regex1 = "f*";
    final String regex2 = "\\/app\\/\\d*\\/resources";
    final String regex3 = "[0-9]";

    regexMap.put(regex1, "foo");
    regexMap.put(regex2, "bar");
    regexMap.put(regex3, "fizz");

    assertEquals(3, regexMap.entrySet().size());
  }

  @Test
  public void testGetValues() {
    final RegexMap<String> regexMap = new RegexMap<>();
    final String regex1 = "f*";
    final String regex2 = "\\/app\\/\\d*\\/resources";
    final String regex3 = "[0-9]";

    regexMap.put(regex1, "foo");
    regexMap.put(regex2, "bar");
    regexMap.put(regex3, "fizz");

    assertEquals(3, regexMap.values().size());
    assertTrue(regexMap.values().contains("foo"));
    assertTrue(regexMap.values().contains("bar"));
    assertTrue(regexMap.values().contains("fizz"));
  }

  @Test
  public void testPutAll() {
    final RegexMap<String> regexMap = new RegexMap<>();
    final String regex1 = "f*";
    final String regex2 = "\\/app\\/\\d*\\/resources";
    final String regex3 = "[0-9]";

    final Map<String, String> map = new HashMap<>();
    map.put(regex1, "foo");
    map.put(regex2, "bar");
    map.put(regex3, "fizz");
    regexMap.putAll(map);

    assertEquals(3, regexMap.size());
    assertEquals("foo", regexMap.get("f"));
    assertEquals("bar", regexMap.get("/app/1/resources"));
    assertEquals("fizz", regexMap.get("1"));
  }
}
