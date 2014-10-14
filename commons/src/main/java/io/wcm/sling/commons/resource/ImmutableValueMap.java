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
package io.wcm.sling.commons.resource;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;

import aQute.bnd.annotation.ProviderType;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;

/**
 * {@link ValueMap} that does not support changing its content.
 * <p>
 * All methods that may change the content will throw a {@link UnsupportedOperationException}.
 * </p>
 * <p>
 * Static convenience methods provide similar behavior as Guava ImmutableMap variants.
 * </p>
 */
@ProviderType
public final class ImmutableValueMap implements ValueMap {

  private final ValueMap map;

  /**
   * @param map Value map
   */
  private ImmutableValueMap(ValueMap map) {
    this.map = map;
  }

  /**
   * @param map Map
   */
  private ImmutableValueMap(Map<String, Object> map) {
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
  public int hashCode() {
    return this.map.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof ImmutableValueMap)) {
      return false;
    }
    return this.map.entrySet().equals(((ImmutableValueMap)obj).map.entrySet());
  }

  @Override
  public String toString() {
    SortedMap sortedMap = ImmutableSortedMap.<String, Object>copyOf(this.map);
    return "{" + Joiner.on(",").withKeyValueSeparator("=").join(sortedMap) + "}";
  }

  // mutable operations not supported
  /**
   * @deprecated Unsupported operation
   */
  @Override
  @Deprecated
  public Object put(String key, Object value) {
    throw new UnsupportedOperationException();
  }

  /**
   * @deprecated Unsupported operation
   */
  @Override
  @Deprecated
  public Object remove(Object key) {
    throw new UnsupportedOperationException();
  }

  /**
   * @deprecated Unsupported operation
   */
  @Override
  @Deprecated
  public void putAll(Map<? extends String, ? extends Object> m) {
    throw new UnsupportedOperationException();
  }

  /**
   * @deprecated Unsupported operation
   */
  @Override
  @Deprecated
  public void clear() {
    throw new UnsupportedOperationException();
  }


  /**
   * Returns the empty map. This map behaves and performs comparably to {@link Collections#emptyMap}, and is preferable
   * mainly for consistency
   * and maintainability of your code.
   */
  public static ImmutableValueMap of() {
    return new ImmutableValueMap(ValueMap.EMPTY);
  }

  /**
   * Returns an immutable map containing a single entry. This map behaves and
   * performs comparably to {@link Collections#singletonMap} but will not accept
   * a null key or value. It is preferable mainly for consistency and
   * maintainability of your code.
   */
  public static ImmutableValueMap of(String k1, Object v1) {
    return new ImmutableValueMap(ImmutableMap.<String, Object>of(k1, v1));
  }

  /**
   * Returns an immutable map containing the given entries, in order.
   * @throws IllegalArgumentException if duplicate keys are provided
   */
  public static ImmutableValueMap of(String k1, Object v1, String k2, Object v2) {
    return new ImmutableValueMap(ImmutableMap.<String, Object>of(k1, v1, k2, v2));
  }

  /**
   * Returns an immutable map containing the given entries, in order.
   * @throws IllegalArgumentException if duplicate keys are provided
   */
  public static ImmutableValueMap of(
      String k1, Object v1, String k2, Object v2, String k3, Object v3) {
    return new ImmutableValueMap(ImmutableMap.<String, Object>of(k1, v1, k2, v2, k3, v3));
  }

  /**
   * Returns an immutable map containing the given entries, in order.
   * @throws IllegalArgumentException if duplicate keys are provided
   */
  public static ImmutableValueMap of(
      String k1, Object v1, String k2, Object v2, String k3, Object v3, String k4, Object v4) {
    return new ImmutableValueMap(ImmutableMap.<String, Object>of(k1, v1, k2, v2, k3, v3, k4, v4));
  }

  /**
   * Returns an immutable map containing the given entries, in order.
   * @throws IllegalArgumentException if duplicate keys are provided
   */
  public static ImmutableValueMap of(
      String k1, Object v1, String k2, Object v2, String k3, Object v3, String k4, Object v4, String k5, Object v5) {
    return new ImmutableValueMap(ImmutableMap.<String, Object>of(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5));
  }

  // looking for of() with > 5 entries? Use the builder instead.

  /**
   * Returns a new builder. The generated builder is equivalent to the builder
   * created by the {@link Builder} constructor.
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Returns an immutable map containing the same entries as {@code map}. If {@code map} somehow contains entries with
   * duplicate keys (for example, if
   * it is a {@code SortedMap} whose comparator is not <i>consistent with
   * equals</i>), the results of this method are undefined.
   * <p>
   * Despite the method name, this method attempts to avoid actually copying the data when it is safe to do so. The
   * exact circumstances under which a copy will or will not be performed are undocumented and subject to change.
   * @throws NullPointerException if any key or value in {@code map} is null
   */
  public static ImmutableValueMap copyOf(Map<String, Object> map) {
    if (map instanceof ValueMap) {
      return new ImmutableValueMap((ValueMap)map);
    }
    else {
      return new ImmutableValueMap(map);
    }
  }

  /**
   * Builder interface for {@link ImmutableValueMap}.
   */
  public static final class Builder {

    private Map<String, Object> map = new HashMap<>();

    /**
     * Associates {@code key} with {@code value} in the built map. Duplicate
     * keys are not allowed, and will cause {@link #build} to fail.
     */
    public Builder put(String key, Object value) {
      map.put(key, value);
      return this;
    }

    /**
     * Adds the given {@code entry} to the map, making it immutable if
     * necessary. Duplicate keys are not allowed, and will cause {@link #build} to fail.
     */
    public Builder put(Entry<String, Object> entry) {
      return put(entry.getKey(), entry.getValue());
    }

    /**
     * Associates all of the given map's keys and values in the built map.
     * Duplicate keys are not allowed, and will cause {@link #build} to fail.
     * @throws NullPointerException if any key or value in {@code map} is null
     */
    public Builder putAll(Map<String, Object> value) {
      map.putAll(value);
      return this;
    }

    /**
     * Returns a newly-created immutable map.
     * @throws IllegalArgumentException if duplicate keys were added
     */
    public ImmutableValueMap build() {
      if (map.isEmpty()) {
        return ImmutableValueMap.of();
      }
      else {
        return new ImmutableValueMap(map);
      }
    }
  }

}
