package result;

public record LoginResult(String username, String authToken) {
    @Override
    public String toString() {
        return "LoginResult{" +
                "username='" + username + '\'' +
                ", authToken='" + authToken + '\'' +
                '}';
    }
}
