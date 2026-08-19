package it.aboutbits.archunit.fixture.recordaccessor.goodoptout;

import it.aboutbits.springboot.toolbox.archunit.ArchAllowDirectAccess;

public class EnclosingReadsOptedOutRecord {
    @ArchAllowDirectAccess(reason = "fixture")
    record Money(long amount) {
    }

    public long readDirectly(Money money) {
        return money.amount;
    }
}
