package it.aboutbits.archunit.toolbox.rule.base;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.ArchTest;
import org.jspecify.annotations.NullMarked;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

@SuppressWarnings({"checkstyle:InterfaceIsType", "java:S1214"})
@NullMarked
public interface TestMethodVisibilityArchRule {
    @SuppressWarnings({"unused", "checkstyle:MethodName", "java:S100"})
    @ArchTest
    default void test_methods_must_be_package_private(JavaClasses classes) {
        methods()
                .that()
                .areAnnotatedWith(org.junit.jupiter.api.Test.class)
                .or()
                .areAnnotatedWith(org.junit.jupiter.api.RepeatedTest.class)
                .or()
                .areAnnotatedWith(org.junit.jupiter.params.ParameterizedTest.class)
                .or()
                .areAnnotatedWith(ArchTest.class)
                .and()
                .areDeclaredInClassesThat(new DescribedPredicate<>("are not an interface") {
                    @Override
                    public boolean test(JavaClass javaClass) {
                        return !javaClass.isInterface();
                    }
                })
                .should()
                .bePackagePrivate()
                .allowEmptyShould(true)
                .check(classes);
    }
}
