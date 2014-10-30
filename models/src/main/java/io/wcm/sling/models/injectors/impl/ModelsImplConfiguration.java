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
package io.wcm.sling.models.injectors.impl;

import java.util.Dictionary;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.component.ComponentContext;

/**
 * Configures behavior of the wcm.io Injectors for Sling Models
 */
@Component(immediate = true, metatype = true,
label = "wcm.io Models Configuration",
description = "Configures behavior of the wcm.io Injectors for Sling Models")
@Service(ModelsImplConfiguration.class)
public class ModelsImplConfiguration {

  @Property(label = "Request Thread Local",
      description = "Enables the thread-local based injection of all request-derived objects in AEM Object " +
          "and Sling Object injectors. These objects can than always be injected regardless of the adaptable.",
          boolValue = ModelsImplConfiguration.PARAM_REQUEST_THREAD_LOCAL_DEFAULT)
  static final String PARAM_REQUEST_THREAD_LOCAL = "requestThreadLocal";
  static final boolean PARAM_REQUEST_THREAD_LOCAL_DEFAULT = true;

  private boolean requestThreadLocal;

  @Activate
  private void activate(ComponentContext componentContext) {
    Dictionary config = componentContext.getProperties();
    requestThreadLocal = PropertiesUtil.toBoolean(config.get(PARAM_REQUEST_THREAD_LOCAL), PARAM_REQUEST_THREAD_LOCAL_DEFAULT);
  }

  /**
   * Enables the thread-local based injection of all request-derived objects in AEM Object
   * and Sling Object injectors. These objects can than always be injected regardless of the adaptable.
   * @return Request Thread Local
   */
  public boolean isRequestThreadLocal() {
    return this.requestThreadLocal;
  }

}
