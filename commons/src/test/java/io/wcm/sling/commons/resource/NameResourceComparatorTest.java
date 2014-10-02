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
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.sling.api.resource.Resource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NameResourceComparatorTest {

  @Mock
  private Resource resource1;
  @Mock
  private Resource resource2;

  private NameResourceComparator underTest;

  @Before
  public void setUp() {
    underTest = new NameResourceComparator();
    when(resource1.getName()).thenReturn("resource1");
    when(resource2.getName()).thenReturn("resource2");
  }

  @Test
  public void testCompare() {
    SortedSet<Resource> set = new TreeSet<>(underTest);
    set.add(resource2);
    set.add(resource1);

    Resource[] resources = set.toArray(new Resource[set.size()]);
    assertSame(resource1, resources[0]);
    assertSame(resource2, resources[1]);
  }

  @Test
  public void testCompareSpecialCases() {
    assertEquals(0, underTest.compare(resource1, resource1));
    assertEquals(1, underTest.compare(null, resource1));
    assertEquals(-1, underTest.compare(resource1, null));
  }

}
