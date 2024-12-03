package server;

import com.google.gson.Gson;
import dataaccess.*;
import request.*;
import result.*;
import server.websocket.WebSocketHandler;
import service.GameService;
import service.UserService;
import spark.*;

import java.util.Map;

public class Server {
    private UserDAO userDAO = new MysqlUserDAO();
    private AuthDAO authDAO = new MysqlAuthDAO();
    private GameDAO gameDAO = new MysqlGameDAO();
    //change these guys to be the memory if needed ^^
    private final UserService serviceUser = new UserService(userDAO, authDAO);
    private final GameService serviceGame = new GameService(userDAO, authDAO, gameDAO);
    private WebSocketHandler webSocketHandler = new WebSocketHandler(serviceUser, serviceGame);

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Endpoints
        Spark.webSocket("/ws", webSocketHandler);
        Spark.delete("/db", this::clear);
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.exception(UnauthorizedException.class, this::exceptionHandlerUnauthorized);
        Spark.exception(AlreadyTakenException.class, this::exceptionHandlerAlreadyTaken);
        Spark.exception(BadRequestException.class, this::exceptionHandlerBadRequest);
        Spark.exception(DataAccessException.class, this::exceptionHandlerDataAccess);

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
        String playerColor = (String) bodyParams.get("playerColor");
        JoinRequest request;
        try {
            double gameIDdouble = (double) bodyParams.get("gameID");
            int gameID = (int) gameIDdouble;
            request = new JoinRequest(req.headers("authorization"), playerColor, gameID);
        } catch (Exception ex) {
            throw new BadRequestException();
        }
        return serviceGame.joinGame(request);
        //this used to have a check for if there's 3 params. If not throw a bad request error, but might not need that?
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

    private void exceptionHandlerDataAccess(DataAccessException e, Request req, Response res){
        res.status(500);
        res.body(new Gson().toJson(new ServerResponse(e.getMessage())));
    }




}
