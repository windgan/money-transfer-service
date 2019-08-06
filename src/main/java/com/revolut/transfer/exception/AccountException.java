package com.revolut.transfer.exception;

import com.revolut.transfer.hibernate.Account;

public class AccountException extends RuntimeException {

    public AccountException(String message) {
        super(message);
    }

    public static AccountException createWithAccountNotFound(Long id) {
        return new AccountException("Account not found: id = " + id);
    }

    public static AccountException createWithAccountNotCreated(Account account) {
        return new AccountException("Account not created: " + account);
    }
}
