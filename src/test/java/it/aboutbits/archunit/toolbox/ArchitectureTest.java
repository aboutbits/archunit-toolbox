package it.aboutbits.archunit.toolbox;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.CacheMode;
import it.aboutbits.archunit.toolbox.support.ArchIgnoreNoProductionCounterpart;
import org.jspecify.annotations.NullMarked;

/**
 * The toolbox checked against its own base rules.
 * <p>
 * Carries @ArchIgnoreNoProductionCounterpart because there is no production class named
 * "Architecture". That the 11 rules below still run is also what pins that the annotation exempts a
 * class from one rule rather than skipping every @ArchTest on it.
 * </p>
 */
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
@AnalyzeClasses(
        packages = ArchitectureTest.PACKAGE,
        cacheMode = CacheMode.PER_CLASS
)
@NullMarked
@ArchIgnoreNoProductionCounterpart
class ArchitectureTest implements BaseArchRuleCollection {
    static final String PACKAGE = "it.aboutbits.archunit.toolbox";
}
