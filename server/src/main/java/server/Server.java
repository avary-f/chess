package server;

import com.google.gson.Gson;
import request.*;
import result.*;
import service.GameService;
import service.UserService;
import spark.*;

import java.util.Map;

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
        //Spark.delete("/db", this::clear); //what are these paths?
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/get", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.exception(UnauthorizedException.class, this::exceptionHandlerUnauthorized); //make response exception a super class to all other exceptions.
        Spark.exception(AlreadyTakenException.class, this::exceptionHandlerAlreadyTaken);
        Spark.exception(BadRequestException.class, this::exceptionHandlerBadRequest);

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
//    private Object clear(Request req, Response res) {
//        var clear = new Gson().fromJson(req.body(), ClearRequest.class);
//        clear = serviceUser.clearAllUserAuthData(clear);
//        return new Gson().toJson(clear);
//    } //not sure how to do this one


    private Object register(Request req, Response res) throws Exception {
        //need to check the request to make sure it is real --> also listGames and joinGame?
        RegisterRequest request = new Gson().fromJson(req.body(), RegisterRequest.class);
        RegisterResult result = serviceUser.register(request);
        return new Gson().toJson(result);
    }

    private Object login(Request req, Response res) throws Exception{
        LoginRequest request = new Gson().fromJson(req.body(), LoginRequest.class);
        LoginResult result = serviceUser.login(request);
        return new Gson().toJson(result); //the default status code for success is 200, so you don't need to set it
    }

    private Object logout(Request req, Response res) throws Exception {
        LogoutRequest request = new LogoutRequest(req.headers("authorization"));
        serviceUser.logout(request);
        return "";
    }

    private Object listGames(Request req, Response res) throws Exception {
        ListRequest request = new ListRequest(req.headers("authorization"));
        ListResult result = serviceGame.listGames(request);
        return new Gson().toJson(result);
    }

    private Object createGame(Request req, Response res) throws Exception {
        CreateRequest request = new Gson().fromJson(req.body(), CreateRequest.class);
        request.setAuthtoken(req.headers("authorization"));
        CreateResult result = serviceGame.createGame(request);
        return new Gson().toJson(result);
    }

    private Object joinGame(Request req, Response res) throws Exception {
        Map<String, Object> bodyParams = new Gson().fromJson(req.body(), Map.class);
        String playerColor = (String) bodyParams.get("playerColor");
        int gameID = (int) bodyParams.get("gameID");
        JoinRequest request = new JoinRequest(req.headers("authorization"), playerColor, gameID);
        serviceGame.joinGame(request);
        return "";
    }

    private void exceptionHandlerBadRequest(BadRequestException e, Request req, Response res) {
        res.status(400);
        res.body(new Gson().toJson(new ServerResponse(e.getMessage())));
    }

    private void exceptionHandlerUnauthorized(UnauthorizedException e, Request req, Response res) {
        res.status(401);
        res.body(new Gson().toJson(new ServerResponse(e.getMessage())));
    } //make exceptional handlers for each type of error

    private void exceptionHandlerAlreadyTaken(AlreadyTakenException e, Request req, Response res) {
        res.status(403);
        res.body(new Gson().toJson(new ServerResponse(e.getMessage())));
    }


}
