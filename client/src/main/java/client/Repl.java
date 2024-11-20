package client;

//import client.websocket.NotificationHandler;
//import webSocketMessages.Notification;

import model.GameData;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl{
    private ChessClient client;
    private ClientPrelogin preClient;
    private ClientPostlogin postClient;
    private ClientGameplay gameClient;
    private String serverUrl;

    public Repl(String serverUrl) { //this might need a notification handler
        this.serverUrl = serverUrl;
        client = new ClientPrelogin(serverUrl);
        client.setState(State.LOGGEDOUT);//starts out in Prelogin

    }

    public void run() {
        System.out.println("\uD83D\uDC51 Welcome to the 240 Chess.\uD83D\uDC51");
        System.out.println();
        System.out.println("Type 'help' to get started.");
        System.out.println();
        //System.out.print(BLUE + client.help() + RESET); //reset used to return console to default color

        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!(result.equals("quit") && client instanceof ClientPrelogin)) {
        //if you press quit & you are in the prelogin repl loop
            clientUpdate();
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = client.eval(line);
                System.out.print(BLUE + result);

            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void clientUpdate(){
        if(client.isLoggedIn()) { //if client is logged in
            if(client.isInGameplay()){
                String auth = client.getAuth();
                String clientName = client.getClientName();
                String teamColor = client.getTeamColor();
                GameData game = client.getGame();
                client = new ClientGameplay(serverUrl);
                client.setGame(game);
                client.setTeamColor(teamColor);
                client.setClientName(clientName);
                client.setState(State.GAMEPLAY);
                client.setAuth(auth);
            }
            else{
                String auth = client.getAuth();
                String clientName = client.getClientName();
                client = new ClientPostlogin(serverUrl);
                client.setClientName(clientName);
                client.setState(State.LOGGEDIN);
                client.setAuth(auth);
            }
        }
        else if(!client.isLoggedIn()){
            client = new ClientPrelogin(serverUrl);
        }
    }
    private void printPrompt() {
        if(client instanceof ClientPrelogin && !client.isLoggedIn() && !client.isInGameplay()){ //if they are signed out
            System.out.print("\n" + RESET + "[LOGGED_OUT] >>> " + GREEN);
        }
        else{ //if they are signed in
            System.out.print("\n" + RESET + "[" + client.getClientName().toUpperCase() + "] >>> " + GREEN);
        }

    }

}
