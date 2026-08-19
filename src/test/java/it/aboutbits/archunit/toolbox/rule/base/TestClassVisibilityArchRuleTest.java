package it.aboutbits.archunit.toolbox.rule.base;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static it.aboutbits.archunit.toolbox.RuleEvaluation.fixture;
import static it.aboutbits.archunit.toolbox.RuleEvaluation.violationCount;
import static it.aboutbits.archunit.toolbox.RuleEvaluation.violationOf;
import static org.assertj.core.api.Assertions.assertThat;

@NullMarked
class TestClassVisibilityArchRuleTest implements TestClassVisibilityArchRule {
    @Test
    void a_public_test_class_is_reported() {
        var classes = fixture("testclassvisibility.bad");

        var failure = violationOf(() -> test_classes_must_be_package_private(classes));

        assertThat(violationCount(failure)).isEqualTo(1);
        assertThat(failure)
                .hasMessageContaining("WidgetTest")
                .hasMessageContaining("package private");
    }

    @Test
    void a_package_private_test_class_is_accepted() {
        test_classes_must_be_package_private(fixture("testclassvisibility.good"));
    }
}
