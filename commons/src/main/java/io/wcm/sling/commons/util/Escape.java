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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;

import aQute.bnd.annotation.ProviderType;

/**
 * Encoding utility functions.
 */
@ProviderType
public final class Escape {

  private Escape() {
    // Utility class - no instancing allowed
  }

  /**
   * Applies URL-Encoding to the given parameter name or value. Uses {@link URLEncoder#encode(String, String)} with
   * UTF-8 character set, while avoiding the need to catch the UnsupportedEncodingException.
   * @param value the parameter name or value to encode
   * @return URL-encoded string - or empty string if the specified value was null
   * @throws RuntimeException in the very unlikely case that UTF-8 is not supported on the current system
   */
  public static String urlEncode(String value) {
    if (value == null) {
      return "";
    }
    try {
      return URLEncoder.encode(value, CharEncoding.UTF_8);
    }
    catch (UnsupportedEncodingException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Creates a valid node name. Replaces all chars not in a-z, A-Z and 0-9 or '_' with '-' and converts all to lowercase.
   * @param value String to be labelized.
   * @return The labelized string.
   */
  public static String validName(String value) {

    // convert to lowercase
    String text = value.toLowerCase();

    // replace some special chars first
    text = StringUtils.replace(text, "ä", "ae");
    text = StringUtils.replace(text, "ö", "oe");
    text = StringUtils.replace(text, "ü", "ue");
    text = StringUtils.replace(text, "ß", "ss");

    // replace all invalid chars
    StringBuilder sb = new StringBuilder(text);
    for (int i = 0; i < sb.length(); i++) {
      char ch = sb.charAt(i);
      if (!((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9') || (ch == '_'))) {
        ch = '-';
        sb.setCharAt(i, ch);
      }
    }
    return sb.toString();
  }

  /**
   * Create valid filename by applying method {@link Escape#validName(String)} on filename and extension.
   * @param value Filename
   * @return Valid filename
   */
  public static String validFilename(String value) {
    String fileExtension = StringUtils.substringAfterLast(value, ".");
    String fileName = StringUtils.substringBeforeLast(value, ".");
    if (StringUtils.isEmpty(fileExtension)) {
      return validName(fileName);
    }
    return validName(fileName) + "." + validName(fileExtension);
  }

}
