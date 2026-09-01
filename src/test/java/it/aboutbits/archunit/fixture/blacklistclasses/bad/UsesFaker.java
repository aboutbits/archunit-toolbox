package it.aboutbits.archunit.fixture.blacklistclasses.bad;

import net.datafaker.Faker;

public class UsesFaker {
    public String randomName() {
        return new Faker().name();
    }
}
