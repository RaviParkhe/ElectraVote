// package com.electrovotesuperx.dao.OfflineDAO;

// import com.electrovotesuperx.config.DatabaseConfig;
// import com.electrovotesuperx.model.OfflineModel.Token;

// import java.sql.Connection;
// import java.sql.PreparedStatement;
// import java.sql.ResultSet;
// import java.sql.SQLException;
// import java.time.LocalDateTime;

// public class TokenDAO {

//         // =============================================================
//         // CREATE TOKEN
//         // =============================================================

//         public void create(
//                         String token,
//                         String electionId,
//                         String voterId) throws SQLException {

//                 String sql = "INSERT INTO tokens " +
//                                 "(token, election_id, voter_id, status, created_at) " +
//                                 "VALUES (?, ?, ?, ?, ?)";

//                 try (
//                                 Connection c = DatabaseConfig.getConnection();

//                                 PreparedStatement ps = c.prepareStatement(sql)) {

//                         ps.setString(1, token);
//                         ps.setString(2, electionId);
//                         ps.setString(3, voterId);
//                         ps.setString(4, "ACTIVE");
//                         ps.setString(
//                                         5,
//                                         LocalDateTime.now().toString());

//                         ps.executeUpdate();
//                 }
//         }

//         // =============================================================
//         // FIND TOKEN
//         // =============================================================

//         public Token find(
//                         String token) throws SQLException {

//                 String sql = "SELECT token, election_id, voter_id, " +
//                                 "status, created_at, used_at " +
//                                 "FROM tokens " +
//                                 "WHERE token = ?";

//                 try (
//                                 Connection c = DatabaseConfig.getConnection();

//                                 PreparedStatement ps = c.prepareStatement(sql)) {

//                         ps.setString(1, token);

//                         try (
//                                         ResultSet rs = ps.executeQuery()) {

//                                 if (rs.next()) {

//                                         return new Token(
//                                                         rs.getString("token"),
//                                                         rs.getString("election_id"),
//                                                         rs.getString("voter_id"),
//                                                         rs.getString("status"),
//                                                         rs.getString("created_at"),
//                                                         rs.getString("used_at"));
//                                 }
//                         }
//                 }

//                 return null;
//         }

//         // =============================================================
//         // MARK TOKEN AS USED
//         // =============================================================

//         public boolean markUsed(
//                         String token) throws SQLException {

//                 String sql = "UPDATE tokens " +
//                                 "SET status = 'USED', used_at = ? " +
//                                 "WHERE token = ? " +
//                                 "AND status = 'ACTIVE'";

//                 try (
//                                 Connection c = DatabaseConfig.getConnection();

//                                 PreparedStatement ps = c.prepareStatement(sql)) {

//                         ps.setString(
//                                         1,
//                                         LocalDateTime.now().toString());

//                         ps.setString(
//                                         2,
//                                         token);

//                         int updatedRows = ps.executeUpdate();

//                         return updatedRows == 1;
//                 }
//         }

//         // =============================================================
//         // COUNT ACTIVE TOKENS
//         // =============================================================

//         public int countActive()
//                         throws SQLException {

//                 String sql = "SELECT COUNT(*) " +
//                                 "FROM tokens " +
//                                 "WHERE status = 'ACTIVE'";

//                 try (
//                                 Connection c = DatabaseConfig.getConnection();

//                                 PreparedStatement ps = c.prepareStatement(sql);

//                                 ResultSet rs = ps.executeQuery()) {

//                         return rs.next()
//                                         ? rs.getInt(1)
//                                         : 0;
//                 }
//         }
// }
package com.electrovotesuperx.dao.OfflineDAO;

import com.electrovotesuperx.config.DatabaseConfig;
import com.electrovotesuperx.model.OfflineModel.Token;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class TokenDAO {

        // =============================================================
        // CREATE TOKEN
        // =============================================================

        public void create(
                        String token,
                        String electionId,
                        String voterId)
                        throws SQLException {

                String sql = """
                                INSERT INTO tokens
                                (
                                    token,
                                    election_id,
                                    voter_id,
                                    status,
                                    created_at
                                )
                                VALUES (?, ?, ?, ?, ?)
                                """;

                try (
                                Connection connection = DatabaseConfig.getConnection();

                                PreparedStatement statement = connection.prepareStatement(sql)) {

                        statement.setString(1, token);
                        statement.setString(2, electionId);
                        statement.setString(3, voterId);
                        statement.setString(4, "ACTIVE");
                        statement.setString(
                                        5,
                                        LocalDateTime.now().toString());

                        statement.executeUpdate();
                }
        }

        // =============================================================
        // FIND TOKEN
        // =============================================================

        public Token find(
                        String token)
                        throws SQLException {

                String sql = """
                                SELECT
                                    token,
                                    election_id,
                                    voter_id,
                                    status,
                                    created_at,
                                    used_at
                                FROM tokens
                                WHERE token = ?
                                """;

                try (
                                Connection connection = DatabaseConfig.getConnection();

                                PreparedStatement statement = connection.prepareStatement(sql)) {

                        statement.setString(1, token);

                        try (
                                        ResultSet result = statement.executeQuery()) {

                                if (result.next()) {

                                        return new Token(
                                                        result.getString("token"),
                                                        result.getString("election_id"),
                                                        result.getString("voter_id"),
                                                        result.getString("status"),
                                                        result.getString("created_at"),
                                                        result.getString("used_at"));
                                }
                        }
                }

                return null;
        }

        // =============================================================
        // MARK TOKEN AS USED
        // =============================================================

        public boolean markUsed(
                        String token)
                        throws SQLException {

                String sql = """
                                UPDATE tokens
                                SET
                                    status = 'USED',
                                    used_at = ?
                                WHERE token = ?
                                  AND status = 'ACTIVE'
                                """;

                try (
                                Connection connection = DatabaseConfig.getConnection();

                                PreparedStatement statement = connection.prepareStatement(sql)) {

                        statement.setString(
                                        1,
                                        LocalDateTime.now().toString());

                        statement.setString(
                                        2,
                                        token);

                        return statement.executeUpdate() == 1;
                }
        }

        // =============================================================
        // COUNT ACTIVE TOKENS
        // =============================================================

        public int countActive()
                        throws SQLException {

                String sql = """
                                SELECT COUNT(*)
                                FROM tokens
                                WHERE status = 'ACTIVE'
                                """;

                try (
                                Connection connection = DatabaseConfig.getConnection();

                                PreparedStatement statement = connection.prepareStatement(sql);

                                ResultSet result = statement.executeQuery()) {

                        return result.next()
                                        ? result.getInt(1)
                                        : 0;
                }
        }
}