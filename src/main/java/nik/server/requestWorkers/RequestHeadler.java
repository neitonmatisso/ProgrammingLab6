package nik.server.requestWorkers;

import nik.businessLogic.collectionWorker.CollectionShell;
import nik.businessLogic.collectionWorker.HashMapShell;
import nik.businessLogic.commands.*;
import nik.businessLogic.fileWorker.FileManager;
import nik.businessLogic.fileWorker.IOInterface;
import nik.businessLogic.workClasses.ResultShell;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

public class RequestHeadler {
    private Queue<String> queryQueue;
    private Queue<String> answerQueue;
    private ControlUnit controlUnit;
    private CollectionShell hashMapWrapper;
    private IOInterface fileManager;

    public RequestHeadler(Queue<String> queryQueue, Queue<String> answerQueue, ControlUnit controlUnit, CollectionShell hashMapWrapper
            , IOInterface fileManager) {
        this.queryQueue = queryQueue;
        this.answerQueue = answerQueue;
        this.controlUnit = controlUnit;
        this.hashMapWrapper = hashMapWrapper;
        this.fileManager = fileManager;
        loadCommands(controlUnit,hashMapWrapper,fileManager);
    }

    public void completeRequest(){
        String command = queryQueue.poll();
        List<String> commandOption = Arrays.asList(command.split("@"));
        ResultShell result = new ResultShell();
        controlUnit.executeCommand(commandOption.get(0),commandOption.get(1),result);
        answerQueue.add(result.getCommandResult());
    }


    private void loadCommands(ControlUnit controlUnit,CollectionShell collectionShell, IOInterface fileManager){
        Command clearCommand = new ClearCommand(collectionShell,controlUnit);
        Command exitCommand = new ExitCommand(controlUnit);
        Command filter = new FilterByOwnerCommand(controlUnit,collectionShell);
        Command filter_less = new FilterLessCommand(controlUnit,collectionShell);
        Command infoCommand = new InfoCommand(controlUnit,collectionShell);
        Command insertCommand = new InsertCommand(controlUnit,collectionShell);
        Command loadCommand = new LoadCommand(controlUnit,fileManager);
        Command minByCoordinates = new MinByCoordinatesCommand(controlUnit,collectionShell);
        Command removeCommand = new RemoveCommand(controlUnit,collectionShell);
        Command removeGreaterCommand = new RemoveGreaterCommand(controlUnit,collectionShell);
        Command removeGreaterKeyCommand = new RemoveGreaterKeyCommand(controlUnit,collectionShell);
        Command saveCommand = new SaveCommand(controlUnit,fileManager);
        Command showCommand = new ShowCommand(controlUnit,collectionShell);
        Command script = new ExecuteScriptCommand(controlUnit);
        Command help = new HelpCommand(controlUnit);

        controlUnit.executeCommand("load", "",new ResultShell());
    }
}
