/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2017 wcm.io
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

import org.junit.Test;

import com.day.jcr.vault.util.Text;

@SuppressWarnings("deprecation")
public class ResourcePathTest {

  @Test
  public void testGetAbsoluteLevel() {
    assertEquals("/a", Text.getAbsoluteParent("/a/b/c/d", ResourcePath.getAbsoluteLevel("/a")));
    assertEquals("/a/b", Text.getAbsoluteParent("/a/b/c/d", ResourcePath.getAbsoluteLevel("/a/b")));
    assertEquals("/a/b/c", Text.getAbsoluteParent("/a/b/c/d", ResourcePath.getAbsoluteLevel("/a/b/c")));

    assertEquals(2, ResourcePath.getAbsoluteLevel("/a/b/c"));
    assertEquals(1, ResourcePath.getAbsoluteLevel("/a/b"));
    assertEquals(0, ResourcePath.getAbsoluteLevel("/a"));
    assertEquals(-1, ResourcePath.getAbsoluteLevel("/"));
    assertEquals(-1, ResourcePath.getAbsoluteLevel(""));
    assertEquals(-1, ResourcePath.getAbsoluteLevel(null));
  }

}
