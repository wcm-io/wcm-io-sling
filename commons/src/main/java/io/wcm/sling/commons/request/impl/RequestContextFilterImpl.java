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
import java.util.Stack;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
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
@Property(name = "sling.filter.scope", value = "component")
public final class RequestContextFilterImpl implements RequestContext, Filter {

  private static final ThreadLocal<Stack<SlingHttpServletRequest>> REQUEST_THREADLOCAL = new ThreadLocal<Stack<SlingHttpServletRequest>>() {
    @Override
    protected Stack<SlingHttpServletRequest> initialValue() {
      return new Stack<SlingHttpServletRequest>();
    }
  };

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    if (request instanceof SlingHttpServletRequest) {
      REQUEST_THREADLOCAL.get().push((SlingHttpServletRequest)request);
    }
    try {
      chain.doFilter(request, response);
    }
    finally {
      REQUEST_THREADLOCAL.get().pop();
    }
  }

  @Override
  public SlingHttpServletRequest getThreadRequest() {
    Stack<SlingHttpServletRequest> stack = REQUEST_THREADLOCAL.get();
    if (stack.isEmpty()) {
      return null;
    }
    else {
      return stack.peek();
    }
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
