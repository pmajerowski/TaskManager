package pl.coderslab;

import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Test {
    private static final Path path;
    private static final String isImportant;
    private static final String isNotImportant;
    private static final String endOfTaskDescription;
    private static final String endOfDate;
    private static final String regex;
    private static Scanner scanner = new Scanner(System.in);

    static {
        scanner = new Scanner(System.in);
        path = Paths.get("tasklist/tasks.csv");
        isImportant = "$$$_is_important_$$@";
        isNotImportant = "$$$_is_not_important_$$@";
        endOfTaskDescription = "$$$_end_of_task_description_$$@";
        endOfDate = "$$$_end_of_date_$$@";
        regex = "^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\" +
                "/|-|\\.)(?:0?[13-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\" +
                "/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|" +
                "(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\" +
                ".)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$";
    }

    public static void main(String[] args) {
        int index = 2;
        String[] tasks = readFile();
        String taskToMarkAsDone = tasks[index - 1];
//        System.out.println(taskToMarkAsDone);
        String[] temp = ArrayUtils.remove(tasks, index - 1);
        String[] tasksUpdated = ArrayUtils.insert(index - 1, temp,
                taskMarkDone(taskToMarkAsDone));

        for(String task : tasksUpdated) {
            System.out.println(task);
        }
    }
    private static String[] readFile() {
        File file = getTaskListMethod();
        StringBuilder reading = new StringBuilder();
        try {
            Scanner scan = new Scanner(file);
            while (scan.hasNextLine()) {
                reading.append(scan.nextLine()).append("\n");
            }
        } catch (FileNotFoundException e) {
            System.out.println("File doesn't exist");
        }
        return reading.toString().split("\n");
    }
    private static File getTaskListMethod() {
        File file = new File(Paths.get("tasklist/tasks.csv").toString());
        Scanner scan = null;
        try {
            scan = new Scanner(file);
            while (scan.hasNextLine()) {
                scan.nextLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return file;

    }
    private static String taskMarkDone(String taskToMark) {
        String markedTask = taskToMark.substring(
                        0, (taskToMark.indexOf(endOfTaskDescription)+endOfTaskDescription.length()))
                .concat("\tDone! ✅").concat(taskToMark
                        .substring(taskToMark.indexOf(endOfDate)));

        return markedTask;
    }
}
