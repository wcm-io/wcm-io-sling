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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestPathInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class RequestPathTest {

  @Mock
  private SlingHttpServletRequest request;
  @Mock
  private RequestPathInfo requestPathInfo;

  @BeforeEach
  void setUp() throws Exception {
    when(request.getRequestPathInfo()).thenReturn(requestPathInfo);
  }

  @Test
  void testHasSelector() {
    when(requestPathInfo.getSelectors()).thenReturn(new String[] {
        "sel1", "sel2"
    });
    assertTrue(RequestPath.hasSelector(request, "sel1"));
    assertTrue(RequestPath.hasSelector(request, "sel2"));
    assertFalse(RequestPath.hasSelector(request, "sel3"));
  }

  @Test
  void testHasSelector_NoPresent() {
    when(requestPathInfo.getSelectors()).thenReturn(new String[0]);
    assertFalse(RequestPath.hasSelector(request, "sel1"));
    assertFalse(RequestPath.hasSelector(request, "sel2"));
    assertFalse(RequestPath.hasSelector(request, "sel3"));
  }

  @Test
  void testHasSelector_NullArray() {
    when(requestPathInfo.getSelectors()).thenReturn(null);
    assertFalse(RequestPath.hasSelector(request, "sel1"));
    assertFalse(RequestPath.hasSelector(request, "sel2"));
    assertFalse(RequestPath.hasSelector(request, "sel3"));
  }

  @Test
  void testHasSelector_InvalidArgs() {
    when(requestPathInfo.getSelectors()).thenReturn(new String[] {
        "sel1", "sel2"
    });
    assertFalse(RequestPath.hasSelector(request, ""));
    assertFalse(RequestPath.hasSelector(request, null));
  }

  @Test
  void testHasAnySelector() {
    when(requestPathInfo.getSelectors()).thenReturn(new String[] {
        "sel1", "sel2"
    });
    assertTrue(RequestPath.hasAnySelector(request, "sel1"));
    assertTrue(RequestPath.hasAnySelector(request, "sel2"));
    assertFalse(RequestPath.hasAnySelector(request, "sel3"));
    assertTrue(RequestPath.hasAnySelector(request, "sel1", "sel2"));
    assertTrue(RequestPath.hasAnySelector(request, "sel1", "sel2", "sel3"));
  }

  @Test
  void testHasAnySelector_NoPresent() {
    when(requestPathInfo.getSelectors()).thenReturn(new String[0]);
    assertFalse(RequestPath.hasAnySelector(request, "sel1"));
    assertFalse(RequestPath.hasAnySelector(request, "sel2"));
    assertFalse(RequestPath.hasAnySelector(request, "sel3"));
  }

  @Test
  void testHasAnySelector_NullArray() {
    when(requestPathInfo.getSelectors()).thenReturn(null);
    assertFalse(RequestPath.hasAnySelector(request, "sel1"));
    assertFalse(RequestPath.hasAnySelector(request, "sel2"));
    assertFalse(RequestPath.hasAnySelector(request, "sel3"));
  }

  @Test
  void testHasAnySelector_InvalidArgs() {
    when(requestPathInfo.getSelectors()).thenReturn(new String[] {
        "sel1", "sel2"
    });
    assertFalse(RequestPath.hasAnySelector(request, ""));
    assertFalse(RequestPath.hasAnySelector(request, (String)null));
    assertFalse(RequestPath.hasAnySelector(request, (String[])null));
  }

}
