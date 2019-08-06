package com.revolut.transfer.dao;

import com.revolut.transfer.exception.AccountException;
import com.revolut.transfer.hibernate.Account;
import com.revolut.transfer.hibernate.HibernateSessionFactoryUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
public class PostgresAccountDao implements AccountDao {

    @Override
    public Account get(long id) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            return session.get(Account.class, id);
        }
    }

    @Override
    public List<Account> getAll() {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Account> criteriaQuery = criteriaBuilder.createQuery(Account.class);
            Root<Account> root = criteriaQuery.from(Account.class);
            criteriaQuery.select(root);
            Query<Account> query = session.createQuery(criteriaQuery);
            return query.getResultList();
        }
    }

    @Override
    public boolean add(Account account) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.save(account);
            transaction.commit();
            return true;
        } catch (Exception e) {
            log.error("Exception while create: {}", e);
            throw e;
        }
    }

    @Override
    public boolean update(Account account) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.update(account);
            transaction.commit();
            return true;
        } catch (Exception e) {
            log.error("Exception while update: {}", e);
            throw e;
        }
    }

    @Override
    public boolean delete(long id) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            Account account = session.get(Account.class, id);
            if (account == null) {
                return false;
            }
            Transaction transaction = session.beginTransaction();
            session.delete(account);
            transaction.commit();
            return true;
        } catch (Exception e) {
            log.error("Exception while delete: {}", e);
            throw e;
        }
    }

    @Override
    public void transfer(long senderId, long receiverId, BigDecimal amount) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            updateSender(senderId, amount);
            updateReceiver(receiverId, amount);
            transaction.commit();
        }
    }

    private void updateSender(long senderId, BigDecimal amount) {
        Account account = getOrThrowException(senderId);
        account.setBalance(account.getBalance().subtract(amount));
        update(account);
    }

    private void updateReceiver(long receiverId, BigDecimal amount) {
        Account account = getOrThrowException(receiverId);
        account.setBalance(account.getBalance().add(amount));
        update(account);
    }

    private Account getOrThrowException(long accountId) {
        Account account = get(accountId);
        if (account == null) {
            throw AccountException.createWithAccountNotFound(accountId);
        }
        return account;
    }
}
