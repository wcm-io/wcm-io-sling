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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.AnnotatedElement;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.adobe.granite.workflow.WorkflowSession;
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
@SuppressWarnings("null")
public class AemObjectInjectorResourceResolverTest {

  @Rule
  public SlingContext context = new SlingContext(ResourceResolverType.RESOURCERESOLVER_MOCK);

  @Mock
  private AnnotatedElement annotatedElement;
  @Mock
  private ResourceResolver resourceResolver;
  @Mock
  private PageManager pageManager;
  @Mock
  private Designer designer;
  @Mock
  protected TagManager tagManager;
  @Mock
  protected RequestContext requestContext;
  @Mock
  protected WorkflowSession workflowSession;

  private AemObjectInjector injector;

  @Before
  public void setUp() {
    context.registerService(RequestContext.class, requestContext);
    context.registerInjectActivateService(new ModelsImplConfiguration(),
        "requestThreadLocal", true);

    injector = context.registerInjectActivateService(new AemObjectInjector());

    when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
    when(resourceResolver.adaptTo(Designer.class)).thenReturn(designer);
    when(resourceResolver.adaptTo(WorkflowSession.class)).thenReturn(workflowSession);
    when(resourceResolver.adaptTo(TagManager.class)).thenReturn(tagManager);
  }

  @Test
  public void testPageManager() {
    Object result = injector.getValue(resourceResolver, null, PageManager.class, annotatedElement, mock(DisposalCallbackRegistry.class));
    assertSame(pageManager, result);
  }

  @Test
  public void testCurrentPage() {
    Object result = injector.getValue(resourceResolver, null, Page.class, annotatedElement, mock(DisposalCallbackRegistry.class));
    assertNull(result);
  }

  @Test
  public void testResourcePage() {
    Object result = injector.getValue(resourceResolver, "resourcePage", Page.class, annotatedElement, mock(DisposalCallbackRegistry.class));
    assertNull(result);
  }

  @Test
  public void testWcmMode() {
    Object result = injector.getValue(resourceResolver, null, WCMMode.class, annotatedElement, mock(DisposalCallbackRegistry.class));
    assertNull(result);
  }

  @Test
  public void testAuthoringUiMode() {
    Object result = injector.getValue(resourceResolver, null, AuthoringUIMode.class, annotatedElement, mock(DisposalCallbackRegistry.class));
    assertNull(result);
  }

  @Test
  public void testComponentContext() {
    Object result = injector.getValue(resourceResolver, null, ComponentContext.class, annotatedElement, mock(DisposalCallbackRegistry.class));
    assertNull(result);
  }

  @Test
  public void testDesigner() {
    Object result = injector.getValue(resourceResolver, null, Designer.class, annotatedElement, mock(DisposalCallbackRegistry.class));
    assertSame(designer, result);
  }

  @Test
  public void testTagManager() {
    Object result = injector.getValue(resourceResolver, null, TagManager.class, annotatedElement, mock(DisposalCallbackRegistry.class));
    assertSame(tagManager, result);
  }

  @Test
  public void testDesign() {
    Object result = injector.getValue(resourceResolver, null, Design.class, annotatedElement, mock(DisposalCallbackRegistry.class));
    assertNull(result);
  }

  @Test
  public void testStyle() {
    Object result = injector.getValue(resourceResolver, null, Style.class, annotatedElement, mock(DisposalCallbackRegistry.class));
    assertNull(result);
  }

  @Test
  public void testXssApi() {
    Object result = injector.getValue(resourceResolver, null, XSSAPI.class, annotatedElement, mock(DisposalCallbackRegistry.class));
    assertNull(result);
  }

  @Test
  public void testI18n() {
    Object result = injector.getValue(resourceResolver, null, I18n.class, annotatedElement, mock(DisposalCallbackRegistry.class));
    assertNull(result);
  }

  @Test
  public void testWorkflowSession() {
    Object result = injector.getValue(resourceResolver, null, WorkflowSession.class, annotatedElement, mock(DisposalCallbackRegistry.class));
    assertSame(workflowSession, result);
  }

  @Test
  public void testInvalid() {
    Object result = injector.getValue(this, null, PageManager.class, annotatedElement, mock(DisposalCallbackRegistry.class));
    assertNull(result);
  }

}
