package it.aboutbits.archunit.toolbox.rule.base;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static it.aboutbits.archunit.toolbox.RuleEvaluation.fixture;
import static it.aboutbits.archunit.toolbox.RuleEvaluation.violationCount;
import static it.aboutbits.archunit.toolbox.RuleEvaluation.violationOf;
import static org.assertj.core.api.Assertions.assertThat;

@NullMarked
class BlacklistAnnotationsArchRuleTest implements BlacklistAnnotationsArchRule {
    @Test
    void a_blacklisted_annotation_on_a_class_is_reported() {
        var failure = violationOf(() -> no_blacklisted_annotations_are_used(fixture("blacklistannotations.badclass")));

        assertThat(violationCount(failure)).isEqualTo(1);
        assertThat(failure).hasMessageContaining("Class").hasMessageContaining("org.junit.Ignore");
    }

    @Test
    void a_blacklisted_annotation_on_a_method_is_reported() {
        var failure = violationOf(() -> no_blacklisted_annotations_are_used(fixture("blacklistannotations.badmethod")));

        assertThat(violationCount(failure)).isEqualTo(1);
        assertThat(failure).hasMessageContaining("Method").hasMessageContaining("org.junit.Ignore");
    }

    @Test
    void a_blacklisted_annotation_on_a_method_parameter_is_reported() {
        var failure = violationOf(() -> no_blacklisted_annotations_are_used(fixture("blacklistannotations.badparam")));

        assertThat(violationCount(failure)).isEqualTo(1);
        assertThat(failure).hasMessageContaining("Parameter 0 of method").hasMessageContaining("lombok.NonNull");
    }

    @Test
    void a_blacklisted_annotation_on_a_field_is_reported() {
        var failure = violationOf(() -> no_blacklisted_annotations_are_used(fixture("blacklistannotations.badfield")));

        assertThat(violationCount(failure)).isEqualTo(1);
        assertThat(failure).hasMessageContaining("Field value").hasMessageContaining("lombok.NonNull");
    }

    /**
     * The position that matters most in practice, and the one a rule looking only at getMethods()
     * cannot see.
     */
    @Test
    void a_blacklisted_annotation_on_a_constructor_parameter_is_reported() {
        var failure = violationOf(
                () -> no_blacklisted_annotations_are_used(fixture("blacklistannotations.badctorparam")));

        assertThat(violationCount(failure)).isEqualTo(1);
        assertThat(failure)
                .hasMessageContaining("Parameter 0 of constructor")
                .hasMessageContaining("lombok.NonNull");
    }

    @Test
    void a_class_using_no_blacklisted_annotation_is_accepted() {
        no_blacklisted_annotations_are_used(fixture("blacklistannotations.good"));
    }
}
