import chess.*;
import server.Server;

import javax.sound.sampled.Port;

public class Main {
    public static void main(String[] args) {
        var server = new Server();
        var port = 8080;
        server.run(port);
        System.out.println("♕ 240 Chess Server: " + port);
    }
}