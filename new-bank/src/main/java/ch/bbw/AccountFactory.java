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

    public Account create(AccountType type) {
        return create(type, 0);
    }

    public Account create(AccountType type, long creditLimit) {
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }

        return switch (type) {
            case SAVINGS -> createSavingsAccount();
            case PROMO_YOUTH -> createPromoYouthSavingsAccount();
            case SALARY -> createSalaryAccount(creditLimit);
        };
    }

    private String nextId(AccountType type) {
        return type.getPrefix() + "-" + nextAccountId++;
    }
}
