package websocket.messages;

public class Notification extends ServerMessage{

    public Notification(String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }
}
