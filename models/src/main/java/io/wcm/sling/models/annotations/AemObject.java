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
package io.wcm.sling.models.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import io.wcm.sling.models.injectors.impl.AemObjectInjector;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotation;

import com.adobe.granite.xss.XSSAPI;
import com.day.cq.i18n.I18n;
import com.day.cq.wcm.api.AuthoringUIMode;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMMode;
import com.day.cq.wcm.api.components.ComponentContext;
import com.day.cq.wcm.api.designer.Design;
import com.day.cq.wcm.api.designer.Designer;
import com.day.cq.wcm.api.designer.Style;

/**
 * Injects common AEM objects that can be derived from a SlingHttpServletRequest.
 * The injection is class-based, but may be supported by name hints when multiple targets are available for the same
 * class.
 * <p>
 * Supports the following objects:
 * <table>
 * <caption>Supported objects</caption>
 * <tr>
 * <th style="text-align:left">Class</th>
 * <th style="text-align:left">Description</th>
 * <th style="text-align:left">Name hint</th>
 * <th style="text-align:center">Request</th>
 * <th style="text-align:center">ResourceResolver</th>
 * <th style="text-align:center">Resource</th>
 * </tr>
 * <tr style="background-color:#eee">
 * <td>{@link PageManager}</td>
 * <td>AEM Page manager</td>
 * <td></td>
 * <td style="text-align:center">X</td>
 * <td style="text-align:center">X</td>
 * <td style="text-align:center">X</td>
 * </tr>
 * <tr>
 * <td>{@link Page}</td>
 * <td>AEM page addressed by the current request. Default to be injected for {@link Page} types.</td>
 * <td>currentPage</td>
 * <td style="text-align:center">X</td>
 * <td style="text-align:center">X*</td>
 * <td style="text-align:center">X</td>
 * </tr>
 * <tr style="background-color:#eee">
 * <td>{@link Page}</td>
 * <td>AEM page containing the current resource.</td>
 * <td>resourcePage</td>
 * <td style="text-align:center">X</td>
 * <td style="text-align:center">X*</td>
 * <td style="text-align:center">X</td>
 * </tr>
 * <tr>
 * <td>{@link WCMMode}</td>
 * <td>Current AEM WCM mode</td>
 * <td></td>
 * <td style="text-align:center">X</td>
 * <td style="text-align:center">X*</td>
 * <td style="text-align:center">X*</td>
 * </tr>
 * <tr style="background-color:#eee">
 * <td>{@link AuthoringUIMode}</td>
 * <td>Current AEM Authoring UI mode. Defaults to Touch UI if mode is not set.</td>
 * <td></td>
 * <td style="text-align:center">X</td>
 * <td style="text-align:center">X*</td>
 * <td style="text-align:center">X*</td>
 * </tr>
 * <tr>
 * <td>{@link ComponentContext}</td>
 * <td>AEM component context of current request</td>
 * <td></td>
 * <td style="text-align:center">X</td>
 * <td style="text-align:center">X*</td>
 * <td style="text-align:center">X*</td>
 * </tr>
 * <tr style="background-color:#eee">
 * <td>{@link Designer}</td>
 * <td>AEM designer</td>
 * <td></td>
 * <td style="text-align:center">X</td>
 * <td style="text-align:center">X</td>
 * <td style="text-align:center">X</td>
 * </tr>
 * <tr>
 * <td>{@link Design}</td>
 * <td>AEM design of the current page</td>
 * <td></td>
 * <td style="text-align:center">X</td>
 * <td style="text-align:center">X*</td>
 * <td style="text-align:center">X</td>
 * </tr>
 * <tr style="background-color:#eee">
 * <td>{@link Style}</td>
 * <td>AEM design style of the current component</td>
 * <td></td>
 * <td style="text-align:center">X</td>
 * <td style="text-align:center">X*</td>
 * <td style="text-align:center">X*</td>
 * </tr>
 * <tr>
 * <td>{@link XSSAPI}</td>
 * <td>AEM XSS API object for the current request</td>
 * <td></td>
 * <td style="text-align:center">X</td>
 * <td style="text-align:center">X*</td>
 * <td style="text-align:center">X*</td>
 * </tr>
 * <tr style="background-color:#eee">
 * <td>{@link I18n}</td>
 * <td>I18n object for the current resource/page context. Default to be inejctes for {@link I18n} types.</td>
 * <td>resourceI18n</td>
 * <td style="text-align:center">X</td>
 * <td style="text-align:center">X*</td>
 * <td style="text-align:center">X*</td>
 * </tr>
 * <tr>
 * <td>{@link I18n}</td>
 * <td>I18n object for the current user</td>
 * <td>userI18n</td>
 * <td style="text-align:center">X</td>
 * <td style="text-align:center">X*</td>
 * <td style="text-align:center">X*</td>
 * </tr>
 * </table>
 * <p>
 * In case of X* the class cannot be derived from the adaptable, but is derived from the request of the current thread
 * detected via {@link io.wcm.sling.commons.request.RequestContext}. If the current thread is not associated with a
 * request nothing is injected.
 * </p>
 */
@Target({ METHOD, FIELD, PARAMETER })
@Retention(RUNTIME)
@InjectAnnotation
@Source(AemObjectInjector.NAME)
public @interface AemObject {

  /**
   * Specifies the name of the request attribute. If empty or not set, then the name
   * is derived from the method or field.
   * <p>
   * For most injections of AemObject this is not required, it is only use as name-hint for injectint a Page object.
   * </p>
   */
  String name() default "";

  /**
   * If set to true, the model can be instantiated even if there is no request attribute
   * with the given name found.
   * Default = false.
   */
  boolean optional() default false;

}
