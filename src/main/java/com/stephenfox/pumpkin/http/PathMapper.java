package com.stephenfox.pumpkin.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class PathMapper<V> {
  /*
   * This regex matches all alphanumeric and _ characters
   * within a path enclosed in and including curly braces { }.
   * e.g. /api/resource/{id}.
   */
  private static final Pattern PATH_PARAM_REGEX = Pattern.compile("\\{([A-Za-z][A-Za-z0-9_]*)}");

  // This is a partially constructed regex for capturing names groups.
  private static final String NAMED_GROUP_REGEX = "(?<%s>[^/]+)";

  // A mappings of paths to their respective values.
  private final Map<Path, V> pathsToValuesMap = new HashMap<>();

  void put(String path, V value) {
    final Matcher paramMatcher = PATH_PARAM_REGEX.matcher(path);
    final StringBuffer buffer = new StringBuffer();

    final List<String> pathParams = new ArrayList<>();
    int groupIndex = 0;

    while (paramMatcher.find()) {
      final String group = paramMatcher.group();
      final String pathParam = group.substring(1, group.length() - 1);
      if (pathParams.contains(pathParam)) {
        throw new IllegalArgumentException(
            "Cannot use path param " + pathParam + " more than once");
      }
      pathParams.add(pathParam);
      paramMatcher.appendReplacement(buffer, String.format(NAMED_GROUP_REGEX, "p" + groupIndex));
      groupIndex++;
    }

    paramMatcher.appendTail(buffer);
    final Pattern pattern = Pattern.compile(buffer.toString());
    pathsToValuesMap.put(new Path(pattern, pathParams), value);
  }

  Entry<V> get(String path) {
    for (Map.Entry<Path, V> entry : pathsToValuesMap.entrySet()) {
      final Path key = entry.getKey();
      final Matcher paramMatcher = key.pattern.matcher(path);
      if (paramMatcher.matches()) {
        final Map<String, String> paramNameToValueMap = new HashMap<>();
        for (int i = 0; i < paramMatcher.groupCount(); i++) {
          // Get group at i + 1 as first group is the value of `path` passed.
          final String paramValue = paramMatcher.group(i + 1);
          final String paramName = key.paramNames.get(i);
          paramNameToValueMap.put(paramName, paramValue);
        }

        return new Entry<>(paramNameToValueMap, entry.getValue());
      }
    }
    return null;
  }

  private class Path {
    private final List<String> paramNames;
    private final Pattern pattern;

    Path(Pattern pattern, List<String> paramNames) {
      this.pattern = pattern;
      this.paramNames = paramNames;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      @SuppressWarnings("unchecked")
      Path path = (Path) o;
      return paramNames.equals(path.paramNames) && pattern.equals(path.pattern);
    }

    @Override
    public int hashCode() {
      return Objects.hash(paramNames, pattern);
    }
  }

  static class Entry<V> {
    private final Map<String, String> pathValues;
    private final V value;

    Entry(Map<String, String> pathValues, V value) {
      this.pathValues = pathValues;
      this.value = value;
    }

    Map<String, String> pathParams() {
      return pathValues;
    }

    V getValue() {
      return value;
    }
  }
}
