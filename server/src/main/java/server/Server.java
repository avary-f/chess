package server;

import com.google.gson.Gson;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import request.*;
import result.*;
import service.GameService;
import service.UserService;
import spark.*;

import java.util.Map;

public class Server {
    private MemoryUserDAO userDAO = new MemoryUserDAO();
    private MemoryAuthDAO authDAO = new MemoryAuthDAO();
    private MemoryGameDAO gameDAO = new MemoryGameDAO();
    private final UserService serviceUser = new UserService(userDAO, authDAO);
    private final GameService serviceGame = new GameService(userDAO, authDAO, gameDAO);

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        //register, login, logout, createGame, joinGame, listGames, clear
        Spark.delete("/db", this::clear); //what are these paths?
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.exception(UnauthorizedException.class, this::exceptionHandlerUnauthorized);
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
    private Object clear(Request req, Response res) {
        serviceUser.clearAllUserAuthData();
        serviceGame.clearAllGames();
        return "";
    }


    private Object register(Request req, Response res) throws Exception {
        //need to check the request to make sure it is real --> also listGames and joinGame?
        RegisterRequest request = new Gson().fromJson(req.body(), RegisterRequest.class);
        if(request.username() == null || request.password() == null || request.email() == null){
            throw new BadRequestException();
        }
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
        if(bodyParams.size() == 2){
            String playerColor = (String) bodyParams.get("playerColor");
            double gameIDdouble = (double) bodyParams.get("gameID");
            int gameID = (int) gameIDdouble;
            JoinRequest request = new JoinRequest(req.headers("authorization"), playerColor, gameID);
            serviceGame.joinGame(request);
            return "";
        }
        else{
            throw new BadRequestException();
        }
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
