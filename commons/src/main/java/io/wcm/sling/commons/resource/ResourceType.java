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

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import com.day.cq.wcm.api.components.Component;
import com.day.cq.wcm.api.components.ComponentManager;

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
   * /libs prefix for resource types
   */
  public static final String LIBS_PREFIX = "/libs/";

  /**
   * Converts the resource type to an absolute path. If it does not start with "/" the resource is resolved
   * via search paths using resource resolver. If not matching resource is found it is returned unchanged.
   * @param resourceType Resource type
   * @param resourceResolver Resource resolver
   * @return Absolute resource type
   */
  public static String makeAbsolute(String resourceType, ResourceResolver resourceResolver) {
    if (StringUtils.isEmpty(resourceType) || StringUtils.startsWith(resourceType, "/")) {
      return resourceType;
    }

    // first try to resolve path via component manager - because on publish instance the original resource may not accessible
    ComponentManager componentManager = resourceResolver.adaptTo(ComponentManager.class);
    if (componentManager != null) {
      Component component = componentManager.getComponent(resourceType);
      if (component != null) {
        return component.getPath();
      }
      else {
        return resourceType;
      }
    }

    // otherwise use resource resolver directly
    Resource resource = resourceResolver.getResource(resourceType);
    if (resource != null) {
      return resource.getPath();
    }
    else {
      return resourceType;
    }
  }

  /**
   * Makes the given resource type relative by stripping off an /apps/ or /libs/ prefix.
   * In case the given resource type does not start with any of these prefixes it is returned unmodified.
   * This method does not take the real configured search paths into account, but in case of AEM usually only /apps/ and
   * /libs/ are used.
   * @param resourceType The resource type to make relative.
   * @return Relative resource type
   */
  public static String makeRelative(String resourceType) {
    if (StringUtils.startsWith(resourceType, APPS_PREFIX)) {
      return resourceType.substring(APPS_PREFIX.length());
    }
    else if (StringUtils.startsWith(resourceType, LIBS_PREFIX)) {
      return resourceType.substring(LIBS_PREFIX.length());
    }
    return resourceType;
  }

  /**
   * Returns <code>true</code> if the given resource type are equal.
   * In case the value of any of the given resource types starts with /apps/ or /libs/ prefix this is removed before
   * doing the comparison.
   * @param resourceType A resource type
   * @param anotherResourceType Another resource type to compare with
   * @return <code>true</code> if the resource type equals the given resource type.
   */
  public static boolean equals(String resourceType, String anotherResourceType) {
    return StringUtils.equals(makeRelative(resourceType), makeRelative(anotherResourceType));
  }

  /**
   * Returns <code>true</code> if the resource type or any of the resource's super type(s) equals the given resource
   * type.
   * This implementation is equal to {@link ResourceResolver#isResourceType(Resource, String)} - but in earlier sling
   * version the comparison check did not take potentieal mixtures of relative and absolute resource types into account.
   * This method respects this.
   * @param resource The resource to check
   * @param resourceType The resource type to check this resource against.
   * @return <code>true</code> if the resource type or any of the resource's super type(s) equals the given resource
   *         type. <code>false</code> is also returned if <code>resource</code> or<code>resourceType</code> are
   *         <code>null</code>.
   */
  public static boolean is(Resource resource, String resourceType) {
    if (resource == null || resourceType == null) {
      return false;
    }
    ResourceResolver resolver = resource.getResourceResolver();
    // Check if the resource is of the given type. This method first checks the
    // resource type of the resource, then its super resource type and continues
    //  to go up the resource super type hierarchy.
    boolean result = false;
    if (ResourceType.equals(resourceType, resource.getResourceType())) {
      result = true;
    }
    else {
      Set<String> superTypesChecked = new HashSet<>();
      String superType = resolver.getParentResourceType(resource);
      while (!result && superType != null) {
        if (ResourceType.equals(resourceType, superType)) {
          result = true;
        }
        else {
          superTypesChecked.add(superType);
          superType = resolver.getParentResourceType(superType);
          if (superType != null && superTypesChecked.contains(superType)) {
            throw new SlingException("Cyclic dependency for resourceSuperType hierarchy detected on resource " + resource.getPath(), null);
          }
        }
      }
    }
    return result;
  }

}
