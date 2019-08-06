package com.revolut.transfer.service;

import com.revolut.transfer.hibernate.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {

    Account get(long id);

    List<Account> getAll();

    void add(Account account);

    void update(Account account);

    void delete(long id);

    void transfer(long senderId, long receiverId, BigDecimal amount);
}