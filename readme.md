# ArchUnit Toolbox

Common ArchUnit tooling for Java / Spring Boot projects.

## Setup

Add this library to the classpath by adding the following maven dependency. Versions can be found [here](../../packages)

```xml

<dependency>
    <groupId>it.aboutbits</groupId>
    <artifactId>archunit-toolbox</artifactId>
    <version>x.x.x</version>
    <scope>test</scope>
</dependency>
```

## Usage

To use this package, simply extend one of the provided ArchUnit classes.
For example `ArchitectureTestBase`:

```java

@AnalyzeClasses(
        packages = ArchitectureTest.PACKAGE
)
@NullMarked
class ArchitectureTest extends ArchitectureTestBase {
    static final String PACKAGE = "the.base.package.of.your.project";

    static {
        // Configuration
    }
}
```

In the static block you can configure some blacklists provided by the base class.

```java
    static {
    ArchitectureTestBase.BLACKLISTED_CLASSES.remove("net.datafaker.Faker");
}
```

## Local Development

To use this library as a local development dependency, you can simply refer to the version `BUILD-SNAPSHOT`.

Check out this repository and run the maven goal `install`. This will build and install this library as version `BUILD-SNAPSHOT` into your local maven cache.

Note that you may have to tell your IDE to reload your main maven project each time you build the library.

## Build & Publish

To build and publish the chart, visit the GitHub Actions page of the repository and trigger the workflow "Release Package" manually.

## Information

About Bits is a company based in South Tyrol, Italy. You can find more information about us on [our website](https://aboutbits.it).

### Support

For support, please contact [info@aboutbits.it](mailto:info@aboutbits.it).

### Credits

- [All Contributors](../../contributors)

### License

The MIT License (MIT). Please see the [license file](license.md) for more information.
