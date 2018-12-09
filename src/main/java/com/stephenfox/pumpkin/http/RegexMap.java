package com.stephenfox.pumpkin.http;

import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

class RegexMap<V> implements Map<String, V> {

  private final Set<Pattern> patterns;
  private final Map<Pattern, V> map;

  RegexMap() {
    this.patterns = new HashSet<>();
    this.map = new HashMap<>();
  }

  @Override
  public int size() {
    return map.size();
  }

  @Override
  public boolean isEmpty() {
    return map.isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    return getPatternForKey(key) != null;
  }

  @Override
  public boolean containsValue(Object value) {
    return map.containsValue(value);
  }

  @Override
  public V get(Object key) {
    for (Pattern pattern : patterns) {
      if (pattern.matcher(((String) key)).matches()) {
        return map.get(pattern);
      }
    }
    return null;
  }

  @Override
  public V put(String key, V value) {
    final Pattern pattern = Pattern.compile(key);
    patterns.add(pattern);
    return map.put(pattern, value);
  }

  @Override
  public V remove(Object key) {
    final Pattern pattern = getPatternForKey(key);
    if (pattern != null) {
      patterns.remove(pattern);
      return map.remove(pattern);
    }
    return null;
  }

  @Override
  public void putAll(Map<? extends String, ? extends V> m) {
    for (Entry<? extends String, ? extends V> entry : m.entrySet()) {
      put(entry.getKey(), entry.getValue());
    }
  }

  @Override
  public void clear() {
    patterns.clear();
    map.clear();
  }

  @Override
  public Set<String> keySet() {
    return map.keySet().stream().map(Pattern::toString).collect(toSet());
  }

  @Override
  public Collection<V> values() {
    return map.values();
  }

  @Override
  public Set<Entry<String, V>> entrySet() {
    return map.entrySet().stream().map(RegexEntry::new).collect(toSet());
  }

  // Returns the compiled pattern for a given key.
  private Pattern getPatternForKey(Object key) {
    for (Pattern pattern : patterns) {
      // Instead of compiling a pattern each time
      // just toString existing patterns for their regexes
      // and compare the string.
      if (pattern.toString().equals(key)) {
        return pattern;
      }
    }
    return null;
  }

  private static class RegexEntry<V> implements Map.Entry<String, V> {
    private V value;
    private final String key;

    RegexEntry(Entry<Pattern, V> entry) {
      this.key = entry.getKey().toString();
      this.value = entry.getValue();
    }

    @Override
    public String getKey() {
      return key;
    }

    @Override
    public V getValue() {
      return value;
    }

    @Override
    public V setValue(V value) {
      this.value = value;
      return value;
    }
  }
}
