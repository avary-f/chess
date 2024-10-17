package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import request.CreateRequest;
import request.ListRequest;
import result.CreateResult;
import result.ListResult;

public class GameService {
    private final GameDAO dataAccessGame;
    private final UserDAO dataAccessUser;
    private final AuthDAO dataAccessAuth;

    public GameService(GameDAO dataAccessGame, UserDAO dataAccessUser, AuthDAO dataAccessAuth){
        this.dataAccessGame = dataAccessGame;
        this.dataAccessUser = dataAccessUser;
        this.dataAccessAuth = dataAccessAuth;
    }
//    public ListResult listGames(ListRequest req) throws DataAccessException{
//        AuthData auth = dataAccessAuth.get(req.auth());
//        dataAccessAuth.checkAuthTokenValid(auth);
//        return new ListResult(dataAccessGame.getAll());
//    }
//    public CreateResult createGame(CreateRequest game) throws DataAccessException {
//        AuthData auth = dataAccessAuth.get(game.auth());
//        dataAccessAuth.checkAuthTokenValid(auth);
//
//    }
//    //List of things this will
//    public void joinGame(GameRequest game){
//
//    }

//    public void clearAllGames(){
//    }

}
