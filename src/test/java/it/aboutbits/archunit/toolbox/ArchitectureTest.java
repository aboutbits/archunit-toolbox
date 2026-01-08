package it.aboutbits.archunit.toolbox;

import com.tngtech.archunit.junit.AnalyzeClasses;
import org.jspecify.annotations.NullMarked;

@AnalyzeClasses(
        packages = ArchitectureTest.PACKAGE
)
@NullMarked
class ArchitectureTest extends ArchitectureTestBase {
    static final String PACKAGE = "it.aboutbits.archunit.toolbox";
}
