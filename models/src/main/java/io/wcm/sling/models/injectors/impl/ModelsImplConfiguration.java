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

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * Configures behavior of the wcm.io Injectors for Sling Models
 */
@Component(service = ModelsImplConfiguration.class, immediate = true)
@Designate(ocd = ModelsImplConfiguration.Config.class)
public class ModelsImplConfiguration {

  @ObjectClassDefinition(name = "wcm.io Models Configuration",
      description = "Configures behavior of the wcm.io Injectors for Sling Models")
  @interface Config {

    @AttributeDefinition(name = "Request Thread Local",
        description = "Enables the thread-local based injection of all request-derived objects in AEM Object " +
            "and Sling Object injectors. These objects can than always be injected regardless of the adaptable.")
    boolean requestThreadLocal() default true;

  }

  private boolean requestThreadLocal;

  @Activate
  private void activate(Config config) {
    requestThreadLocal = config.requestThreadLocal();
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
