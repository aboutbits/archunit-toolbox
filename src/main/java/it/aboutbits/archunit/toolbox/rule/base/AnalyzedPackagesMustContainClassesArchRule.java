package it.aboutbits.archunit.toolbox.rule.base;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.ArchTest;
import org.jspecify.annotations.NullMarked;

/// Checks that the analyzed packages contain any classes at all.
///
/// Every other rule tolerates an empty selection, because whether a project has records, controllers
/// or `@Nested` test classes is the project's business and not something this library gets to require.
/// That leaves exactly one dangerous case: a mistyped or moved package in `@AnalyzeClasses` imports
/// nothing, and every rule then passes without looking at a single class. This rule is what turns that
/// into a failure, once, with a message that names the actual problem.
@SuppressWarnings({"checkstyle:InterfaceIsType", "java:S1214"})
@NullMarked
public interface AnalyzedPackagesMustContainClassesArchRule {
    @SuppressWarnings({"unused", "checkstyle:MethodName", "java:S100"})
    @ArchTest
    default void analyzed_packages_must_contain_classes(JavaClasses classes) {
        if (classes.isEmpty()) {
            throw new AssertionError(
                    "No classes were imported, so none of the architecture rules checked anything. "
                            + "Verify the packages passed to @AnalyzeClasses."
            );
        }
    }
}
