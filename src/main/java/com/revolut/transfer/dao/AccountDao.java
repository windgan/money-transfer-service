package com.revolut.transfer.dao;

import com.revolut.transfer.hibernate.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountDao {

    Account get(long id);

    List<Account> getAll();

    boolean add(Account account);

    boolean update(Account account);

    boolean delete(long id);

    void transfer(long senderId, long receiverId, BigDecimal amount);
}
