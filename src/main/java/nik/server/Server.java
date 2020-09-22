package nik.server;

import com.google.gson.Gson;
import nik.businessLogic.collectionWorker.CollectionShell;
import nik.businessLogic.collectionWorker.HashMapShell;
import nik.businessLogic.commands.ControlUnit;
import nik.businessLogic.fileWorker.FileManager;
import nik.businessLogic.fileWorker.IOInterface;
import nik.businessLogic.workClasses.ResultShell;
import nik.connectSource.Connection;
import nik.connectSource.ConnectionData.Request;
import nik.connectSource.ConnectionData.Responce;
import nik.connectSource.ConnectionData.ResponseType;
import nik.connectSource.ConnectionData.TransferObject;
import nik.connectSource.ConnectionListener;
import nik.server.requestWorkers.RequestHeadler;
import nik.server.requestWorkers.RequestReciever;
import nik.server.requestWorkers.ResponceSender;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Server implements ConnectionListener {
    private List<Connection> connectionsList;
    private Queue<String> queryQueue;
    private Queue<String> answerQueue;

    private RequestReciever requestReciever;
    private RequestHeadler requestHeadler;
    private ResponceSender responceSender;

    private ControlUnit controlUnit;
    private CollectionShell hashMapWrapper;
    private IOInterface fileManager;

    public Server()  {

        queryQueue = new ArrayDeque<>();
        answerQueue = new ArrayDeque<>();
        connectionsList = new ArrayList<>();

        controlUnit = new ControlUnit();
        hashMapWrapper = new HashMapShell();
        fileManager = new FileManager(hashMapWrapper,"saveFile");

        requestReciever = new RequestReciever(queryQueue);
        requestHeadler = new RequestHeadler(queryQueue,answerQueue,controlUnit,hashMapWrapper,fileManager);
        responceSender = new ResponceSender(answerQueue);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                controlUnit.executeCommand("save","", new ResultShell());
            }
        });

        try(ServerSocket serverSocket = new ServerSocket(7878)) {
            System.out.println("Server start...");
            while (true){
                Connection connection = new Connection(serverSocket.accept(),this);
                connectionsList.add(connection);
                System.out.println("New connection");
            }

        } catch (IOException ex){
            System.out.println("oops... something wrong with server");
        }
    }

    public static void main(String[] args) {
            new Server();
    }
    @Override
    public void getTransferObject(Connection connection, TransferObject transferObject) {
        Request request = new Gson().fromJson(transferObject.getJsonTransfer(),Request.class);
        switch (request.getRequestType()){
            case QUERY:
                requestReciever.recievRequest(request);
                requestHeadler.completeRequest();
                responceSender.sendAnswer(connection);
                break;
            case LOGIN:
                //для авторизации в 7 лабе
                break;
            case REGISTRATION:
                //для регистрации в 7 лабе
                break;
        }
    }

    @Override
    public void connectionReady(Connection connection) {
        try {
            String jsonMap = new Gson().toJson(controlUnit.getCommands());
            Responce responce = new Responce(ResponseType.SETTINGS, jsonMap);
            connection.sendTransferObject(new TransferObject(new Gson().toJson(responce)));
        } catch (IOException exception) {
            System.out.println("Cannot send command map");
            exception.printStackTrace();
        }

    }

    @Override
    public void disconnect(Connection connection) {
        System.out.println("Отключился клиент , а вот какой... секрет");
        connectionsList.remove(connection);
    }

}
