package it.aboutbits.archunit.toolbox.util;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import org.jspecify.annotations.NullMarked;

import java.util.Comparator;
import java.util.stream.Collectors;

import static it.aboutbits.archunit.toolbox.config.ArchRuleConfig.TEST_CLASS_SUFFIXES;

/**
 * Single source of truth for recognising a test class by its name suffix.
 * <p>
 * Every rule that selects test classes must go through {@link #testClasses()}, and every rule that
 * derives a production class name must go through {@link #productionClassSimpleName(String)}.
 * Hand-building the suffix regex per rule is what allowed a condition to be anchored differently
 * from the selection that fed it, silently disabling the rule.
 * </p>
 */
@NullMarked
public final class TestClassNames {
    private TestClassNames() {
    }

    /**
     * Matches the name of a test class: at least one character, then one of the configured suffixes.
     * <p>
     * The leading {@code .+} is load-bearing. {@link String#matches(String)} and ArchUnit's
     * {@code haveNameMatching} both anchor at each end, so without it the pattern only matches a
     * class named exactly {@code Test}. It is also what keeps such a class out of the selection,
     * since it has no name left once the suffix is stripped.
     * </p>
     */
    public static String testClassNameRegex() {
        return ".+(" + suffixAlternation() + ")$";
    }

    /**
     * Matches only the trailing suffix, for stripping it off a test class name. Deliberately not
     * anchored at the start - this is used with {@link String#replaceAll(String, String)}, never
     * with {@link String#matches(String)}.
     */
    public static String suffixRegex() {
        return "(" + suffixAlternation() + ")$";
    }

    /**
     * Whether a simple name is the name of a test class.
     * <p>
     * Requires a production class name to be left over once the suffix is stripped, so that the
     * selection cannot disagree with {@link #productionClassSimpleName(String)}. "CacheTest" matches
     * the pattern with "Cache" as the leading {@code .+}, yet stripping removes "CacheTest" whole and
     * leaves nothing to look for.
     * </p>
     */
    public static boolean isTestClassName(String simpleName) {
        return simpleName.matches(testClassNameRegex())
                && !productionClassSimpleName(simpleName).isEmpty();
    }

    /**
     * The simple name of the production class a test class belongs to, e.g. {@code WidgetCacheTest}
     * to {@code Widget}.
     */
    public static String productionClassSimpleName(String testClassSimpleName) {
        return testClassSimpleName.replaceAll(suffixRegex(), "");
    }

    /**
     * Selects test classes by their <em>simple</em> name. Matching the simple name rather than the
     * fully qualified name keeps the selection and the conditions that follow it in agreement:
     * {@code some.pkg.Test} matches the fully qualified name but is not a test class.
     */
    public static DescribedPredicate<JavaClass> testClasses() {
        return new DescribedPredicate<>("have a simple name matching '%s'".formatted(testClassNameRegex())) {
            @Override
            public boolean test(JavaClass javaClass) {
                return isTestClassName(javaClass.getSimpleName());
            }
        };
    }

    /*
     * Longest suffix first, then alphabetically. TEST_CLASS_SUFFIXES is a mutable HashSet, so
     * without an explicit order the generated regex - and every rule description built from it -
     * varies between JVM runs.
     */
    private static String suffixAlternation() {
        var longestFirst = Comparator.<String>comparingInt(String::length)
                .reversed()
                .thenComparing(Comparator.naturalOrder());

        return TEST_CLASS_SUFFIXES.stream()
                .sorted(longestFirst)
                .collect(Collectors.joining("|"));
    }
}
