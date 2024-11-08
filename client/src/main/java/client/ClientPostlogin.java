package client;

public class ClientPostlogin extends ChessClient{
    public ClientPostlogin(String serverUrl) {
        super(serverUrl);
    }

    @Override
    public String help() {
        return "";
    }

    @Override
    public String performCmd(String cmd) {
        return "";
    }
}
