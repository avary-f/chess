package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    public String auth;
    public Session session;

    public Connection(String auth, Session session) {
        this.auth = auth;
        this.session = session;
    }

    public void send(String msg) throws IOException {
        //take the msg and turn it into a json string
        session.getRemote().sendString(new Gson().toJson(msg));
    }
}