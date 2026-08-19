package it.aboutbits.archunit.toolbox.support;

import com.tngtech.archunit.junit.ArchIgnore;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@NullMarked
class ArchIgnoreNoProductionCounterpartTest {
    /**
     * ArchUnit's JUnit engine resolves meta-annotations, so meta-annotating this with @ArchIgnore
     * makes it skip every @ArchTest on the annotated class - reported as success - rather than
     * exempting the class from one rule. The rules read this annotation by its own type, so the
     * meta-annotation buys nothing and costs all of them.
     */
    @Test
    void the_annotation_is_not_meta_annotated_with_arch_ignore() {
        assertThat(ArchIgnoreNoProductionCounterpart.class.isAnnotationPresent(ArchIgnore.class))
                .as("@ArchIgnore here would silently disable every arch rule on the annotated class")
                .isFalse();
    }
}
