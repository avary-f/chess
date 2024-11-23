package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, ArrayList<Connection>> connections = new ConcurrentHashMap<>();
    // gameID and connections associated with it

    public void add(String auth, Integer gameID, Session session) {
        var connection = new Connection(auth, session);
        ArrayList<Connection> list = connections.get(gameID);
        if(list != null){ //connections to this game already exist
            list.add(connection);
            connections.replace(gameID, list);
        }
        else{ //no connections to this game exist
           ArrayList<Connection> listToAdd = new ArrayList<>();
           listToAdd.add(connection);
           connections.put(gameID, listToAdd);
        }
    }

    public void remove(String auth) {
        for(Integer gameID: connections.keySet()){
            ArrayList<Connection> list = connections.get(gameID);
            for(Connection c: list){
                if(c.auth.equals(auth)){
                    list.remove(c); //remove that specific connection to the game
                    if(list.isEmpty()){ //if no one is joined to the game
                        connections.remove(gameID);
                    }
                    else{ //if there are still people connected to the game
                        connections.replace(gameID, list);
                    }
                    return; //if you found the thing you need to remove, end the function
                }
            }
        }
    }

    public void broadcast(String excludeAuth, ServerMessage message) throws IOException {
        HashMap<Integer, ArrayList<Connection>> updatedLists = new HashMap<>();
        for (Integer gameID : connections.keySet()) {
            ArrayList<Connection> list = connections.get(gameID);
            for(Connection c: list){
                if (c.session.isOpen()) {
                    if (!c.auth.equals(excludeAuth)) {
                        c.send(message.toString());
                    }
                } else {
                    ArrayList<Connection> newList= connections.get(gameID);
                    newList.remove(c); //make a copy of the list and remove the unused connection
                    if(updatedLists.containsKey(gameID)){
                        updatedLists.replace(gameID, newList); //if a list of connections already exists for that game, replace it
                    }
                    else{
                        updatedLists.put(gameID, newList); //add it to the list of connections that need to be removed
                    }
                }
            }
        }
        for (Integer gameID : updatedLists.keySet()) {
            connections.replace(gameID, updatedLists.get(gameID)); //replace the old list with the updated one
        }
    }
}