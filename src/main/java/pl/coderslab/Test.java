package pl.coderslab;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class Test {
    private static final Path path = Paths.get("tasklist/tasks.csv");
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        ArrayList<String> lines = new ArrayList<>();
        try {
            for (String line : Files.readAllLines(path)) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(lines);
        System.out.println(lines.size());
        System.out.println(lines.get(1));


    }

    public static void printMenu(Scanner scanner) {

    }


}
