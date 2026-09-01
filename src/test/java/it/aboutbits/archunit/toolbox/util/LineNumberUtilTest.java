package it.aboutbits.archunit.toolbox.util;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static it.aboutbits.archunit.toolbox.util.LineNumberUtil.getLineNumber;
import static org.assertj.core.api.Assertions.assertThat;

@NullMarked
class LineNumberUtilTest {
    @Nested
    class GetLineNumber {
        @Test
        void a_class_falls_back_to_the_line_of_its_constructor() {
            var javaClass = new ClassFileImporter().importClass(WithConstructor.class);

            // Bytecode carries no line for the class itself, hence the constructor fallback
            assertThat(getLineNumber(javaClass)).isPositive();
        }

        @Test
        void a_type_without_any_constructor_reports_no_line() {
            var javaInterface = new ClassFileImporter().importClass(WithoutConstructor.class);

            assertThat(getLineNumber(javaInterface)).isZero();
        }

        @Test
        void a_method_reports_its_own_line() {
            var javaClass = new ClassFileImporter().importClass(WithConstructor.class);

            assertThat(getLineNumber(javaClass.getMethod("value"))).isPositive();
        }
    }

    private static final class WithConstructor {
        private final String value;

        private WithConstructor(String value) {
            this.value = value;
        }

        @SuppressWarnings("unused")
        String value() {
            return value;
        }
    }

    private interface WithoutConstructor {
        String value();
    }
}
