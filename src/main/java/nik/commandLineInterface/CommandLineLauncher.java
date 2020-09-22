package nik.commandLineInterface;

import javafx.util.Pair;
import nik.client.Client;
import nik.client.RequestBuilder;
import nik.client.ServerStatus;
import nik.client.exceptions.InvalidCommandException;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class CommandLineLauncher {
    public static void main(String[] args) throws InterruptedException {
        Client client = new Client();
        Scanner scanner = new Scanner(System.in);
        connect(client,scanner);

        RequestBuilder requestBuilder = new RequestBuilder(client.getSettings());
        while (true){
            scanner = new Scanner(System.in);

            if(client.getServerStatus().equals(ServerStatus.CLOSE)){
                connect(client,scanner);
            }

            System.out.println("Create your request");
            System.out.print("> ");

            String commandData = scanner.nextLine();

            if(commandData.equals("")){
                continue;
            }

            List<String> request = Arrays.asList(commandData.split(" "));
            Pair<String,String> query = null;


            try {
                switch (request.size()){
                    case 1:
                        query = requestBuilder.completeQuery(request.get(0),null);
                        break;
                    case 2:
                        query = requestBuilder.completeQuery(request.get(0),request.get(1));
                        break;
                    default:
                        continue;
                }

                client.createQuery(query.getKey(),query.getValue());

            } catch (InvalidCommandException exception){
                continue;
            }
            Thread.sleep(100);
        }
    }

    public static void connect(Client client, Scanner scanner) throws InterruptedException {
        while (client.getServerStatus().equals(ServerStatus.CLOSE)) {
            System.out.println("Welcome! Enter server IP and Port");
            String[] serverData = scanner.nextLine().split(" ");
            try {
                client.connectToServer(serverData[0], Integer.parseInt(serverData[1]));
            } catch (Exception e){
                continue;
            }
            Thread.sleep(1000);
        }
    }
}
