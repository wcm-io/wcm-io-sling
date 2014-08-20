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

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("javadoc")
public class SlingObjectInjectorTest {

  private final SlingObjectInjector injector = new SlingObjectInjector();

  @Mock
  private AnnotatedElement annotatedElement;
  @Mock
  private SlingHttpServletRequest request;
  @Mock
  private SlingHttpServletResponse response;
  @Mock
  private SlingScriptHelper scriptHelper;

  @Before
  public void setUp() {
    SlingBindings bindings = new SlingBindings();
    bindings.put(SlingBindings.SLING, this.scriptHelper);
    when(this.request.getAttribute(SlingBindings.class.getName())).thenReturn(bindings);
    when(this.scriptHelper.getResponse()).thenReturn(this.response);
  }

  @Test
  public void testResponse() {
    Object result = this.injector.getValue(this.request, null, SlingHttpServletResponse.class, this.annotatedElement, null);
    assertSame(this.response, result);
  }

  @Test
  public void testScriptHelper() {
    Object result = this.injector.getValue(this.request, null, SlingScriptHelper.class, this.annotatedElement, null);
    assertSame(this.scriptHelper, result);
  }

  @Test
  public void testInvalid() {
    Object result = this.injector.getValue(this, null, SlingScriptHelper.class, this.annotatedElement, null);
    assertNull(result);
  }

}
