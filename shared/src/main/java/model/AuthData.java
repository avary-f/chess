package model;

public record AuthData(String authToken, String username) {

    @Override
    public String toString() {
        return "token=" + authToken + ", userID=" + username;
    }

}
