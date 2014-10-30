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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.when;
import io.wcm.sling.commons.request.RequestContext;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.AnnotatedElement;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
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
import com.day.cq.wcm.api.designer.Cell;
import com.day.cq.wcm.api.designer.Design;
import com.day.cq.wcm.api.designer.Designer;
import com.day.cq.wcm.api.designer.Style;
import com.google.common.collect.ImmutableMap;

@RunWith(MockitoJUnitRunner.class)
public class AemObjectInjectorRequestTest {

  @Rule
  public SlingContext context = new SlingContext(ResourceResolverType.RESOURCERESOLVER_MOCK);

  @Mock
  protected AnnotatedElement annotatedElement;
  @Mock
  protected SlingHttpServletRequest request;
  @Mock
  protected Resource resource;
  @Mock
  protected ResourceResolver resourceResolver;
  @Mock
  protected PageManager pageManager;
  @Mock
  protected Page currentPage;
  @Mock
  protected Page resourcePage;
  @Mock
  protected ComponentContext componentContext;
  @Mock
  protected Designer designer;
  @Mock
  protected Design design;
  @Mock
  protected Style style;
  @Mock
  protected Cell cell;
  @Mock
  protected XSSAPI xssApi;
  @Mock
  protected RequestContext requestContext;

  protected AemObjectInjector injector;

  @Before
  public void setUp() {
    context.registerService(RequestContext.class, requestContext);
    context.registerInjectActivateService(new ModelsImplConfiguration(),
        ImmutableMap.<String, Object>of(ModelsImplConfiguration.PARAM_REQUEST_THREAD_LOCAL,
            ModelsImplConfiguration.PARAM_REQUEST_THREAD_LOCAL_DEFAULT));

    injector = context.registerInjectActivateService(new AemObjectInjector());
    when(request.getResource()).thenReturn(resource);
    when(request.getResourceResolver()).thenReturn(resourceResolver);
    when(request.getAttribute(ComponentContext.CONTEXT_ATTR_NAME)).thenReturn(componentContext);
    when(request.getAttribute(WCMMode.REQUEST_ATTRIBUTE_NAME)).thenReturn(WCMMode.PREVIEW);
    when(request.getAttribute(AuthoringUIMode.REQUEST_ATTRIBUTE_NAME)).thenReturn(AuthoringUIMode.TOUCH);
    when(request.adaptTo(XSSAPI.class)).thenReturn(xssApi);
    when(resource.getResourceResolver()).thenReturn(resourceResolver);
    when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
    when(resourceResolver.adaptTo(Designer.class)).thenReturn(designer);
    when(componentContext.getPage()).thenReturn(currentPage);
    when(componentContext.getCell()).thenReturn(cell);
    when(pageManager.getContainingPage(resource)).thenReturn(resourcePage);
    when(designer.getDesign(any(Page.class))).thenReturn(design);
    when(design.getStyle(cell)).thenReturn(style);
  }

  protected Object adaptable() {
    return request;
  }

  @Test
  public void testPageManager() {
    Object result = injector.getValue(adaptable(), null, PageManager.class, annotatedElement, null);
    assertSame(pageManager, result);
  }

  @Test
  public void testCurrentPage() {
    Object result = injector.getValue(adaptable(), null, Page.class, annotatedElement, null);
    assertSame(currentPage, result);
  }

  @Test
  public void testResourcePage() {
    Object result = injector.getValue(adaptable(), AemObjectInjector.RESOURCE_PAGE, Page.class, annotatedElement, null);
    assertSame(resourcePage, result);
  }

  @Test
  public void testWcmMode() {
    Object result = injector.getValue(adaptable(), null, WCMMode.class, annotatedElement, null);
    assertSame(WCMMode.PREVIEW, result);
  }

  @Test
  public void testAuthoringUiMode() {
    Object result = injector.getValue(adaptable(), null, AuthoringUIMode.class, annotatedElement, null);
    assertSame(AuthoringUIMode.TOUCH, result);
  }

  @Test
  public void testComponentContext() {
    Object result = injector.getValue(adaptable(), null, ComponentContext.class, annotatedElement, null);
    assertSame(componentContext, result);
  }

  @Test
  public void testDesigner() {
    Object result = injector.getValue(adaptable(), null, Designer.class, annotatedElement, null);
    assertSame(designer, result);
  }

  @Test
  public void testDesign() {
    Object result = injector.getValue(adaptable(), null, Design.class, annotatedElement, null);
    assertSame(design, result);
  }

  @Test
  public void testStyle() {
    Object result = injector.getValue(adaptable(), null, Style.class, annotatedElement, null);
    assertSame(style, result);
  }

  @Test
  public void testXssApi() {
    Object result = injector.getValue(adaptable(), null, XSSAPI.class, annotatedElement, null);
    assertSame(xssApi, result);
  }

  @Test
  public void testResourceI18n() throws IOException {
    when(currentPage.getLanguage(anyBoolean())).thenReturn(Locale.US);
    when(request.getResourceBundle(Locale.US)).thenReturn(getSampleResourceBundle());

    I18n result = (I18n)injector.getValue(adaptable(), null, I18n.class, annotatedElement, null);
    assertNotNull(result);
    assertEquals("mytranslation", result.get("mykey"));
  }

  @Test
  public void testUserI18n() throws IOException {
    when(request.getResourceBundle(null)).thenReturn(getSampleResourceBundle());

    I18n result = (I18n)injector.getValue(adaptable(), AemObjectInjector.USER_I18N, I18n.class, annotatedElement, null);
    assertNotNull(result);
    assertEquals("mytranslation", result.get("mykey"));
  }

  @Test
  public void testInvalid() {
    Object result = injector.getValue(new StringBuffer(), null, PageManager.class, annotatedElement, null);
    assertNull(result);
  }

  private static ResourceBundle getSampleResourceBundle() throws IOException {
    InputStream resourceBundleStream = AemObjectInjectorRequestTest.class.getResourceAsStream("/sample-i18n.properties");
    ResourceBundle resourceBundle = new PropertyResourceBundle(resourceBundleStream);
    resourceBundleStream.close();
    return resourceBundle;
  }

}
