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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.AnnotatedElement;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.adobe.cq.sightly.WCMBindings;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.xss.XSSAPI;
import com.day.cq.i18n.I18n;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.AuthoringUIMode;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMMode;
import com.day.cq.wcm.api.components.ComponentContext;
import com.day.cq.wcm.api.designer.Cell;
import com.day.cq.wcm.api.designer.Design;
import com.day.cq.wcm.api.designer.Designer;
import com.day.cq.wcm.api.designer.Style;

import io.wcm.sling.commons.request.RequestContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("deprecation")
class AemObjectInjectorRequestTest {

  private final AemContext context = new AemContext(ResourceResolverType.RESOURCERESOLVER_MOCK);

  @Mock
  protected AnnotatedElement annotatedElement;
  @Mock
  protected SlingHttpServletRequest request;
  @Mock
  protected Page page;
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
  protected TagManager tagManager;
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
  @Mock
  protected WorkflowSession workflowSession;

  protected AemObjectInjector injector;

  @BeforeEach
  @SuppressWarnings("null")
  void setUp() {
    context.registerService(RequestContext.class, requestContext);
    context.registerInjectActivateService(new ModelsImplConfiguration(),
        "requestThreadLocal", true);

    injector = context.registerInjectActivateService(new AemObjectInjector());

    when(page.adaptTo(Resource.class)).thenReturn(resource);
    when(request.getResource()).thenReturn(resource);
    when(request.getResourceResolver()).thenReturn(resourceResolver);
    when(request.getAttribute(ComponentContext.CONTEXT_ATTR_NAME)).thenReturn(componentContext);
    when(request.getAttribute(WCMMode.REQUEST_ATTRIBUTE_NAME)).thenReturn(WCMMode.PREVIEW);
    when(request.getAttribute(AuthoringUIMode.REQUEST_ATTRIBUTE_NAME)).thenReturn(AuthoringUIMode.TOUCH);
    when(request.adaptTo(XSSAPI.class)).thenReturn(xssApi);
    when(resource.getResourceResolver()).thenReturn(resourceResolver);
    when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
    when(resourceResolver.adaptTo(Designer.class)).thenReturn(designer);
    when(resourceResolver.adaptTo(TagManager.class)).thenReturn(tagManager);
    when(resourceResolver.adaptTo(WorkflowSession.class)).thenReturn(workflowSession);
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
  void testPageManager() {
    Object result = injector.getValue(adaptable(), null, PageManager.class, annotatedElement, mock(DisposalCallbackRegistry.class));
    assertSame(pageManager, result);
  }

  @Test
  void testCurrentPage() {
    Object result = injector.getValue(adaptable(), null, Page.class, annotatedElement, mock(DisposalCallbackRegistry.class));
    assertSame(currentPage, result);
  }

  @Test
  void testResourcePage() {
    Object result = injector.getValue(adaptable(), AemObjectInjector.RESOURCE_PAGE, Page.class, annotatedElement, mock(DisposalCallbackRegistry.class));
    assertSame(resourcePage, result);
  }

  @Test
  void testWcmMode() {
    Object result = injector.getValue(adaptable(), null, WCMMode.class, annotatedElement, mock(DisposalCallbackRegistry.class));
    assertSame(WCMMode.PREVIEW, result);
  }

  @Test
  void testAuthoringUiMode() {
    Object result = injector.getValue(adaptable(), null, AuthoringUIMode.class, annotatedElement, mock(DisposalCallbackRegistry.class));
    assertSame(AuthoringUIMode.TOUCH, result);
  }

  @Test
  void testComponentContext() {
    Object result = injector.getValue(adaptable(), null, ComponentContext.class, annotatedElement, mock(DisposalCallbackRegistry.class));
    assertSame(componentContext, result);
  }

  @Test
  void testDesigner() {
    Object result = injector.getValue(adaptable(), null, Designer.class, annotatedElement, mock(DisposalCallbackRegistry.class));
    assertSame(designer, result);
  }

  @Test
  void testTagManager() {
    Object result = injector.getValue(adaptable(), null, TagManager.class, annotatedElement, mock(DisposalCallbackRegistry.class));
    assertSame(tagManager, result);
  }

  @Test
  void testDesign() {
    Object result = injector.getValue(adaptable(), null, Design.class, annotatedElement, mock(DisposalCallbackRegistry.class));
    assertSame(design, result);
  }

  @Test
  void testStyle_FromDesigner() {
    Object result = injector.getValue(adaptable(), null, Style.class, annotatedElement, mock(DisposalCallbackRegistry.class));
    assertSame(style, result);
  }

  @Test
  @SuppressWarnings("null")
  void testStyle_FromSlingBindings() {
    Style styleFromBindings = mock(Style.class);

    SlingBindings slingBindings = new SlingBindings();
    slingBindings.setRequest(context.request());
    slingBindings.put(WCMBindings.CURRENT_STYLE, styleFromBindings);
    when(request.getAttribute(SlingBindings.class.getName())).thenReturn(slingBindings);

    Object result = injector.getValue(adaptable(), null, Style.class, annotatedElement, mock(DisposalCallbackRegistry.class));
    assertSame(styleFromBindings, result);
  }

  @Test
  void testXssApi() {
    Object result = injector.getValue(adaptable(), null, XSSAPI.class, annotatedElement, mock(DisposalCallbackRegistry.class));
    assertSame(xssApi, result);
  }

  @Test
  void testResourceI18n() throws IOException {
    when(currentPage.getLanguage(anyBoolean())).thenReturn(Locale.US);
    when(request.getResourceBundle(Locale.US)).thenReturn(getSampleResourceBundle());

    I18n result = (I18n)injector.getValue(adaptable(), null, I18n.class, annotatedElement, mock(DisposalCallbackRegistry.class));
    assertNotNull(result);
    assertEquals("mytranslation", result.get("mykey"));
  }

  @Test
  void testUserI18n() throws IOException {
    when(request.getResourceBundle(null)).thenReturn(getSampleResourceBundle());

    I18n result = (I18n)injector.getValue(adaptable(), AemObjectInjector.USER_I18N, I18n.class, annotatedElement, mock(DisposalCallbackRegistry.class));
    assertNotNull(result);
    assertEquals("mytranslation", result.get("mykey"));
  }

  @Test
  void testWorkflowSession() {
    Object result = injector.getValue(adaptable(), null, WorkflowSession.class, annotatedElement, mock(DisposalCallbackRegistry.class));
    assertSame(workflowSession, result);
  }


  @Test
  void testInvalid() {
    Object result = injector.getValue(new StringBuffer(), null, PageManager.class, annotatedElement, mock(DisposalCallbackRegistry.class));
    assertNull(result);
  }

  private static ResourceBundle getSampleResourceBundle() throws IOException {
    InputStream resourceBundleStream = AemObjectInjectorRequestTest.class.getResourceAsStream("/sample-i18n.properties");
    ResourceBundle resourceBundle = new PropertyResourceBundle(resourceBundleStream);
    resourceBundleStream.close();
    return resourceBundle;
  }

}
