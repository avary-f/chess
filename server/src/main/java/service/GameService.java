package service;

public class GameService {
    public void removeGame(GameRequest game){}
    public void clear(){
        for all the games in db
                delete(game);
    }
    public void update(GameRequest game){} //why doesn't this return back the updated game
    public CreateResult create(CreateRequest game){}
    public ReadResult read(){} //this should only be called once the token is checked, so no need to pass anything i

}
