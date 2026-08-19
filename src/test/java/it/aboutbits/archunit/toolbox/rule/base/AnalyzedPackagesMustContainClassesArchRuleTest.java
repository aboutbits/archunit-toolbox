package it.aboutbits.archunit.toolbox.rule.base;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static it.aboutbits.archunit.toolbox.RuleEvaluation.fixture;
import static it.aboutbits.archunit.toolbox.RuleEvaluation.noClassesImported;
import static it.aboutbits.archunit.toolbox.RuleEvaluation.violationOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@NullMarked
class AnalyzedPackagesMustContainClassesArchRuleTest implements AnalyzedPackagesMustContainClassesArchRule {
    /**
     * The one case every other rule deliberately tolerates: nothing was imported, so nothing can be
     * checked and every rule would otherwise pass.
     */
    @Test
    void an_import_without_any_classes_is_reported() {
        var classes = noClassesImported();

        var failure = violationOf(() -> analyzed_packages_must_contain_classes(classes));

        assertThat(failure)
                .hasMessageContaining("No classes were imported")
                .hasMessageContaining("@AnalyzeClasses");
    }

    @Test
    void an_import_with_classes_is_accepted() {
        var classes = fixture("barren");

        assertThatCode(() -> analyzed_packages_must_contain_classes(classes)).doesNotThrowAnyException();
    }
}
