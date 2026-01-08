package it.aboutbits.archunit.toolbox;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.CacheMode;
import org.jspecify.annotations.NullMarked;

@AnalyzeClasses(
        packages = ArchitectureTest.PACKAGE,
        cacheMode = CacheMode.PER_CLASS
)
@NullMarked
class ArchitectureTest extends ArchitectureTestBase {
    static final String PACKAGE = "it.aboutbits.archunit.toolbox";
}
