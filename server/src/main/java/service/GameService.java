package service;

import chess.ChessGame;
import chess.ChessMove;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import request.*;
import result.CreateResult;
import result.ListResult;
import server.AlreadyTakenException;
import server.BadRequestException;

import java.util.UUID;

public class GameService {
    private final GameDAO dataAccessGame;
    private final UserDAO dataAccessUser;
    private final AuthDAO dataAccessAuth;
    private final AuthService serviceAuth;

    public GameService(UserDAO dataAccessUser, AuthDAO dataAccessAuth, GameDAO dataAccessGame){
        this.dataAccessGame = dataAccessGame;
        this.dataAccessUser = dataAccessUser;
        this.dataAccessAuth = dataAccessAuth;
        serviceAuth = new AuthService(dataAccessAuth);
    }
    public ListResult listGames(ListRequest req) throws Exception{
        AuthData auth = dataAccessAuth.get(req.auth());
        serviceAuth.checkAuthTokenValid(auth);
        return new ListResult(dataAccessGame.getAll());
    }
    public CreateResult createGame(CreateRequest req) throws Exception {
        AuthData auth = dataAccessAuth.get(req.auth());
        serviceAuth.checkAuthTokenValid(auth);
        GameData game = new GameData(Math.abs(UUID.randomUUID().hashCode()), null, null,  req.gameName(), new ChessGame());
        if(dataAccessGame.nameExists(game)){
            throw new AlreadyTakenException();
        } //will throw an error if it is already taken
        dataAccessGame.create(game);
        return new CreateResult(game.gameID());

    }

    public Object joinGame(JoinRequest req) throws Exception {
        ChessGame.TeamColor passedInColor = convertToColorType(req.playerColor());
        if(passedInColor == null){
            throw new BadRequestException();
        }
        AuthData auth = dataAccessAuth.get(req.auth());
        serviceAuth.checkAuthTokenValid(auth);
        GameData game = new GameData(req.gameID(), null, null, null, null);
        game = dataAccessGame.get(game);
        UserData user = new UserData(serviceAuth.getUsername(req.auth()), null, null);
        user = dataAccessUser.get(user);
        playerTaken(game, passedInColor);
        dataAccessGame.updatePlayer(game, passedInColor, user.username());
        return "{}"; //json for empty object
    }

    private ChessGame.TeamColor convertToColorType(String s) {
        if(s == null){
            return null;
        }
        if(s.equals("WHITE")){
            return ChessGame.TeamColor.WHITE;
        }
        else if(s.equals("BLACK")){
            return ChessGame.TeamColor.BLACK;
        }
        return null;
    }

    public void unjoinGame(UnjoinRequest req) throws Exception {
        AuthData auth = dataAccessAuth.get(req.auth());
        serviceAuth.checkAuthTokenValid(auth);
        GameData game = new GameData(req.gameID(), null, null, null, null);
        game = dataAccessGame.get(game);
        UserData user = new UserData(serviceAuth.getUsername(req.auth()), null, null);
        user = dataAccessUser.get(user);
        ChessGame.TeamColor playerToChange = getPlayerColor(game, user);
        if(playerToChange != null){ //if the person was an observer
            dataAccessGame.updatePlayer(game, playerToChange, null);
        }
    }

    public void endGame(EndGameRequest req) throws Exception {
        AuthData auth = dataAccessAuth.get(req.auth());
        serviceAuth.checkAuthTokenValid(auth);
        UserData user = new UserData(serviceAuth.getUsername(req.auth()), null, null);
        user = dataAccessUser.get(user);
        GameData game = new GameData(req.gameID(), null, null, null, null);
        game = dataAccessGame.get(game);
        ChessGame.TeamColor color = getPlayerColor(game, user);
        game.game.setEndOfGame();
        game.game.setWinner(req.isResigning(), color);
        dataAccessGame.updateGame(game);
    }

    private ChessGame.TeamColor getPlayerColor(GameData game, UserData user){
        if(game.whiteUsername() != null && game.whiteUsername().equals(user.username())){
            return ChessGame.TeamColor.WHITE;
        }
        else if(game.blackUsername() != null && game.blackUsername().equals(user.username())){
            return ChessGame.TeamColor.BLACK;
        }
        return null;
    }

    public GameData makeMove(MoveRequest req) throws Exception {
        AuthData auth = dataAccessAuth.get(req.auth());
        serviceAuth.checkAuthTokenValid(auth);
        ChessMove move = req.move();
        GameData game = new GameData(req.gameID(), null, null, null, null);
        game = dataAccessGame.get(game);
        UserData user = new UserData(serviceAuth.getUsername(req.auth()), null, null);
        user = dataAccessUser.get(user);
        if(game.game.getBoard().getPiece(move.getStartPosition()) == null ||
                !game.game.getBoard().getPiece(move.getStartPosition()).getTeamColor().equals(getPlayerColor(game, user))){
            throw new BadRequestException();
        }//check that I am asking to move my own piece
        game.game.makeMove(move); //throws invalid MoveException
        dataAccessGame.updateGame(game);
        return game;
    }

    public void clearAllGames(){
        dataAccessGame.clearAll();
    }

    private void playerTaken(GameData game, ChessGame.TeamColor color) throws AlreadyTakenException {
        if (color.equals(ChessGame.TeamColor.WHITE)) { //player wants to be white
            if (dataAccessGame.get(game).whiteUsername() != null) {
                throw new AlreadyTakenException();
            }
        } else { //black
            if (dataAccessGame.get(game).blackUsername() != null) {
                throw new AlreadyTakenException();
            }
        }
    }

    public GameData getGame(Integer gameID) throws BadRequestException {
        GameData game = new GameData(gameID, null, null, null, null);
        game = dataAccessGame.get(game);
        if(game == null){
            throw new BadRequestException();
        }
        return game;
    }

}
