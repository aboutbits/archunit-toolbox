package it.aboutbits.archunit.toolbox;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.jspecify.annotations.NullMarked;

import java.util.regex.Pattern;

/**
 * Imports a rule fixture and inspects what a rule reports about it.
 * <p>
 * Rules are invoked through their own {@code default} method, so a test exercises exactly what a
 * consumer gets, including the message text.
 * </p>
 */
@NullMarked
public final class RuleEvaluation {
    private static final String FIXTURE_ROOT = "it.aboutbits.archunit.fixture.";
    private static final Pattern VIOLATION_COUNT = Pattern.compile("was violated \\((\\d+) times?\\)");

    private RuleEvaluation() {
    }

    /**
     * Imports one fixture package, failing loudly if it is empty: a mistyped package would otherwise
     * make every assertion about it pass for the wrong reason.
     */
    public static JavaClasses fixture(String subPackage) {
        var packageName = FIXTURE_ROOT + subPackage;
        var classes = new ClassFileImporter().importPackages(packageName);

        if (classes.size() == 0) {
            throw new IllegalStateException("Fixture package imported no classes: " + packageName);
        }

        return classes;
    }

    /**
     * Runs a rule that is expected to report at least one violation and returns the failure.
     * Failing here means the rule accepted a fixture that was built to violate it.
     */
    public static AssertionError violationOf(Runnable ruleCheck) {
        try {
            ruleCheck.run();
        } catch (AssertionError failure) {
            return failure;
        }

        throw new AssertionError("Expected the rule to report a violation, but it reported success.");
    }

    /** The number of violations ArchUnit reported, read back from its failure message. */
    public static int violationCount(AssertionError failure) {
        var matcher = VIOLATION_COUNT.matcher(String.valueOf(failure.getMessage()));

        if (!matcher.find()) {
            throw new IllegalStateException("Not an ArchUnit violation report: " + failure.getMessage());
        }

        return Integer.parseInt(matcher.group(1));
    }
}
