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

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.sling.api.scripting.SlingScriptHelper;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.Silent.class)
public class SlingObjectOverlayInjectorResource_RequestContextTest extends SlingObjectOverlayInjectorRequestTest {

  @Before
  public void setupRequestContext() {
    when(this.requestContext.getThreadRequest()).thenReturn(this.request);
  }

  @Override
  protected Object adaptable() {
    return this.resource;
  }

  @Override
  @Test
  public void testInvalid() {
    Object result = this.injector.getValue(new StringBuffer(), null, SlingScriptHelper.class, this.annotatedElement, mock(DisposalCallbackRegistry.class));
    assertSame(this.scriptHelper, result);
  }

}
