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
package io.wcm.sling.commons.adapter;

import org.apache.sling.api.adapter.Adaptable;

/**
 * Utility methods around Sling {@link Adaptable}.
 */
public final class AdaptTo {

  private AdaptTo() {
    // static methods only
  }

  /**
   * Try to adapt the adaptable to the given type and ensures that it succeeds.
   * @param adaptable Adaptable
   * @param type Type
   * @return Adaption result (not null)
   * @throws UnableToAdaptException if the adaption was not successful
   */
  public static <T> T notNull(Adaptable adaptable, Class<T> type) {
    T object = adaptable.adaptTo(type);
    if (object == null) {
      throw new UnableToAdaptException(adaptable, type);
    }
    return object;
  }

}
