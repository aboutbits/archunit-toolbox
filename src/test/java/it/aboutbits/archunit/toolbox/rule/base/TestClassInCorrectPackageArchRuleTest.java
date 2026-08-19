package it.aboutbits.archunit.toolbox.rule.base;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static it.aboutbits.archunit.toolbox.RuleEvaluation.fixture;
import static it.aboutbits.archunit.toolbox.RuleEvaluation.violationCount;
import static it.aboutbits.archunit.toolbox.RuleEvaluation.violationOf;
import static org.assertj.core.api.Assertions.assertThat;

@NullMarked
class TestClassInCorrectPackageArchRuleTest implements TestClassInCorrectPackageArchRule {
    @Test
    void a_test_class_without_a_production_class_in_the_same_package_is_reported() {
        var classes = fixture("testclasspackage.bad");

        var failure = violationOf(
                () -> test_classes_should_be_in_the_same_package_as_their_production_code(classes));

        assertThat(violationCount(failure)).isEqualTo(1);
        assertThat(failure)
                .hasMessageContaining("does not have a matching production class")
                .hasMessageContaining("it.aboutbits.archunit.fixture.testclasspackage.bad.Widget");
    }

    @Test
    void a_test_class_next_to_its_production_class_is_accepted() {
        test_classes_should_be_in_the_same_package_as_their_production_code(fixture("testclasspackage.good"));
    }

    @Test
    void a_test_class_annotated_as_having_no_production_counterpart_is_accepted() {
        test_classes_should_be_in_the_same_package_as_their_production_code(fixture("testclasspackage.goodoptout"));
    }

    /// The opt-out has to be usable once, on a project's own test stereotype, rather than repeated on
    /// every scenario test.
    @Test
    void a_test_class_opted_out_through_a_meta_annotation_is_accepted() {
        test_classes_should_be_in_the_same_package_as_their_production_code(
                fixture("testclasspackage.goodmetaoptout"));
    }

    @Test
    void an_architecture_test_in_its_own_package_is_accepted() {
        test_classes_should_be_in_the_same_package_as_their_production_code(
                fixture("testclasspackage.witharchitecture"));
    }

    /// The exemption is the package, not the name: the rule no longer hardcodes "ArchitectureTest".
    @Test
    void an_architecture_test_outside_an_architecture_package_is_reported() {
        var classes = fixture("testclasspackage.badarchitecture");

        var failure = violationOf(
                () -> test_classes_should_be_in_the_same_package_as_their_production_code(classes));

        assertThat(violationCount(failure)).isEqualTo(1);
        assertThat(failure).hasMessageContaining("testclasspackage.badarchitecture.Architecture");
    }
}
