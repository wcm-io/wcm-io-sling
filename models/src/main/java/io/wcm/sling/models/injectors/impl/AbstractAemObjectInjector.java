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

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.spi.AcceptsNullName;
import org.apache.sling.models.spi.Injector;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.designer.Design;
import com.day.cq.wcm.api.designer.Designer;

/**
 * Shared implementation between {@link AemObjectRequestInjector} and {@link AemObjectResourceInjector}.
 */
abstract class AbstractAemObjectInjector implements Injector, AcceptsNullName {

  protected static final String RESOURCE_PAGE = "resourcePage";

  protected ResourceResolver getResourceResolver(final Object adaptable) {
    if (adaptable instanceof SlingHttpServletRequest) {
      return ((SlingHttpServletRequest)adaptable).getResourceResolver();
    }
    if (adaptable instanceof Resource) {
      return ((Resource)adaptable).getResourceResolver();
    }
    return null;
  }

  protected Resource getResource(final Object adaptable) {
    if (adaptable instanceof SlingHttpServletRequest) {
      return ((SlingHttpServletRequest)adaptable).getResource();
    }
    if (adaptable instanceof Resource) {
      return (Resource)adaptable;
    }
    return null;
  }

  protected PageManager getPageManager(final Object adaptable) {
    ResourceResolver resolver = getResourceResolver(adaptable);
    if (resolver != null) {
      return resolver.adaptTo(PageManager.class);
    }
    return null;
  }

  protected Designer getDesigner(final Object adaptable) {
    ResourceResolver resolver = getResourceResolver(adaptable);
    if (resolver != null) {
      return resolver.adaptTo(Designer.class);
    }
    return null;
  }

  protected Page getResourcePage(final Object adaptable) {
    PageManager pageManager = getPageManager(adaptable);
    Resource resource = getResource(adaptable);
    if (pageManager != null && resource != null) {
      return pageManager.getContainingPage(resource);
    }
    return null;
  }

  protected Page getCurrentPage(final Object adaptable) {
    return getResourcePage(adaptable);
  }

  protected Design getCurrentDesign(final Object adaptable) {
    Page currentPage = getCurrentPage(adaptable);
    Designer designer = getDesigner(adaptable);
    if (currentPage != null && designer != null) {
      return designer.getDesign(currentPage);
    }
    return null;
  }

}
