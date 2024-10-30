package dataaccess;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MysqlDAO {
    public MysqlDAO() throws DataAccessException {
        configureDatabase();
    }

    public static String executeUpdate(String statement, Object... params) { // Object = varargs (variable-length arguments)
        try (var conn = DatabaseManager.getConnection()) { //try (with resources) connecting to db
            //makes sure that you don't run out of ports with connectivity
            //auto closes the resource (conn) after block completes
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                //ps = prepared statement, protects against malicious statements
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    //SQL index starts with 1, so shift over 1 index
                    switch (param) { //checks the type of the variable
                        case String p -> ps.setString(i + 1, p);
                        //  If param is a String, setString is called on ps to bind it
                        //  to the SQL statement at position i + 1.
                        //case Integer p -> ps.setInt(i + 1, p);
                        //case PetType p -> ps.setString(i + 1, p.toString());
                        case null -> ps.setNull(i + 1, NULL);
                        default -> {}
                    }
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getString("username");
                }
                /*
                 NOTE: Use the return statement if inserting rows into a table with an auto-increment
                 column (like an id), allows you to get that auto-generated ID.
                 Useful when you want to reference the inserted row later,
                 without having to manually query for it by other criteria.
                 */

                return ""; //returns empty string if nothing is found
            }
        } catch (SQLException e) { //if there is a connection issue, throw an error and exit program
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private final String[] createStatements = { // `json` TEXT DEFAULT NULL-> might need this, might not
            """
            CREATE TABLE IF NOT EXISTS  users (
              `username` varchar(256) NOT NULL,
              `password` varchar(256),
              `email` varchar(256),
              PRIMARY KEY (`username`),
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS  auths (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`),
              INDEX(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS  games (
              `id` int NOT NULL,
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              `gameName` varchar(256) NOT NULL,
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`id`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };  //the json object is the ChessGame object
        //all of the engine stuff are default values so you can get rid of them if you want


    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to configure database");
        }
    }
}
