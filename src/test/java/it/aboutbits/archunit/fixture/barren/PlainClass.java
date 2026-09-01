package it.aboutbits.archunit.fixture.barren;

import org.jspecify.annotations.NullMarked;

/// A codebase with no test classes, no records, no controllers and no stores. Used to pin that a rule
/// whose selection comes up empty fails instead of reporting success.
@NullMarked
public class PlainClass {
    public String value() {
        return "value";
    }
}
