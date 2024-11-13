package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import request.CreateRequest;
import request.JoinRequest;
import request.ListRequest;
import result.CreateResult;
import result.JoinResult;
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

    public JoinResult joinGame(JoinRequest req) throws Exception {
        String passedInColor = req.playerColor();
        if(passedInColor == null || (!passedInColor.equals("WHITE") && !req.playerColor().equals("BLACK"))){
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
        return new JoinResult(game.gameID(), game.whiteUsername(), game.blackUsername());
    }

    public void clearAllGames(){
        dataAccessGame.clearAll();
    }

    private void playerTaken(GameData game, String color) throws AlreadyTakenException {
        if (color.equals("WHITE")) { //player wants to be white
            if (dataAccessGame.get(game).whiteUsername() != null) {
                throw new AlreadyTakenException();
            }
        } else { //black
            if (dataAccessGame.get(game).blackUsername() != null) {
                throw new AlreadyTakenException();
            }
        }
    }

}
