import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import server.Server;
import service.GameService;
import service.UserService;

public class Main {
    public static void main(String[] args) throws Exception {
        MemoryGameDAO gameDAO = new MemoryGameDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryUserDAO userDAO = new MemoryUserDAO();

        UserService userService = new UserService(userDAO, authDAO);
        GameService gameService = new GameService(userDAO, authDAO, gameDAO);
        var server = new Server(userService, gameService);
        var port = 8080;
        server.run(port);
        System.out.println("♕ 240 Chess Server: " + port);

    }
}