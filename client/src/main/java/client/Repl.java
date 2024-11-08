package client;

//import client.websocket.NotificationHandler;
//import webSocketMessages.Notification;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl{
    private ChessClient client;
    private ClientPrelogin preClient;
    private ClientPostlogin postCient;
    private ClientGameplay gameClient;

    public Repl(String serverUrl) { //this might need a notification handler
        ClientPrelogin preClient = new ClientPrelogin(serverUrl);
//        ClientPostlogin postCient = new ClientPostlogin(serverUrl);
//        ClientGameplay gameClient = new ClientGameplay(serverUrl);
        client = preClient; //starts out in Prelogin
    }

    public void run() {
        System.out.println("\uD83D\uDC51 Welcome to the 240 Chess.\uD83D\uDC51");
        System.out.println("    Type 'help' to get started. ");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!result.equals("quit")) {
            if(client.isLoggedIn()) { //if client is logged in
                client = postCient;
            }
            else if(!client.isLoggedIn()){
                client = preClient;
            }
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
//
//    public void notify(Notification notification) {
//        System.out.println(RED + notification.message());
//        printPrompt();
//    }

    private void printPrompt() {
        if(!client.isLoggedIn()){ //if they are signed out
            System.out.print("\n" + RESET + "[LOGGED_OUT] >>> " + GREEN);
        }
        else{ //if they are signed in
            System.out.print("\n" + RESET + "[" + client.getClientName().toUpperCase() + "] >>> " + GREEN);
        }

    }

}
