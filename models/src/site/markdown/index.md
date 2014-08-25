## About Sling Models Extension

AEM-specific Sling Models Injector.

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

The AEM-specific Sling Models Injector provides:

* Access to PagerManager and Page objects
* Current WCM mode and Authoring UI mode
* Current Designer, Design and Style objects
* XSSAPI for current request

See [Injector API documentation][apidocs-aemobjectinjector] for details.


[usage]: usage.html
[apidocs]: apidocs/
[apidocs-aemobjectinjector]: apidocs/io/wcm/sling/models/injectors/impl/AemObjectInjector.html
[changelog]: changes-report.html
