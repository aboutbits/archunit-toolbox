package it.aboutbits.archunit.toolbox.rule.base;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static it.aboutbits.archunit.toolbox.RuleEvaluation.fixture;
import static it.aboutbits.archunit.toolbox.RuleEvaluation.violationCount;
import static it.aboutbits.archunit.toolbox.RuleEvaluation.violationOf;
import static org.assertj.core.api.Assertions.assertThat;

@NullMarked
class TestNestedClassMatchNameArchRuleTest implements TestNestedClassMatchNameArchRule {
    @Test
    void a_nested_test_class_naming_no_production_method_is_reported() {
        var classes = fixture("nestedclassname.badmethod");

        var failure = violationOf(() -> nested_test_classes_have_matching_production_method_name(classes));

        assertThat(violationCount(failure)).isEqualTo(1);
        assertThat(failure)
                .hasMessageContaining("WidgetTest$DoSomethingElse")
                .hasMessageContaining("doSomethingElse");
    }

    @Test
    void a_nested_group_without_a_matching_production_class_is_reported() {
        var classes = fixture("nestedclassname.badgroup");

        var failure = violationOf(() -> nested_test_classes_have_matching_production_method_name(classes));

        assertThat(violationCount(failure)).isEqualTo(1);
        assertThat(failure)
                .hasMessageContaining("WidgetTest$DeleteAction$DeleteAll")
                .hasMessageContaining("does not have a matching production class");
    }

    @Test
    void a_nested_test_class_whose_production_class_is_missing_entirely_is_reported() {
        var classes = fixture("nestedclassname.badnoproduction");

        var failure = violationOf(() -> nested_test_classes_have_matching_production_method_name(classes));

        assertThat(violationCount(failure)).isEqualTo(1);
        assertThat(failure)
                .hasMessageContaining("WidgetTest$DoWork")
                .hasMessageContaining("does not have a matching production class");
    }

    @Test
    void a_nested_test_class_matching_a_production_method_is_accepted() {
        nested_test_classes_have_matching_production_method_name(fixture("nestedclassname.good"));
    }

    @Test
    void a_test_class_annotated_as_having_no_production_counterpart_is_accepted() {
        nested_test_classes_have_matching_production_method_name(fixture("nestedclassname.goodoptout"));
    }

    /// The opt-out has to be usable once, on a project's own test stereotype.
    @Test
    void a_test_class_opted_out_through_a_meta_annotation_is_accepted() {
        nested_test_classes_have_matching_production_method_name(fixture("nestedclassname.goodmetaoptout"));
    }

    /// Same for the group marker: a project names its own grouping stereotype once.
    @Test
    void a_nested_group_marked_through_a_meta_annotation_is_accepted() {
        nested_test_classes_have_matching_production_method_name(fixture("nestedclassname.goodmetagroup"));
    }
}
