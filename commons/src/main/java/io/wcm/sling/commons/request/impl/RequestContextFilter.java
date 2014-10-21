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
package io.wcm.sling.commons.request.impl;

import io.wcm.sling.commons.request.RequestContext;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;

/**
 * Servlet filter that sets the current sling request during processing to make it available via the
 * {@link RequestContext} interface.
 */
@Component(immediate = true)
@Service({
  RequestContext.class, Filter.class
})
public final class RequestContextFilter implements RequestContext, Filter {

  private static final ThreadLocal<SlingHttpServletRequest> REQUEST_THREADLOCAL = new ThreadLocal<>();

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    if (request instanceof HttpServletRequest) {
      REQUEST_THREADLOCAL.set((SlingHttpServletRequest)request);
    }
    try {
      chain.doFilter(request, response);
    }
    finally {
      REQUEST_THREADLOCAL.remove();
    }
  }

  @Override
  public SlingHttpServletRequest getThreadRequest() {
    return REQUEST_THREADLOCAL.get();
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    // nothing to do
  }

  @Override
  public void destroy() {
    // nothing to do
  }

}
