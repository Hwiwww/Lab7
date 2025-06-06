package org.example.system;

import org.example.recources.Dragon;

import java.io.*;
import java.util.Arrays;
import java.util.Stack;

public class ExecuteScript {
    private static Stack<File> stack = new Stack<>();

    public static void execute(String fileName) {
        Request request = null;
        File file = new File(fileName);

        if (stack.contains(file)) {
            System.err.println("Recursion detected");
        }

        if (file.canRead() && file.canWrite()) {
            stack.add(file);
        } else {
            System.err.println("Something wrong with file");
        }

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
            String line;
            // String[] dragonData = new String[9];
            while ((line = br.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");

                if (parts[0].equals("insert")) {
                    try {
                        // Заполняем массив данных для дракона
                        String[] dragonData = new String[9]; // Убедитесь, что длина массива совпадает с количеством элементов
                        for (int n = 0; n < 9; n++) {
                            dragonData[n] = parts[n + 1]; // Заполняем данные начиная с части 1 (skip "insert")
                            System.out.println("dragonData[" + n + "]: " + dragonData[n]); // Печать значений данных
                        }

                        // Печать информации о запросе перед отправкой
                        System.out.println("Sending dragon data: " + Arrays.toString(dragonData));

                        // Создаем объект Dragon и отправляем запрос
                        Client.sendRequest(new Request("insert", new Dragon(dragonData), dragonData));

                    } catch (Exception e) {
                        System.out.println("Something wrong with dragon data: ");
                        e.printStackTrace(); // Выводим подробности ошибки для диагностики
                    }
                } else if (line.contains("execute_script")) {
                    File anotherFile = new File(line.split(" ")[1]);
                    if (stack.contains(anotherFile)) {
                        System.err.println("Recursion was detected");
                    }
                    if (!file.canRead()) {
                        System.err.println("Something wrong with reading a file");
                    } else {
                        Client.sendRequest(new Request("insert", new Dragon(), null));
                    }
                } else {
                    String[] commandLine = line.split(" ");
                    String[] arguments = Arrays.copyOfRange(commandLine, 1, commandLine.length);
                    System.out.println(arguments.toString());
                    String command = commandLine[0];
                    Client.sendRequest(new Request(command, new Dragon(), arguments));
                }
            }

        } catch (FileNotFoundException e) {
            System.err.println("File can't be found");
        } catch (IOException e) {
            System.err.println("");
        }
    }

    public String getName() {
        return "execute_script";
    }

    public String getDescription() {
        return "executes script from file";
    }
}
