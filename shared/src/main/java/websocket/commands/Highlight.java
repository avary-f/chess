package websocket.commands;

public class Highlight extends UserGameCommand{
    private String highlightSquare;

    public Highlight(String authToken, Integer gameID, String highlightSquare) {
        super(CommandType.HIGHLIGHT, authToken, gameID);
        this.highlightSquare = highlightSquare;
    }

    public String getHighlightSquare(){
        return highlightSquare;
    }
}
