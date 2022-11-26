package pl.coderslab;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class TaskManager {
    private static final Scanner scanner;
    private static final Path path;
    private static final String isImportant;
    private static final String isNotImportant;
    private static final String endOfTaskDescription;
    private static final String endOfDate;
    private static final String regex;

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
        startTaskManager();
    }

    private static void startTaskManager() {
        while (true) {
            switch (printEntryScreen()) {
                case "ADD", "add" -> addTask();
                case "MD", "md" -> markAsDone();
                case "RM", "rm" -> removeTask();
                case "CL", "cl" -> clearAllDoneTasks();
                case "LS", "ls" -> getTaskList();
                case "Q", "q" -> {
                    scanner.close();
                    System.out.println(ConsoleColors.BLUE_BOLD + "\t\t\t\t\t\t\t\t\t\tSee ya!");
                    System.out.println(ConsoleColors.YELLOW
                            + "==================================================");
                    System.exit(0);
                }
                default -> {
                    System.out.println();
                    System.out.println(ConsoleColors.RED_BACKGROUND_BRIGHT + "\t\t\t\t\tWRONG INPUT!");
                }
            }
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

    private static void getTaskList() {
        printPageScreen("TASKS");
        System.out.println("");
        System.out.println(ConsoleColors.YELLOW_BRIGHT + " ");
        String[] lines = readFile();

        try {
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                String dateSubstring = line.substring(line.indexOf(endOfTaskDescription)
                        + endOfTaskDescription.length(), line.indexOf(endOfDate));

                if (i > 0)
                    System.out.println(ConsoleColors.RESET
                            + ". . . . . . . . . . . . . . . . . . . . . . . . . .");

                if (line.contains(isImportant) && !line.contains("Done! ✅")) {
                    System.out.print(ConsoleColors.YELLOW_BRIGHT + (i + 1) + ")");

                    System.out.println(ConsoleColors.RED_BOLD_BRIGHT + "\t"
                            + line.substring(0, line.indexOf(endOfTaskDescription)).toUpperCase());
                    System.out.println(ConsoleColors.WHITE_BRIGHT + "\t\t\t\t\t\t\t\t Due to "
                            + dateSubstring + "!");

                } else if (line.contains(isNotImportant) && !line.contains("Done! ✅")) {

                    System.out.print(ConsoleColors.YELLOW_BRIGHT + (i + 1) + ")");
                    System.out.println(ConsoleColors.WHITE_BRIGHT + "\t"
                            + line.substring(0, line.indexOf(endOfTaskDescription)));
                    System.out.println(ConsoleColors.WHITE_BRIGHT + "\t\t\t\t\t\t\t\t Due to "
                            + dateSubstring);
                } else if (line.contains("Done! ✅")) {
                    System.out.print(ConsoleColors.YELLOW_BRIGHT + (i + 1) + ")");
                    System.out.println(ConsoleColors.WHITE + "\t"
                            + line.substring(0, line.indexOf(endOfTaskDescription)));
                    System.out.println(ConsoleColors.WHITE_BRIGHT + "\t\t\t\t\t\t\t\t\t"
                            + dateSubstring);
                }
            }
            if (areAllTasksDone()) {
                allTasksDone();
            }
        } catch (NullPointerException e) {
            System.out.println(ConsoleColors.PURPLE_BOLD + "\t\t   You have no tasks!");

        }
        System.out.println("  ");
    }

    private static boolean areAllTasksDone() {
        int doneCounter = 0;
        boolean result = false;
        try {
            List<String> tasks = Files.readAllLines(path);
            int tasksCount = tasks.size();
            for (String task : tasks) {
                if (task.contains("Done! ✅")) {
                    doneCounter++;
                }
            }
            if (doneCounter == tasksCount) {
                result = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static File getTaskListMethod() {
        File file = new File(path.toString());
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

    private static void addTask() {
        StringBuilder task = new StringBuilder();
        String important = "";

        printPageScreen("ADD A TASK");
        System.out.println();
        System.out.println(ConsoleColors.RESET);
        System.out.println(ConsoleColors.WHITE_BRIGHT + " Task description");
        System.out.print(ConsoleColors.YELLOW_BRIGHT + " INSERT: ");
        task.append(scanner.nextLine()).append(endOfTaskDescription);

        System.out.println();
        System.out.println(ConsoleColors.WHITE_BRIGHT + " Task deadline date [DD-MM-YYYY]");
        System.out.print(ConsoleColors.YELLOW_BRIGHT + " INSERT: ");
        String date = scanner.nextLine();
        while (true) {
            if (validateDate(date)) {
                task.append(date).append(endOfDate);
                break;
            } else {
                System.out.println();
                System.out.println(ConsoleColors.RED_BACKGROUND_BRIGHT + "\t\t\t\t\tWRONG INPUT!");
                System.out.println("Type in a valid date in given format: DD-MM-YYYY");
                System.out.print(ConsoleColors.YELLOW_BRIGHT + " INSERT: ");
                date = scanner.nextLine();
            }
        }

        System.out.println();
        System.out.println(ConsoleColors.WHITE_BRIGHT + " Mark as important? [Y/N]");
        System.out.print(ConsoleColors.YELLOW_BRIGHT + " INSERT: ");

        important = scanner.nextLine();
        String cancel = "";
        while (true) {
            switch (important) {
                case "Y", "y" -> task.append(isImportant);
                case "N", "n" -> task.append(isNotImportant);
                default -> {
                    System.out.println();
                    System.out.println(ConsoleColors.RED_BACKGROUND_BRIGHT + "\t\t\t\t\tWRONG INPUT!");
                    System.out.println("Type Y to mark as important, N not to mark, or C to cancel.");
                    System.out.print(ConsoleColors.YELLOW_BRIGHT + " INSERT: ");
                    important = scanner.nextLine();
                    if (important.equals("c") || important.equals("C")) {
                        cancel = "cancel";
                    }
                }
            }
            if (cancel.equals("cancel")) {
                break;
            }
            if (task.toString().contains(isImportant) || task.toString().contains(isNotImportant)) {
                break;
            }
        }
        writeToFile(task.toString());
        System.out.println(ConsoleColors.GREEN_BACKGROUND + "\t\t\tTASK ADDED SUCCESSFULLY!");
    }

    private static boolean validateDate(String date) {
        return date.matches(regex);
    }

    private static boolean isNumberGreaterEqualZero(String input) {
        if (NumberUtils.isParsable(input)) {
            return Integer.parseInt(input) >= 0;
        }
        return false;
    }

    private static void removeTask() {
        getTaskList();
        String lineToRemove = "";
        System.out.println(" Which task do you want to remove?");
        System.out.print(ConsoleColors.YELLOW_BRIGHT + " INSERT: ");
        lineToRemove = scanner.nextLine();
        if (!isNumberGreaterEqualZero(lineToRemove)) {
            System.out.println();
            System.out.println(ConsoleColors.RED_BACKGROUND_BRIGHT + "\t\t\t\t\tWRONG INPUT!");
            System.out.println("Type in a number of a task to remove.");
            System.out.print(ConsoleColors.YELLOW_BRIGHT + " INSERT: ");
        } else {
            removeTaskMethod(Integer.parseInt(lineToRemove));
            System.out.println(ConsoleColors.BLUE_BACKGROUND + "\t\t\tTASK REMOVED SUCCESSFULLY!");
        }
    }

    private static void removeTaskMethod(int index) {
        String[] updatedTaskList = ArrayUtils.remove(readFile(), index - 1);

        try {
            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String task : updatedTaskList) {
            writeToFile(task);
        }
    }

    private static String printEntryScreen() {
        String option = "";
        System.out.println(ConsoleColors.YELLOW + "===================================================");
        System.out.print(ConsoleColors.YELLOW_BACKGROUND_BRIGHT);
        System.out.println(ConsoleColors.BLACK_BOLD + "\t\t\t\t\tTASK MANAGER");

        System.out.println(ConsoleColors.YELLOW + "===================================================");
        System.out.print(ConsoleColors.YELLOW_BOLD_BRIGHT + " Choose an option:");
        System.out.print(ConsoleColors.YELLOW_BOLD_BRIGHT + "\t\t\t\t\t\tType:");
        System.out.println();

        System.out.print(ConsoleColors.WHITE_BRIGHT + " Task list ");
        System.out.print(ConsoleColors.WHITE + "  .   .   .   .   .   .   .   ");
        System.out.print(ConsoleColors.BLUE_BRIGHT + "LS");
        System.out.println();

        System.out.print(ConsoleColors.WHITE_BRIGHT + " Add a task");
        System.out.print(ConsoleColors.WHITE + "  .   .   .   .   .   .   .  ");
        System.out.print(ConsoleColors.BLUE_BRIGHT + " ADD");
        System.out.println();

        System.out.print(ConsoleColors.WHITE_BRIGHT + " Mark a task as done");
        System.out.print(ConsoleColors.WHITE + " .   .   .   .   .  ");
        System.out.print(ConsoleColors.BLUE_BRIGHT + " MD");
        System.out.println();

        System.out.print(ConsoleColors.WHITE_BRIGHT + " Clear all done");
        System.out.print(ConsoleColors.WHITE + "  .   .   .   .   .   .  ");
        System.out.print(ConsoleColors.BLUE_BRIGHT + " CL");
        System.out.println();

        System.out.print(ConsoleColors.WHITE_BRIGHT + " Remove a task");
        System.out.print(ConsoleColors.WHITE + "   .   .   .   .   .   .  ");
        System.out.print(ConsoleColors.BLUE_BRIGHT + " RM");
        System.out.println();


        System.out.print(ConsoleColors.WHITE_BRIGHT + " Exit Task Manager");
        System.out.print(ConsoleColors.WHITE + "   .   .   .   .   .  ");
        System.out.print(ConsoleColors.BLUE_BOLD_BRIGHT + " Q");
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.print(ConsoleColors.YELLOW_BRIGHT + " INSERT: ");
        option = scanner.nextLine();

        return option;
    }

    private static void printPageScreen(String page) {
        System.out.println();
        System.out.print(ConsoleColors.YELLOW_BACKGROUND_BRIGHT);
        System.out.print(ConsoleColors.BLACK_BOLD + "\t\t\t\t\t  " + page);
    }

    private static void writeToFile(String task) {
        try {
            if (Files.notExists(path)) {
                Files.createFile(path);
            }
            String result = task.concat("\n");
            Files.writeString(path, result, StandardOpenOption.APPEND);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void markAsDone() {
        getTaskList();
        String lineToMarkAsDone = "";
        System.out.println(" Which task do you want to mark as done?");
        System.out.print(ConsoleColors.YELLOW_BRIGHT + " INSERT: ");
        lineToMarkAsDone = scanner.nextLine();

        while (true) {
            if (lineToMarkAsDone.equals("c") || lineToMarkAsDone.equals("C")) {
                break;
            }

            if (!isNumberGreaterEqualZero(lineToMarkAsDone)) {
                System.out.println();
                System.out.println(ConsoleColors.RED_BACKGROUND_BRIGHT + "\t\t\t\t\tWRONG INPUT!");
                System.out.println("Type in a number of a task to mark as done or C to cancel.");
                System.out.print(ConsoleColors.YELLOW_BRIGHT + " INSERT: ");
                lineToMarkAsDone = scanner.nextLine();
            }
            if (isTaskDone(Integer.parseInt(lineToMarkAsDone))) {
                System.out.println();
                System.out.println(ConsoleColors.BLUE_BACKGROUND + "\t\t\t\tTASK ALREADY MARKED!");
                System.out.println("Type in a number of a task to mark as done or C to cancel.");
                System.out.print(ConsoleColors.YELLOW_BRIGHT + " INSERT: ");
                lineToMarkAsDone = scanner.nextLine();
            }
            if (isNumberGreaterEqualZero(lineToMarkAsDone)
                    && !isTaskDone(Integer.parseInt(lineToMarkAsDone))) {
                markAsDoneMethod(Integer.parseInt(lineToMarkAsDone));
                System.out.println(ConsoleColors.BLUE_BACKGROUND + "\t\t\t\t\t\tDONE!");
                break;
            }
        }
    }

    private static void markAsDoneMethod(int index) {
        String[] tasks = readFile();
        String taskToMarkAsDone = tasks[index - 1];
        String[] temp = ArrayUtils.remove(tasks, index - 1);
        String[] tasksUpdated = ArrayUtils.insert(index - 1, temp,
                taskMarkDone(taskToMarkAsDone));

        try {
            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String task : tasksUpdated) {
            writeToFile(task);
        }
    }

    private static String taskMarkDone(String taskToMark) {

        return taskToMark.substring(
                        0, (taskToMark.indexOf(endOfTaskDescription) + endOfTaskDescription.length()))
                .concat("Done! ✅").concat(taskToMark
                        .substring(taskToMark.indexOf(endOfDate)));
    }

    private static boolean isTaskDone(int index) {
        String[] tasks = readFile();
        String taskToVerify = tasks[index - 1];

        return taskToVerify.contains("Done! ✅");
    }

    private static void clearAllDoneTasks() {
        try {
            List<String> lines = Files.readAllLines(path);
            int negativeCounter = 0;
            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).contains("Done! ✅")) {
                    removeTaskMethod(i + 1 - negativeCounter);
                    negativeCounter++;
                }
            }
            System.out.println(ConsoleColors.GREEN_BACKGROUND + "\t\t\t\t  TASK LIST CLEARED!");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void allTasksDone() {
        System.out.print(ConsoleColors.BLUE_BACKGROUND_BRIGHT);
        System.out.print(ConsoleColors.BLACK_BOLD + "\t\t\t\t Congratulations!\n"
                + "\t\tYou have completed all your tasks.");
    }
}