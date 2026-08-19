package it.aboutbits.archunit.toolbox.rule.base;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static it.aboutbits.archunit.toolbox.RuleEvaluation.fixture;
import static it.aboutbits.archunit.toolbox.RuleEvaluation.violationCount;
import static it.aboutbits.archunit.toolbox.RuleEvaluation.violationOf;
import static org.assertj.core.api.Assertions.assertThat;

@NullMarked
class EnforceJspecifyArchRuleTest implements EnforceJspecifyArchRule {
    @Test
    void a_top_level_class_without_a_jspecify_annotation_is_reported() {
        var classes = fixture("jspecify.bad");

        var failure = violationOf(() -> top_level_classes_must_be_annotated_with_jspecify(classes));

        assertThat(violationCount(failure)).isEqualTo(1);
        assertThat(failure)
                .hasMessageContaining("UnannotatedClass")
                .hasMessageContaining("NullMarked");
    }

    @Test
    void an_annotated_top_level_class_is_accepted() {
        top_level_classes_must_be_annotated_with_jspecify(fixture("jspecify.good"));
    }
}
