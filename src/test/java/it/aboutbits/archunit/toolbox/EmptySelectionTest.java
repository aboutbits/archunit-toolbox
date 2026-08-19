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
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * A rule must not complain about code the project does not have.
 * <p>
 * Whether a project contains records, controllers, @Store classes or @Nested test classes is the
 * project's business, so every rule tolerates a selection that comes up empty. That each rule can
 * still fail is guaranteed by its own red test in this project, not by making consumers fail - an
 * empty selection says nothing about whether a rule's logic works. The counterpart rule proved that:
 * its selection was never empty, its condition was simply broken.
 * </p>
 * <p>
 * The one genuinely dangerous case, nothing imported at all, is covered by
 * AnalyzedPackagesMustContainClassesArchRule.
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
                        consumer(RULES::sort_mappings_cover_all_sort_enum_values)),
                arguments("top level classes must be annotated with jspecify",
                        consumer(RULES::top_level_classes_must_be_annotated_with_jspecify))
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("rulesThatNarrowTheirInput")
    void a_rule_accepts_a_project_that_has_no_code_it_applies_to(
            String ruleDescription,
            Consumer<JavaClasses> rule
    ) {
        // One plain class: no test classes, no records, no controllers, no stores
        var barrenCodebase = fixture("barren");

        assertThatCode(() -> rule.accept(barrenCodebase))
                .as("%s must not fail a project that has no code it applies to", ruleDescription)
                .doesNotThrowAnyException();
    }

    private static Consumer<JavaClasses> consumer(Consumer<JavaClasses> rule) {
        return rule;
    }

    private static final class Rules implements BaseArchRuleCollection, CommonArchRuleCollection {
    }
}
