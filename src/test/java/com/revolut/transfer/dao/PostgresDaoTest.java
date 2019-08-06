package com.revolut.transfer.dao;

import com.revolut.transfer.environment.Database;
import com.revolut.transfer.exception.AccountException;
import com.revolut.transfer.hibernate.Account;
import org.junit.*;
import org.junit.rules.ExpectedException;

import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;
import javax.sql.DataSource;
import javax.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;

public class PostgresDaoTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private static final String INSERT_ACCOUNT1 = "INSERT INTO accounts(id, balance) VALUES(1, 1000)";
    private static final String INSERT_ACCOUNT2 = "INSERT INTO accounts(id, balance) VALUES(2, 1000)";
    private static final String DELETE_DATA = "DELETE FROM accounts";

    private static final long EXISTENT_ID = 1L;
    private static final long EXISTENT_ID_2 = 2L;
    private static final long NON_EXISTENT_ID = 3L;

    private static AccountDao dao = new PostgresAccountDao();

    private static Database database;

    @BeforeClass
    public static void createAndInitDatabase() throws Exception {
        database = new Database();
        database.start();

        dao.get(1);
    }

    @Before
    public void setUpData() throws Exception {
        DataSource dataSource = database.getPostgresDb().getDatabase(Database.USERNAME, Database.DATABASE);
        try (Connection connection = dataSource.getConnection(Database.USERNAME, Database.PASSWORD)) {
            Statement statement = connection.createStatement();
            statement.execute(DELETE_DATA);
            statement.execute(INSERT_ACCOUNT1);
            statement.execute(INSERT_ACCOUNT2);
        }
    }

    @AfterClass
    public static void closeDatabase() {
        database.stop();
    }

    @Test
    public void get() {
        Account actual = dao.get(EXISTENT_ID);
        Account expected = new Account(EXISTENT_ID, new BigDecimal(1000));

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getNonExistent() {
        Assert.assertNull(dao.get(NON_EXISTENT_ID));
    }

    @Test
    public void add() {
        Account notExistYet = dao.get(NON_EXISTENT_ID);
        Assert.assertNull(notExistYet);

        Account candidate = new Account(NON_EXISTENT_ID, new BigDecimal(1000));
        boolean result = dao.add(candidate);
        Assert.assertTrue(result);

        Account saved = dao.get(candidate.getId());
        Assert.assertEquals(candidate, saved);
    }

    @Test(expected = PersistenceException.class)
    public void addExistent() {
        Account existent = dao.get(EXISTENT_ID);
        Assert.assertNotNull(existent);

        dao.add(existent);
    }

    @Test(expected = ConstraintViolationException.class)
    public void addWithNegativeBalance() {
        Account account = new Account(NON_EXISTENT_ID, new BigDecimal(-1000));
        Assert.assertFalse(dao.add(account));
    }

    @Test
    public void update() {
        Account before = new Account(EXISTENT_ID, new BigDecimal(1000));
        Account toBeUpdated = dao.get(EXISTENT_ID);
        Assert.assertEquals(before, toBeUpdated);

        toBeUpdated.setBalance(BigDecimal.valueOf(1100L));
        boolean result = dao.update(toBeUpdated);
        Assert.assertTrue(result);

        Account after = new Account(before.getId(), new BigDecimal(1100));
        Account updated = dao.get(before.getId());
        Assert.assertEquals(after, updated);
    }

    @Test(expected = OptimisticLockException.class)
    public void updateNonExistent() {
        Account nonExistent = new Account(NON_EXISTENT_ID, new BigDecimal(1000));
        boolean result = dao.update(nonExistent);
        Assert.assertFalse(result);
    }

    @Test(expected = ConstraintViolationException.class)
    public void updateByNegativeBalance() {
        Account account = new Account(EXISTENT_ID, new BigDecimal(-1000));
        dao.update(account);
    }

    @Test
    public void delete() {
        Account existent = dao.get(EXISTENT_ID);
        Assert.assertNotNull(existent);

        boolean result = dao.delete(existent.getId());
        Assert.assertTrue(result);

        Account deleted = dao.get(existent.getId());
        Assert.assertNull(deleted);
    }

    @Test
    public void deleteNonExistent() {
        boolean result = dao.delete(NON_EXISTENT_ID);
        Assert.assertFalse(result);
    }

    @Test
    public void transfer() {
        Account sender = dao.get(EXISTENT_ID);
        Assert.assertNotNull(sender);

        Account receiver = dao.get(EXISTENT_ID_2);
        Assert.assertNotNull(receiver);

        Assert.assertEquals(BigDecimal.valueOf(1000L).compareTo(sender.getBalance()), 0);
        Assert.assertEquals(BigDecimal.valueOf(1000L).compareTo(receiver.getBalance()), 0);

        dao.transfer(sender.getId(), receiver.getId(), BigDecimal.valueOf(500L));

        Account senderAfter = dao.get(sender.getId());
        Account receiverAfter = dao.get(receiver.getId());

        Assert.assertEquals(BigDecimal.valueOf(500L).compareTo(senderAfter.getBalance()), 0);
        Assert.assertEquals(BigDecimal.valueOf(1500L).compareTo(receiverAfter.getBalance()), 0);
    }

    @Test
    public void transferInsufficientBalance() {
        Account sender = dao.get(EXISTENT_ID);
        Assert.assertNotNull(sender);

        Account receiver = dao.get(EXISTENT_ID_2);
        Assert.assertNotNull(receiver);

        Assert.assertEquals(BigDecimal.valueOf(1000L).compareTo(sender.getBalance()), 0);
        Assert.assertEquals(BigDecimal.valueOf(1000L).compareTo(receiver.getBalance()), 0);

        expectedException.expect(ConstraintViolationException.class);
        dao.transfer(sender.getId(), receiver.getId(), BigDecimal.valueOf(5000L));

        Account senderAfter = dao.get(sender.getId());
        Account receiverAfter = dao.get(receiver.getId());

        Assert.assertEquals(BigDecimal.valueOf(1000L).compareTo(senderAfter.getBalance()), 0);
        Assert.assertEquals(BigDecimal.valueOf(1000L).compareTo(receiverAfter.getBalance()), 0);
    }

    @Test
    public void transferNonExistentReceiver() {
        long senderId = EXISTENT_ID;
        long receiverId = NON_EXISTENT_ID;

        Account sender = dao.get(senderId);
        Assert.assertNotNull(sender);

        Account receiver = dao.get(receiverId);
        Assert.assertNull(receiver);

        Assert.assertEquals(BigDecimal.valueOf(1000L).compareTo(sender.getBalance()), 0);

        expectedException.expect(AccountException.class);
        dao.transfer(senderId, receiverId, BigDecimal.valueOf(500L));

        Account senderAfter = dao.get(senderId);
        Assert.assertEquals(BigDecimal.valueOf(1000L).compareTo(senderAfter.getBalance()), 0);
    }

    @Test
    public void transferNonExistentSender() {
        long senderId = NON_EXISTENT_ID;
        long receiverId = EXISTENT_ID;

        Account sender = dao.get(senderId);
        Assert.assertNull(sender);

        Account receiver = dao.get(receiverId);
        Assert.assertNotNull(receiver);

        Assert.assertEquals(BigDecimal.valueOf(1000L).compareTo(receiver.getBalance()), 0);

        expectedException.expect(AccountException.class);
        dao.transfer(senderId, receiverId, BigDecimal.valueOf(500L));

        Account receiverAfter = dao.get(receiverId);
        Assert.assertEquals(BigDecimal.valueOf(1000L).compareTo(receiverAfter.getBalance()), 0);
    }
}