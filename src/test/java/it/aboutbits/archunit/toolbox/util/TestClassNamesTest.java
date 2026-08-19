package it.aboutbits.archunit.toolbox.util;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@NullMarked
class TestClassNamesTest {
    @Test
    void a_name_ending_in_a_configured_suffix_is_a_test_class_name() {
        assertThat(TestClassNames.isTestClassName("WidgetTest")).isTrue();
        assertThat(TestClassNames.isTestClassName("WidgetCacheTest")).isTrue();
        assertThat(TestClassNames.isTestClassName("WidgetEventTest")).isTrue();
        assertThat(TestClassNames.isTestClassName("WidgetSecurityTest")).isTrue();
    }

    /**
     * The regression guard for the defect that disabled the counterpart rule outright: the condition
     * rebuilt the pattern without the leading ".+", and String.matches anchors both ends, so only a
     * class named exactly "Test" got through.
     */
    @Test
    void the_pattern_is_not_satisfied_by_the_bare_suffix_alone() {
        assertThat(TestClassNames.isTestClassName("Test")).isFalse();
        assertThat("WidgetTest".matches(TestClassNames.testClassNameRegex())).isTrue();
    }

    /**
     * "CacheTest" matches the pattern with "Cache" as the leading ".+", but stripping removes
     * "CacheTest" whole, so there would be no production class name left to look for.
     */
    @Test
    void a_name_that_strips_down_to_nothing_is_not_a_test_class_name() {
        assertThat(TestClassNames.isTestClassName("CacheTest")).isFalse();
        assertThat(TestClassNames.isTestClassName("SecurityTest")).isFalse();
        assertThat(TestClassNames.isTestClassName("WidgetCacheTest")).isTrue();
    }

    @Test
    void a_name_not_ending_in_a_configured_suffix_is_not_a_test_class_name() {
        assertThat(TestClassNames.isTestClassName("Widget")).isFalse();
        assertThat(TestClassNames.isTestClassName("WidgetTester")).isFalse();
        assertThat(TestClassNames.isTestClassName("TestWidget")).isFalse();
    }

    @Test
    void the_production_class_name_is_the_name_without_its_suffix() {
        assertThat(TestClassNames.productionClassSimpleName("WidgetTest")).isEqualTo("Widget");
        assertThat(TestClassNames.productionClassSimpleName("WidgetCacheTest")).isEqualTo("Widget");
        assertThat(TestClassNames.productionClassSimpleName("WidgetEventTest")).isEqualTo("Widget");
        assertThat(TestClassNames.productionClassSimpleName("WidgetSecurityTest")).isEqualTo("Widget");
    }

    /**
     * TEST_CLASS_SUFFIXES is a mutable HashSet, so without an explicit ordering the generated regex
     * and every rule description built from it would vary between JVM runs.
     */
    @Test
    void the_generated_pattern_has_a_stable_order() {
        assertThat(TestClassNames.testClassNameRegex())
                .isEqualTo(TestClassNames.testClassNameRegex())
                .isEqualTo(".+(SecurityTest|CacheTest|EventTest|Test)$");
    }
}
