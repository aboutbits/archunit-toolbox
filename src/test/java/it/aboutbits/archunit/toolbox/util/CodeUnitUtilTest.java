package it.aboutbits.archunit.toolbox.util;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@NullMarked
class CodeUnitUtilTest {
    @Test
    void every_kind_of_code_unit_is_described() {
        var javaClass = new ClassFileImporter().importClass(Described.class);

        var kinds = javaClass.getCodeUnits()
                .stream()
                .map(CodeUnitUtil::describeKind)
                .distinct()
                .toList();

        assertThat(kinds).containsExactlyInAnyOrder("Method", "Constructor", "Static initializer");
    }

    @SuppressWarnings("unused")
    private static final class Described {
        private static final String CONSTANT;

        static {
            CONSTANT = "constant";
        }

        private Described() {
        }

        private void method() {
        }
    }
}
