package it.aboutbits.archunit.toolbox.rule.common;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static it.aboutbits.archunit.toolbox.RuleEvaluation.fixture;
import static it.aboutbits.archunit.toolbox.RuleEvaluation.violationCount;
import static it.aboutbits.archunit.toolbox.RuleEvaluation.violationOf;
import static org.assertj.core.api.Assertions.assertThat;

@NullMarked
class ControllerRequestMappingsMustBeSecurityTestedTest implements ControllerRequestMappingsMustBeSecurityTested {
    @Test
    void a_mapped_method_without_a_security_test_class_is_reported() {
        var classes = fixture("securitytested.badmissing");

        var failure = violationOf(
                () -> controller_methods_with_request_mapping_must_be_security_tested(classes));

        assertThat(violationCount(failure)).isEqualTo(1);
        assertThat(failure)
                .hasMessageContaining("getAll")
                .hasMessageContaining("WidgetControllerSecurityTest is missing");
    }

    @Test
    void a_security_test_class_without_the_nested_method_class_is_reported() {
        var classes = fixture("securitytested.badnonested");

        var failure = violationOf(
                () -> controller_methods_with_request_mapping_must_be_security_tested(classes));

        assertThat(violationCount(failure)).isEqualTo(1);
        assertThat(failure).hasMessageContaining("does not contain a @Nested test class named GetAll");
    }

    /// getAll() must not be considered covered by the `@Nested` class belonging to getAllArchived().
    @Test
    void a_mapped_method_covered_only_by_a_longer_named_sibling_is_reported() {
        var classes = fixture("securitytested.badprefix");

        var failure = violationOf(
                () -> controller_methods_with_request_mapping_must_be_security_tested(classes));

        assertThat(violationCount(failure)).isEqualTo(1);
        assertThat(failure)
                .hasMessageContaining("getAll()")
                .hasMessageContaining("does not contain a @Nested test class named GetAll");
    }

    @Test
    void a_mapped_method_with_a_matching_nested_test_class_is_accepted() {
        controller_methods_with_request_mapping_must_be_security_tested(fixture("securitytested.good"));
    }

    /// A `@Nested` class grouped inside the method-named class still counts as coverage.
    @Test
    void a_mapped_method_covered_by_a_nested_group_is_accepted() {
        controller_methods_with_request_mapping_must_be_security_tested(fixture("securitytested.goodnestedgroup"));
    }

    /// A class marked as organisational through a project's own stereotype is not coverage, so the
    /// method it is named after is still uncovered.
    @Test
    void a_mapped_method_covered_only_by_a_group_marked_class_is_reported() {
        var classes = fixture("securitytested.badmetagroup");

        var failure = violationOf(
                () -> controller_methods_with_request_mapping_must_be_security_tested(classes));

        assertThat(violationCount(failure)).isEqualTo(1);
        assertThat(failure).hasMessageContaining("does not contain a @Nested test class named GetAll");
    }
}
