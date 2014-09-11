## About Sling Models Extension

AEM Object Injector for Sling Models.

### Maven Dependency

```xml
<dependency>
  <groupId>io.wcm</groupId>
  <artifactId>io.wcm.sling.models</artifactId>
  <version>0.1.0</version>
</dependency>
```

### Documentation

* [Usage][usage]
* [API Documentation][apidocs]
* [Changelog][changelog]

### Overview

The AEM Object Injector for Sling Models provides:

* Access to PagerManager and Page objects
* Current WCM mode and Authoring UI mode
* Current Designer, Design and Style objects
* XSSAPI for current request

See [Injector API documentation][apidocs-aemobjectinjector] for details.


### Comparison to ACS Commons

The [ACS AEM Commons][acs-commons] project also contains an implementation of an [AEM Object Injector][acs-commons-aem-object-injrecotr].
It has basically the same feature-set as the wcm.io version, but it uses name-based injection based on the property names.

The wcm.io AEM Object injector use class-based injection, making it easier to use independently from the property name.


[usage]: usage.html
[apidocs]: apidocs/
[apidocs-aemobjectinjector]: apidocs/io/wcm/sling/models/injectors/impl/AemObjectInjector.html
[changelog]: changes-report.html
[acs-commons]: http://adobe-consulting-services.github.io/acs-aem-commons/
[acs-commons-aem-object-injrecotr]: http://adobe-consulting-services.github.io/acs-aem-commons/features/aem-sling-models-injectors.html
