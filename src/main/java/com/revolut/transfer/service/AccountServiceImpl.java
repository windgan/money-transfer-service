package com.revolut.transfer.service;

import com.google.inject.Inject;
import com.revolut.transfer.dao.AccountDao;
import com.revolut.transfer.exception.AccountException;
import com.revolut.transfer.hibernate.Account;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountDao accountDao;

    @Inject
    public AccountServiceImpl(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Override
    public Account get(long id) {
        Account account = accountDao.get(id);

        if (account == null) {
            AccountException exception = AccountException.createWithAccountNotFound(id);
            log.error("Account with id {} not found", id, exception);
            throw exception;
        }
        log.info("Account with id={} was found: {}", id, account);
        return account;
    }

    @Override
    public List<Account> getAll() {
        List<Account> result = accountDao.getAll();
        log.info("All found accounts: {}", result);
        return result;
    }

    @Override
    public void add(Account account) {
        boolean success = accountDao.add(account);
        if (!success) {
            AccountException exception = AccountException.createWithAccountNotCreated(account);
            log.error("Account {} was not created", account, exception);
            throw exception;
        }
        log.info("Account was created: {}", account);
    }

    @Override
    public void update(Account account) {
        boolean success = accountDao.update(account);
        if (!success) {
            AccountException exception = AccountException.createWithAccountNotFound(account.getId());
            log.error("Account {} was not updated", account, exception);
            throw exception;
        }
        log.info("Account was updated: {}", account);
    }

    @Override
    public void delete(long id) {
        boolean success = accountDao.delete(id);
        if (!success) {
            AccountException exception = AccountException.createWithAccountNotFound(id);
            log.error("Account with id={} was not deleted", id, exception);
            throw exception;
        }
        log.info("Account with id={} was deleted", id);
    }

    @Override
    public void transfer(long senderId, long receiverId, BigDecimal amount) {
        accountDao.transfer(senderId, receiverId, amount);
        log.info("Money amount={} from {} to was transfered", amount, senderId, receiverId);
    }
}
