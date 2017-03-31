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

import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.inventory.Format;
import org.apache.felix.inventory.InventoryPrinter;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.adapter.Adaptable;
import org.apache.sling.api.resource.Resource;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import io.wcm.sling.commons.caservice.ContextAwareService;
import io.wcm.sling.commons.caservice.ContextAwareServiceResolver;

/**
 * {@link ContextAwareServiceResolver} implementation.
 */
@Component(service = { ContextAwareServiceResolver.class, InventoryPrinter.class }, immediate = true, property = {
    InventoryPrinter.NAME + "=wcmio-caservice",
    InventoryPrinter.TITLE + "=wcm.io Context-Aware Services",
    InventoryPrinter.FORMAT + "=TEXT"
})
public class ContextAwareServiceResolverImpl implements ContextAwareServiceResolver, InventoryPrinter {

  private BundleContext bundleContext;

  // cache of service trackers for each SPI interface
  private final LoadingCache<String, ContextAwareServiceTracker> serviceTrackerCache = CacheBuilder.newBuilder()
      .removalListener(new RemovalListener<String, ContextAwareServiceTracker>() {
        @Override
        public void onRemoval(RemovalNotification<String, ContextAwareServiceTracker> notification) {
          notification.getValue().dispose();
        }
      })
      .build(new CacheLoader<String, ContextAwareServiceTracker>() {
        @Override
        public ContextAwareServiceTracker load(String className) throws Exception {
          return new ContextAwareServiceTracker(className, bundleContext);
        }
      });

  @Activate
  private void activate(BundleContext context) {
    this.bundleContext = context;
  }

  @Deactivate
  private void deactivate(BundleContext context) {
    serviceTrackerCache.invalidateAll();
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends ContextAwareService> T resolve(Class<T> serviceClass, Adaptable adaptable) {
    Resource resource = getResource(adaptable);
    ContextAwareServiceTracker serviceTracker = getServiceTracker(serviceClass);
    return serviceTracker.resolve(resource)
        .map(serviceInfo -> (T)serviceInfo.getService())
        .findFirst().orElse(null);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends ContextAwareService> ResolveAllResult<T> resolveAll(Class<T> serviceClass, Adaptable adaptable) {
    Resource resource = getResource(adaptable);
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

  @Override
  public void print(PrintWriter pw, Format format, boolean isZip) {
    if (format != Format.TEXT) {
      return;
    }
    ConcurrentMap<String, ContextAwareServiceTracker> map = serviceTrackerCache.asMap();
    if (map.isEmpty()) {
      pw.println();
      pw.println("No context-aware services found.");
      pw.println("The services are registered lazily on first access of the service interface or class.");
      return;
    }
    for (Map.Entry<String, ContextAwareServiceTracker> entry : map.entrySet()) {
      pw.println();
      pw.println(entry.getKey());
      pw.println(StringUtils.repeat('-', entry.getKey().length()));
      for (ServiceInfo serviceInfo : entry.getValue().getServiceInfos()) {
        pw.print("- ");
        pw.println(serviceInfo.toString());
      }
    }
  }

}
