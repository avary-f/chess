import server.Server;

public class Main {
    public static void main(String[] args) throws Exception {
        var server = new Server();
        var port = 8080;
        server.run(port);
        System.out.println("♕ 240 Chess Server: " + port);

    }
}