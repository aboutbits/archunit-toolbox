package it.aboutbits.archunit.fixture.blacklistannotations.badctorparam;

/// The canonical Lombok position, and the one a rule iterating only getMethods() cannot see.
public class AnnotatedConstructorParameter {
    private final String value;

    public AnnotatedConstructorParameter(@lombok.NonNull String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
