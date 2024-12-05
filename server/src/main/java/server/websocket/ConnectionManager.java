package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, CopyOnWriteArrayList<Connection>> connections = new ConcurrentHashMap<>();
    //copyonwritearraylist = a thread safe implementation of arraylist, allows for iteration & modification
    // gameID and connections associated with it

    public void add(String auth, Integer gameID, Session session) {
        Connection connection = new Connection(auth, session);
        connections.computeIfAbsent(gameID, key -> new CopyOnWriteArrayList<>()).add(connection);
        //if the gameID doesn't exist, create a new array and then add the connection to it
    }

    public void remove(String auth) {
        for(Integer gameID: connections.keySet()) {
            CopyOnWriteArrayList<Connection> list = connections.get(gameID);
            if (list == null) {
                System.out.println("Having trouble connecting to the other client. Try logging out and back in."); // Nothing to broadcast
                return;
            }
            for (Connection c : list) {
                if (c.auth.equals(auth)) {
                    list.remove(c); //remove that specific connection to the game
                    if (list.isEmpty()) { //if no one is joined to the game
                        connections.remove(gameID);
                    }
                    return; //if you found the thing you need to remove, end the function
                }
            }
        }
    }

    //make a remove function and remove unopen sessions outside of the function

    public void broadcast(String excludeAuth, ServerMessage message, Integer gameID) throws IOException {
        CopyOnWriteArrayList<Connection> list = connections.get(gameID);
        if(list == null) {
            System.out.println("Having trouble connecting to the other client. Please try logging out and back in."); // Nothing to broadcast
            return;
        }
        for (Connection c : list) {
            if (c.session.isOpen()) {
                if (!c.auth.equals(excludeAuth)) { //don't send the notification to yourself
                    c.send(message);
                }
            } else {
                list.remove(c);
                if (list.isEmpty()) {
                    connections.remove(gameID); //if there are no connections
                }
            }
        }

    }

    public void broadcastToMe(Session session, ServerMessage message) throws IOException {
        if (session.isOpen()) {
            session.getRemote().sendString(new Gson().toJson(message));
        }
    }
}