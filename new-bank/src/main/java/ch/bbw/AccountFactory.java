package ch.bbw;

import ch.bbw.accounts.*;

public class AccountFactory {
    /**
     * Next account id.
     */
    private long nextAccountId;

    public AccountFactory() {
        this.nextAccountId = 1000;
    }

    public Account createPromoYouthSavingsAccount() {
        String id = nextId(AccountType.PROMO_YOUTH);

        return new PromoYouthSavingsAccount(id);
    }

    public Account createSalaryAccount(long creditLimit) {
        String id = nextId(AccountType.SALARY);

        return new SalaryAccount(id, creditLimit);
    }

    public Account createSavingsAccount(){
        String id = nextId(AccountType.SAVINGS);

        return new SavingsAccount(id);
    }

    private String nextId(AccountType type) {
        return type.getPrefix() + "-" + nextAccountId++;
    }
}
