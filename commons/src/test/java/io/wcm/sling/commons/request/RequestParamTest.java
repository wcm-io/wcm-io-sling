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
package io.wcm.sling.commons.request;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import io.wcm.testing.mock.sling.servlet.MockSlingHttpServletRequest;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.commons.lang3.CharEncoding;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class RequestParamTest {

  private static final String PARAM_NONEXISTING = "paramNonExisting";
  protected static final String STRING_PARAM = "stringParam";
  protected static final String STRING_VALUE = "value1 äöüß€ ᚠᛇᚻ γλώσσα";
  private static final String MULTI_STRING_PARAM = "multiStringParam";
  private static final String[] MULTI_STRING_VALUE = new String[] {
    STRING_VALUE, "value2", "value3"
  };
  private static final String INTEGER_PARAM = "intParam";
  private static final int INTEGER_VALUE = 123;
  private static final String LONG_PARAM = "longParam";
  private static final long LONG_VALUE = 654597898978L;
  private static final String FLOAT_PARAM = "floatParam";
  private static final float FLOAT_VALUE = 45656.1f;
  private static final String DOUBLE_PARAM = "doubleParam";
  private static final double DOUBLE_VALUE = 564698752463.293d;
  private static final String BOOLEAN_PARAM = "booleanParam";
  private static final boolean BOOLEAN_VALUE = true;
  private static final String ENUM_PARAM = "enumParam";
  private static final SAMPLE_ENUM ENUM_VALUE = SAMPLE_ENUM.ENUM_VALUE2;

  private Map<String, Object> paramMap;
  private MockSlingHttpServletRequest request;

  @Before
  public void setUp() throws Exception {
    request = new MockSlingHttpServletRequest();
    paramMap = getParamMap();
    request.setParameterMap(paramMap);
  }

  @SuppressWarnings("unused")
  protected Map<String, Object> getParamMap() throws UnsupportedEncodingException {
    return ImmutableMap.<String, Object>builder()
        .put(STRING_PARAM, new String[] {
            STRING_VALUE
        })
        .put(MULTI_STRING_PARAM, MULTI_STRING_VALUE)
        .put(INTEGER_PARAM, new String[] {
            Integer.toString(INTEGER_VALUE)
        })
        .put(LONG_PARAM, new String[] {
            Long.toString(LONG_VALUE)
        })
        .put(FLOAT_PARAM, new String[] {
            Float.toString(FLOAT_VALUE)
        })
        .put(DOUBLE_PARAM, new String[] {
            Double.toString(DOUBLE_VALUE)
        })
        .put(BOOLEAN_PARAM, new String[] {
            Boolean.toString(BOOLEAN_VALUE)
        })
        .put(ENUM_PARAM, new String[] {
            ENUM_VALUE.name()
        })
        .put(RequestParam.PARAMETER_FORMENCODING, new String[] {
            CharEncoding.UTF_8
        })
        .build();
  }

  public enum SAMPLE_ENUM {
    ENUM_VALUE1,
    ENUM_VALUE2
  }

  @Test
  public void testGet() {
    assertEquals(STRING_VALUE, RequestParam.get(request, STRING_PARAM));
    assertNull(RequestParam.get(request, PARAM_NONEXISTING));
  }

  @Test
  public void testGetDefault() {
    assertEquals(STRING_VALUE, RequestParam.get(request, STRING_PARAM, "defValue"));
    assertEquals("defValue", RequestParam.get(request, PARAM_NONEXISTING, "defValue"));
  }

  @Test
  public void testGetMultiple() {
    assertArrayEquals(MULTI_STRING_VALUE, RequestParam.getMultiple(request, MULTI_STRING_PARAM));
    assertNull(RequestParam.get(request, PARAM_NONEXISTING));
  }

  @Test
  public void testGetMap() {
    assertEquals(STRING_VALUE, RequestParam.get(request.getParameterMap(), STRING_PARAM));
    assertNull(RequestParam.get(request.getParameterMap(), PARAM_NONEXISTING));
  }

  @Test
  public void testGetInt() {
    assertEquals(INTEGER_VALUE, RequestParam.getInt(request, INTEGER_PARAM));
    assertEquals(0, RequestParam.getInt(request, PARAM_NONEXISTING));
  }

  @Test
  public void testGetIntDefault() {
    assertEquals(INTEGER_VALUE, RequestParam.getInt(request, INTEGER_PARAM, 25));
    assertEquals(25, RequestParam.getInt(request, PARAM_NONEXISTING, 25));
  }

  @Test
  public void testGetLong() {
    assertEquals(LONG_VALUE, RequestParam.getLong(request, LONG_PARAM));
    assertEquals(0L, RequestParam.getLong(request, PARAM_NONEXISTING));
  }

  @Test
  public void testGetLongDefault() {
    assertEquals(LONG_VALUE, RequestParam.getLong(request, LONG_PARAM, 33L));
    assertEquals(33L, RequestParam.getLong(request, PARAM_NONEXISTING, 33L));
  }

  @Test
  public void testGetFloat() {
    assertEquals(FLOAT_VALUE, RequestParam.getFloat(request, FLOAT_PARAM), 0.0001f);
    assertEquals(0f, RequestParam.getFloat(request, PARAM_NONEXISTING), 0.0001f);
  }

  @Test
  public void testGetFloatDefault() {
    assertEquals(FLOAT_VALUE, RequestParam.getFloat(request, FLOAT_PARAM, 1.234f), 0.0001f);
    assertEquals(1.234f, RequestParam.getFloat(request, PARAM_NONEXISTING, 1.234f), 0.0001f);
  }

  @Test
  public void testGetDouble() {
    assertEquals(DOUBLE_VALUE, RequestParam.getDouble(request, DOUBLE_PARAM), 0.0001d);
    assertEquals(0d, RequestParam.getDouble(request, PARAM_NONEXISTING), 0.0001d);
  }

  @Test
  public void testGetDoubleDefault() {
    assertEquals(DOUBLE_VALUE, RequestParam.getDouble(request, DOUBLE_PARAM, 2.4456d), 0.0001d);
    assertEquals(2.4456d, RequestParam.getDouble(request, PARAM_NONEXISTING, 2.4456d), 0.0001d);
  }

  @Test
  public void testGetEnum() {
    assertEquals(ENUM_VALUE, RequestParam.getEnum(request, ENUM_PARAM, SAMPLE_ENUM.class));
    assertNull(RequestParam.getEnum(request, PARAM_NONEXISTING, SAMPLE_ENUM.class));
  }

  @Test
  public void testGetEnumDefault() {
    assertEquals(ENUM_VALUE, RequestParam.getEnum(request, ENUM_PARAM, SAMPLE_ENUM.class, SAMPLE_ENUM.ENUM_VALUE1));
    assertEquals(SAMPLE_ENUM.ENUM_VALUE1, RequestParam.getEnum(request, PARAM_NONEXISTING, SAMPLE_ENUM.class, SAMPLE_ENUM.ENUM_VALUE1));
  }

}
