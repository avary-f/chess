package server.websocket;

import websocket.messages.*;

public interface ServerMessageHandler {
    void notify(Notification notification);
}