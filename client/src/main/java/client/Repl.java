package client;

import server.websocket.ServerMessageHandler;
import websocket.messages.Error;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private ChessClient client;
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
            client.printPrompt();
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
                client = new ClientGameplay(serverUrl, client.getAuth(), client.getClientName(), client.getGame(), client.getTeamColor());
            }
            else{
                client = new ClientPostlogin(serverUrl, client.getAuth(), client.getClientName());
            }
        }
        else if(!client.isLoggedIn()){
            client = new ClientPrelogin(serverUrl);
        }
    }

}
