package it.aboutbits.archunit.toolbox.rule.base;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import it.aboutbits.archunit.toolbox.util.TestClassNames;
import org.jspecify.annotations.NullMarked;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@SuppressWarnings({"checkstyle:InterfaceIsType", "java:S1214"})
@NullMarked
public interface TestClassInCorrectPackageArchRule {
    @SuppressWarnings({"unused", "checkstyle:MethodName", "java:S100"})
    @ArchTest
    default void test_classes_should_be_in_the_same_package_as_their_production_code(JavaClasses classes) {
        classes().that(TestClassNames.testClasses())
                .and()
                .areNotAnnotatedWith(org.junit.jupiter.api.Disabled.class)
                .and()
                .areNotAnnotatedWith(com.tngtech.archunit.junit.ArchIgnore.class)
                .and()
                .areNotAnnotatedWith(it.aboutbits.archunit.toolbox.support.ArchIgnoreNoProductionCounterpart.class)
                .and()
                .resideOutsideOfPackages(".._support..", ".._config..")
                .should(new BeInTheSamePackageAsTheProductionClass(classes))
                .allowEmptyShould(true)
                .check(classes);
    }

    class BeInTheSamePackageAsTheProductionClass extends ArchCondition<JavaClass> {
        private final JavaClasses allClasses;

        public BeInTheSamePackageAsTheProductionClass(JavaClasses allClasses) {
            super("be in the same package as their production class");
            this.allClasses = allClasses;
        }

        @Override
        public void check(JavaClass testClass, ConditionEvents events) {
            /*
             * No suffix guard here on purpose. The selection above already guarantees the suffix,
             * and re-deriving it in the condition is what previously disabled this rule outright:
             * the guard rebuilt the regex without the leading ".+", and String.matches anchors both
             * ends, so every test class returned before ever looking for its production class.
             */
            var productionClassSimpleName = TestClassNames.productionClassSimpleName(testClass.getSimpleName());
            var productionClassFullName = testClass.getPackageName() + "." + productionClassSimpleName;

            var productionClass = allClasses.stream()
                    .filter(clazz -> clazz.getFullName().equals(productionClassFullName))
                    .findFirst();

            if (productionClass.isEmpty()) {
                var message = "Test class <%s> does not have a matching production class <%s> in the same package (%s.java:0)".formatted(
                        testClass.getFullName(),
                        productionClassFullName,
                        productionClassSimpleName
                );
                events.add(SimpleConditionEvent.violated(testClass, message));
            }
        }
    }
}
