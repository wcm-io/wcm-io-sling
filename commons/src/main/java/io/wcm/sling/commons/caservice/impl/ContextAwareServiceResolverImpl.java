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

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.adapter.Adaptable;
import org.apache.sling.api.resource.Resource;
import org.jetbrains.annotations.NotNull;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import io.wcm.sling.commons.caservice.ContextAwareService;
import io.wcm.sling.commons.caservice.ContextAwareServiceResolver;
import io.wcm.sling.commons.caservice.PathPreprocessor;

/**
 * {@link ContextAwareServiceResolver} implementation.
 */
@Component(service = ContextAwareServiceResolver.class, immediate = true)
public class ContextAwareServiceResolverImpl implements ContextAwareServiceResolver {

  @Reference(policy = ReferencePolicy.STATIC, cardinality = ReferenceCardinality.OPTIONAL, policyOption = ReferencePolicyOption.GREEDY)
  private PathPreprocessor pathPreprocessor;

  private BundleContext bundleContext;

  private static final Logger log = LoggerFactory.getLogger(ContextAwareServiceResolverImpl.class);

  // cache of service trackers for each SPI interface
  private LoadingCache<String, ContextAwareServiceTracker> serviceTrackerCache;

  @Activate
  private void activate(BundleContext context) {
    this.bundleContext = context;
    this.serviceTrackerCache = CacheBuilder.newBuilder()
        .removalListener(new RemovalListener<String, ContextAwareServiceTracker>() {
          @SuppressWarnings("null")
          @Override
          public void onRemoval(RemovalNotification<String, ContextAwareServiceTracker> notification) {
            notification.getValue().dispose();
          }
        })
        .build(new CacheLoader<String, ContextAwareServiceTracker>() {
          @Override
          public ContextAwareServiceTracker load(String className) {
            return new ContextAwareServiceTracker(className, bundleContext, pathPreprocessor);
          }
        });
  }

  @Deactivate
  private void deactivate(BundleContext context) {
    this.serviceTrackerCache.invalidateAll();
    this.serviceTrackerCache = null;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends ContextAwareService> T resolve(@NotNull Class<T> serviceClass, @NotNull Adaptable adaptable) {
    Resource resource = getResource(adaptable);
    if (log.isTraceEnabled()) {
      log.trace("Resolve {} for resource {}", serviceClass.getName(), (resource != null ? resource.getPath() : "null"));
    }
    ContextAwareServiceTracker serviceTracker = getServiceTracker(serviceClass);
    return serviceTracker.resolve(resource)
        .map(serviceInfo -> (T)serviceInfo.getService())
        .findFirst().orElse(null);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends ContextAwareService> @NotNull ResolveAllResult<T> resolveAll(@NotNull Class<T> serviceClass, @NotNull Adaptable adaptable) {
    Resource resource = getResource(adaptable);
    if (log.isTraceEnabled()) {
      log.trace("Resolve all {} for resource {}", serviceClass.getName(), (resource != null ? resource.getPath() : "null"));
    }
    ContextAwareServiceTracker serviceTracker = getServiceTracker(serviceClass);
    return new ResolveAllResultImpl(
        serviceTracker.resolve(resource).map(serviceInfo -> (T)serviceInfo.getService()),
        () -> buildCombinedKey(serviceTracker, serviceTracker.resolve(resource)));
  }

  private String buildCombinedKey(ContextAwareServiceTracker serviceTracker, Stream<ServiceInfo> result) {
    return serviceTracker.getLastServiceChangeTimestamp() + "\n"
        + result.map(ServiceInfo::getKey).collect(Collectors.joining("\n~\n"));
  }

  private Resource getResource(Adaptable adaptable) {
    if (adaptable instanceof Resource) {
      return (Resource)adaptable;
    }
    else if (adaptable instanceof SlingHttpServletRequest) {
      return ((SlingHttpServletRequest)adaptable).getResource();
    }
    return null;
  }

  private ContextAwareServiceTracker getServiceTracker(Class<?> serviceClass) {
    try {
      return serviceTrackerCache.get(serviceClass.getName());
    }
    catch (ExecutionException ex) {
      throw new RuntimeException("Error getting service tracker for " + serviceClass.getName() + " from cache.", ex);
    }
  }

  ConcurrentMap<String, ContextAwareServiceTracker> getContextAwareServiceTrackerMap() {
    return serviceTrackerCache.asMap();
  }

}
