package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;

public class GameService {
    private final GameDAO dataAccess;

    public GameService(GameDAO dataAccess){
        this.dataAccess = dataAccess;
    }

//    public CreateResult createGame(CreateRequest game){}
//    public void clearAllGames(){
//        for all the games in db
//                delete(game);
//    }
//    public void updateGame(GameRequest game){} //why doesn't this return back the updated game
    //this will be the primary method for joining a game
//    public ListResult listGames(ListRequest req){} //this should only be called once the token is checked, so no need to pass anything i

}
