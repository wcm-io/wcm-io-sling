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
package io.wcm.sling.commons.caservice.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.testing.mock.osgi.MockBundle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.Constants;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import io.wcm.sling.commons.caservice.ContextAwareService;
import io.wcm.sling.commons.caservice.ContextAwareServiceResolver;
import io.wcm.sling.commons.caservice.ContextAwareServiceResolver.ResolveAllResult;
import io.wcm.sling.commons.caservice.PathPreprocessor;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
@SuppressWarnings("null")
class ContextAwareServiceResolverImplTest {

  private final AemContext context = new AemContext();

  private DummySpi contentImpl;
  private DummySpi contentDamImpl;
  private DummySpi contentSampleImpl;

  private ContextAwareServiceResolver underTest;

  @BeforeEach
  void setUp() {
    contentImpl = context.registerService(DummySpi.class, new DummySpiImpl(),
        ContextAwareService.PROPERTY_CONTEXT_PATH_PATTERN, "^/content(/.*)?$",
        Constants.SERVICE_RANKING, 100);
    contentDamImpl = context.registerService(DummySpi.class, new DummySpiImpl(),
        ContextAwareService.PROPERTY_CONTEXT_PATH_PATTERN, "^/content/dam(/.*)?$",
        Constants.SERVICE_RANKING, 200);
    contentSampleImpl = context.registerService(DummySpi.class, new DummySpiImpl(),
        ContextAwareService.PROPERTY_CONTEXT_PATH_PATTERN, "^/content/sample(/.*)?$",
        ContextAwareService.PROPERTY_CONTEXT_PATH_BLACKLIST_PATTERN, "^/content/sample/exclude(/.*)?$",
        Constants.SERVICE_RANKING, 300);

    // add some more services with high ranking but invalid properties - they should never be returned
    context.registerService(DummySpi.class, new DummySpiImpl(),
        ContextAwareService.PROPERTY_CONTEXT_PATH_PATTERN, "(",
        Constants.SERVICE_RANKING, 10000);
    context.registerService(DummySpi.class, new DummySpiImpl(),
        ContextAwareService.PROPERTY_CONTEXT_PATH_PATTERN, "^/content(/.*)?$",
        ContextAwareService.PROPERTY_CONTEXT_PATH_BLACKLIST_PATTERN, ")",
        Constants.SERVICE_RANKING, 20000);

    underTest = context.registerInjectActivateService(new ContextAwareServiceResolverImpl());
  }

  @Test
  void testWithDefaultImpl() {
    DummySpi defaultImpl = context.registerService(DummySpi.class, new DummySpiImpl(),
        Constants.SERVICE_RANKING, Integer.MIN_VALUE,
        ContextAwareService.PROPERTY_ACCEPTS_CONTEXT_PATH_EMPTY, true);

    assertSame(contentImpl, underTest.resolve(DummySpi.class, context.create().resource("/content/test1")));
    assertSame(contentSampleImpl, underTest.resolve(DummySpi.class, context.create().resource("/content/sample/test1")));
    assertSame(contentImpl, underTest.resolve(DummySpi.class, context.create().resource("/content/sample/exclude/test1")));
    assertSame(contentDamImpl, underTest.resolve(DummySpi.class, context.create().resource("/content/dam/test1")));
    assertSame(defaultImpl, underTest.resolve(DummySpi.class, context.create().resource("/etc/test1")));

    assertEquals(ImmutableList.of(contentDamImpl, contentImpl, defaultImpl),
        underTest.resolveAll(DummySpi.class, context.create().resource("/content/dam/test2")).getServices().collect(Collectors.toList()));
  }

  @Test
  void testWithoutDefaultImpl() {
    assertSame(contentImpl, underTest.resolve(DummySpi.class, context.create().resource("/content/test1")));
    assertSame(contentSampleImpl, underTest.resolve(DummySpi.class, context.create().resource("/content/sample/test1")));
    assertSame(contentImpl, underTest.resolve(DummySpi.class, context.create().resource("/content/sample/exclude/test1")));
    assertSame(contentDamImpl, underTest.resolve(DummySpi.class, context.create().resource("/content/dam/test1")));
    assertNull(underTest.resolve(DummySpi.class, context.create().resource("/etc/test1")));

    assertEquals(ImmutableList.of(contentDamImpl, contentImpl),
        underTest.resolveAll(DummySpi.class, context.create().resource("/content/dam/test2")).getServices().collect(Collectors.toList()));
  }

  @Test
  void testWithSlingHttpServletRequest() {
    DummySpi defaultImpl = context.registerService(DummySpi.class, new DummySpiImpl(),
        Constants.SERVICE_RANKING, Integer.MIN_VALUE,
        ContextAwareService.PROPERTY_ACCEPTS_CONTEXT_PATH_EMPTY, true);

    context.currentResource(context.create().resource("/content/sample/test1"));
    assertSame(contentSampleImpl, underTest.resolve(DummySpi.class, context.request()));

    assertEquals(ImmutableList.of(contentSampleImpl, contentImpl, defaultImpl),
        underTest.resolveAll(DummySpi.class, context.request()).getServices().collect(Collectors.toList()));
  }

  /**
   * Simulate an experience fragment resource included in a page.
   * Context-aware service resolver should take the current page as context to resolve, not the current resource.
   */
  @Test
  void testWithSlingHttpServletRequest_ResourceOtherContext() {
    DummySpi defaultImpl = context.registerService(DummySpi.class, new DummySpiImpl(),
        Constants.SERVICE_RANKING, Integer.MIN_VALUE,
        ContextAwareService.PROPERTY_ACCEPTS_CONTEXT_PATH_EMPTY, true);

    context.currentPage(context.create().page("/content/sample/test1"));
    context.currentResource(context.create().resource("/content/experience-fragments/test1"));
    assertSame(contentSampleImpl, underTest.resolve(DummySpi.class, context.request()));

    assertEquals(ImmutableList.of(contentSampleImpl, contentImpl, defaultImpl),
        underTest.resolveAll(DummySpi.class, context.request()).getServices().collect(Collectors.toList()));
  }

  @Test
  void testWithNull() {
    DummySpi defaultImpl = context.registerService(DummySpi.class, new DummySpiImpl(),
        Constants.SERVICE_RANKING, Integer.MIN_VALUE,
        ContextAwareService.PROPERTY_ACCEPTS_CONTEXT_PATH_EMPTY, true);

    assertSame(defaultImpl, underTest.resolve(DummySpi.class, null));

    assertEquals(ImmutableList.of(defaultImpl), underTest.resolveAll(DummySpi.class, null).getServices().collect(Collectors.toList()));
  }

  @Test
  void testResolveAllCombindedKey() {
    ResolveAllResult result1 = underTest.resolveAll(DummySpi.class, context.create().resource("/content/dam/test1"));
    ResolveAllResult result2 = underTest.resolveAll(DummySpi.class, context.create().resource("/content/dam/test2"));
    ResolveAllResult result3 = underTest.resolveAll(DummySpi.class, context.create().resource("/content/sample/test3"));

    assertEquals(result1.getCombinedKey(), result2.getCombinedKey());
    assertNotEquals(result1.getCombinedKey(), result3.getCombinedKey());
  }

  @Test
  void testWithBundleHeader() {

    // service gets path pattern from bundle header instead of service property
    ((MockBundle)context.bundleContext().getBundle()).setHeaders(ImmutableMap.of(
        ContextAwareService.PROPERTY_CONTEXT_PATH_PATTERN, "^/content/dam(/.*)?$"));
    DummySpi contentDamImplWithBundleHeader = context.registerService(DummySpi.class, new DummySpiImpl(),
        Constants.SERVICE_RANKING, 1000);


    assertSame(contentImpl, underTest.resolve(DummySpi.class, context.create().resource("/content/test1")));
    assertSame(contentSampleImpl, underTest.resolve(DummySpi.class, context.create().resource("/content/sample/test1")));
    assertSame(contentImpl, underTest.resolve(DummySpi.class, context.create().resource("/content/sample/exclude/test1")));
    assertSame(contentDamImplWithBundleHeader, underTest.resolve(DummySpi.class, context.create().resource("/content/dam/test1")));
    assertNull(underTest.resolve(DummySpi.class, context.create().resource("/etc/test1")));

    assertEquals(ImmutableList.of(contentDamImplWithBundleHeader, contentDamImpl, contentImpl),
        underTest.resolveAll(DummySpi.class, context.create().resource("/content/dam/test2")).getServices().collect(Collectors.toList()));
  }

  @Test
  void testWithPathPreProcessor() {
    context.registerService(PathPreprocessor.class, (path, resourceResolver) -> StringUtils.removeStart(path, "/pathprefix"));
    underTest = context.registerInjectActivateService(new ContextAwareServiceResolverImpl());

    assertSame(contentImpl, underTest.resolve(DummySpi.class, context.create().resource("/pathprefix/content/test1")));
    assertSame(contentSampleImpl, underTest.resolve(DummySpi.class, context.create().resource("/pathprefix/content/sample/test1")));
    assertSame(contentImpl, underTest.resolve(DummySpi.class, context.create().resource("/pathprefix/content/sample/exclude/test1")));
    assertSame(contentDamImpl, underTest.resolve(DummySpi.class, context.create().resource("/pathprefix/content/dam/test1")));
    assertNull(underTest.resolve(DummySpi.class, context.create().resource("/pathprefix/etc/test1")));

    assertEquals(ImmutableList.of(contentDamImpl, contentImpl),
        underTest.resolveAll(DummySpi.class, context.create().resource("/pathprefix/content/dam/test2")).getServices().collect(Collectors.toList()));
  }

}
