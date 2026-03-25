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

@SuppressWarnings({"checkstyle:InterfaceIsType", "java:S1214"})
@NullMarked
public interface BlacklistMethodsArchRule {
    @SuppressWarnings("java:S2386")
    Set<String> BLACKLISTED_METHODS = new HashSet<>(
            Set.of(
                    // We should use `assertThatExceptionOfType(...).isThrownBy(...)` instead of `assertThatThrownBy(...)`
                    "org.assertj.core.api.Assertions.assertThatThrownBy",
                    "org.junit.jupiter.api.Assertions.assertThrows",
                    "org.junit.jupiter.api.Assertions.assertDoesNotThrow",
                    // assertThat (allowed is only org.assertj.core.api.Assertions.assertThat)
                    "org.assertj.core.api.AssertionsForClassTypes.assertThat",
                    "org.assertj.core.api.AssertionsForClassTypes.assertThatCode",
                    "org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType",
                    "org.assertj.core.api.AssertionsForInterfaceTypes.assertThat",
                    "org.assertj.core.api.ClassBasedNavigableIterableAssert.assertThat",
                    "org.assertj.core.api.ClassBasedNavigableListAssert.assertThat",
                    "org.assertj.core.api.FactoryBasedNavigableIterableAssert.assertThat",
                    "org.assertj.core.api.FactoryBasedNavigableListAssert.assertThat",
                    "org.assertj.core.api.Java6Assertions.assertThat",
                    "org.hamcrest.MatcherAssert.assertThat",
                    "org.junit.Assert.assertArrayEquals",
                    "org.junit.Assert.assertEquals",
                    "org.junit.Assert.assertFalse",
                    "org.junit.Assert.assertNotEquals",
                    "org.junit.Assert.assertNotSame",
                    "org.junit.Assert.assertSame",
                    "org.junit.Assert.assertThat",
                    "org.junit.Assert.assertThrows",
                    "org.junit.Assert.assertTrue",
                    "org.junit.Assert.fail",
                    // use assertThat (org.assertj.core.api.Assertions.assertThat)
                    "org.junit.jupiter.api.Assertions.assertArrayEquals",
                    "org.junit.jupiter.api.Assertions.assertEquals",
                    "org.junit.jupiter.api.Assertions.assertFalse",
                    "org.junit.jupiter.api.Assertions.assertInstanceOf",
                    "org.junit.jupiter.api.Assertions.assertIterableEquals",
                    "org.junit.jupiter.api.Assertions.assertNotEquals",
                    "org.junit.jupiter.api.Assertions.assertNotNull",
                    "org.junit.jupiter.api.Assertions.assertNull",
                    "org.junit.jupiter.api.Assertions.assertTrue",
                    "org.testcontainers.shaded.org.hamcrest.MatcherAssert.assertThat"
            )
    );

    @SuppressWarnings({"unused", "checkstyle:MethodName", "java:S100"})
    @ArchTest
    default void no_blacklisted_methods_are_used(JavaClasses classes) {
        classes()
                .should(new NotUseBlacklistedMethods())
                .check(classes);
    }

    class NotUseBlacklistedMethods extends ArchCondition<JavaClass> {
        public NotUseBlacklistedMethods() {
            super("not use blacklisted methods or statically import them");
        }

        @Override
        public void check(JavaClass javaClass, ConditionEvents events) {
            // Check all method calls from this class
            for (var method : javaClass.getMethods()) {
                for (var methodCall : method.getMethodCallsFromSelf()) {
                    var fullMethodName = "%s.%s".formatted(
                            methodCall.getTargetOwner().getFullName(),
                            methodCall.getTarget().getName()
                    );

                    if (BLACKLISTED_METHODS.contains(fullMethodName)) {
                        var message = String.format(
                                "Method %s calls blacklisted method %s (%s.java:%d)",
                                method.getFullName(),
                                fullMethodName,
                                javaClass.getSimpleName(),
                                methodCall.getSourceCodeLocation().getLineNumber()
                        );
                        events.add(SimpleConditionEvent.violated(method, message));
                    }
                }
            }

            // Check static initializers for method calls
            javaClass.getStaticInitializer().ifPresent(staticInitializer -> {
                for (var methodCall : staticInitializer.getMethodCallsFromSelf()) {
                    var fullMethodName = "%s.%s".formatted(
                            methodCall.getTargetOwner().getFullName(),
                            methodCall.getTarget().getName()
                    );

                    if (BLACKLISTED_METHODS.contains(fullMethodName)) {
                        var message = String.format(
                                "Static initializer in %s calls blacklisted method %s (%s.java:%d)",
                                javaClass.getFullName(),
                                fullMethodName,
                                javaClass.getSimpleName(),
                                methodCall.getSourceCodeLocation().getLineNumber()
                        );
                        events.add(SimpleConditionEvent.violated(staticInitializer, message));
                    }
                }
            });
        }
    }
}
