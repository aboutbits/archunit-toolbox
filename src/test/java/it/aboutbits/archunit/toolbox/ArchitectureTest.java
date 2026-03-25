package it.aboutbits.archunit.toolbox;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.CacheMode;
import org.jspecify.annotations.NullMarked;

@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
@AnalyzeClasses(
        packages = ArchitectureTest.PACKAGE,
        cacheMode = CacheMode.PER_CLASS
)
@NullMarked
class ArchitectureTest implements BaseArchRuleCollection {
    static final String PACKAGE = "it.aboutbits.archunit.toolbox";
}
