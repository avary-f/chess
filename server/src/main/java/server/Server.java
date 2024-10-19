package server;

import com.google.gson.Gson;
import request.*;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {
    private final UserService serviceUser;
    private final GameService serviceGame;

    public Server(UserService serviceUser, GameService serviceGame) {
        this.serviceUser = serviceUser;
        this.serviceGame = serviceGame;
    }
    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        //register, login, logout, createGame, joinGame, listGames, clear
        Spark.delete("/db", this::clear); //what are these paths?
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/get", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.exception(ResponseException.class, this::exceptionHandler);
        //how do I add the clear path? Do I do that here?
        //Why do I need a clear path?
        //Do I just run the server and start testing things?
        //What type of errors should I be throwing? do I need to use their class?


        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }
    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    //Handlers
    private Object clear(Request req, Response res) {
    }

    private Object register(Request req, Response res) {
        var user = new Gson().fromJson(req.body(), RegisterRequest.class);
        user = serviceUser.register(user);
        return new Gson().toJson(user);
    }

    private Object login(Request req, Response res) {
        var user = new Gson().fromJson(req.body(), LoginRequest.class);
        user = serviceUser.login(user);
        return new Gson().toJson(user);
    }

    private Object logout(Request req, Response res) {
        var user = new Gson().fromJson(req.body(), LogoutRequest.class);
        user = serviceUser.logout(user);
        return new Gson().toJson(user);
    }

    private Object listGames(Request req, Response res) {
        var games = new Gson().fromJson(req.body(), ListRequest.class);
        games = serviceGame.listGames(games);
        return new Gson().toJson(games);
    }

    private Object createGame(Request req, Response res) {
        var game = new Gson().fromJson(req.body(), CreateRequest.class);
        game = serviceGame.createGame(game);
        return new Gson().toJson(game);
    }

    private Object joinGame(Request req, Response res) {
        var game = new Gson().fromJson(req.body(), CreateRequest.class);
        game = serviceGame.joinGame(game);
        return new Gson().toJson(game);
    }

    private void exceptionHandler(ResponseException ex, Request req, Response res) {
        res.status(ex.StatusCode());
    }


}
