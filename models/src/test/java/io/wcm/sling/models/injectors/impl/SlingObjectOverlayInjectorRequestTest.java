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

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.AnnotatedElement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.day.cq.wcm.api.Page;

import io.wcm.sling.commons.request.RequestContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SlingObjectOverlayInjectorRequestTest {

  private final AemContext context = new AemContext(ResourceResolverType.RESOURCERESOLVER_MOCK);

  @Mock
  protected AnnotatedElement annotatedElement;
  @Mock
  protected SlingHttpServletRequest request;
  @Mock
  protected SlingHttpServletResponse response;
  @Mock
  protected SlingScriptHelper scriptHelper;
  @Mock
  protected ResourceResolver resourceResolver;
  @Mock
  protected Page page;
  @Mock
  protected Resource resource;
  @Mock
  protected RequestContext requestContext;

  protected SlingObjectOverlayInjector injector;

  @BeforeEach
  @SuppressWarnings("null")
  void setUp() {
    context.registerService(RequestContext.class, requestContext);
    context.registerInjectActivateService(new ModelsImplConfiguration(),
        "requestThreadLocal", true);

    injector = context.registerInjectActivateService(new SlingObjectOverlayInjector());

    SlingBindings bindings = new SlingBindings();
    bindings.put(SlingBindings.SLING, this.scriptHelper);

    when(page.adaptTo(Resource.class)).thenReturn(resource);
    when(this.resource.getResourceResolver()).thenReturn(this.resourceResolver);
    when(this.request.getResourceResolver()).thenReturn(this.resourceResolver);
    when(this.request.getResource()).thenReturn(this.resource);
    when(this.request.getAttribute(SlingBindings.class.getName())).thenReturn(bindings);
    when(this.scriptHelper.getResponse()).thenReturn(this.response);
  }

  protected Object adaptable() {
    return this.request;
  }

  @Test
  void testResourceResolver() {
    Object result = this.injector.getValue(adaptable(), null, ResourceResolver.class, this.annotatedElement, mock(DisposalCallbackRegistry.class));
    assertSame(this.resourceResolver, result);
  }

  @Test
  void testResource() {
    Object result = this.injector.getValue(adaptable(), null, Resource.class, this.annotatedElement, mock(DisposalCallbackRegistry.class));
    assertNull(result);

    when(annotatedElement.isAnnotationPresent(SlingObject.class)).thenReturn(true);
    result = this.injector.getValue(adaptable(), null, Resource.class, this.annotatedElement, mock(DisposalCallbackRegistry.class));
    assertSame(resource, result);
  }

  @Test
  void testRequest() {
    Object result = this.injector.getValue(adaptable(), null, SlingHttpServletRequest.class,
        this.annotatedElement, mock(DisposalCallbackRegistry.class));
    assertSame(this.request, result);

    result = this.injector.getValue(adaptable(), null, HttpServletRequest.class, this.annotatedElement, mock(DisposalCallbackRegistry.class));
    assertSame(this.request, result);
  }

  @Test
  void testResponse() {
    Object result = this.injector.getValue(adaptable(), null, SlingHttpServletResponse.class, this.annotatedElement, mock(DisposalCallbackRegistry.class));
    assertSame(this.response, result);

    result = this.injector.getValue(adaptable(), null, HttpServletResponse.class, this.annotatedElement, mock(DisposalCallbackRegistry.class));
    assertSame(this.response, result);
  }

  @Test
  void testScriptHelper() {
    Object result = this.injector.getValue(adaptable(), null, SlingScriptHelper.class, this.annotatedElement, mock(DisposalCallbackRegistry.class));
    assertSame(this.scriptHelper, result);
  }

  @Test
  void testInvalid() {
    Object result = this.injector.getValue(new StringBuffer(), null, SlingScriptHelper.class, this.annotatedElement, mock(DisposalCallbackRegistry.class));
    assertNull(result);
  }

}
