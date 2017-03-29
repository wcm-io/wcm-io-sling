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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.lang.reflect.AnnotatedElement;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.adobe.granite.xss.XSSAPI;
import com.day.cq.i18n.I18n;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.AuthoringUIMode;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMMode;
import com.day.cq.wcm.api.components.ComponentContext;
import com.day.cq.wcm.api.designer.Design;
import com.day.cq.wcm.api.designer.Designer;
import com.day.cq.wcm.api.designer.Style;

import io.wcm.sling.commons.request.RequestContext;

@RunWith(MockitoJUnitRunner.class)
public class AemObjectInjectorPageTest {

  @Rule
  public SlingContext context = new SlingContext(ResourceResolverType.RESOURCERESOLVER_MOCK);

  @Mock
  private AnnotatedElement annotatedElement;
  @Mock
  private Page page;
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
  protected TagManager tagManager;
  @Mock
  private Design design;
  @Mock
  protected RequestContext requestContext;

  private AemObjectInjector injector;

  @Before
  public void setUp() {
    context.registerService(RequestContext.class, requestContext);
    context.registerInjectActivateService(new ModelsImplConfiguration(),
        "requestThreadLocal", true);

    injector = context.registerInjectActivateService(new AemObjectInjector());

    when(page.adaptTo(Resource.class)).thenReturn(resource);
    when(resource.getResourceResolver()).thenReturn(resourceResolver);
    when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
    when(resourceResolver.adaptTo(Designer.class)).thenReturn(designer);
    when(resourceResolver.adaptTo(TagManager.class)).thenReturn(tagManager);
    when(pageManager.getContainingPage(resource)).thenReturn(resourcePage);
    when(designer.getDesign(any(Page.class))).thenReturn(design);
  }

  @Test
  public void testPageManager() {
    Object result = injector.getValue(page, null, PageManager.class, annotatedElement, null);
    assertSame(pageManager, result);
  }

  @Test
  public void testCurrentPage() {
    Object result = injector.getValue(page, null, Page.class, annotatedElement, null);
    assertSame(resourcePage, result);
  }

  @Test
  public void testResourcePage() {
    Object result = injector.getValue(page, "resourcePage", Page.class, annotatedElement, null);
    assertSame(resourcePage, result);
  }

  @Test
  public void testWcmMode() {
    Object result = injector.getValue(page, null, WCMMode.class, annotatedElement, null);
    assertNull(result);
  }

  @Test
  public void testAuthoringUiMode() {
    Object result = injector.getValue(page, null, AuthoringUIMode.class, annotatedElement, null);
    assertNull(result);
  }

  @Test
  public void testComponentContext() {
    Object result = injector.getValue(page, null, ComponentContext.class, annotatedElement, null);
    assertNull(result);
  }

  @Test
  public void testDesigner() {
    Object result = injector.getValue(page, null, Designer.class, annotatedElement, null);
    assertSame(designer, result);
  }

  @Test
  public void testTagManager() {
    Object result = injector.getValue(page, null, TagManager.class, annotatedElement, null);
    assertSame(tagManager, result);
  }

  @Test
  public void testDesign() {
    Object result = injector.getValue(page, null, Design.class, annotatedElement, null);
    assertSame(design, result);
  }

  @Test
  public void testStyle() {
    Object result = injector.getValue(page, null, Style.class, annotatedElement, null);
    assertNull(result);
  }

  @Test
  public void testXssApi() {
    Object result = injector.getValue(page, null, XSSAPI.class, annotatedElement, null);
    assertNull(result);
  }

  @Test
  public void testI18n() {
    Object result = injector.getValue(page, null, I18n.class, annotatedElement, null);
    assertNull(result);
  }

  @Test
  public void testInvalid() {
    Object result = injector.getValue(this, null, PageManager.class, annotatedElement, null);
    assertNull(result);
  }

}
