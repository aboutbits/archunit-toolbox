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

## Upgrading to 1.3.0

**This release will fail builds that passed on 1.2.0, on purpose.** Nine rules were silently
reporting success because they matched nothing; fixing them turns real violations into build
failures for the first time. Expect two kinds:

- **Revived rules surface real violations.** Chiefly the two that were fully dead:
  `test_classes_should_be_in_the_same_package_as_their_production_code` and
  `nested_test_classes_have_matching_production_method_name`. Triage them with the opt-out
  stereotype described under [Opting out](#opting-out) before annotating classes one at a time.
  The stricter paths (`getCodeUnits()` reaching constructors and field initializers, exact
  security-test matching, `SortMappings` reporting what it cannot read) surfaced nothing in a
  large codebase, so noise from those is unlikely.
- **`analyzed_packages_must_contain_classes` is new and fails on an empty import.** If the
  packages given to `@AnalyzeClasses` are mistyped or have moved, that is now a failure instead
  of 13 rules quietly passing.

Nothing else needs a migration: no rule fails over code your project does not have.

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

### Rules your project has no code for

Every rule tolerates a selection that comes up empty, so a rule simply passes on a project it does
not apply to. Whether a project has records, controllers, `@Store` classes or `@Nested` test classes
is the project's business, not something this library requires.

That each rule can actually fail is guaranteed by a red test per rule in this repository, rather than
by making your build fail over code you do not have. An empty selection says nothing about whether a
rule's logic works.

One case is a real problem though, and `analyzed_packages_must_contain_classes` covers it: if the
packages given to `@AnalyzeClasses` are mistyped or have moved, nothing is imported and every other
rule would pass without looking at a single class. That fails, once, with a message naming the cause.

### Opting out

Two annotations exempt a class from a specific rule. Neither is meta-annotated with ArchUnit's
`@ArchIgnore`: the ArchUnit JUnit engine resolves meta-annotations, so that would skip *every*
`@ArchTest` on the annotated class and report success rather than exempting it from one rule.

| annotation | put it on | exempts from |
|---|---|---|
| `@ArchIgnoreNoProductionCounterpart` | a test class | needing a production class of the same name in the same package, and having its `@Nested` classes matched against production methods |
| `@ArchIgnoreGroupName` | a `@Nested` test class | needing a production method of the same name, for a class that only groups tests |

Use `@ArchIgnoreNoProductionCounterpart` for a test named after the behaviour it describes rather than
after a production class.

Both are read as meta-annotations, so a project declares its intent once on its own stereotype instead
of repeating the annotation on every class:

```java

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ArchIgnoreNoProductionCounterpart
public @interface BusinessTest {
}
```

Annotating a single class directly still works — ArchUnit counts a direct annotation as
meta-annotated.

The same applies to `@Disabled` and ArchUnit's `@ArchIgnore`, which these rules also honour: a
stereotype that carries either of them exempts every class using it. That matches how JUnit and the
ArchUnit engine themselves read those two annotations — a class whose tests do not run is not held to
naming rules — but it does mean a stereotype can exempt more than it appears to, so keep an eye on
what your own test annotations carry.

Architecture tests need neither: any class in a package named `_architecture` is exempt from the
production-counterpart rule, alongside the existing `_support` and `_config` exclusions. Use the
annotation for the one-off that lives elsewhere.

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
