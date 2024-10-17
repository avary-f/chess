package result;

public record CreateResult (int gameID){
    @Override
    public String toString() {
        return "CreateResult{" +
                "gameID=" + gameID +
                '}';
    }
}
