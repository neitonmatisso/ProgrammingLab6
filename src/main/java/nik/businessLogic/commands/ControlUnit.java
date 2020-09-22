package nik.businessLogic.commands;

import nik.businessLogic.exceptions.CommandExecutingException;
import nik.businessLogic.workClasses.ResultShell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControlUnit {

    private Map<String, Command> commandMap;
    private Map<CommandType, List<String>> commandTypeMap;
    private HashMap<String,CommandType> commandTypeHashMap;

    public ControlUnit() {
        commandMap = new HashMap<>();
        commandTypeMap = new HashMap<>();
        commandTypeHashMap = new HashMap<>();

        addNewTypeOfCommand(CommandType.CLEAR);
        addNewTypeOfCommand(CommandType.ARG);
        addNewTypeOfCommand(CommandType.OBJECT);
        addNewTypeOfCommand(CommandType.MIXED);
        addNewTypeOfCommand(CommandType.SCRIPT);
        commandTypeHashMap = new HashMap<>();

    }

    public void addNewCommand(String name,Command newCommand){
        commandMap.put(name,newCommand);
    }
    public void addNewSettings(CommandType type, String name){
        commandTypeMap.get(type).add(name);
        commandTypeHashMap.put(name,type);
    }
    public void addNewTypeOfCommand(CommandType commandType){
        commandTypeMap.put(commandType,new ArrayList<>());
    }
    public List getCommandsByType(CommandType commandType){
        return commandTypeMap.get(commandType);
    }

    public List<Command> getAllCommand(){
        return new ArrayList<>(commandMap.values());
    }

    public CommandType getTypeByName(String name){
        for (CommandType commandType : commandTypeMap.keySet()){
            if(commandTypeMap.get(commandType).contains(name)){
                return commandType;
            }
        }
        throw new NullPointerException();
    }

    public void executeCommand(String name, String options, ResultShell resultShell){
        try {
            if(options.equals("null")){
                commandMap.get(name).execute(null,resultShell);
                return;
            }
            commandMap.get(name).execute(options,resultShell);
        } catch (CommandExecutingException exception){
            resultShell.setCommandResult("Работа команды прервана");
        }

    }

    public HashMap<String,CommandType> getCommands(){
        return commandTypeHashMap;
    }

}
