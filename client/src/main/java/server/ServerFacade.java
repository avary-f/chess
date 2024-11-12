package server;

import com.google.gson.Gson;
import request.*;
import result.*;
import spark.Spark;

import java.io.*;
import java.net.*;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public RegisterResult register(RegisterRequest request) throws ResponseException{
        return this.makeRequest("POST", "/user", request, RegisterResult.class);
    }

    public LoginResult login(LoginRequest request) throws ResponseException{
        return this.makeRequest("POST", "/session", request, LoginResult.class);
    }

    public void logout(LogoutRequest request) throws ResponseException{
        this.makeRequest("DELETE", "/session", request, null);
    }

    public void deleteAll() throws ResponseException{
        this.makeRequest("DELETE", "/session", null, null);
    }

    public ListResult listGames(ListRequest request) throws ResponseException{
        return this.makeRequest("GET", "/game", request, ListResult.class);
    }

    public CreateResult createGame(CreateRequest request) throws ResponseException{
        return this.makeRequest("POST", "/game", request, CreateResult.class);
    }

    public JoinResult joinGame(JoinRequest request) throws ResponseException{
        return this.makeRequest("PUT", "/game", request, JoinResult.class);
    }


    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL(); // make a url to the server
            HttpURLConnection http = (HttpURLConnection) url.openConnection(); //opens a connection to that url (the server)
            if(it is a request that has an authtoken){
                http.setRequestProperty("authorization", request.authToken());
            }
            http.setRequestMethod(method); //tells if you will be using POST, DELETE, etc
            http.setDoOutput(true); //magic, do it always

            writeBody(request, http); //sets the format for the HTTP request using your request that you passed in
            http.connect(); //actually makes the request
            throwIfNotSuccessful(http); //if there was an error when you made the request (not 200)
            return readBody(http, responseClass); //reads the result, converts it into the response type that you passed in
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage()); //need to make this Exception class
        } //this error will only happen if the server is crashing or something
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException(status, getErrorMessage(status));
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                    // takes what the server sent and deseralizes it and returns that
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

    private String getErrorMessage(int status){
        return switch (status) {
            case 400 -> "Bad request";
            case 401 -> "Invalid username or password";
            case 403 -> "Username already taken"; //Forbidden (invalid credentials or auth fails) -
            // happens when username is taken, server understood the request but refused to process it
            case 404 -> "Not found";
            case 500 -> "Internal Server Error";
            case 501 -> "Not implemented";
            default -> "Unknown status";
        };


    }

}