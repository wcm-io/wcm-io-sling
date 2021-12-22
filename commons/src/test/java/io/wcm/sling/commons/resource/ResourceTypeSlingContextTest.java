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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
@SuppressWarnings("null")
class ResourceTypeSlingContextTest {

  private final AemContext context = new AemContext();

  @Test
  @SuppressWarnings("unused")
  void testMakeAbsolute_Invalid() {
    assertNull(ResourceType.makeAbsolute(null, context.resourceResolver()));
    assertEquals("", ResourceType.makeAbsolute("", context.resourceResolver()));
  }

  @Test
  void testMakeAbsolute_ExistingAppsResource() {
    context.create().resource("/apps/any/path");

    assertEquals("/any/path", ResourceType.makeAbsolute("/any/path", context.resourceResolver()));
    assertEquals("/apps/any/path", ResourceType.makeAbsolute("any/path", context.resourceResolver()));
  }

  @Test
  void testMakeAbsolute_ExistingLibsResource() {
    context.create().resource("/libs/any/path");

    assertEquals("/any/path", ResourceType.makeAbsolute("/any/path", context.resourceResolver()));
    assertEquals("/libs/any/path", ResourceType.makeAbsolute("any/path", context.resourceResolver()));
  }

  @Test
  void testMakeAbsolute_NonExistingResource() {
    assertEquals("/any/path", ResourceType.makeAbsolute("/any/path", context.resourceResolver()));
    assertEquals("any/path", ResourceType.makeAbsolute("any/path", context.resourceResolver()));
  }

}
