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

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

class RequestParamNoFormEncodingTest extends RequestParamTest {

  @Override
  protected Map<String, Object> getParamMap() {
    Map<String, Object> paramMap = new HashMap<>();

    for (Map.Entry<String, Object> entry : super.getParamMap().entrySet()) {
      String[] values = (String[])entry.getValue();
      String[] convertedValues;
      if (values == null) {
        convertedValues = null;
      }
      else {
        convertedValues = new String[values.length];
        for (int i = 0; i < values.length; i++) {
          convertedValues[i] = convertUTF8toISO88591(values[i]);
        }
      }
      paramMap.put(entry.getKey(), convertedValues);
    }

    // simulate a request that has no form encoding set
    paramMap.remove(RequestParam.PARAMETER_FORMENCODING);

    return paramMap;
  }

  private static String convertUTF8toISO88591(String value) {
    if (value == null) {
      return null;
    }
    return new String(value.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
  }


}
