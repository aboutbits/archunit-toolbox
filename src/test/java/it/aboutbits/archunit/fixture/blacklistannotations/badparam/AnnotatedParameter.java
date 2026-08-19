package it.aboutbits.archunit.fixture.blacklistannotations.badparam;

public class AnnotatedParameter {
    public void doWork(@lombok.NonNull String value) {
        System.identityHashCode(value);
    }
}
