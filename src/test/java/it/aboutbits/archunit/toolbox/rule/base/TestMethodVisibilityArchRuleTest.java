package it.aboutbits.archunit.toolbox.rule.base;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static it.aboutbits.archunit.toolbox.RuleEvaluation.fixture;
import static it.aboutbits.archunit.toolbox.RuleEvaluation.violationCount;
import static it.aboutbits.archunit.toolbox.RuleEvaluation.violationOf;
import static org.assertj.core.api.Assertions.assertThat;

@NullMarked
class TestMethodVisibilityArchRuleTest implements TestMethodVisibilityArchRule {
    @Test
    void a_public_test_method_is_reported() {
        var classes = fixture("testmethodvisibility.bad");

        var failure = violationOf(() -> test_methods_must_be_package_private(classes));

        assertThat(violationCount(failure)).isEqualTo(1);
        assertThat(failure)
                .hasMessageContaining("it_works")
                .hasMessageContaining("package private");
    }

    @Test
    void a_package_private_test_method_is_accepted() {
        test_methods_must_be_package_private(fixture("testmethodvisibility.good"));
    }
}
