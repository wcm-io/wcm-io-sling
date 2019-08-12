## About Context-Aware Services

OSGi-based applications and libraries offer SPI interfaces which allow to configure or customize the behavior. If multiple applications are deployed in an AEM instance it may be required to apply different customizations for different applications = content paths.

Context-Aware Services is a solution for this problem. Each application can register their own implementation of the SPI interface and add additional service properties (or alternatively bundle headers) containing a path pattern which defines for which resource path contexts (e.g. content pages or DAM assets) this SPI implementation should be applied.


### Defining a SPI interface as context-aware

To make a SPI interface or abstract class context-aware it has to extend or implement the interface [`io.wcm.sling.commons.caservice.ContextAwareService`][ContextAwareService]. This is just a marker-interface signaling that the implementors can rely on supporting the context-awareness for this SPI.

The application or library that provides a context-aware SPI in this way has to use the service [`io.wcm.sling.commons.caservice.ContextAwareServiceResolver`][ContextAwareServiceResolver] which provides methods to pick the best-matching SPI implementation for a given resource. The matching is based on the resource path. If multiple implementations match, the one with the highest service ranking wins.

The service additionally supports returning all matching implementation, e.g. to calculate an aggregated result.


### Implementing a context-aware SPI interface

Implementing a context-aware SPI interface or abstract class is done as usual in OSGi - e.g. using the declarative service OSGi annotations.

Additional properties have to be provided which contexts (resource paths) are supported by this implementation. These properties can be provided as service properties, or as bundle headers. When using bundle headers the same settings apply to all context-aware service implementations in this bundle (which is often the recommended way).

The available service property name/bundle header names are defined in [`io.wcm.sling.commons.caservice.ContextAwareService`][ContextAwareService]:

* `Wcmio-CAService-ContextPathRegex` - Context path whitelist expression.
* `Wcmio-CAService-ContextPathBlacklistRegex` - Context path blacklist expression.
* `Wcmio-CASService-AcceptsContextPathEmpty` - Accepts empty context paths.


### Usage example

[wcm.io Handler][wcmio-handler] makes use of Context-Aware Services for it's SPI.

The [wcm.io Sample Application][wcmio-samples] uses the Handler infrastructure and implements some SPI implementations with setting the path context in the bundle headers.


[ContextAwareService]: apidocs/io/wcm/sling/commons/caservice/ContextAwareService.html
[ContextAwareServiceResolver]: apidocs/io/wcm/sling/commons/caservice/ContextAwareServiceResolver.html
[wcmio-handler]: https://wcm.io/handler/
[wcmio-samples]: https://wcm.io/samples/
