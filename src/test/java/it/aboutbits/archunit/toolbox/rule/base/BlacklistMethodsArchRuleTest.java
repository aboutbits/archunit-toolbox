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

    @Test
    void a_blacklisted_junit_assertion_is_reported() {
        var failure = violationOf(
                () -> no_blacklisted_methods_are_used(fixture("blacklistmethods.badjunitassertion")));

        assertThat(violationCount(failure)).isEqualTo(1);
        assertThat(failure)
                .hasMessageContaining("org.junit.jupiter.api.Assertions.assertThrowsExactly");
    }

    /// Removing the AssertJ entries below must not drop the house rule itself: these three methods
    /// exist on JUnit's Assertions and have to stay blacklisted under that owner.
    @Test
    void the_blacklist_names_the_junit_assertion_methods_under_their_real_owner() {
        assertThat(BLACKLISTED_METHODS).contains(
                "org.junit.jupiter.api.Assertions.assertThrows",
                "org.junit.jupiter.api.Assertions.assertThrowsExactly",
                "org.junit.jupiter.api.Assertions.assertDoesNotThrow"
        );
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
