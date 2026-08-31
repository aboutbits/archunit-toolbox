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
                .areNotMetaAnnotatedWith(org.junit.jupiter.api.Disabled.class)
                .and()
                .areNotMetaAnnotatedWith(com.tngtech.archunit.junit.ArchIgnore.class)
                .and()
                /*
                 * Meta-annotated, not annotated: a project marks its scenario tests with one
                 * stereotype of its own that carries this annotation, rather than repeating the
                 * annotation on every class. ArchUnit counts a direct annotation as meta-annotated,
                 * so annotating a single class still works.
                 */
                .areNotMetaAnnotatedWith(it.aboutbits.archunit.toolbox.support.ArchIgnoreNoProductionCounterpart.class)
                .and()
                /*
                 * An architecture test is named after no production class by definition. Excluded by
                 * package here, but deliberately not in TestClassVisibilityArchRule: being package
                 * private is just as achievable for an architecture test as for any other test.
                 */
                .resideOutsideOfPackages(".._support..", ".._config..", ".._architecture..")
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

            // JavaClasses is map-backed by fully qualified name, so this is a lookup rather than a
            // scan of every imported class per test class.
            if (!allClasses.contain(productionClassFullName)) {
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
