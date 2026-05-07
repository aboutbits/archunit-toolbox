package it.aboutbits.archunit.toolbox.config;

import org.jspecify.annotations.NullMarked;

import java.util.HashSet;
import java.util.Set;

@NullMarked
public final class ArchRuleConfig {
    private ArchRuleConfig() {
    }

    /**
     * List of supported test class name suffixes.
     * <p>
     * When introducing a new test type (e.g. IntegrationTest), add its suffix here
     * instead of directly modifying the regex pattern.
     **/
    public static final Set<String> TEST_CLASS_SUFFIXES = new HashSet<>(
            Set.of(
                    "Test",
                    "CacheTest",
                    "EventTest",
                    "SecurityTest"
            )
    );
}
