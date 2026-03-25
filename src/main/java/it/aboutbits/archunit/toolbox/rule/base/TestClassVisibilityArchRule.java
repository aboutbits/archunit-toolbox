package it.aboutbits.archunit.toolbox.rule.base;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.ArchTest;
import org.jspecify.annotations.NullMarked;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static it.aboutbits.archunit.toolbox.config.ArchRuleConfig.TEST_CLASS_SUFFIXES;

@SuppressWarnings({"checkstyle:InterfaceIsType", "java:S1214"})
@NullMarked
public interface TestClassVisibilityArchRule {
    @SuppressWarnings({"unused", "checkstyle:MethodName", "java:S100"})
    @ArchTest
    default void test_classes_must_be_package_private(JavaClasses classes) {
        classes()
                .that()
                .haveNameMatching(".+(" + String.join("|", TEST_CLASS_SUFFIXES) + ")$")
                .and()
                .resideOutsideOfPackages(".._support..", ".._config..")
                .should()
                .bePackagePrivate()
                .check(classes);
    }
}
