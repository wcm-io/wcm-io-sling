## About Sling Commons

Common Sling utility and helper functions.

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.sling.commons/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.sling.commons)


### Documentation

* [Context-Aware Services][caservice]
* [API documentation][apidocs]
* [Changelog][changelog]


### Overview

The Sling Commons library contains:

* [Context-Aware Services][caservice] which pick the best-matching OSGi service implementation based on resource context.
* `RequestContext` interface which supports getting the current request via a ThreadLocal
* Helper `AdaptTo` class for adapting with non-null check
* Helper classes for accessing typed request parameters and selectors from the current request
* An `ImmutableValueMap` implementation with similar interface like Google Guava's `ImmutableMap`
* Other useful helper classes, see [API documentation][apidocs]


[apidocs]: apidocs/
[changelog]: changes-report.html
[caservice]: context-aware-services.html
