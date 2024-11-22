package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.Notification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String auth, Session session) {
        var connection = new Connection(auth, session);
        connections.put(auth, connection);
    }

    public void remove(String auth) {
        connections.remove(auth);
    }

    public void broadcast(String excludeAuth, Notification notification) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.auth.equals(excludeAuth)) {
                    c.send(notification.toString());
                }
            } else {
                removeList.add(c);
            }
        }

        for (var c : removeList) {
            connections.remove(c.auth); // Clean up any connections that were left open.
        }
    }
}