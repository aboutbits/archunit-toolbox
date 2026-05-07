package it.aboutbits.archunit.toolbox.rule.base;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.jspecify.annotations.NullMarked;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static it.aboutbits.archunit.toolbox.util.LineNumberUtil.getLineNumber;

@SuppressWarnings({"checkstyle:InterfaceIsType", "java:S1214"})
@NullMarked
public interface NoSystemOutOrErrArchRule {
    @SuppressWarnings({"unused", "checkstyle:MethodName", "java:S100"})
    @ArchTest
    default void no_system_out_or_err_is_used(JavaClasses classes) {
        classes()
                .should(new NotUseSystemOutOrErr())
                .check(classes);
    }

    class NotUseSystemOutOrErr extends ArchCondition<JavaClass> {
        private static final String SYSTEM_CLASS = "java.lang.System";
        private static final String FIELD_OUT = "out";
        private static final String FIELD_ERR = "err";

        public NotUseSystemOutOrErr() {
            super("not use System.out or System.err");
        }

        @Override
        public void check(JavaClass javaClass, ConditionEvents events) {
            checkCodeUnits(javaClass, events);
            javaClass.getStaticInitializer().ifPresent(staticInitializer -> {
                for (var fieldAccess : staticInitializer.getFieldAccesses()) {
                    if (isSystemOutOrErr(
                            fieldAccess.getTargetOwner().getFullName(),
                            fieldAccess.getTarget().getName()
                    )) {
                        var message = String.format(
                                "Static initializer in %s accesses %s.%s (%s.java:%d)",
                                javaClass.getFullName(),
                                SYSTEM_CLASS,
                                fieldAccess.getTarget().getName(),
                                javaClass.getSimpleName(),
                                getLineNumber(fieldAccess)
                        );
                        events.add(SimpleConditionEvent.violated(staticInitializer, message));
                    }
                }
            });
        }

        private void checkCodeUnits(JavaClass javaClass, ConditionEvents events) {
            for (var method : javaClass.getMethods()) {
                for (var fieldAccess : method.getFieldAccesses()) {
                    if (isSystemOutOrErr(
                            fieldAccess.getTargetOwner().getFullName(),
                            fieldAccess.getTarget().getName()
                    )) {
                        var message = String.format(
                                "Method %s accesses %s.%s (%s.java:%d)",
                                method.getFullName(),
                                SYSTEM_CLASS,
                                fieldAccess.getTarget().getName(),
                                javaClass.getSimpleName(),
                                getLineNumber(fieldAccess)
                        );
                        events.add(SimpleConditionEvent.violated(method, message));
                    }
                }
            }
        }

        private boolean isSystemOutOrErr(String ownerFullName, String fieldName) {
            return SYSTEM_CLASS.equals(ownerFullName)
                    && (FIELD_OUT.equals(fieldName) || FIELD_ERR.equals(fieldName));
        }
    }
}
