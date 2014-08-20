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

import java.lang.reflect.AnnotatedElement;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.adobe.granite.xss.XSSAPI;
import com.day.cq.wcm.api.AuthoringUIMode;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMMode;
import com.day.cq.wcm.api.components.ComponentContext;
import com.day.cq.wcm.api.designer.Cell;
import com.day.cq.wcm.api.designer.Design;
import com.day.cq.wcm.api.designer.Designer;
import com.day.cq.wcm.api.designer.Style;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("javadoc")
public class AemObjectRequestInjectorTest {

  private final AemObjectRequestInjector injector = new AemObjectRequestInjector();

  @Mock
  private AnnotatedElement annotatedElement;
  @Mock
  private SlingHttpServletRequest request;
  @Mock
  private Resource resource;
  @Mock
  private ResourceResolver resourceResolver;
  @Mock
  private PageManager pageManager;
  @Mock
  private Page currentPage;
  @Mock
  private Page resourcePage;
  @Mock
  private ComponentContext componentContext;
  @Mock
  private Designer designer;
  @Mock
  private Design design;
  @Mock
  private Style style;
  @Mock
  private Cell cell;
  @Mock
  private XSSAPI xssApi;

  @Before
  public void setUp() {
    when(this.request.getResource()).thenReturn(this.resource);
    when(this.request.getResourceResolver()).thenReturn(this.resourceResolver);
    when(this.request.getAttribute(ComponentContext.CONTEXT_ATTR_NAME)).thenReturn(this.componentContext);
    when(this.request.getAttribute(WCMMode.REQUEST_ATTRIBUTE_NAME)).thenReturn(WCMMode.PREVIEW);
    when(this.request.getAttribute(AuthoringUIMode.REQUEST_ATTRIBUTE_NAME)).thenReturn(AuthoringUIMode.TOUCH);
    when(this.request.adaptTo(XSSAPI.class)).thenReturn(this.xssApi);
    when(this.resource.getResourceResolver()).thenReturn(this.resourceResolver);
    when(this.resourceResolver.adaptTo(PageManager.class)).thenReturn(this.pageManager);
    when(this.resourceResolver.adaptTo(Designer.class)).thenReturn(this.designer);
    when(this.componentContext.getPage()).thenReturn(this.currentPage);
    when(this.componentContext.getCell()).thenReturn(this.cell);
    when(this.pageManager.getContainingPage(this.resource)).thenReturn(this.resourcePage);
    when(this.designer.getDesign(any(Page.class))).thenReturn(this.design);
    when(this.design.getStyle(this.cell)).thenReturn(this.style);
  }

  @Test
  public void testPageManager() {
    Object result = this.injector.getValue(this.request, null, PageManager.class, this.annotatedElement, null);
    assertSame(this.pageManager, result);
  }

  @Test
  public void testCurrentPage() {
    Object result = this.injector.getValue(this.request, null, Page.class, this.annotatedElement, null);
    assertSame(this.currentPage, result);
  }

  @Test
  public void testResourcePage() {
    Object result = this.injector.getValue(this.request, "resourcePage", Page.class, this.annotatedElement, null);
    assertSame(this.resourcePage, result);
  }

  @Test
  public void testWcmMode() {
    Object result = this.injector.getValue(this.request, null, WCMMode.class, this.annotatedElement, null);
    assertSame(WCMMode.PREVIEW, result);
  }

  @Test
  public void testAuthoringUiMode() {
    Object result = this.injector.getValue(this.request, null, AuthoringUIMode.class, this.annotatedElement, null);
    assertSame(AuthoringUIMode.TOUCH, result);
  }

  @Test
  public void testComponentContext() {
    Object result = this.injector.getValue(this.request, null, ComponentContext.class, this.annotatedElement, null);
    assertSame(this.componentContext, result);
  }

  @Test
  public void testDesigner() {
    Object result = this.injector.getValue(this.request, null, Designer.class, this.annotatedElement, null);
    assertSame(this.designer, result);
  }

  @Test
  public void testDesign() {
    Object result = this.injector.getValue(this.request, null, Design.class, this.annotatedElement, null);
    assertSame(this.design, result);
  }

  @Test
  public void testStyle() {
    Object result = this.injector.getValue(this.request, null, Style.class, this.annotatedElement, null);
    assertSame(this.style, result);
  }

  @Test
  public void testXssApi() {
    Object result = this.injector.getValue(this.request, null, XSSAPI.class, this.annotatedElement, null);
    assertSame(this.xssApi, result);
  }

  @Test
  public void testInvalid() {
    Object result = this.injector.getValue(this, null, PageManager.class, this.annotatedElement, null);
    assertNull(result);
  }

}
