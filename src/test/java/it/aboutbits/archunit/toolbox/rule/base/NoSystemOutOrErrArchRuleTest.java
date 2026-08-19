package it.aboutbits.archunit.toolbox.rule.base;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static it.aboutbits.archunit.toolbox.RuleEvaluation.fixture;
import static it.aboutbits.archunit.toolbox.RuleEvaluation.violationCount;
import static it.aboutbits.archunit.toolbox.RuleEvaluation.violationOf;
import static org.assertj.core.api.Assertions.assertThat;

@NullMarked
class NoSystemOutOrErrArchRuleTest implements NoSystemOutOrErrArchRule {
    @Test
    void a_console_write_from_a_method_is_reported() {
        var failure = violationOf(() -> no_system_out_or_err_is_used(fixture("systemout.badmethod")));

        assertThat(violationCount(failure)).isEqualTo(1);
        assertThat(failure).hasMessageContaining("Method").hasMessageContaining("java.lang.System.out");
    }

    @Test
    void a_console_write_from_a_constructor_is_reported() {
        var failure = violationOf(() -> no_system_out_or_err_is_used(fixture("systemout.badconstructor")));

        assertThat(violationCount(failure)).isEqualTo(1);
        assertThat(failure).hasMessageContaining("Constructor").hasMessageContaining("java.lang.System.err");
    }

    @Test
    void a_console_write_from_a_static_initializer_is_reported() {
        var failure = violationOf(() -> no_system_out_or_err_is_used(fixture("systemout.badstatic")));

        assertThat(violationCount(failure)).isEqualTo(1);
        assertThat(failure).hasMessageContaining("Static initializer");
    }

    @Test
    void a_console_write_from_a_lambda_is_reported() {
        var failure = violationOf(() -> no_system_out_or_err_is_used(fixture("systemout.badlambda")));

        assertThat(violationCount(failure)).isEqualTo(1);
        assertThat(failure).hasMessageContaining("java.lang.System.out");
    }

    @Test
    void a_class_writing_to_no_console_is_accepted() {
        no_system_out_or_err_is_used(fixture("systemout.good"));
    }
}
