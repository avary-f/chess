package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import request.CreateRequest;
import request.ListRequest;
import result.CreateResult;
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
//    //List of things this will
//    public void joinGame(GameRequest game){
//
//    }

//    public void clearAllGames(){
//    }

}
