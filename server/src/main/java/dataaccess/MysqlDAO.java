package dataaccess;
import java.sql.SQLException;
import java.util.ArrayList;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MysqlDAO {
    public MysqlDAO() throws DataAccessException {
        configureDatabase();
    }
    public static Object execute(String statement, Object... params) throws DataAccessException {
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
                        case Integer p -> ps.setInt(i + 1, p);
                        case null -> ps.setNull(i + 1, NULL);
                        default -> {
                        }
                    }
                }
                String[] words = statement.trim().split("\\s+");
                if(words[0].equals("SELECT")){ //if the first word in the statement is select
                    var rs = ps.executeQuery();
                    ArrayList<Object> list = new ArrayList<>();
                    if (rs.next()) { //if there's at least one thing found
                        if(!words[words.length - 1].equals("?")){ //check if they need multiple objects
                            //it will be a ? if they only want one thing
                            list.add(rs.getObject(words[1]));
                            while(rs.next()){
                                list.add(rs.getObject(words[1]));
                            }
                            return list; //trying to select multiple things
                        }
                        return rs.getObject(words[1]); //if trying to select one thing
                    }
                    return null; //returns null if there's nothing found
                }
                else{ //if it requires an update
                    ps.executeUpdate();
                    var rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        return rs.getInt(1);
                    }

                    return 0;
                }
            }
        } catch (SQLException e) { //if there is a connection issue, throw an error and exit program
                throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  users (
              `username` varchar(256) NOT NULL,
              `password` varchar(256),
              `email` varchar(256),
              PRIMARY KEY (`username`)
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
              `game` TEXT,
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
                    preparedStatement.executeUpdate(); //CHECK - it is failing here. What do I do about it?
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to configure database");
        }
    }
    public boolean isEmpty(){
        String statement = "SELECT * FROM auths";
        String resultUser = (String)execute(statement);
        return resultUser == null;
    }

}
