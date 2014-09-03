/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2014 wcm.io
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
package io.wcm.sling.commons.wrappers;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;

/**
 * {@link ValueMap} that does not support changing its content.
 * All methods that may change the content will throw a {@link UnsupportedOperationException}.
 */
public final class ImmutableValueMapDecorator implements ValueMap {

  private final ValueMap map;

  /**
   * @param map Value map
   */
  public ImmutableValueMapDecorator(final ValueMap map) {
    this.map = map;
  }

  /**
   * @param map Map
   */
  public ImmutableValueMapDecorator(final Map<String, Object> map) {
    this.map = new ValueMapDecorator(map);
  }

  @Override
  public <T> T get(String name, Class<T> type) {
    return this.map.get(name, type);
  }

  @Override
  public <T> T get(String name, T defaultValue) {
    return this.map.get(name, defaultValue);
  }

  @Override
  public int size() {
    return this.map.size();
  }

  @Override
  public boolean isEmpty() {
    return this.map.isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    return this.map.containsKey(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return this.map.containsValue(value);
  }

  @Override
  public Object get(Object key) {
    return this.map.get(key);
  }

  @Override
  public Set<String> keySet() {
    return this.map.keySet();
  }

  @Override
  public Collection<Object> values() {
    return this.map.values();
  }

  @Override
  public Set<java.util.Map.Entry<String, Object>> entrySet() {
    return Collections.unmodifiableSet(this.map.entrySet());
  }

  @Override
  public boolean equals(Object o) {
    return this.map.equals(o);
  }

  @Override
  public int hashCode() {
    return this.map.hashCode();
  }

  // mutable operations not supported
  @Override
  public Object put(String key, Object value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object remove(Object key) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void putAll(Map<? extends String, ? extends Object> m) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

}
