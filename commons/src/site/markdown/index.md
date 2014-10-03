## About Sling Commons

Common Sling utility and helper funtions.

### Maven Dependency

```xml
<dependency>
  <groupId>io.wcm</groupId>
  <artifactId>io.wcm.sling.commons</artifactId>
  <version>0.1.0</version>
</dependency>
```

### Documentation

* [API Documentation][apidocs]
* [Changelog][changelog]


### Overview

The Sling Commons library contains:

* Helper `AdaptTo` class for adapting with non-null check
* OSGi `RankedServices` class for making it easier consuming a list of services orderd by service ranking.
* Helper classes for accessing typed request parameters and selectors from the current request
* An `ImmutableValueMap` implementation with similar interface like Google Guava's `ImmutableMap`
* Other useful helper classes, see [API Documentation][apidocs]


[apidocs]: apidocs/
[changelog]: changes-report.html
