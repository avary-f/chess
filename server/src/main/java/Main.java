import chess.*;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import request.RegisterRequest;
import result.RegisterResult;
import server.Server;
import service.UserService;

import javax.sound.sampled.Port;
import java.rmi.registry.RegistryHandler;

public class Main {
    public static void main(String[] args) throws DataAccessException {
        var server = new Server();
        var port = 8080;
        server.run(port);
        System.out.println("♕ 240 Chess Server: " + port);

    }
}