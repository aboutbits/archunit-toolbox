package it.aboutbits.archunit.toolbox.rule.base;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static it.aboutbits.archunit.toolbox.RuleEvaluation.fixture;
import static it.aboutbits.archunit.toolbox.RuleEvaluation.violationCount;
import static it.aboutbits.archunit.toolbox.RuleEvaluation.violationOf;
import static org.assertj.core.api.Assertions.assertThat;

@NullMarked
class TestNestedClassVisibilityArchRuleTest implements TestNestedClassVisibilityArchRule {
    @Test
    void a_public_nested_test_class_is_reported() {
        var classes = fixture("nestedclassvisibility.bad");

        var failure = violationOf(() -> nested_test_classes_must_be_package_private(classes));

        assertThat(violationCount(failure)).isEqualTo(1);
        assertThat(failure)
                .hasMessageContaining("WidgetTest$DoWork")
                .hasMessageContaining("package private");
    }

    @Test
    void a_package_private_nested_test_class_is_accepted() {
        nested_test_classes_must_be_package_private(fixture("nestedclassvisibility.good"));
    }
}
