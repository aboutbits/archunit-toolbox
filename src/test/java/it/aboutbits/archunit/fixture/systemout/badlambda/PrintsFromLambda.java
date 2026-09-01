package it.aboutbits.archunit.fixture.systemout.badlambda;

import java.util.List;

public class PrintsFromLambda {
    public void shout() {
        List.of("a").forEach(value -> System.out.println(value));
    }
}
