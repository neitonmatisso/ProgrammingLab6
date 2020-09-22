package nik.businessLogic.fileWorker;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import nik.businessLogic.collectionWorker.CollectionShell;
import nik.businessLogic.sourceClasses.Product;
import nik.businessLogic.workClasses.ResultShell;

import java.io.*;
import java.util.List;
import java.util.Scanner;

public class FileManager implements IOInterface {

    private CollectionShell collectionShell;
    private Gson jsoner;
    private File file;

    public FileManager (CollectionShell collectionShell, String filePath){
        this.collectionShell = collectionShell;
        file = new File(filePath);
    }


    @Override
    public void write(ResultShell resultShell) {

        jsoner = new Gson();
        if(collectionShell.isEmpty()){
            resultShell.setCommandResult("Коллекция пуста, запись не была произведена");
            return;
        }

        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            for(int i =0; i<collectionShell.getProductList().size(); i++) {
                System.out.println(collectionShell.getProductList().get(i));
                bufferedWriter.write(jsoner.toJson(collectionShell.getProductList().get(i)) + "\n");
            }
            bufferedWriter.flush();
        } catch (IOException exception) {
            resultShell.setCommandResult("Ошибка записи в файл. Файл отстувует или не путь !неверный");
            return;
        }

        resultShell.setCommandResult("Запись прошла успешно!");
    }

    @Override
    public void read(ResultShell resultShell) {
        jsoner = new Gson();
        try {
            Scanner scanner = new Scanner(file);

            while (scanner.hasNext()) {
                collectionShell.addNewProduct(jsoner.fromJson(scanner.nextLine(), Product.class));
            }

            resultShell.setCommandResult("Данные загружены успешно!");
            scanner.close();

        } catch (FileNotFoundException foundException){
            resultShell.setCommandResult("Файл не найден");
        } catch (JsonSyntaxException e){
            resultShell.setCommandResult("Ошибка в файле!");
        } catch (Exception ex) {
            resultShell.setCommandResult("Серьезная ошибка");
        }
    }
}
