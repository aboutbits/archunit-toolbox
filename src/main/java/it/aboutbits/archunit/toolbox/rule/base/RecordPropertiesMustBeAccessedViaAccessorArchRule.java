package it.aboutbits.archunit.toolbox.rule.base;

import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.jspecify.annotations.NullMarked;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;

@SuppressWarnings({"checkstyle:InterfaceIsType", "java:S1214"})
@NullMarked
public interface RecordPropertiesMustBeAccessedViaAccessorArchRule {
    String ARCH_ALLOW_DIRECT_ACCESS = "it.aboutbits.springboot.toolbox.archunit.ArchAllowDirectAccess";

    @SuppressWarnings({"unused", "checkstyle:ConstantName", "java:S115"})
    @ArchTest
    ArchRule record_properties_must_be_accessed_via_accessor = fields()
            .that().areDeclaredInClassesThat().areRecords()
            .and().areNotStatic()
            .should(new ArchCondition<>("only be accessed by the record itself") {
                @Override
                public void check(JavaField field, ConditionEvents events) {
                    if (field.isAnnotatedWith(ARCH_ALLOW_DIRECT_ACCESS)
                            || field.getOwner().isAnnotatedWith(ARCH_ALLOW_DIRECT_ACCESS)) {
                        return;
                    }

                    for (var access : field.getAccessesToSelf()) {
                        // Check if the origin of the access is NOT the record class that owns the field
                        if (!access.getOrigin().getOwner().equals(field.getOwner())) {
                            var message = "Record property [%s] in [%s] accessed directly by [%s]. Use accessor method [%s()] instead. (%s.java:%d)"
                                    .formatted(
                                            field.getName(),
                                            field.getOwner().getSimpleName(),
                                            access.getOrigin().getFullName(),
                                            field.getName(),
                                            access.getOrigin().getOwner().getSimpleName(),
                                            access.getLineNumber()
                                    );
                            events.add(SimpleConditionEvent.violated(access, message));
                        }
                    }
                }
            });
}
