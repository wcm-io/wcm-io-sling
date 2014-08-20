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
import static org.mockito.Mockito.when;

import java.lang.reflect.AnnotatedElement;

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
import com.day.cq.wcm.api.designer.Design;
import com.day.cq.wcm.api.designer.Designer;
import com.day.cq.wcm.api.designer.Style;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("javadoc")
public class AemObjectInjectorResourceResolverTest {

  private final AemObjectInjector injector = new AemObjectInjector();

  @Mock
  private AnnotatedElement annotatedElement;
  @Mock
  private ResourceResolver resourceResolver;
  @Mock
  private PageManager pageManager;
  @Mock
  private Designer designer;

  @Before
  public void setUp() {
    when(this.resourceResolver.adaptTo(PageManager.class)).thenReturn(this.pageManager);
    when(this.resourceResolver.adaptTo(Designer.class)).thenReturn(this.designer);
  }

  @Test
  public void testPageManager() {
    Object result = this.injector.getValue(this.resourceResolver, null, PageManager.class, this.annotatedElement, null);
    assertSame(this.pageManager, result);
  }

  @Test
  public void testCurrentPage() {
    Object result = this.injector.getValue(this.resourceResolver, null, Page.class, this.annotatedElement, null);
    assertNull(result);
  }

  @Test
  public void testResourcePage() {
    Object result = this.injector.getValue(this.resourceResolver, "resourcePage", Page.class, this.annotatedElement, null);
    assertNull(result);
  }

  @Test
  public void testWcmMode() {
    Object result = this.injector.getValue(this.resourceResolver, null, WCMMode.class, this.annotatedElement, null);
    assertNull(result);
  }

  @Test
  public void testAuthoringUiMode() {
    Object result = this.injector.getValue(this.resourceResolver, null, AuthoringUIMode.class, this.annotatedElement, null);
    assertNull(result);
  }

  @Test
  public void testComponentContext() {
    Object result = this.injector.getValue(this.resourceResolver, null, ComponentContext.class, this.annotatedElement, null);
    assertNull(result);
  }

  @Test
  public void testDesigner() {
    Object result = this.injector.getValue(this.resourceResolver, null, Designer.class, this.annotatedElement, null);
    assertSame(this.designer, result);
  }

  @Test
  public void testDesign() {
    Object result = this.injector.getValue(this.resourceResolver, null, Design.class, this.annotatedElement, null);
    assertNull(result);
  }

  @Test
  public void testStyle() {
    Object result = this.injector.getValue(this.resourceResolver, null, Style.class, this.annotatedElement, null);
    assertNull(result);
  }

  @Test
  public void testXssApi() {
    Object result = this.injector.getValue(this.resourceResolver, null, XSSAPI.class, this.annotatedElement, null);
    assertNull(result);
  }

  @Test
  public void testInvalid() {
    Object result = this.injector.getValue(this, null, PageManager.class, this.annotatedElement, null);
    assertNull(result);
  }

}
