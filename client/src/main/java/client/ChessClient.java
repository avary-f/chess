package client;

//import client.websocket.NotificationHandler;
import com.sun.nio.sctp.NotificationHandler;
import server.ServerFacade;
//import client.websocket.WebSocketFacade;

public abstract class ChessClient {
    private String clientName = null;
    private final ServerFacade server;
    private final String serverUrl;
    private final NotificationHandler notificationHandler;
    //WebSocketFacade ws;
    State state = State.SIGNEDOUT;

    public ChessClient(String serverUrl, NotificationHandler notificationHandler){
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;
    }

    public abstract String eval(String input);





}

