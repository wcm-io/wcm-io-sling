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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

/**
 * Test {@link Escape} class.
 */
@SuppressWarnings("null")
class EscapeTest {

  @Test
  void testUrlEncode() throws UnsupportedEncodingException {
    assertEquals(URLEncoder.encode("abc", StandardCharsets.UTF_8.name()), Escape.urlEncode("abc"));
    assertEquals(URLEncoder.encode("Abc_äöüÄÖÜß", StandardCharsets.UTF_8.name()), Escape.urlEncode("Abc_äöüÄÖÜß"));
    assertEquals(URLEncoder.encode("Der Jodelkaiser", StandardCharsets.UTF_8.name()), Escape.urlEncode("Der Jodelkaiser"));
    assertEquals(URLEncoder.encode("Der  Jodelkaiser", StandardCharsets.UTF_8.name()), Escape.urlEncode("Der  Jodelkaiser"));
    assertEquals(URLEncoder.encode("äölsa§$5x !?_", StandardCharsets.UTF_8.name()), Escape.urlEncode("äölsa§$5x !?_"));
  }

  @Test
  void testValidName() {
    assertEquals("abc", Escape.validName("abc"));
    assertEquals("abc_aeoeueaeoeuess", Escape.validName("Abc_äöüÄÖÜß"));
    assertEquals("der-jodelkaiser", Escape.validName("Der Jodelkaiser"));
    assertEquals("der--jodelkaiser", Escape.validName("Der  Jodelkaiser"));
    assertEquals("aeoelsa--5x---_", Escape.validName("äölsa§$5x !?_"));
    assertEquals("test-file", Escape.validName("test.file"));
    assertEquals("test-sel1-sel2-file", Escape.validName("test.sel1.sel2.file"));
  }

  @Test
  void testValidFilename() {
    assertEquals("abc", Escape.validFilename("abc"));
    assertEquals("abc_aeoeueaeoeuess", Escape.validFilename("Abc_äöüÄÖÜß"));
    assertEquals("der-jodelkaiser", Escape.validFilename("Der Jodelkaiser"));
    assertEquals("der--jodelkaiser", Escape.validFilename("Der  Jodelkaiser"));
    assertEquals("aeoelsa--5x---_", Escape.validFilename("äölsa§$5x !?_"));
    assertEquals("test.file", Escape.validFilename("test.file"));
    assertEquals("test-sel1-sel2.file", Escape.validFilename("test.sel1.sel2.file"));
  }

  @Test
  void testJcrQueryLiteral() {
    assertEquals("''", Escape.jcrQueryLiteral(""));
    assertEquals("'abc'", Escape.jcrQueryLiteral("abc"));
    assertEquals("'a''bc'", Escape.jcrQueryLiteral("a'bc"));
    assertEquals("'a''bc'''''", Escape.jcrQueryLiteral("a'bc''"));
    assertEquals("'abc?'", Escape.jcrQueryLiteral("abc?"));
  }

  @Test
  void testJcrQueryLiteralNull() {
    assertThrows(IllegalArgumentException.class, () -> {
      Escape.jcrQueryLiteral(null);
    });
  }

  @Test
  void testJcrQueryContainsExpr() throws Exception {
    assertEquals("'abc'", Escape.jcrQueryContainsExpr("abc"));
    assertEquals("'a''bc'", Escape.jcrQueryContainsExpr("a'bc"));
    assertEquals("'a''bc'''''", Escape.jcrQueryContainsExpr("a'bc''"));
    assertEquals("'abc\\?'", Escape.jcrQueryContainsExpr("abc?"));
  }

  @Test
  void testJcrQueryContainsExprNull() {
    assertThrows(IllegalArgumentException.class, () -> {
      Escape.jcrQueryContainsExpr(null);
    });
  }

  @Test
  void testJcrQueryContainsExprEmpty() {
    assertThrows(IllegalArgumentException.class, () -> {
      Escape.jcrQueryContainsExpr("");
    });
  }

}
