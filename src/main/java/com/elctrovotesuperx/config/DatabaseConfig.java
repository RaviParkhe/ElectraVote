// package com.electrovotesuperx.config;

// import java.io.IOException;
// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.sql.Connection;
// import java.sql.DriverManager;
// import java.sql.SQLException;
// import java.sql.Statement;

// public final class DatabaseConfig {

//     // =====================================================
//     // DATABASE LOCATION
//     // =====================================================

//     private static final Path DATA_DIR = Path.of("data");

//     private static final Path DB_FILE = DATA_DIR.resolve("electravote.db");

//     private static final String URL = "jdbc:sqlite:" + DB_FILE.toAbsolutePath();

//     private DatabaseConfig() {
//     }

//     // =====================================================
//     // INITIALIZE DATABASE
//     // =====================================================

//     public static void initializeDatabase() {

//         try {

//             Files.createDirectories(DATA_DIR);

//             try (
//                     Connection connection = getConnection();
//                     Statement statement = connection.createStatement()) {

//                 statement.execute("PRAGMA foreign_keys = ON");

//                 // =================================================
//                 // 1. ELECTIONS
//                 // =================================================

//                 statement.executeUpdate("""
//                             CREATE TABLE IF NOT EXISTS elections (
//                                 election_id TEXT PRIMARY KEY,
//                                 name TEXT NOT NULL UNIQUE,
//                                 status TEXT NOT NULL DEFAULT 'OPEN'
//                             )
//                         """);

//                 // =================================================
//                 // 2. VOTERS
//                 // =================================================

//                 statement.executeUpdate("""
//                             CREATE TABLE IF NOT EXISTS voters (
//                                 voter_id TEXT PRIMARY KEY,
//                                 full_name TEXT NOT NULL,
//                                 email TEXT UNIQUE,
//                                 gender TEXT,
//                                 phone TEXT,
//                                 status TEXT NOT NULL DEFAULT 'ACTIVE',
//                                 joined_date TEXT
//                             )
//                         """);

//                 // =================================================
//                 // 3. ELECTION VOTERS
//                 // =================================================

//                 statement.executeUpdate("""
//                             CREATE TABLE IF NOT EXISTS election_voters (
//                                 election_id TEXT NOT NULL,
//                                 voter_id TEXT NOT NULL,
//                                 status TEXT NOT NULL DEFAULT 'NOT_VOTED',

//                                 PRIMARY KEY (election_id, voter_id),

//                                 FOREIGN KEY (election_id)
//                                     REFERENCES elections(election_id)
//                                     ON DELETE CASCADE,

//                                 FOREIGN KEY (voter_id)
//                                     REFERENCES voters(voter_id)
//                                     ON DELETE CASCADE
//                             )
//                         """);

//                 // =================================================
//                 // 4. TOKENS
//                 // =================================================

//                 statement.executeUpdate("""
//                             CREATE TABLE IF NOT EXISTS tokens (
//                                 token TEXT PRIMARY KEY,

//                                 election_id TEXT NOT NULL,
//                                 voter_id TEXT NOT NULL,

//                                 status TEXT NOT NULL DEFAULT 'ACTIVE',

//                                 created_at TEXT NOT NULL,
//                                 used_at TEXT,

//                                 FOREIGN KEY (election_id, voter_id)
//                                     REFERENCES election_voters(
//                                         election_id,
//                                         voter_id
//                                     )
//                                     ON DELETE CASCADE
//                             )
//                         """);

//                 // =================================================
//                 // 5. AUDIT LOGS
//                 // =================================================

//                 statement.executeUpdate("""
//                             CREATE TABLE IF NOT EXISTS audit_logs (
//                                 id INTEGER PRIMARY KEY AUTOINCREMENT,

//                                 action TEXT NOT NULL,

//                                 voter_id TEXT,
//                                 election_id TEXT,
//                                 token TEXT,

//                                 details TEXT,

//                                 created_at TEXT NOT NULL
//                             )
//                         """);

//                 // =================================================
//                 // INDEXES
//                 // =================================================

//                 statement.executeUpdate("""
//                             CREATE INDEX IF NOT EXISTS idx_election_voters_voter
//                             ON election_voters(voter_id)
//                         """);

//                 statement.executeUpdate("""
//                             CREATE INDEX IF NOT EXISTS idx_tokens_voter
//                             ON tokens(voter_id)
//                         """);

//                 statement.executeUpdate("""
//                             CREATE INDEX IF NOT EXISTS idx_tokens_election
//                             ON tokens(election_id)
//                         """);

//                 statement.executeUpdate("""
//                             CREATE INDEX IF NOT EXISTS idx_audit_voter
//                             ON audit_logs(voter_id)
//                         """);

//                 statement.executeUpdate("""
//                             CREATE INDEX IF NOT EXISTS idx_audit_election
//                             ON audit_logs(election_id)
//                         """);
//             }

//         } catch (IOException | SQLException e) {

//             throw new RuntimeException(
//                     "Database initialization failed.",
//                     e);
//         }
//     }

//     // =====================================================
//     // CONNECTION
//     // =====================================================

//     public static Connection getConnection()
//             throws SQLException {

//         Connection connection = DriverManager.getConnection(URL);

//         try (Statement statement = connection.createStatement()) {

//             statement.execute(
//                     "PRAGMA foreign_keys = ON");
//         }

//         return connection;
//     }
// }

package com.electrovotesuperx.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseConfig {

    // =====================================================
    // DATABASE LOCATION
    // =====================================================

    private static final Path DATA_DIR = Path.of("data");

    private static final Path DB_FILE = DATA_DIR.resolve("electravote.db");

    private static final String URL = "jdbc:sqlite:" + DB_FILE.toAbsolutePath();

    private DatabaseConfig() {
    }

    // =====================================================
    // INITIALIZE DATABASE
    // =====================================================

    public static void initializeDatabase() {

        try {

            Files.createDirectories(DATA_DIR);

            try (
                    Connection connection = getConnection();
                    Statement statement = connection.createStatement()) {

                statement.execute("PRAGMA foreign_keys = ON");

                // =================================================
                // 1. ELECTIONS
                // =================================================

                statement.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS elections (
                            election_id TEXT PRIMARY KEY,
                            name TEXT NOT NULL UNIQUE,
                            status TEXT NOT NULL DEFAULT 'OPEN'
                        )
                        """);

                // =================================================
                // 2. VOTERS
                // =================================================

                statement.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS voters (
                            voter_id TEXT PRIMARY KEY,
                            full_name TEXT NOT NULL,
                            email TEXT UNIQUE,
                            gender TEXT,
                            phone TEXT,
                            status TEXT NOT NULL DEFAULT 'ACTIVE',
                            joined_date TEXT
                        )
                        """);

                // =================================================
                // IMPORTANT DATABASE MIGRATION
                //
                // If your existing electravote.db was created
                // before phone was added, CREATE TABLE IF NOT
                // EXISTS will NOT add the column.
                // =================================================

                addColumnIfMissing(
                        connection,
                        "voters",
                        "phone",
                        "TEXT");

                // =================================================
                // 3. ELECTION VOTERS
                // =================================================

                statement.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS election_voters (
                            election_id TEXT NOT NULL,
                            voter_id TEXT NOT NULL,
                            status TEXT NOT NULL DEFAULT 'NOT_VOTED',

                            PRIMARY KEY (election_id, voter_id),

                            FOREIGN KEY (election_id)
                                REFERENCES elections(election_id)
                                ON DELETE CASCADE,

                            FOREIGN KEY (voter_id)
                                REFERENCES voters(voter_id)
                                ON DELETE CASCADE
                        )
                        """);

                // =================================================
                // 4. TOKENS
                // =================================================

                statement.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS tokens (
                            token TEXT PRIMARY KEY,

                            election_id TEXT NOT NULL,
                            voter_id TEXT NOT NULL,

                            status TEXT NOT NULL DEFAULT 'ACTIVE',

                            created_at TEXT NOT NULL,
                            used_at TEXT,

                            FOREIGN KEY (election_id, voter_id)
                                REFERENCES election_voters(
                                    election_id,
                                    voter_id
                                )
                                ON DELETE CASCADE
                        )
                        """);

                // =================================================
                // 5. AUDIT LOGS
                // =================================================

                statement.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS audit_logs (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,

                            action TEXT NOT NULL,

                            voter_id TEXT,
                            election_id TEXT,
                            token TEXT,

                            details TEXT,

                            created_at TEXT NOT NULL
                        )
                        """);

                // =================================================
                // INDEXES
                // =================================================

                statement.executeUpdate("""
                        CREATE INDEX IF NOT EXISTS
                        idx_election_voters_voter
                        ON election_voters(voter_id)
                        """);

                statement.executeUpdate("""
                        CREATE INDEX IF NOT EXISTS
                        idx_tokens_voter
                        ON tokens(voter_id)
                        """);

                statement.executeUpdate("""
                        CREATE INDEX IF NOT EXISTS
                        idx_tokens_election
                        ON tokens(election_id)
                        """);

                statement.executeUpdate("""
                        CREATE INDEX IF NOT EXISTS
                        idx_audit_voter
                        ON audit_logs(voter_id)
                        """);

                statement.executeUpdate("""
                        CREATE INDEX IF NOT EXISTS
                        idx_audit_election
                        ON audit_logs(election_id)
                        """);
            }

        } catch (IOException | SQLException e) {

            throw new RuntimeException(
                    "Database initialization failed.",
                    e);
        }
    }

    // =====================================================
    // ADD COLUMN IF MISSING
    // =====================================================

    private static void addColumnIfMissing(
            Connection connection,
            String table,
            String column,
            String definition)
            throws SQLException {

        String sql = "PRAGMA table_info(" + table + ")";

        try (
                Statement statement = connection.createStatement();
                var rs = statement.executeQuery(sql)) {

            while (rs.next()) {

                String existingColumn = rs.getString("name");

                if (column.equalsIgnoreCase(existingColumn)) {
                    return;
                }
            }
        }

        String alterSql = "ALTER TABLE " + table +
                " ADD COLUMN " + column +
                " " + definition;

        try (Statement statement = connection.createStatement()) {

            statement.executeUpdate(alterSql);
        }
    }

    // =====================================================
    // CONNECTION
    // =====================================================

    public static Connection getConnection()
            throws SQLException {

        try {
            Files.createDirectories(DATA_DIR);
        } catch (IOException e) {
            System.err.println("Warning: could not create data directory: " + e.getMessage());
        }

        Connection connection = DriverManager.getConnection(URL);

        try (Statement statement = connection.createStatement()) {

            statement.execute(
                    "PRAGMA foreign_keys = ON");
        }

        return connection;
    }
}