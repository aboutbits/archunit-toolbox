package it.aboutbits.archunit.toolbox.rule.base;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.jspecify.annotations.NullMarked;

import java.util.HashSet;
import java.util.Set;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static it.aboutbits.archunit.toolbox.util.CodeUnitUtil.describeKind;
import static it.aboutbits.archunit.toolbox.util.LineNumberUtil.getLineNumber;

@SuppressWarnings({"checkstyle:InterfaceIsType", "java:S1214"})
@NullMarked
public interface BlacklistAnnotationsArchRule {
    @SuppressWarnings("java:S2386")
    Set<String> BLACKLISTED_ANNOTATIONS = new HashSet<>(
            Set.of(
                    "org.junit.After",
                    "org.junit.AfterClass",
                    "org.junit.Before",
                    "org.junit.BeforeClass",
                    "org.junit.ClassRule",
                    "org.junit.FixMethodOrder",
                    "org.junit.Ignore",
                    "org.junit.Rule",
                    "org.junit.Test",
                    // @NonNull (allowed is only org.jspecify.annotations.NonNull)
                    "lombok.NonNull",
                    "edu.umd.cs.findbugs.annotations.NonNull",
                    "io.micrometer.common.lang.NonNull",
                    "io.micrometer.core.lang.NonNull",
                    "org.springframework.lang.NonNull",
                    "org.testcontainers.shaded.org.checkerframework.checker.nullness.qual.NonNull",
                    // @NotNull (allowed is only jakarta.validation.constraints.NotNull)
                    "com.drew.lang.annotations.NotNull",
                    "com.sun.istack.NotNull",
                    "org.antlr.v4.runtime.misc.NotNull",
                    "org.jetbrains.annotations.NotNull",
                    "software.amazon.awssdk.annotations.NotNull",
                    // @Nullable (allowed is only org.jspecify.annotations.Nullable)
                    "org.springframework.lang.Nullable",
                    "com.drew.lang.annotations.Nullable",
                    "com.sun.istack.Nullable",
                    "edu.umd.cs.findbugs.annotations.Nullable",
                    "io.micrometer.common.lang.Nullable",
                    "io.micrometer.core.lang.Nullable",
                    "jakarta.annotation.Nullable",
                    "javax.annotation.Nullable",
                    "org.jetbrains.annotations.Nullable",
                    "org.testcontainers.shaded.org.checkerframework.checker.nullness.qual.Nullable",
                    // @Transactional (allowed is only org.springframework.transaction.annotation.Transactional)
                    "jakarta.transaction.Transactional"
            )
    );

    @SuppressWarnings({"unused", "checkstyle:MethodName", "java:S100"})
    @ArchTest
    default void no_blacklisted_annotations_are_used(JavaClasses classes) {
        classes()
                .should(new NotUseBlacklistedAnnotations())
                .allowEmptyShould(true)
                .check(classes);
    }

    class NotUseBlacklistedAnnotations extends ArchCondition<JavaClass> {
        public NotUseBlacklistedAnnotations() {
            super("not use blacklisted annotations on classes, methods, method parameters, or fields");
        }

        @Override
        public void check(JavaClass javaClass, ConditionEvents events) {
            // Check annotations on the class itself
            for (var annotation : javaClass.getAnnotations()) {
                if (BLACKLISTED_ANNOTATIONS.contains(annotation.getRawType().getFullName())) {
                    var message = String.format(
                            "Class %s is annotated with blacklisted annotation @%s (%s.java:%d)",
                            javaClass.getFullName(),
                            annotation.getRawType().getFullName(),
                            javaClass.getSimpleName(),
                            getLineNumber(javaClass)
                    );
                    events.add(SimpleConditionEvent.violated(javaClass, message));
                }
            }

            // getCodeUnits() covers methods, constructors and the static initializer. getMethods()
            // would miss constructors, and with them the most common position of all: a blacklisted
            // annotation on a constructor parameter.
            for (var codeUnit : javaClass.getCodeUnits()) {
                for (var annotation : codeUnit.getAnnotations()) {
                    if (BLACKLISTED_ANNOTATIONS.contains(annotation.getRawType().getFullName())) {
                        var message = String.format(
                                "%s %s is annotated with blacklisted annotation @%s (%s.java:%d)",
                                describeKind(codeUnit),
                                codeUnit.getFullName(),
                                annotation.getRawType().getFullName(),
                                javaClass.getSimpleName(),
                                getLineNumber(codeUnit)
                        );
                        events.add(SimpleConditionEvent.violated(codeUnit, message));
                    }
                }

                for (var parameter : codeUnit.getParameters()) {
                    for (var annotation : parameter.getAnnotations()) {
                        if (BLACKLISTED_ANNOTATIONS.contains(annotation.getRawType().getFullName())) {
                            var message = String.format(
                                    "Parameter %s of %s %s is annotated with blacklisted annotation @%s (%s.java:%d)",
                                    parameter.getIndex(),
                                    describeKind(codeUnit).toLowerCase(java.util.Locale.ROOT),
                                    codeUnit.getFullName(),
                                    annotation.getRawType().getFullName(),
                                    javaClass.getSimpleName(),
                                    getLineNumber(codeUnit)
                            ); // Parameter doesn't have its own SLOC, use the code unit's
                            events.add(SimpleConditionEvent.violated(parameter, message));
                        }
                    }
                }
            }

            // Check annotations on fields (ArchUnit includes record components as fields)
            for (var field : javaClass.getFields()) {
                for (var annotation : field.getAnnotations()) {
                    if (BLACKLISTED_ANNOTATIONS.contains(annotation.getRawType().getFullName())) {
                        var message = String.format(
                                "Field %s in class %s is annotated with blacklisted annotation @%s (%s.java:%d)",
                                field.getName(),
                                javaClass.getFullName(),
                                annotation.getRawType().getFullName(),
                                javaClass.getSimpleName(),
                                getLineNumber(field)
                        );
                        events.add(SimpleConditionEvent.violated(field, message));
                    }
                }
            }
        }
    }
}
