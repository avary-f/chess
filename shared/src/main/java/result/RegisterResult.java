package result;

public record RegisterResult(String username, String authToken){
    @Override
    public String toString() {
        return "RegisterResult: " + "user='" + username + " auth='" + authToken;
    }
}
