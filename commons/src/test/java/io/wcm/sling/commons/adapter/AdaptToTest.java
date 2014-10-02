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
package io.wcm.sling.commons.adapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import org.apache.sling.api.adapter.Adaptable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AdaptToTest {

  @Mock
  private Adaptable adaptable;

  @Test
  public void testAdaptSuccess() {
    final String SAMPLE = "sampleString";
    when(adaptable.adaptTo(Comparable.class)).thenReturn(SAMPLE);

    assertSame(SAMPLE, AdaptTo.notNull(adaptable, Comparable.class));
  }

  @Test
  public void testAdaptNotSuccess() {
    when(adaptable.adaptTo(Comparable.class)).thenReturn(null);

    try {
      AdaptTo.notNull(adaptable, Comparable.class);
      fail("No exception thrown.");
    }
    catch (Throwable ex) {
      assertTrue(ex instanceof UnableToAdaptException);
      assertSame(adaptable, ((UnableToAdaptException)ex).getAdaptable());
      assertEquals(Comparable.class, ((UnableToAdaptException)ex).getType());
    }
  }

}
