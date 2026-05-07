package it.aboutbits.archunit.toolbox.rule.base;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.jspecify.annotations.NullMarked;

import java.util.stream.Collectors;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static it.aboutbits.archunit.toolbox.config.ArchRuleConfig.TEST_CLASS_SUFFIXES;
import static it.aboutbits.archunit.toolbox.util.LineNumberUtil.getLineNumber;

@SuppressWarnings({"checkstyle:InterfaceIsType", "java:S1214"})
@NullMarked
public interface TestNestedClassMatchNameArchRule {
    @SuppressWarnings({"unused", "checkstyle:MethodName", "java:S100"})
    @ArchTest
    default void nested_test_classes_have_matching_production_method_name(JavaClasses classes) {
        classes().that()
                .haveNameMatching(".+(" + String.join("|", TEST_CLASS_SUFFIXES) + ")$")
                .and()
                .areNotAnnotatedWith(org.junit.jupiter.api.Disabled.class)
                .and()
                .areNotAnnotatedWith(com.tngtech.archunit.junit.ArchIgnore.class)
                .should(new HaveNestedClassesThatHaveAMatchingProductionMethodName(classes))
                .allowEmptyShould(true)
                .check(classes);
    }

    class HaveNestedClassesThatHaveAMatchingProductionMethodName extends ArchCondition<JavaClass> {
        private final JavaClasses allClasses;

        public HaveNestedClassesThatHaveAMatchingProductionMethodName(JavaClasses allClasses) {
            super("have a @Nested class that matches the method name in the production code class");
            this.allClasses = allClasses;
        }

        @Override
        public void check(JavaClass testClass, ConditionEvents events) {
            // Find all @Nested classes that are nested in the test class, except the $Validation classes
            var nestedClasses = testClass.getPackage()
                    .getClasses()
                    .stream()
                    .filter(clazz -> clazz.getName().startsWith(testClass.getName() + "$")
                            && clazz.isAnnotatedWith(org.junit.jupiter.api.Nested.class)
                            && !clazz.isAnnotatedWith(it.aboutbits.archunit.toolbox.support.ArchIgnoreGroupName.class)
                            && !clazz.getName().endsWith("$Validation")
                    )
                    .collect(Collectors.toSet());

            if (nestedClasses.isEmpty()) {
                return;
            }

            for (var nestedClass : nestedClasses) {
                /*
                 * We want to skip classes that are a @Nested Group
                 * (for example, $ExportAction of class RwValueListGroupTest)
                 * as they are not directly related to a method in the production class,
                 * but the @Nested classes within this @Nested Group class are.
                 *
                 * Example:
                 * We want to check if method deleteAll
                 * of @Nested test class
                 * it.aboutbits.example.admin.domain.rw_value.action.RwValueListActionGroupTest$DeleteAction$DeleteAll
                 * exists in production class
                 * it.aboutbits.example.admin.domain.rw_value.action.RwValueListActionGroup
                 * but this code skips @Nested class
                 * it.aboutbits.example.admin.domain.rw_value.action.RwValueListActionGroupTest$DeleteAction
                 */
                if (nestedClass.getPackage()
                        .getClasses()
                        .stream()
                        .anyMatch(clazz -> clazz.getName().startsWith(nestedClass.getName() + "$")
                                && clazz.isAnnotatedWith(org.junit.jupiter.api.Nested.class)
                                && !clazz.isAnnotatedWith(it.aboutbits.archunit.toolbox.support.ArchIgnoreGroupName.class)
                                && !clazz.getName().endsWith("$Validation")
                        )
                ) {
                    continue;
                }

                var nestedClassName = nestedClass.getSimpleName();
                var expectedMethodName = Character.toLowerCase(nestedClassName.charAt(0))
                        + nestedClassName.substring(1);

                var nestedClassBaseClassSimpleName = nestedClass.getName()
                        .replace(nestedClass.getPackageName() + ".", "")
                        .replaceAll("\\$.+", "");
                var nestedClassLineNumber = getLineNumber(nestedClass);

                /*
                 * This is only true for inner @Nested group classes like for example
                 * it.aboutbits.example.admin.domain.rw_value.action.RwValueListActionGroupTest$DeleteAction$DeleteAll
                 * where the enclosing class is
                 * it.aboutbits.example.admin.domain.rw_value.action.RwValueListActionGroupTest$DeleteAction
                 *
                 * If an enclosing class is found, this will produce a suffix like "$DeleteAction"
                 */
                var enclosingClassSuffix = nestedClass.getEnclosingClass()
                        .map(enclosingClass -> {
                            if (!enclosingClass.getName().contains("$")) {
                                return null;
                            }

                            return "$%s".formatted(enclosingClass.getSimpleName());
                        });

                var productionClassName = "%s.%s%s".formatted(
                        testClass.getPackageName(),
                        testClass.getSimpleName()
                                .replaceAll("(" + String.join("|", TEST_CLASS_SUFFIXES) + ")$", ""),
                        enclosingClassSuffix.orElse("")
                );

                var productionClassOptional = allClasses.stream()
                        .filter(clazz -> clazz.getFullName().equals(productionClassName))
                        .findFirst();

                if (productionClassOptional.isEmpty() && enclosingClassSuffix.isPresent()) {
                    var message = "The @Nested test class <%s> (%s.java:%s)%ndoes not have a matching production class <%s>".formatted(
                            nestedClass.getName(),
                            nestedClassBaseClassSimpleName,
                            nestedClassLineNumber,
                            productionClassName
                    );
                    events.add(SimpleConditionEvent.violated(nestedClass, message));
                }

                if (productionClassOptional.isPresent()) {
                    var productionClass = productionClassOptional.get();

                    var methodExists = productionClass.getMethods()
                            .stream()
                            .map(JavaMethod::getName)
                            .anyMatch(methodName -> methodName.equals(expectedMethodName));

                    if (!methodExists) {
                        var productionClassLineNumber = getLineNumber(productionClass);

                        var message = "The @Nested test class <%s> (%s.java:%s)%ndoes not match any expected method name <%s> in production class <%s> (%s.java:%s)".formatted(
                                nestedClass.getName(),
                                nestedClassBaseClassSimpleName,
                                nestedClassLineNumber,
                                expectedMethodName,
                                productionClass.getName(),
                                productionClass.getName()
                                        .replace(nestedClass.getPackageName() + ".", "")
                                        .replaceAll("\\$.+", ""),
                                productionClassLineNumber
                        );
                        events.add(SimpleConditionEvent.violated(nestedClass, message));
                    }
                }
            }
        }
    }
}
