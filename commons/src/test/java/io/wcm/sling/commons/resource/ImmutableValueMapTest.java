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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableMap;

class ImmutableValueMapTest {

  private static final Map<String, Object> SAMPLE_PROPS = ImmutableMap.<String, Object>builder()
      .put("prop1", "value1")
      .put("prop2", 55)
      .build();

  private ValueMap underTest;

  @BeforeEach
  void setUp() {
    underTest = ImmutableValueMap.copyOf(SAMPLE_PROPS);
  }

  @Test
  void testProperties() {
    assertEquals("value1", underTest.get("prop1", String.class));
    assertEquals((Integer)55, underTest.get("prop2", Integer.class));
  }

  @Test
  void testDefault() {
    assertEquals("def", underTest.get("prop3", "def"));
  }

  @Test
  void testMapAccessMethods() {
    assertEquals(2, underTest.size());
    assertFalse(underTest.isEmpty());
    assertTrue(underTest.containsKey("prop1"));
    assertTrue(underTest.containsValue("value1"));
    assertEquals("value1", underTest.get("prop1"));
    assertTrue(underTest.keySet().contains("prop1"));
    assertTrue(underTest.values().contains("value1"));
    assertEquals(2, underTest.entrySet().size());
  }

  @Test
  void testMapClear() {
    assertThrows(UnsupportedOperationException.class, () -> {
      underTest.clear();
    });
  }

  @Test
  void testMapRemove() {
    assertThrows(UnsupportedOperationException.class, () -> {
      underTest.remove("prop2");
    });
  }

  @Test
  void testMapPut() {
    assertThrows(UnsupportedOperationException.class, () -> {
      underTest.put("prop3", "value3");
    });
  }

  @Test
  void testMapPutAll() {
    assertThrows(UnsupportedOperationException.class, () -> {
      underTest.putAll(ImmutableMap.<String, Object>builder()
          .put("prop4", 25)
          .put("prop5", 33)
          .build());
    });
  }

  @Test
  void testOf() {
    ValueMap map = ImmutableValueMap.of();
    assertTrue(map.isEmpty());
  }

  @Test
  void testOfx1() {
    ValueMap map = ImmutableValueMap.of("p1", "v1");
    assertEquals(1, map.size());
    assertEquals("v1", map.get("p1"));
  }

  @Test
  void testOfx2() {
    ValueMap map = ImmutableValueMap.of("p1", "v1", "p2", "v2");
    assertEquals(2, map.size());
    assertEquals("v1", map.get("p1"));
    assertEquals("v2", map.get("p2"));
  }

  @Test
  void testOfx3() {
    ValueMap map = ImmutableValueMap.of("p1", "v1", "p2", "v2", "p3", "v3");
    assertEquals(3, map.size());
    assertEquals("v1", map.get("p1"));
    assertEquals("v2", map.get("p2"));
    assertEquals("v3", map.get("p3"));
  }

  @Test
  void testOfx4() {
    ValueMap map = ImmutableValueMap.of("p1", "v1", "p2", "v2", "p3", "v3", "p4", "v4");
    assertEquals(4, map.size());
    assertEquals("v1", map.get("p1"));
    assertEquals("v2", map.get("p2"));
    assertEquals("v3", map.get("p3"));
    assertEquals("v4", map.get("p4"));
  }

  @Test
  void testOfx5() {
    ValueMap map = ImmutableValueMap.of("p1", "v1", "p2", "v2", "p3", "v3", "p4", "v4", "p5", "v5");
    assertEquals(5, map.size());
    assertEquals("v1", map.get("p1"));
    assertEquals("v2", map.get("p2"));
    assertEquals("v3", map.get("p3"));
    assertEquals("v4", map.get("p4"));
    assertEquals("v5", map.get("p5"));
  }

  @Test
  void testBuilder() {
    ValueMap map = ImmutableValueMap.builder()
        .put("p1", "v1")
        .putAll(ImmutableMap.<String, Object>of("p2", "v2", "p3", "v3"))
        .put(ImmutableMap.<String, Object>of("p4", "v4").entrySet().iterator().next())
        .put("p5", "v5")
        .build();
    assertEquals(5, map.size());
    assertEquals("v1", map.get("p1"));
    assertEquals("v2", map.get("p2"));
    assertEquals("v3", map.get("p3"));
    assertEquals("v4", map.get("p4"));
    assertEquals("v5", map.get("p5"));
  }

  @Test
  void testBuilderEmpty() {
    ValueMap map = ImmutableValueMap.builder().build();
    assertTrue(map.isEmpty());
  }

  @Test
  void testCopyOfValueMap() {
    ValueMap valueMap = new ValueMapDecorator(SAMPLE_PROPS);
    ValueMap map = ImmutableValueMap.copyOf(valueMap);
    assertEquals(map.size(), SAMPLE_PROPS.size());
  }

  @Test
  void testEquals() {
    ValueMap map1 = ImmutableValueMap.builder()
        .put("prop1", "value1")
        .put("prop2", 55)
        .build();
    ValueMap map2 = ImmutableValueMap.builder()
        .put("prop1", "value1")
        .put("prop2", 55)
        .build();
    ValueMap map3 = ImmutableValueMap.builder()
        .put("prop1", "value2")
        .put("prop2", 55)
        .build();

    assertEquals(map1, map2);
    assertEquals(map2, map1);
    assertNotEquals(map1, map3);
    assertNotEquals(map2, map3);
  }

  @Test
  void testToString() {
    ValueMap map = ImmutableValueMap.builder()
        .put("prop1", "value1")
        .put("prop2", 55)
        .put("prop0", true)
        .build();
    assertEquals("{prop0=true,prop1=value1,prop2=55}", map.toString());

    assertEquals("{}", ImmutableValueMap.of().toString());
  }

}
