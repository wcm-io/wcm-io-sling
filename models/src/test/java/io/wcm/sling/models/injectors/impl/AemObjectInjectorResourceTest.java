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

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import io.wcm.sling.commons.request.RequestContext;
import io.wcm.testing.mock.aem.junit.AemContext;

import java.lang.reflect.AnnotatedElement;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.adobe.granite.xss.XSSAPI;
import com.day.cq.i18n.I18n;
import com.day.cq.wcm.api.AuthoringUIMode;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMMode;
import com.day.cq.wcm.api.components.ComponentContext;
import com.day.cq.wcm.api.designer.Design;
import com.day.cq.wcm.api.designer.Designer;
import com.day.cq.wcm.api.designer.Style;

@RunWith(MockitoJUnitRunner.class)
public class AemObjectInjectorResourceTest {

  @Rule
  public AemContext context = new AemContext();

  @Mock
  private AnnotatedElement annotatedElement;
  @Mock
  private Resource resource;
  @Mock
  private ResourceResolver resourceResolver;
  @Mock
  private PageManager pageManager;
  @Mock
  private Page resourcePage;
  @Mock
  private Designer designer;
  @Mock
  private Design design;
  @Mock
  private RequestContext requestContext;

  private AemObjectInjector injector;

  @Before
  public void setUp() {
    context.registerService(RequestContext.class, requestContext);
    injector = context.registerInjectActivateService(new AemObjectInjector());
    when(resource.getResourceResolver()).thenReturn(resourceResolver);
    when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
    when(resourceResolver.adaptTo(Designer.class)).thenReturn(designer);
    when(pageManager.getContainingPage(resource)).thenReturn(resourcePage);
    when(designer.getDesign(any(Page.class))).thenReturn(design);
  }

  @Test
  public void testPageManager() {
    Object result = injector.getValue(resource, null, PageManager.class, annotatedElement, null);
    assertSame(pageManager, result);
  }

  @Test
  public void testCurrentPage() {
    Object result = injector.getValue(resource, null, Page.class, annotatedElement, null);
    assertSame(resourcePage, result);
  }

  @Test
  public void testResourcePage() {
    Object result = injector.getValue(resource, "resourcePage", Page.class, annotatedElement, null);
    assertSame(resourcePage, result);
  }

  @Test
  public void testWcmMode() {
    Object result = injector.getValue(resource, null, WCMMode.class, annotatedElement, null);
    assertNull(result);
  }

  @Test
  public void testAuthoringUiMode() {
    Object result = injector.getValue(resource, null, AuthoringUIMode.class, annotatedElement, null);
    assertNull(result);
  }

  @Test
  public void testComponentContext() {
    Object result = injector.getValue(resource, null, ComponentContext.class, annotatedElement, null);
    assertNull(result);
  }

  @Test
  public void testDesigner() {
    Object result = injector.getValue(resource, null, Designer.class, annotatedElement, null);
    assertSame(designer, result);
  }

  @Test
  public void testDesign() {
    Object result = injector.getValue(resource, null, Design.class, annotatedElement, null);
    assertSame(design, result);
  }

  @Test
  public void testStyle() {
    Object result = injector.getValue(resource, null, Style.class, annotatedElement, null);
    assertNull(result);
  }

  @Test
  public void testXssApi() {
    Object result = injector.getValue(resource, null, XSSAPI.class, annotatedElement, null);
    assertNull(result);
  }

  @Test
  public void testI18n() {
    Object result = injector.getValue(resource, null, I18n.class, annotatedElement, null);
    assertNull(result);
  }

  @Test
  public void testInvalid() {
    Object result = injector.getValue(this, null, PageManager.class, annotatedElement, null);
    assertNull(result);
  }

}
