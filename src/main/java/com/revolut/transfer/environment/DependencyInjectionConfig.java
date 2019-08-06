package com.revolut.transfer.environment;

import com.google.inject.AbstractModule;
import com.revolut.transfer.dao.AccountDao;
import com.revolut.transfer.dao.PostgresAccountDao;
import com.revolut.transfer.service.AccountService;
import com.revolut.transfer.service.AccountServiceImpl;

public class DependencyInjectionConfig extends AbstractModule {

    @Override
    protected void configure() {
        bind(AccountDao.class).to(PostgresAccountDao.class);
        bind(AccountService.class).to(AccountServiceImpl.class);
    }
}