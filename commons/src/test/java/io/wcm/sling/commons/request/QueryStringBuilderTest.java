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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedSet;

import io.wcm.sling.commons.util.Escape;

@SuppressWarnings("null")
public class QueryStringBuilderTest {

  private static final String SPECIAL_CHARS = "a!:$&=";

  @Test
  public void testEmpty() {
    assertNull(new QueryStringBuilder().build());
  }

  @Test
  public void testSimple() {
    assertEquals("p1=value1&p2=123&p3=true&p4=", new QueryStringBuilder()
        .param("p1", "value1")
        .param("p2", 123)
        .param("p3", true)
        .param("p4", null)
        .build());
  }

  @Test
  public void testUrlEncoding() {
    assertEquals("p1=" + Escape.urlEncode(SPECIAL_CHARS) + "&" + Escape.urlEncode(SPECIAL_CHARS) + "=value2", new QueryStringBuilder()
        .param("p1", SPECIAL_CHARS)
        .param(SPECIAL_CHARS, "value2")
        .build());
  }

  @Test
  public void testMulti() {
    assertEquals("p1=value1&p1=value2&p1=&p2=1&p2=2&p3=false&p3=true&p4=abc", new QueryStringBuilder()
        .param("p1", new String[] { "value1", "value2", null })
        .param("p2", ImmutableList.of(1, 2))
        .param("p3", ImmutableSortedSet.of(false, true))
        .param("p4", "abc")
        .build());
  }

  @Test
  public void testMap() {
    assertEquals("p1=value1&p1=value2&p1=&p2=1&p2=2&p3=false&p3=true&p4=abc", new QueryStringBuilder()
        .params(ImmutableSortedMap.of(
            "p1", new String[] { "value1", "value2", null },
            "p2", ImmutableList.of(1, 2),
            "p3", ImmutableSortedSet.of(false, true),
            "p4", "abc"
        ))
        .build());
  }

}
