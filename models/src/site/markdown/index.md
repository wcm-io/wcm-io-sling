## About AEM Sling Models Extension

AEM Object Injector for Sling Models.

### Maven Dependency

```xml
<dependency>
  <groupId>io.wcm</groupId>
  <artifactId>io.wcm.sling.models</artifactId>
  <version>1.1.0-SNAPSHOT</version>
</dependency>
```

### Documentation

* [Usage][usage]
* [API documentation][apidocs]
* [Changelog][changelog]


### Overview

The AEM Object Injector for Sling Models provides:

* Access to PagerManager and Page objects
* Current WCM mode and Authoring UI mode
* Current Designer, Design and Style objects
* XSSAPI for current request
* CQ I18n object for the current request
* Support injection request-derived context objects on all models, not only when the adaptable is a request

See [Injector API documentation][apidocs-aemobjectinjector] for details.


### Comparison to ACS Commons

The [ACS AEM Commons][acs-commons] project also contains an implementation of an [AEM Object Injector][acs-commons-aem-object-injrecotr].
It has basically the same feature-set as the wcm.io version, but it uses name-based injection based on field names.

The wcm.io AEM Object injector use class-based injection, making it easier to use independently from the field name. Since version 1.1 it supports more features than the ACS AEM Commons version.


[usage]: usage.html
[apidocs]: apidocs/
[apidocs-aemobjectinjector]: apidocs/io/wcm/sling/models/annotations/AemObject.html
[changelog]: changes-report.html
[acs-commons]: http://adobe-consulting-services.github.io/acs-aem-commons/
[acs-commons-aem-object-injrecotr]: http://adobe-consulting-services.github.io/acs-aem-commons/features/aem-sling-models-injectors.html


### Maven Dependency for CQ5

Although wcm.io generally targets only AEM6, for this package a version for CQ55 and CQ56 is published as well:

```xml
<dependency>
  <groupId>io.wcm.cq5</groupId>
  <artifactId>io.wcm.cq5.sling.models</artifactId>
  <version>1.0.0</version>
</dependency>
```

It supports the same features, only the Authoring UI mode detection is not available.
