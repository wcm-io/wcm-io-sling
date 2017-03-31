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

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.inventory.Format;
import org.apache.felix.inventory.InventoryPrinter;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import io.wcm.sling.commons.caservice.ContextAwareServiceResolver;

/**
 * Inventory printer for context-aware services.
 */
@Component(service = InventoryPrinter.class, property = {
    InventoryPrinter.NAME + "=wcmio-caservice",
    InventoryPrinter.TITLE + "=wcm.io Context-Aware Services",
    InventoryPrinter.FORMAT + "=TEXT"
})
public class ContextAwareServiceInventoryPrinter implements InventoryPrinter {

  @Reference
  private ContextAwareServiceResolver contextAwareServiceResolver;

  @Override
  public void print(PrintWriter pw, Format format, boolean isZip) {
    if (format != Format.TEXT) {
      return;
    }
    if (!(contextAwareServiceResolver instanceof ContextAwareServiceResolverImpl)) {
      return;
    }

    ConcurrentMap<String, ContextAwareServiceTracker> map = ((ContextAwareServiceResolverImpl)contextAwareServiceResolver).getContextAwareServiceTrackerMap();
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
