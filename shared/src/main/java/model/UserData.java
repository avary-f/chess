package model;

public record UserData(String username, String password, String email) {

    @Override
    public String toString() {
        return "user='" + username + " pass='" + password + " email='" + email;
    }
}
