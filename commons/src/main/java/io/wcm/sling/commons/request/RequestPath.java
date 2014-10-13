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

import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.api.SlingHttpServletRequest;

import aQute.bnd.annotation.ProviderType;

/**
 * Utility methods when for sling paths and resources.
 */
@ProviderType
public final class RequestPath {

  private RequestPath() {
    // static util methods only
  }

  /**
   * Checks if the given selector is present in the current URL request (at any position).
   * @param request Sling request
   * @param expectedSelector Selector string to check for.
   * @return true if the selector was found
   */
  public static boolean hasSelector(SlingHttpServletRequest request, String expectedSelector) {
    String[] selectors = request.getRequestPathInfo().getSelectors();
    return ArrayUtils.contains(selectors, expectedSelector);
  }

  /**
   * Checks if one of the given selectors is present in the current URL request (at any position).
   * @param request Sling request
   * @param expectedSelectors Selectors string to check for.
   * @return true if the selector was found
   */
  public static boolean hasAnySelector(SlingHttpServletRequest request, String... expectedSelectors) {
    String[] selectors = request.getRequestPathInfo().getSelectors();
    if (selectors != null && expectedSelectors != null) {
      for (String expectedSelector : expectedSelectors) {
        if (ArrayUtils.contains(selectors, expectedSelector)) {
          return true;
        }
      }
    }
    return false;
  }

}
