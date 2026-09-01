package it.aboutbits.archunit.toolbox.rule.base;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.jspecify.annotations.NullMarked;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static it.aboutbits.archunit.toolbox.util.CodeUnitUtil.describeKind;
import static it.aboutbits.archunit.toolbox.util.LineNumberUtil.getLineNumber;

@SuppressWarnings({"checkstyle:InterfaceIsType", "java:S1214"})
@NullMarked
public interface NoSystemOutOrErrArchRule {
    @SuppressWarnings({"unused", "checkstyle:MethodName", "java:S100"})
    @ArchTest
    default void no_system_out_or_err_is_used(JavaClasses classes) {
        classes()
                .should(new NotUseSystemOutOrErr())
                .allowEmptyShould(true)
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
            // getCodeUnits() covers methods, constructors and the static initializer. getMethods()
            // would miss constructors, and with them every instance field initializer.
            for (var codeUnit : javaClass.getCodeUnits()) {
                for (var fieldAccess : codeUnit.getFieldAccesses()) {
                    if (!isSystemOutOrErr(
                            fieldAccess.getTargetOwner().getFullName(),
                            fieldAccess.getTarget().getName()
                    )) {
                        continue;
                    }

                    var message = String.format(
                            "%s %s accesses %s.%s (%s.java:%d)",
                            describeKind(codeUnit),
                            codeUnit.getFullName(),
                            SYSTEM_CLASS,
                            fieldAccess.getTarget().getName(),
                            javaClass.getSimpleName(),
                            getLineNumber(fieldAccess)
                    );
                    events.add(SimpleConditionEvent.violated(codeUnit, message));
                }
            }
        }

        private boolean isSystemOutOrErr(String ownerFullName, String fieldName) {
            return SYSTEM_CLASS.equals(ownerFullName)
                    && (FIELD_OUT.equals(fieldName) || FIELD_ERR.equals(fieldName));
        }
    }
}
