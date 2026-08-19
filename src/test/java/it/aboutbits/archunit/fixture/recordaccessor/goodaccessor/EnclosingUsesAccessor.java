package it.aboutbits.archunit.fixture.recordaccessor.goodaccessor;

public class EnclosingUsesAccessor {
    record Money(long amount) {
    }

    public long readViaAccessor(Money money) {
        return money.amount();
    }
}
