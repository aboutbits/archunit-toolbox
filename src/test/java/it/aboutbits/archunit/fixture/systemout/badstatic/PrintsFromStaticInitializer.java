package it.aboutbits.archunit.fixture.systemout.badstatic;

public class PrintsFromStaticInitializer {
    static {
        System.out.println("noise");
    }
}
