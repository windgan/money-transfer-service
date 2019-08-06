package com.revolut.transfer.environment;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@Getter
public class Database {

    public static final String DATABASE = "postgres";
    public static final String USERNAME = "postgres";
    public static final String PASSWORD = "postgres";

    private static final int DB_PORT = 65021;

    private EmbeddedPostgres postgresDb;

    public void start() throws Exception {
        postgresDb = EmbeddedPostgres.builder().setPort(DB_PORT).start();
        log.info("JDBC URL: {}", postgresDb.getJdbcUrl(USERNAME, DATABASE));
        log.info("Database started");
    }

    public void stop() {
        try {
            postgresDb.close();
            log.info("database stopped");
        } catch (IOException e) {
            log.error("Error during database stop", e);
            throw new RuntimeException(e);
        }
    }
}
