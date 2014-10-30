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

import io.wcm.sling.commons.request.RequestContext;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.spi.AcceptsNullName;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.apache.sling.models.spi.injectorspecific.AbstractInjectAnnotationProcessor;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessorFactory;
import org.osgi.framework.Constants;

/**
 * Injects common Sling objects that can be derived from either a SlingHttpServletRequest, a ResourceResolver or a
 * Resource.
 * Documentation see {@link SlingObject}.
 * <p>
 * This is an overlay of the SlingObject injector provided by the Sling Models implementation itself. It adds support to
 * always get the sling request and all objects that can be derived from it whether the adaptable is a request or not -
 * using a thread local (see also SLING-4083).
 * </p>
 * <p>
 * With this overlay it is possible to always get these context objects if the adaption is done in context of a
 * request-bound thread: resource resolver, current resource, request, response, sling script helper.
 * </p>
 */
@Component
@Service
// use ranking MAX_VALUE - 10 to overlay the sling-object injector of sling which is registered to MAX_VALUE
@Property(name = Constants.SERVICE_RANKING, intValue = Integer.MAX_VALUE - 10)
public final class SlingObjectInjector implements Injector, InjectAnnotationProcessorFactory, AcceptsNullName {

  /**
   * Injector name
   */
  public static final String NAME = "sling-object";

  @Reference
  private RequestContext requestContext;

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public Object getValue(final Object adaptable, final String name, final Type type, final AnnotatedElement element,
      final DisposalCallbackRegistry callbackRegistry) {

    // only class types are supported
    if (!(type instanceof Class<?>)) {
      return null;
    }
    Class<?> requestedClass = (Class<?>)type;

    // validate input
    if (adaptable instanceof ResourceResolver) {
      ResourceResolver resourceResolver = (ResourceResolver)adaptable;
      if (requestedClass.equals(ResourceResolver.class)) {
        return resourceResolver;
      }
    }
    else if (adaptable instanceof Resource) {
      Resource resource = (Resource)adaptable;
      if (requestedClass.equals(ResourceResolver.class)) {
        return resource.getResourceResolver();
      }
      if (requestedClass.equals(Resource.class) && element.isAnnotationPresent(SlingObject.class)) {
        return resource;
      }
    }
    SlingHttpServletRequest request = getRequest(adaptable);
    if (request != null) {
      if (requestedClass.equals(ResourceResolver.class)) {
        return request.getResourceResolver();
      }
      if (requestedClass.equals(Resource.class) && element.isAnnotationPresent(SlingObject.class)) {
        return request.getResource();
      }
      if (requestedClass.equals(SlingHttpServletRequest.class) || requestedClass.equals(HttpServletRequest.class)) {
        return request;
      }
      if (requestedClass.equals(SlingHttpServletResponse.class) || requestedClass.equals(HttpServletResponse.class)) {
        return getSlingHttpServletResponse(request);
      }
      if (requestedClass.equals(SlingScriptHelper.class)) {
        return getSlingScriptHelper(request);
      }
    }

    return null;
  }

  private SlingHttpServletRequest getRequest(final Object adaptable) {
    if (adaptable instanceof SlingHttpServletRequest) {
      return (SlingHttpServletRequest)adaptable;
    }
    else if (requestContext != null) {
      return requestContext.getThreadRequest();
    }
    else {
      return null;
    }
  }

  private SlingScriptHelper getSlingScriptHelper(final SlingHttpServletRequest request) {
    SlingBindings bindings = (SlingBindings)request.getAttribute(SlingBindings.class.getName());
    if (bindings != null) {
      return bindings.getSling();
    }
    return null;
  }

  private SlingHttpServletResponse getSlingHttpServletResponse(final SlingHttpServletRequest request) {
    SlingScriptHelper scriptHelper = getSlingScriptHelper(request);
    if (scriptHelper != null) {
      return scriptHelper.getResponse();
    }
    return null;
  }

  @Override
  public InjectAnnotationProcessor createAnnotationProcessor(final Object adaptable, final AnnotatedElement element) {
    // check if the element has the expected annotation
    SlingObject annotation = element.getAnnotation(SlingObject.class);
    if (annotation != null) {
      return new SlingObjectAnnotationProcessor(annotation);
    }
    return null;
  }

  private static class SlingObjectAnnotationProcessor extends AbstractInjectAnnotationProcessor {

    private final SlingObject annotation;

    public SlingObjectAnnotationProcessor(final SlingObject annotation) {
      this.annotation = annotation;
    }

    @Override
    public Boolean isOptional() {
      return this.annotation.optional();
    }
  }

}
