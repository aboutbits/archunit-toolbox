package it.aboutbits.archunit.fixture.blacklistannotations.good;

public class CleanClass {
    private String value = "x";

    public void doWork(String input) {
        this.value = input;
    }

    public String value() {
        return value;
    }
}
