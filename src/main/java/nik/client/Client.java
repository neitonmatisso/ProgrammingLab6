package nik.client;

import com.google.gson.Gson;
import nik.businessLogic.commands.CommandType;
import nik.connectSource.Connection;
import nik.connectSource.ConnectionData.Request;
import nik.connectSource.ConnectionData.RequestType;
import nik.connectSource.ConnectionData.Responce;
import nik.connectSource.ConnectionData.TransferObject;
import nik.connectSource.ConnectionListener;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Queue;

public class Client implements ConnectionListener {
    private Connection connection;
    private HashMap commandMap;
    private Queue<String> answersQueue;
    private ServerStatus serverStatus;

    public Client(){
        commandMap = new HashMap<>();
        answersQueue = new ArrayDeque<>();
        serverStatus = ServerStatus.CLOSE;
    }

    public HashMap<String, CommandType> getSettings(){
        return commandMap;
    }

    public String getNextAnswer(){
        return answersQueue.poll();
    }

    public ServerStatus getServerStatus(){
        return serverStatus;
    }

    public void createQuery(String commandName, String args){
        Request request = new Request(RequestType.QUERY,commandName,args);
        try {
            connection.sendTransferObject(new TransferObject(new Gson().toJson(request)));
        } catch (IOException ex ){
            System.out.println("Cannot send request!");
        }
    }



    public boolean connectToServer(String IP, int port){
        try{
            connection = new Connection(this,IP,port);
            return true;
        }catch (IOException ex){
            return false;
        }
    }


    @Override
    public void getTransferObject(Connection connection, TransferObject transferObject) {
        Responce response = new Gson().fromJson(transferObject.getJsonTransfer(),Responce.class);
        switch (response.getResponseType()){
            case ANSWER:
                answersQueue.add(response.getResponseOption());
                System.out.println(response.getResponseOption());
                break;
            case SETTINGS:
                Responce jsonMap = new Gson().fromJson(transferObject.getJsonTransfer(), Responce.class);
                commandMap = new Gson().fromJson(jsonMap.getResponseOption(),HashMap.class);
                System.out.println("Get settings");
                break;
            case BAD_REQUEST:
                System.out.println("something wrong...");
                //breakpoint, можно для какой-нибудь ошибки обработать
                break;
        }

    }

    @Override
    public void connectionReady(Connection connection) {
        serverStatus = ServerStatus.READY;
        System.out.println("Connection ready");
    }

    @Override
    public void disconnect(Connection connection) {
        serverStatus = ServerStatus.CLOSE;
        System.out.println("Connection close");

    }
}
