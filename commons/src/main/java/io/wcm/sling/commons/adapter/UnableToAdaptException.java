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

import aQute.bnd.annotation.ProviderType;

/**
 * Exception is thrown if {@link AdaptTo#notNull} call was not successful.
 */
@ProviderType
public final class UnableToAdaptException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private final Adaptable adaptable;
  private final Class<?> type;

  /**
   * @param adaptable Adaptable object instance
   * @param type Interface to adapt to
   */
  public UnableToAdaptException(Adaptable adaptable, Class<?> type) {
    super("Unable to adapt " + adaptable + " to " + type.getName());
    this.adaptable = adaptable;
    this.type = type;
  }

  /**
   * @return Adaptable object instance
   */
  public Adaptable getAdaptable() {
    return adaptable;
  }

  /**
   * @return Interface to adapt to
   */
  public Class<?> getType() {
    return type;
  }

}
