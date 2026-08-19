package it.aboutbits.archunit.fixture.systemout.badconstructor;

public class PrintsFromConstructor {
    public PrintsFromConstructor() {
        System.err.println("noise");
    }
}
