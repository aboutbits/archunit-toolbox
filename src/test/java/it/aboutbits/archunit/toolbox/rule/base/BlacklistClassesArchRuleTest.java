package it.aboutbits.archunit.toolbox.rule.base;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static it.aboutbits.archunit.toolbox.RuleEvaluation.fixture;
import static it.aboutbits.archunit.toolbox.RuleEvaluation.violationCount;
import static it.aboutbits.archunit.toolbox.RuleEvaluation.violationOf;
import static org.assertj.core.api.Assertions.assertThat;

@NullMarked
class BlacklistClassesArchRuleTest implements BlacklistClassesArchRule {
    @Test
    void depending_on_a_blacklisted_class_is_reported() {
        var failure = violationOf(() -> no_blacklisted_classes_are_used(fixture("blacklistclasses.bad")));

        assertThat(violationCount(failure)).isPositive();
        assertThat(failure)
                .hasMessageContaining("UsesFaker")
                .hasMessageContaining("net.datafaker.Faker");
    }

    @Test
    void depending_on_no_blacklisted_class_is_accepted() {
        no_blacklisted_classes_are_used(fixture("blacklistclasses.good"));
    }
}
