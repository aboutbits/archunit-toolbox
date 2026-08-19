package it.aboutbits.archunit.toolbox.support;

import com.tngtech.archunit.junit.ArchIgnore;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@NullMarked
class ArchIgnoreGroupNameTest {
    /** See ArchIgnoreNoProductionCounterpartTest: a meta @ArchIgnore skips arch tests wholesale. */
    @Test
    void the_annotation_is_not_meta_annotated_with_arch_ignore() {
        assertThat(ArchIgnoreGroupName.class.isAnnotationPresent(ArchIgnore.class))
                .as("@ArchIgnore here would silently disable every arch rule on the annotated class")
                .isFalse();
    }
}
