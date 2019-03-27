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
package io.wcm.sling.commons.osgi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.osgi.framework.Constants;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;

import io.wcm.sling.commons.osgi.RankedServices.ChangeListener;

@SuppressWarnings({ "deprecation", "null" })
class RankedServicesTest {

  private static final String SERVICE_1 = "service1";
  private static final Map<String, Object> SERVICE_1_PROPS = ImmutableMap.<String, Object>builder()
      .put(Constants.SERVICE_RANKING, 50).put(Constants.SERVICE_ID, 1L).build();
  private static final String SERVICE_2 = "service2";
  private static final Map<String, Object> SERVICE_2_PROPS = ImmutableMap.<String, Object>builder()
      .put(Constants.SERVICE_RANKING, 10).put(Constants.SERVICE_ID, 2L).build();
  private static final String SERVICE_3 = "service3";
  private static final Map<String, Object> SERVICE_3_PROPS = ImmutableMap.<String, Object>builder()
      .put(Constants.SERVICE_RANKING, 100).put(Constants.SERVICE_ID, 3L).build();

  @Test
  void testSortedServices() {

    RankedServices<Comparable> underTest = new RankedServices<>();
    assertEquals(0, underTest.get().size());

    underTest.bind(SERVICE_1, SERVICE_1_PROPS);
    assertEquals(1, underTest.get().size());
    Comparable[] services = Iterators.toArray(underTest.get().iterator(), Comparable.class);
    assertSame(SERVICE_1, services[0]);

    underTest.bind(SERVICE_2, SERVICE_2_PROPS);
    underTest.bind(SERVICE_3, SERVICE_3_PROPS);
    assertEquals(3, underTest.get().size());
    services = Iterators.toArray(underTest.get().iterator(), Comparable.class);
    assertSame(SERVICE_2, services[0]);
    assertSame(SERVICE_1, services[1]);
    assertSame(SERVICE_3, services[2]);

    underTest.unbind(SERVICE_2, SERVICE_2_PROPS);
    assertEquals(2, underTest.get().size());
    services = Iterators.toArray(underTest.get().iterator(), Comparable.class);
    assertSame(SERVICE_1, services[0]);
    assertSame(SERVICE_3, services[1]);

  }

  @Test
  void testChangeListener() {
    ChangeListener changeListener = mock(ChangeListener.class);

    RankedServices<Comparable> underTest = new RankedServices<>(changeListener);
    underTest.bind(SERVICE_1, SERVICE_1_PROPS);
    verify(changeListener).changed();
  }

}
