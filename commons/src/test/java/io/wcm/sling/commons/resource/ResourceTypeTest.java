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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.sling.api.resource.Resource;
import org.junit.Rule;
import org.junit.Test;

import io.wcm.testing.mock.aem.junit.AemContext;

public class ResourceTypeTest {

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

  @Test
  public void testMakeRelative() {
    assertEquals("path1/path2", ResourceType.makeRelative("/apps/path1/path2", context.resourceResolver()));
    assertEquals("path1/path2", ResourceType.makeRelative("/libs/path1/path2", context.resourceResolver()));
    assertEquals("/any/path", ResourceType.makeRelative("/any/path", context.resourceResolver()));
    assertEquals("", ResourceType.makeRelative("", context.resourceResolver()));
    assertNull(ResourceType.makeRelative(null, context.resourceResolver()));
  }

  @SuppressWarnings("deprecation")
  @Test
  public void testMakeRelativeWithoutResourceResolver() {
    assertEquals("path1/path2", ResourceType.makeRelative("/apps/path1/path2"));
    assertEquals("path1/path2", ResourceType.makeRelative("/libs/path1/path2"));
    assertEquals("/any/path", ResourceType.makeRelative("/any/path"));
    assertEquals("", ResourceType.makeRelative(""));
    assertNull(ResourceType.makeRelative(null));
  }

  @Test
  public void testEquals() {
    assertTrue(ResourceType.equals("/apps/path1/path2", "/apps/path1/path2", context.resourceResolver()));
    assertTrue(ResourceType.equals("path1/path2", "/apps/path1/path2", context.resourceResolver()));
    assertTrue(ResourceType.equals("/apps/path1/path2", "path1/path2", context.resourceResolver()));
    assertTrue(ResourceType.equals("path1/path2", "path1/path2", context.resourceResolver()));
    assertTrue(ResourceType.equals("/apps/path1/path2", "/libs/path1/path2", context.resourceResolver()));

    assertFalse(ResourceType.equals("/apps/path1/path2", "/any/path1/path2", context.resourceResolver()));
    assertFalse(ResourceType.equals("", "/apps/path1/path2", context.resourceResolver()));
    assertFalse(ResourceType.equals("/apps/path1/path2", "", context.resourceResolver()));
    assertFalse(ResourceType.equals("/apps/path1/path2", null, context.resourceResolver()));
    assertFalse(ResourceType.equals(null, "/any/path1/path2", context.resourceResolver()));
    assertTrue(ResourceType.equals("", "", context.resourceResolver()));
    assertTrue(ResourceType.equals(null, null, context.resourceResolver()));
  }

  @SuppressWarnings("deprecation")
  @Test
  public void testEqualsWithoutResourceResolver() {
    assertTrue(ResourceType.equals("/apps/path1/path2", "/apps/path1/path2"));
    assertTrue(ResourceType.equals("path1/path2", "/apps/path1/path2"));
    assertTrue(ResourceType.equals("/apps/path1/path2", "path1/path2"));
    assertTrue(ResourceType.equals("path1/path2", "path1/path2"));
    assertTrue(ResourceType.equals("/apps/path1/path2", "/libs/path1/path2"));

    assertFalse(ResourceType.equals("/apps/path1/path2", "/any/path1/path2"));
    assertFalse(ResourceType.equals("", "/apps/path1/path2"));
    assertFalse(ResourceType.equals("/apps/path1/path2", ""));
    assertFalse(ResourceType.equals("/apps/path1/path2", null));
    assertFalse(ResourceType.equals(null, "/any/path1/path2"));
    assertTrue(ResourceType.equals("", ""));
    assertTrue(ResourceType.equals(null, null));
  }

  @Test
  public void testIs() {
    context.create().resource("/apps/app1/type1");
    context.create().resource("/apps/app1/type2", "sling:resourceSuperType", "/apps/app1/type1");
    context.create().resource("/apps/app1/type3", "sling:resourceSuperType", "/apps/app1/type2");
    context.create().resource("/apps/app1/type4");

    Resource resource1 = context.create().resource("/content/resource1", "sling:resourceType", "/apps/app1/type1");
    Resource resource2 = context.create().resource("/content/resource2", "sling:resourceType", "/apps/app1/type2");
    Resource resource3 = context.create().resource("/content/resource3", "sling:resourceType", "/apps/app1/type3");
    Resource resource4 = context.create().resource("/content/resource4", "sling:resourceType", "/apps/app1/type4");

    assertTrue(ResourceType.is(resource1, "/apps/app1/type1"));
    assertFalse(ResourceType.is(resource1, "/apps/app1/type2"));
    assertFalse(ResourceType.is(resource1, "/apps/app1/type3"));
    assertFalse(ResourceType.is(resource1, "/apps/app1/type4"));

    assertTrue(ResourceType.is(resource2, "/apps/app1/type1"));
    assertTrue(ResourceType.is(resource2, "/apps/app1/type2"));
    assertFalse(ResourceType.is(resource2, "/apps/app1/type3"));
    assertFalse(ResourceType.is(resource2, "/apps/app1/type4"));

    assertTrue(ResourceType.is(resource3, "/apps/app1/type1"));
    assertTrue(ResourceType.is(resource3, "/apps/app1/type2"));
    assertTrue(ResourceType.is(resource3, "/apps/app1/type3"));
    assertFalse(ResourceType.is(resource3, "/apps/app1/type4"));

    assertFalse(ResourceType.is(resource4, "/apps/app1/type1"));
    assertFalse(ResourceType.is(resource4, "/apps/app1/type2"));
    assertFalse(ResourceType.is(resource4, "/apps/app1/type3"));
    assertTrue(ResourceType.is(resource4, "/apps/app1/type4"));
  }

}
