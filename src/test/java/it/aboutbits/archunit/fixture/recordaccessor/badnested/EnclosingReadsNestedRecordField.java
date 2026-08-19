package it.aboutbits.archunit.fixture.recordaccessor.badnested;

public class EnclosingReadsNestedRecordField {
    record Money(long amount) {
    }

    /** Nestmates share access to private members, so this compiles to a direct field read. */
    public long readDirectly(Money money) {
        return money.amount;
    }
}
