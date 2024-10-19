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

import java.util.UUID;

public class GameService {
    private final GameDAO dataAccessGame;
    private final UserDAO dataAccessUser;
    private final AuthDAO dataAccessAuth;

    public GameService(UserDAO dataAccessUser, AuthDAO dataAccessAuth, GameDAO dataAccessGame){
        this.dataAccessGame = dataAccessGame;
        this.dataAccessUser = dataAccessUser;
        this.dataAccessAuth = dataAccessAuth;
    }
    public ListResult listGames(ListRequest req) throws DataAccessException{
        AuthData auth = dataAccessAuth.get(req.auth());
        dataAccessAuth.checkAuthTokenValid(auth);
        return new ListResult(dataAccessGame.getAll());
    }
    public CreateResult createGame(CreateRequest req) throws DataAccessException {
        AuthData auth = dataAccessAuth.get(req.auth());
        dataAccessAuth.checkAuthTokenValid(auth);
        GameData game = new GameData(UUID.randomUUID().hashCode(), null, null,  req.gameName(), null);
        dataAccessGame.getName(game); //will throw an error if it is already taken
        dataAccessGame.create(game);
        return new CreateResult(game.gameID());

    }

    public JoinResult joinGame(JoinRequest req) throws DataAccessException {
        if(!req.playerColor().equals("WHITE") && !req.playerColor().equals("BLACK")){
            throw new DataAccessException("Bad request");
        }
        AuthData auth = dataAccessAuth.get(req.auth());
        dataAccessAuth.checkAuthTokenValid(auth);
        GameData game = new GameData(req.gameID(), null, null, null, null);
        game = dataAccessGame.getID(game);
        UserData user = new UserData(dataAccessAuth.getUsername(req.auth()), null, null);
        user = dataAccessUser.get(user);
        GameData newGame = dataAccessGame.updatePlayer(user.username(), req.playerColor(), game);
        return new JoinResult(newGame.gameID(), newGame.whiteUsername(), newGame.blackUsername());
    }

//    public void clearAllGames(){
//    }

}
