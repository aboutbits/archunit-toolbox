package it.aboutbits.archunit.toolbox.rule.common;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaAnnotation;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

public interface ControllerRequestMappingsMustBeSecurityTested {
    @SuppressWarnings({"unused", "checkstyle:MethodName", "java:S100"})
    @ArchTest
    default void controller_methods_with_request_mapping_must_be_security_tested(JavaClasses classes) {
        methods()
                .that()
                .areDeclaredInClassesThat(
                        new DescribedPredicate<>("are annotated with @Controller or @RestController") {
                            @Override
                            public boolean test(JavaClass javaClass) {
                                return javaClass.isAnnotatedWith("org.springframework.stereotype.Controller")
                                        || javaClass.isAnnotatedWith(
                                        "org.springframework.web.bind.annotation.RestController");
                            }
                        }
                )
                .and()
                .areAnnotatedWith(new DescribedPredicate<>("a RequestMapping annotation") {
                    @Override
                    public boolean test(JavaAnnotation<?> javaAnnotation) {
                        var rawJavaAnnotation = javaAnnotation.getRawType();

                        return rawJavaAnnotation.isAssignableTo("org.springframework.web.bind.annotation.RequestMapping")
                                // The (Get|Post|Put|Delete|Patch)Mapping annotations are annotated with @RequestMapping
                                || rawJavaAnnotation.isAnnotatedWith(
                                "org.springframework.web.bind.annotation.RequestMapping"
                        );
                    }
                })
                .should(new BeSecurityTested())
                .check(classes);
    }

    class BeSecurityTested extends ArchCondition<JavaMethod> {
        public BeSecurityTested() {
            super("be security tested");
        }

        @Override
        public void check(JavaMethod method, ConditionEvents events) {
            var controllerClass = method.getOwner();
            var controllerClassName = controllerClass.getFullName();
            var securityTestClassName = controllerClassName + "SecurityTest";
            var methodName = method.getName();
            var expectedNestedMethodClassName = Character.toUpperCase(methodName.charAt(0))
                    + methodName.substring(1);

            JavaClass securityTestClass;
            try {
                securityTestClass = controllerClass
                        .getPackage()
                        .getClassWithFullyQualifiedName(securityTestClassName);
            } catch (IllegalArgumentException _) {
                events.add(SimpleConditionEvent.violated(
                        method,
                        String.format(
                                "Method %s in class %s%nis annotated with @RequestMapping but the corresponding SecurityTest class %s is missing",
                                method.getFullName(),
                                controllerClass.getFullName(),
                                securityTestClassName
                        )
                ));

                return;
            }

            var nestedMethodTestClassFound = securityTestClass.getPackage()
                    .getClasses()
                    .stream()
                    .anyMatch(clazz -> clazz.getName()
                            .startsWith("%s$%s".formatted(
                                    securityTestClass.getName(),
                                    expectedNestedMethodClassName
                            ))
                            && clazz.isAnnotatedWith(org.junit.jupiter.api.Nested.class)
                            && !clazz.isAnnotatedWith(com.tngtech.archunit.junit.ArchIgnore.class)
                            && !clazz.isAnnotatedWith(it.aboutbits.archunit.toolbox.support.ArchIgnoreGroupName.class)
                    );

            if (!nestedMethodTestClassFound) {
                events.add(SimpleConditionEvent.violated(
                        method,
                        String.format(
                                "Method %s%nis annotated with @RequestMapping, and SecurityTest class %s exists, but it does not contain a @Nested test class named %s (%s.java:0)",
                                method.getFullName(),
                                securityTestClass.getFullName(),
                                expectedNestedMethodClassName,
                                securityTestClass.getSimpleName()
                        )
                ));
            }
        }
    }
}
