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
package io.wcm.sling.commons.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import io.wcm.testing.mock.aem.junit.AemContext;

import org.junit.Rule;
import org.junit.Test;

public class ResourceTypeAemContextTest {

  @Rule
  public AemContext context = new AemContext();

  @Test
  public void testMakeAbsolute_Invalid() {
    assertNull(ResourceType.makeAbsolute(null, context.resourceResolver()));
    assertEquals("", ResourceType.makeAbsolute("", context.resourceResolver()));
  }

  @Test
  public void testMakeAbsolute_ExistingAppsResource() {
    context.create().resource("/apps/any/path");

    assertEquals("/any/path", ResourceType.makeAbsolute("/any/path", context.resourceResolver()));
    assertEquals("/apps/any/path", ResourceType.makeAbsolute("any/path", context.resourceResolver()));
  }

  @Test
  public void testMakeAbsolute_ExistingLibsResource() {
    context.create().resource("/libs/any/path");

    assertEquals("/any/path", ResourceType.makeAbsolute("/any/path", context.resourceResolver()));
    assertEquals("/libs/any/path", ResourceType.makeAbsolute("any/path", context.resourceResolver()));
  }

  @Test
  public void testMakeAbsolute_NonExistingResource() {
    assertEquals("/any/path", ResourceType.makeAbsolute("/any/path", context.resourceResolver()));
    assertEquals("any/path", ResourceType.makeAbsolute("any/path", context.resourceResolver()));
  }

}
