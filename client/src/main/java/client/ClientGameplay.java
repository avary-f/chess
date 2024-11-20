package client;

import model.GameData;

public class ClientGameplay extends ChessClient{
    private String teamColor;


    public ClientGameplay(String serverUrl) {
        super(serverUrl);
    }

    @Override
    public String performCmd(String cmd, String[] params) {
        return switch (cmd) {
            case "redraw" -> redraw();
//            case "highlight" -> highlight(params);
//            case "make" -> makeMove(params);
//            case "resign" -> resign();
//            case "leave" -> leave();
            default -> help();
        };
    }

    private String redraw() {
        BoardReader boardReaderMyColor = new BoardReader(getGame(), getTeamColor());
        boardReaderMyColor.drawChessBoard();
        return "";
    }

    @Override
    public String help() {
        return """
            redraw chess board
            highlight legal moves <RowCol>
            make move <Current_RowCol> <Desired_RowCol>
            resign
            leave
            help
            """;
    }

}
