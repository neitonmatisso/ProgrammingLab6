package nik.client;

import com.google.gson.Gson;
import nik.businessLogic.commands.CommandType;
import nik.businessLogic.factories.ProductFactory;
import nik.businessLogic.sourceClasses.Product;
import nik.client.exceptions.InvalidCommandException;
import javafx.util.Pair;

import java.util.HashMap;

public class RequestBuilder {
    private String commandName = "";
    private String commandArgs = "";
    private HashMap<String, CommandType> commandMap;

    public RequestBuilder(HashMap<String, CommandType> commandMap) {
        this.commandMap = commandMap;
    }

    public Pair<String,String> completeQuery(String name, String args) throws InvalidCommandException {
        if(!commandMap.containsKey(name)){
            System.out.println("Invalid command name");
            throw new InvalidCommandException();
        }

        String commandType = String.valueOf(commandMap.get(name));

        switch (commandType){
            case "ARG":
                if(args == null){
                    System.out.println("Данная команда  содержит аргументы");
                    throw new InvalidCommandException();
                }
                return new Pair<String, String>(name,args);
            case "CLEAR":
                if(!(args == null) ){
                    System.out.println("Данная команда не содержит аргументов");
                    throw new InvalidCommandException();
                }
                return new Pair<String, String>(name,null);
            case "OBJECT":
                String jsonProduct = new ProductFactory().getNewProduct();
                return new Pair<String, String>(name, jsonProduct);
            case "SCRIPT":
                return new Pair<String, String>(name,args);
            default:
                throw new InvalidCommandException();
        }


    }
}
