package it.aboutbits.archunit.toolbox.rule.common;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static it.aboutbits.archunit.toolbox.RuleEvaluation.fixture;
import static it.aboutbits.archunit.toolbox.RuleEvaluation.violationCount;
import static it.aboutbits.archunit.toolbox.RuleEvaluation.violationOf;
import static org.assertj.core.api.Assertions.assertThat;

@NullMarked
class SortMappingsExhaustiveArchRuleTest implements SortMappingsExhaustiveArchRule {
    @Test
    void a_sort_enum_value_without_a_mapping_is_reported() {
        var classes = fixture("sortmappings.bad");

        var failure = violationOf(() -> sort_mappings_cover_all_sort_enum_values(classes));

        assertThat(violationCount(failure)).isEqualTo(1);
        assertThat(failure)
                .hasMessageContaining("is missing mappings for enum WidgetSort values")
                .hasMessageContaining("CREATED_AT");
    }

    /**
     * A non-static field cannot be read reflectively, so it used to be skipped with nothing but a
     * discarded log warning.
     */
    @Test
    void a_non_static_sort_mappings_field_is_reported() {
        var classes = fixture("sortmappings.badnonstatic");

        var failure = violationOf(() -> sort_mappings_cover_all_sort_enum_values(classes));

        assertThat(violationCount(failure)).isEqualTo(1);
        assertThat(failure).hasMessageContaining("must be static");
    }

    @Test
    void exhaustive_sort_mappings_are_accepted() {
        sort_mappings_cover_all_sort_enum_values(fixture("sortmappings.good"));
    }
}
