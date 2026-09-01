package it.aboutbits.archunit.toolbox.util;

import com.tngtech.archunit.core.domain.JavaCodeUnit;
import com.tngtech.archunit.core.domain.JavaConstructor;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaStaticInitializer;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class CodeUnitUtil {
    private CodeUnitUtil() {
    }

    /// Human readable kind of a code unit, for violation messages.
    ///
    /// Rules that inspect bodies must iterate `getCodeUnits()` rather than `getMethods()`:
    /// the latter excludes constructors, and an instance field initializer is compiled into the
    /// constructor, so both are invisible to a rule that only looks at methods.
    public static String describeKind(JavaCodeUnit codeUnit) {
        return switch (codeUnit) {
            case JavaMethod _ -> "Method";
            case JavaConstructor _ -> "Constructor";
            case JavaStaticInitializer _ -> "Static initializer";
            default -> "Code unit";
        };
    }
}
