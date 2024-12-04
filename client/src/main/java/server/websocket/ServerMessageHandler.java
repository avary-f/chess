package server.websocket;

import websocket.messages.*;
import websocket.messages.Error;

public interface ServerMessageHandler {
    void notify(Notification message);
    void notify(Error message);
    void notify(LoadGame message);
    void notify(LoadGameHighlight message);
}