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

import java.util.stream.Stream;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.commons.osgi.Order;
import org.apache.sling.commons.osgi.RankedServices;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.wcm.sling.commons.caservice.ContextAwareService;

class ContextAwareServiceTracker implements ServiceTrackerCustomizer<ContextAwareService, ServiceInfo> {

  private final BundleContext bundleContext;
  private final ServiceTracker<ContextAwareService, ServiceInfo> serviceTracker;
  private volatile RankedServices<ServiceInfo> rankedServices;
  private volatile long lastServiceChange;

  private static final Logger log = LoggerFactory.getLogger(ContextAwareServiceTracker.class);

  ContextAwareServiceTracker(String serviceClassName, BundleContext bundleContext) {
    this.bundleContext = bundleContext;
    this.rankedServices = new RankedServices<ServiceInfo>(Order.DESCENDING);
    this.serviceTracker = new ServiceTracker<ContextAwareService, ServiceInfo>(bundleContext, serviceClassName, this);
    this.serviceTracker.open();
  }

  public void dispose() {
    serviceTracker.close();
    rankedServices = null;
  }

  @Override
  public ServiceInfo addingService(ServiceReference<ContextAwareService> reference) {
    ServiceInfo serviceInfo = new ServiceInfo(reference, bundleContext);
    if (log.isDebugEnabled()) {
      log.debug("Add service {}", serviceInfo.getService().getClass().getName());
    }
    if (rankedServices != null) {
      rankedServices.bind(serviceInfo, serviceInfo.getServiceProperties());
    }
    lastServiceChange = System.currentTimeMillis();
    return serviceInfo;
  }

  @Override
  public void modifiedService(ServiceReference<ContextAwareService> reference, ServiceInfo serviceInfo) {
    // nothing to do
  }

  @Override
  public void removedService(ServiceReference<ContextAwareService> reference, ServiceInfo serviceInfo) {
    if (log.isDebugEnabled()) {
      log.debug("Remove service {}", serviceInfo.getService().getClass().getName());
    }
    if (rankedServices != null) {
      rankedServices.unbind(serviceInfo, serviceInfo.getServiceProperties());
    }
    lastServiceChange = System.currentTimeMillis();
    bundleContext.ungetService(reference);
  }

  public Stream<ServiceInfo> resolve(Resource resource) {
    if (rankedServices == null) {
      return Stream.empty();
    }
    return rankedServices.getList().stream()
        .filter(serviceInfo -> serviceInfo.matches(resource));
  }

  public long getLastServiceChangeTimestamp() {
    return this.lastServiceChange;
  }

}
