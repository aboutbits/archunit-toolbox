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

Implement one of the provided rule collections in your own architecture test.

```java

@AnalyzeClasses(
        packages = ArchitectureTest.PACKAGE
)
@NullMarked
@ArchIgnoreNoProductionCounterpart
class ArchitectureTest implements BaseArchRuleCollection {
    static final String PACKAGE = "the.base.package.of.your.project";
}
```

`BaseArchRuleCollection` holds the rules that apply to any Java project. `CommonArchRuleCollection`
adds rules for Spring MVC controllers and for `SortMappings`, so implement it only in a project that
has them.

The blacklists are mutable, so a project can drop an entry it disagrees with:

```java

static {
    BlacklistClassesArchRule.BLACKLISTED_CLASSES.remove("net.datafaker.Faker");
}
```

The same applies to `ArchRuleConfig.TEST_CLASS_SUFFIXES` when a project introduces a new test type.

### Every rule must be able to fail

No rule uses `allowEmptyShould(true)`. A rule that selects nothing would otherwise report success,
which is indistinguishable from a rule that is satisfied - and that is how a broken rule survives
unnoticed. So a rule whose selection comes up empty fails, and the fix is either to remove the rule
collection you do not need or to add the code it is meant to check.

### Opting out

Two annotations exempt a class from a specific rule. Neither is meta-annotated with ArchUnit's
`@ArchIgnore`: the ArchUnit JUnit engine resolves meta-annotations, so that would skip *every*
`@ArchTest` on the annotated class and report success rather than exempting it from one rule.

| annotation | put it on | exempts from |
|---|---|---|
| `@ArchIgnoreNoProductionCounterpart` | a test class | needing a production class of the same name in the same package, and having its `@Nested` classes matched against production methods |
| `@ArchIgnoreGroupName` | a `@Nested` test class | needing a production method of the same name, for a class that only groups tests |

Use `@ArchIgnoreNoProductionCounterpart` for a test named after the behaviour it describes rather than
after a production class, and on your own `ArchitectureTest`.

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
