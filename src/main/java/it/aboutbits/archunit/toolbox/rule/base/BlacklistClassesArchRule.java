package it.aboutbits.archunit.toolbox.rule.base;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.ArchTest;
import org.jspecify.annotations.NullMarked;

import java.util.HashSet;
import java.util.Set;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@SuppressWarnings({"checkstyle:InterfaceIsType", "java:S1214"})
@NullMarked
public interface BlacklistClassesArchRule {
    @SuppressWarnings("java:S2386")
    Set<String> BLACKLISTED_CLASSES = new HashSet<>(
            Set.of(
                    // use FakerExtended from toolbox
                    "net.datafaker.Faker"
            )
    );

    @SuppressWarnings({"unused", "checkstyle:MethodName", "java:S100"})
    @ArchTest
    default void no_blacklisted_classes_are_used(JavaClasses classes) {
        noClasses()
                .should()
                .dependOnClassesThat(
                        new DescribedPredicate<>("not use blacklisted classes") {
                            @Override
                            public boolean test(JavaClass javaClass) {
                                return BLACKLISTED_CLASSES.contains(javaClass.getFullName());
                            }
                        }
                )
                .check(classes);
    }
}
