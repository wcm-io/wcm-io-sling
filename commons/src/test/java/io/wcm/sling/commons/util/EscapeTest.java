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
package io.wcm.sling.commons.util;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang3.CharEncoding;
import org.junit.Test;

/**
 * Test {@link Escape} class.
 */
public class EscapeTest {

  @Test
  public void testUrlEncode() throws UnsupportedEncodingException {
    assertEquals(URLEncoder.encode("abc", CharEncoding.UTF_8), Escape.urlEncode("abc"));
    assertEquals(URLEncoder.encode("Abc_äöüÄÖÜß", CharEncoding.UTF_8), Escape.urlEncode("Abc_äöüÄÖÜß"));
    assertEquals(URLEncoder.encode("Der Jodelkaiser", CharEncoding.UTF_8), Escape.urlEncode("Der Jodelkaiser"));
    assertEquals(URLEncoder.encode("Der  Jodelkaiser", CharEncoding.UTF_8), Escape.urlEncode("Der  Jodelkaiser"));
    assertEquals(URLEncoder.encode("äölsa§$5x !?_", CharEncoding.UTF_8), Escape.urlEncode("äölsa§$5x !?_"));
  }

  @Test
  public void testValidName() {
    assertEquals("abc", Escape.validName("abc"));
    assertEquals("abc_aeoeueaeoeuess", Escape.validName("Abc_äöüÄÖÜß"));
    assertEquals("der-jodelkaiser", Escape.validName("Der Jodelkaiser"));
    assertEquals("der--jodelkaiser", Escape.validName("Der  Jodelkaiser"));
    assertEquals("aeoelsa--5x---_", Escape.validName("äölsa§$5x !?_"));
    assertEquals("test-file", Escape.validName("test.file"));
    assertEquals("test-sel1-sel2-file", Escape.validName("test.sel1.sel2.file"));
  }

  @Test
  public void testValidFilename() {
    assertEquals("abc", Escape.validFilename("abc"));
    assertEquals("abc_aeoeueaeoeuess", Escape.validFilename("Abc_äöüÄÖÜß"));
    assertEquals("der-jodelkaiser", Escape.validFilename("Der Jodelkaiser"));
    assertEquals("der--jodelkaiser", Escape.validFilename("Der  Jodelkaiser"));
    assertEquals("aeoelsa--5x---_", Escape.validFilename("äölsa§$5x !?_"));
    assertEquals("test.file", Escape.validFilename("test.file"));
    assertEquals("test-sel1-sel2.file", Escape.validFilename("test.sel1.sel2.file"));
  }

}
