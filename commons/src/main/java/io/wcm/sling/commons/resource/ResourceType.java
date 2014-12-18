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
package io.wcm.sling.commons.resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

/**
 * Helper methods for resource type path handling.
 */
public final class ResourceType {

  private ResourceType() {
    // utility methods only
  }

  /**
   * /apps prefix for resource types
   */
  public static final String APPS_PREFIX = "/apps/";

  /**
   * Converts the resource type to an absolute path. If it does not start with "/" the resource is resolved
   * via search paths using resource resolver. If not matching resource is found it is returned unchanged.
   * @param resourceType Resource type
   * @return Absolute resource type
   */
  public static String makeAbsolute(String resourceType, ResourceResolver resourceResolver) {
    if (StringUtils.isEmpty(resourceType) || StringUtils.startsWith(resourceType, "/")) {
      return resourceType;
    }
    Resource resource = resourceResolver.getResource(resourceType);
    if (resource != null) {
      return resource.getPath();
    }
    else {
      return resourceType;
    }
  }

}
