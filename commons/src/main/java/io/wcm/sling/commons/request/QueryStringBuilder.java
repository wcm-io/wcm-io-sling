/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2018 wcm.io
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package io.wcm.sling.commons.request;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

import io.wcm.sling.commons.util.Escape;

/**
 * Builds a property URL-encoded query string.
 */
@ProviderType
public final class QueryStringBuilder {

  private static final String PARAM_SEPARATOR = "&";
  private static final String VALUE_SEPARATOR = "=";

  private List<NameValuePair> params = new ArrayList<>();

  /**
   * Add parameter to query string.
   * @param name Parameter name
   * @param value Parameter value. Will be converted to string.
   *          If value is an array or {@link Iterable} the value items will be added as separate parameters.
   * @return this
   */
  @SuppressWarnings("unchecked")
  public QueryStringBuilder param(String name, Object value) {
    if (value instanceof Iterable) {
      Iterable<Object> valueItems = (Iterable)value;
      for (Object valueItem : valueItems) {
        params.add(new NameValuePair(name, valueItem));
      }
    }
    else if (isArray(value)) {
      int length = Array.getLength(value);
      for (int i = 0; i < length; i++) {
        Object valueItem = Array.get(value, i);
        params.add(new NameValuePair(name, valueItem));
      }
    }
    else {
      params.add(new NameValuePair(name, value));
    }
    return this;
  }

  /**
   * Add map of parameters to query string.
   * @param values Map with parameter names and values. Values will be converted to strings.
   *          If a value is an array or {@link Iterable} the value items will be added as separate parameters.
   * @return this
   */
  public QueryStringBuilder params(Map<String, Object> values) {
    for (Map.Entry<String, Object> entry : values.entrySet()) {
      param(entry.getKey(), entry.getValue());
    }
    return this;
  }

  /**
   * Build query string.
   * @return Query string or null if query string contains no parameters at all.
   */
  public String build() {
    StringBuilder queryString = new StringBuilder();

    for (NameValuePair param : params) {
      if (queryString.length() > 0) {
        queryString.append(PARAM_SEPARATOR);
      }
      queryString.append(Escape.urlEncode(param.getName()));
      queryString.append(VALUE_SEPARATOR);
      queryString.append(Escape.urlEncode(param.getValue()));
    }

    if (queryString.length() > 0) {
      return queryString.toString();
    }
    else {
      return null;
    }
  }

  private static boolean isArray(Object value) {
    return value != null && value.getClass().isArray();
  }

  private static class NameValuePair {

    private final String name;
    private final String value;

    NameValuePair(String name, Object value) {
      this.name = name;
      if (value != null) {
        this.value = value.toString();
      }
      else {
        this.value = "";
      }
    }

    public String getName() {
      return this.name;
    }

    public String getValue() {
      return this.value;
    }

  }

}
