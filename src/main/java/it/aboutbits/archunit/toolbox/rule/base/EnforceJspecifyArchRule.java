package it.aboutbits.archunit.toolbox.rule.base;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.ArchTest;
import org.jspecify.annotations.NullMarked;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@SuppressWarnings({"checkstyle:InterfaceIsType", "java:S1214"})
@NullMarked
public interface EnforceJspecifyArchRule {
    @SuppressWarnings({"unused", "checkstyle:MethodName", "java:S100"})
    @ArchTest
    default void top_level_classes_must_be_annotated_with_jspecify(JavaClasses classes) {
        classes()
                .that()
                .areTopLevelClasses()
                .and()
                .areNotAnnotations()
                .should()
                .beAnnotatedWith(org.jspecify.annotations.NullMarked.class)
                .orShould()
                .beAnnotatedWith(org.jspecify.annotations.NullUnmarked.class)
                .check(classes);
    }
}
