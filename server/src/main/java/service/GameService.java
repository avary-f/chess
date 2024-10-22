package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
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
        GameData game = new GameData(UUID.randomUUID().hashCode(), null, null,  req.gameName(), null);
        if(dataAccessGame.getName(game)){
            throw new AlreadyTakenException();
        } //will throw an error if it is already taken
        dataAccessGame.create(game);
        return new CreateResult(game.gameID());

    }

    public JoinResult joinGame(JoinRequest req) throws Exception {
        if(!req.playerColor().equals("WHITE") && !req.playerColor().equals("BLACK")){
            throw new BadRequestException();
        }
        AuthData auth = dataAccessAuth.get(req.auth());
        serviceAuth.checkAuthTokenValid(auth);
        GameData game = new GameData(req.gameID(), null, null, null, null);
        game = dataAccessGame.getID(game);
        UserData user = new UserData(serviceAuth.getUsername(req.auth()), null, null);
        user = dataAccessUser.get(user);
        GameData newGame = getNewPlayerGame(user.username(), req.playerColor(), game);
        updateGame(game, newGame);
        return new JoinResult(newGame.gameID(), newGame.whiteUsername(), newGame.blackUsername());
    }

    public void clearAllGames(){
        dataAccessGame.clearAll();
    }
    public GameData getNewPlayerGame(String username, String color, GameData game) throws AlreadyTakenException {
        GameData newGame;
        if (color.equals("WHITE")) { //player wants to be white
            if (dataAccessGame.getID(game).whiteUsername() != null) {
                throw new AlreadyTakenException();
            }
            newGame = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
        } else { //black
            if (dataAccessGame.getID(game).blackUsername() != null) {
                throw new AlreadyTakenException();
            }
            newGame = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
        }
        return newGame;
    }
    private void updateGame(GameData game, GameData newGame){
        dataAccessGame.deleteGame(game);
        dataAccessGame.create(newGame);
    }

}
