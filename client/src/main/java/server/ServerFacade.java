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


//    public Pet[] listPets() throws ResponseException { //this error gets caught in the client
//        var path = "/pet";
//        record listPetResponse(Pet[] pet) {
//        }
//        var response = this.makeRequest("GET", path, null, listPetResponse.class);
//        return response.pet();
//    }
    //in your client code, that's where you dumb down the error and omit the error code

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL(); // make a url to the server
            HttpURLConnection http = (HttpURLConnection) url.openConnection(); //opens a connection to that url (the server)
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
            throw new ResponseException(status, "failure: " + status);
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
}