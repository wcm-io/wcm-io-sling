## Usage

The Sling Models Injector `AemObjectInjector` adds support for AEM context objects to Sling Models.

See [Injector API documentation][apidocs-aemobjectinjector] for a full documentation of injections supported.

### Injecting PageManager and Page objects

Example:

```java
@Model(adaptables = SlingHttpServletRequest.class)
private class MyModel {
  
  @AemObject
  private PageManager pageManager;

  @AemObject
  private Page currentPage;

  // add name hint to get resourcePage instead of currentPage
  @AemObject(name="resourcePage")
  private Page resourcePage;
  
}

// get model instance
MyModel model = request.adaptTo(MyModel.class);
```

### Injecting WCM mode and Authoring UI mode

Example:

```java
@Model(adaptables = SlingHttpServletRequest.class)
private class MyModel {

  @Inject
  public MyModel(@AemObject WCMMode wcmMode, @AemObject AuthoringUIMode authoringUiMode) {
    // constructor logic
  }
  
}

// get model instance
MyModel model = request.adaptTo(MyModel.class);
```

[apidocs-aemobjectinjector]: apidocs/io/wcm/sling/models/annotations/AemObject.html
