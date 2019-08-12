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
package io.wcm.sling.commons.request.impl;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestContextFilterImplTest {

  @Mock
  private SlingHttpServletRequest request;
  @Mock
  private SlingHttpServletRequest request2;
  @Mock
  private SlingHttpServletResponse response;

  private RequestContextFilterImpl underTest;

  @BeforeEach
  void setUp() {
    underTest = new RequestContextFilterImpl();
  }

  @Test
  void testFilter() throws Exception {
    assertNull(underTest.getThreadRequest());

    underTest.doFilter(request, response, new FilterChain() {
      @Override
      public void doFilter(ServletRequest req, ServletResponse resp) {
        assertSame(req, underTest.getThreadRequest());
      }
    });

    assertNull(underTest.getThreadRequest());
  }

  @Test
  void testFilterNested() throws Exception {
    assertNull(underTest.getThreadRequest());

    underTest.doFilter(request, response, new FilterChain() {

      @Override
      public void doFilter(ServletRequest req, ServletResponse resp) throws IOException, ServletException {
        assertSame(req, underTest.getThreadRequest());
        assertSame(request, underTest.getThreadRequest());

        underTest.doFilter(request2, resp, new FilterChain() {
          @Override
          public void doFilter(ServletRequest req2, ServletResponse resp2) {
            assertSame(req2, underTest.getThreadRequest());
            assertSame(request2, underTest.getThreadRequest());
          }
        });

        assertSame(req, underTest.getThreadRequest());
        assertSame(request, underTest.getThreadRequest());
      }
    });

    assertNull(underTest.getThreadRequest());
  }

  @Test
  void testWithExeption() throws Exception {
    assertNull(underTest.getThreadRequest());

    try {
      underTest.doFilter(request, response, new FilterChain() {
        @Override
        public void doFilter(ServletRequest req, ServletResponse resp) throws ServletException {
          throw new ServletException("simulated exception.");
        }
      });
      fail("Excpection expected");
    }
    catch (ServletException ex) {
      // expected
    }

    assertNull(underTest.getThreadRequest());
  }

}
