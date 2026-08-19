package it.aboutbits.archunit.toolbox;

import com.tngtech.archunit.core.domain.JavaClasses;
import it.aboutbits.archunit.toolbox.rule.base.RecordPropertiesMustBeAccessedViaAccessorArchRule;
import it.aboutbits.archunit.toolbox.support.ArchIgnoreNoProductionCounterpart;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Consumer;
import java.util.stream.Stream;

import static it.aboutbits.archunit.toolbox.RuleEvaluation.fixture;
import static it.aboutbits.archunit.toolbox.RuleEvaluation.violationOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * No rule may use {@code allowEmptyShould(true)}.
 * <p>
 * A rule that selects nothing reports success, which is indistinguishable from a rule that is
 * satisfied. That is how a broken rule survives unnoticed, so every rule that narrows its input must
 * fail when the selection comes up empty.
 * </p>
 */
@NullMarked
@ArchIgnoreNoProductionCounterpart
class EmptySelectionTest {
    private static final Rules RULES = new Rules();

    static Stream<Arguments> rulesThatNarrowTheirInput() {
        return Stream.of(
                arguments("test classes are in the same package as their production code",
                        consumer(RULES::test_classes_should_be_in_the_same_package_as_their_production_code)),
                arguments("test classes must be package private",
                        consumer(RULES::test_classes_must_be_package_private)),
                arguments("test methods must be package private",
                        consumer(RULES::test_methods_must_be_package_private)),
                arguments("nested test classes must be package private",
                        consumer(RULES::nested_test_classes_must_be_package_private)),
                arguments("nested test classes match a production method name",
                        consumer(RULES::nested_test_classes_have_matching_production_method_name)),
                arguments("record properties are accessed via accessor",
                        consumer(RecordPropertiesMustBeAccessedViaAccessorArchRule
                                .record_properties_must_be_accessed_via_accessor::check)),
                arguments("controller request mappings must be security tested",
                        consumer(RULES::controller_methods_with_request_mapping_must_be_security_tested)),
                arguments("sort mappings cover all sort enum values",
                        consumer(RULES::sort_mappings_cover_all_sort_enum_values))
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("rulesThatNarrowTheirInput")
    void a_rule_that_selects_nothing_fails_instead_of_reporting_success(
            String ruleDescription,
            Consumer<JavaClasses> rule
    ) {
        var barrenCodebase = fixture("barren");

        var failure = violationOf(() -> rule.accept(barrenCodebase));

        assertThat(failure)
                .as("%s must not pass on a codebase it selects nothing from", ruleDescription)
                .hasMessageContaining("failed to check any");
    }

    private static Consumer<JavaClasses> consumer(Consumer<JavaClasses> rule) {
        return rule;
    }

    private static final class Rules implements BaseArchRuleCollection, CommonArchRuleCollection {
    }
}
