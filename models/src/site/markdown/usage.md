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


### Request Thread Local Support

In the original Sling Models implementation only context objects derived from the adaptable can be injected. If the adaptable is not a request, most of the context objects could not be injected.

The AEM Sling Models Extensions enhances this behavior for both `@SlingObject` injector and the `@AemObject` injector to support inject all context objects even if the adaptable is not a request. This is implemented by using a thread local and a servlet filter bound to the current Sling component rendering process.

This features is switched on by default, you can disable it in the OSGi configuration via the "wm.io Models Configuration".


[apidocs-aemobjectinjector]: apidocs/io/wcm/sling/models/annotations/AemObject.html
