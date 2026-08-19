package it.aboutbits.archunit.toolbox.rule.base;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.ArchTest;
import org.jspecify.annotations.NullMarked;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@SuppressWarnings({"checkstyle:InterfaceIsType", "java:S1214"})
@NullMarked
public interface TestNestedClassVisibilityArchRule {
    @SuppressWarnings({"unused", "checkstyle:MethodName", "java:S100"})
    @ArchTest
    default void nested_test_classes_must_be_package_private(JavaClasses classes) {
        classes()
                .that()
                .areAnnotatedWith(org.junit.jupiter.api.Nested.class)
                .should()
                .bePackagePrivate()
                .allowEmptyShould(true)
                .check(classes);
    }
}
