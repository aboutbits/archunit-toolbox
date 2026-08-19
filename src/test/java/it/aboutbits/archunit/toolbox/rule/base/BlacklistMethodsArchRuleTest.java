package it.aboutbits.archunit.toolbox.rule.base;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static it.aboutbits.archunit.toolbox.RuleEvaluation.fixture;
import static it.aboutbits.archunit.toolbox.RuleEvaluation.violationCount;
import static it.aboutbits.archunit.toolbox.RuleEvaluation.violationOf;
import static org.assertj.core.api.Assertions.assertThat;

@NullMarked
class BlacklistMethodsArchRuleTest implements BlacklistMethodsArchRule {
    @Test
    void a_blacklisted_call_from_a_method_is_reported() {
        var failure = violationOf(() -> no_blacklisted_methods_are_used(fixture("blacklistmethods.badmethod")));

        assertThat(violationCount(failure)).isEqualTo(1);
        assertThat(failure)
                .hasMessageContaining("Method")
                .hasMessageContaining("assertThatThrownBy");
    }

    @Test
    void a_blacklisted_call_from_a_constructor_is_reported() {
        var failure = violationOf(() -> no_blacklisted_methods_are_used(fixture("blacklistmethods.badconstructor")));

        assertThat(violationCount(failure)).isEqualTo(1);
        assertThat(failure)
                .hasMessageContaining("Constructor")
                .hasMessageContaining("assertThatThrownBy");
    }

    /// An instance field initializer is compiled into the constructor, so it needs the same reach.
    @Test
    void a_blacklisted_call_from_an_instance_field_initializer_is_reported() {
        var failure = violationOf(() -> no_blacklisted_methods_are_used(fixture("blacklistmethods.badfieldinit")));

        assertThat(violationCount(failure)).isEqualTo(1);
        assertThat(failure)
                .hasMessageContaining("Constructor")
                .hasMessageContaining("assertThatThrownBy");
    }

    @Test
    void a_blacklisted_call_from_a_static_initializer_is_reported() {
        var failure = violationOf(() -> no_blacklisted_methods_are_used(fixture("blacklistmethods.badstatic")));

        assertThat(violationCount(failure)).isEqualTo(1);
        assertThat(failure)
                .hasMessageContaining("Static initializer")
                .hasMessageContaining("assertThatThrownBy");
    }

    @Test
    void an_allowed_assertion_is_accepted() {
        no_blacklisted_methods_are_used(fixture("blacklistmethods.good"));
    }

    /// A blacklist entry naming a method that does not exist can never match, so it reads as coverage
    /// without providing any.
    @Test
    void the_blacklist_does_not_name_assertj_methods_that_do_not_exist() {
        assertThat(BLACKLISTED_METHODS)
                .doesNotContain(
                        "org.assertj.core.api.Assertions.assertThrows",
                        "org.assertj.core.api.Assertions.assertThrowsExactly",
                        "org.assertj.core.api.Assertions.assertDoesNotThrow"
                );
    }
}
