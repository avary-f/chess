package service;

import dataaccess.GameDAO;
import dataaccess.UserDAO;
import request.GameRequest;
import request.ListRequest;
import result.ListResult;

public class GameService {
    private final GameDAO dataAccessGame;
    private final UserDAO dataAccessUser;

    public GameService(GameDAO dataAccessGame, UserDAO dataAccessUser){
        this.dataAccessGame = dataAccessGame;
        this.dataAccessUser = dataAccessUser;
    }
    public ListResult listGames(ListRequest req){
       return;
    }
//    //List of things this will
//    public void joinGame(GameRequest game){
//
//    }

//    public CreateResult createGame(CreateRequest game){}
//    public void clearAllGames(){
//    }

}
