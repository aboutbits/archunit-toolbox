package it.aboutbits.archunit.fixture.blacklistannotations.badfield;

public class AnnotatedField {
    @lombok.NonNull
    private String value = "x";

    public String value() {
        return value;
    }
}
