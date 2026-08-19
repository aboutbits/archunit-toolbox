package it.aboutbits.archunit.toolbox.rule.base;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static it.aboutbits.archunit.toolbox.RuleEvaluation.fixture;
import static it.aboutbits.archunit.toolbox.RuleEvaluation.violationCount;
import static it.aboutbits.archunit.toolbox.RuleEvaluation.violationOf;
import static it.aboutbits.archunit.toolbox.rule.base.RecordPropertiesMustBeAccessedViaAccessorArchRule.record_properties_must_be_accessed_via_accessor;
import static org.assertj.core.api.Assertions.assertThat;

@NullMarked
class RecordPropertiesMustBeAccessedViaAccessorArchRuleTest {
    /**
     * Only reachable for a nested record: nestmates share access to private members, so the read
     * compiles to a direct field access rather than an accessor call.
     */
    @Test
    void a_direct_read_of_a_record_field_from_outside_the_record_is_reported() {
        var classes = fixture("recordaccessor.badnested");

        var failure = violationOf(() -> record_properties_must_be_accessed_via_accessor.check(classes));

        assertThat(violationCount(failure)).isEqualTo(1);
        assertThat(failure)
                .hasMessageContaining("Record property [amount]")
                .hasMessageContaining("Use accessor method [amount()] instead");
    }

    @Test
    void a_read_through_the_accessor_is_accepted() {
        record_properties_must_be_accessed_via_accessor.check(fixture("recordaccessor.goodaccessor"));
    }

    @Test
    void a_record_opting_out_of_the_rule_is_accepted() {
        record_properties_must_be_accessed_via_accessor.check(fixture("recordaccessor.goodoptout"));
    }
}
