package request;

public record LogoutRequest(String authToken) {
    @Override
    public String toString() {
        return "LogoutReqeust{" +
                "authToken='" + authToken + '\'' +
                '}';
    }
}
