package pl.coderslab;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Scanner;

public class TaskManager {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Path path = Paths.get("tasklist/tasks.csv");
    private static final String isImportant = "$$$_is_important_$$$";
    private static final String isNotImportant = "$$$_is_not_important_$$$";
    private static final String endOfTaskDescription = "$$$_end_of_task_description_$$$";
    private static final String endOfDate = "$$$_end_of_date_$$$";
    private static final String regex = "^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\" +
            "/|-|\\.)(?:0?[13-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\" +
            "/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|" +
            "(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\" +
            ".)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$";


    public static void main(String[] args) {
        startTaskManager();
    }

    public static void startTaskManager() {
        while (true) {
            switch (printEntryScreen()) {
                case "ADD", "add" -> addTask();
                case "RM", "rm" -> removeTask();
                case "LS", "ls" -> getTaskList();
                case "Q", "q" -> {
                    scanner.close();
                    System.exit(0);
                }
                default -> {
                    System.out.println();
                    System.out.println(ConsoleColors.RED_BACKGROUND_BRIGHT + "\t\t\t\tWRONG INPUT!");
                }
            }
        }
    }

    public static void addTask() {
        String[] task = new String[3];
        String important = "";

        printPageScreen("ADD A TASK");
        System.out.println();
        System.out.println(ConsoleColors.RESET);
        System.out.println(ConsoleColors.WHITE_BRIGHT + "\tTask description");
        System.out.print(ConsoleColors.YELLOW_BRIGHT + "\tINSERT: ");
        task[0] = scanner.nextLine() + endOfTaskDescription;

        System.out.println();
        System.out.println(ConsoleColors.WHITE_BRIGHT + "\tTask deadline date [DD-MM-YYYY]");
        System.out.print(ConsoleColors.YELLOW_BRIGHT + "\tINSERT: ");
        String date = scanner.nextLine();
        while (true) {
            if (validateDate(date)) {
                task[1] = date + endOfDate;
                break;
            } else {
                System.out.println();
                System.out.println(ConsoleColors.RED_BACKGROUND_BRIGHT + "\t\t\t\tWRONG INPUT!");
                System.out.println("Type in a valid date in given format: DD-MM-YYYY");
                System.out.print(ConsoleColors.YELLOW_BRIGHT + "\tINSERT: ");
                date = scanner.nextLine();
            }
        }

        System.out.println();
        System.out.println(ConsoleColors.WHITE_BRIGHT + "\tMark as important? [Y/N]");
        System.out.print(ConsoleColors.YELLOW_BRIGHT + "\tINSERT: ");

        important = scanner.nextLine();
        String cancel = "";
        while (true) {
            switch (important) {
                case "Y", "y" -> task[2] = isImportant;
                case "N", "n" -> task[2] = isNotImportant;
                default -> {
                    System.out.println();
                    System.out.println(ConsoleColors.RED_BACKGROUND_BRIGHT + "\t\t\t\tWRONG INPUT!");
                    System.out.println("Type Y to mark as important, N not to mark, or C to cancel.");
                    System.out.print(ConsoleColors.YELLOW_BRIGHT + "\tINSERT: ");
                    important = scanner.nextLine();
                    if (important.equals("c") || important.equals("C")) {
                        cancel = "cancel";
                        break;
                    }
                }
            }
            if (cancel.equals("cancel")) {
                startTaskManager();
            }
            if (task[2] == isImportant || task[2] == isNotImportant) {
                break;
            }
        }
        writeToFile(task);
        System.out.println(ConsoleColors.GREEN_BACKGROUND + "\t\t\tTASK ADDED SUCCESSFULLY!");
    }

    private static boolean validateDate(String date) {

        if (date.matches(regex)) return true;
        return false;
    }

    public static void removeTask() {
        getTaskList();
    }

    public static void stolenRemoveLineFromFile(String lineToRemove) {

        try {

            File inFile = new File(String.valueOf(path));

            if (!inFile.isFile()) {
                System.out.println("No such file!");
                return;
            }

            File tempFile = new File(inFile.getAbsolutePath() + ".tmp");

            BufferedReader br = new BufferedReader(new FileReader(String.valueOf(path)));
            PrintWriter pw = new PrintWriter(new FileWriter(tempFile));

            String line = null;

            while ((line = br.readLine()) != null) {

                if (!line.trim().contains(lineToRemove)) {

                    pw.println(line);
                    pw.flush();
                }
            }
            pw.close();
            br.close();

            if (!inFile.delete()) {
                System.out.println("Could not delete file");
                return;
            }

            if (!tempFile.renameTo(inFile))
                System.out.println("Could not rename file");

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void removeLineFromFile(String lineToRemove) {
        ArrayList<String> lines = new ArrayList<>();

        try {
            for (String line : Files.readAllLines(path)) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            int lineNumber = Integer.parseInt(lineToRemove);
            if (lineNumber > lines.size() || lineNumber < 1 ) {
                System.out.println("Wrong value!");
                System.out.println("Insert index number of task to remove");
            } else {
                lines.remove(lineNumber);
                try {
                    for (String line : lines) {
                        Files.writeString(path, line);
                    }
                } catch (IOException ex) {
                    System.out.println("Nie można zapisać pliku.");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Insert index number of task to remove");
        }


    }


    public static void getTaskList() {
        printPageScreen("TASKS");
        System.out.println("");
        ArrayList<String> lines = new ArrayList<>();
        try {
            for (String line : Files.readAllLines(path)) {
                lines.add(line);
            }
            if (lines.isEmpty()) {
                System.out.println();
                System.out.println();
                System.out.println(ConsoleColors.PURPLE_BOLD + "\t\t   You have no tasks!");
            }

            for (int i = 0; i < lines.size(); i++) {
//                System.out.println(ConsoleColors.RESET);

                if (lines.get(i).contains(isImportant)) {
                    if (i > 0) {
                        System.out.println("\n. . . . . . . . . . . . . . . . . . . . . . . . . .");
                    } else System.out.println();
                    System.out.print(ConsoleColors.YELLOW_BRIGHT + (i + 1) + ")");
                    System.out.println(ConsoleColors.RED_BRIGHT + "\t"
                            + lines.get(i).substring(0, lines.get(i).indexOf(endOfTaskDescription)).toUpperCase());
                    System.out.print(ConsoleColors.WHITE_BRIGHT + "\t\t\t\t\t\t\t\tDue to "
                            + lines.get(i).substring(
                            lines.get(i).indexOf(endOfTaskDescription) + 2
                                    + endOfTaskDescription.length(), lines.get(i).indexOf(endOfDate)) + "!");

                } else if (lines.get(i).contains(isNotImportant)) {
                    if (i > 0) {
                        System.out.println("\n. . . . . . . . . . . . . . . . . . . . . . . . . .");
                    } else System.out.println();
                    System.out.print(ConsoleColors.YELLOW_BRIGHT + (i + 1) + ")");
                    System.out.println(ConsoleColors.WHITE_BRIGHT + "\t"
                            + lines.get(i).substring(0, lines.get(i).indexOf(endOfTaskDescription)));
                    System.out.print(ConsoleColors.WHITE_BRIGHT + "\t\t\t\t\t\t\t\tDue to "
                            + lines.get(i).substring(lines.get(i).indexOf(endOfTaskDescription) + 2
                            + endOfTaskDescription.length(), lines.get(i).indexOf(endOfDate)));
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
        System.out.println();
        System.out.println(ConsoleColors.YELLOW_BRIGHT + "\tType Q to go back to menu, \n\tR to remove a task");
        System.out.print(ConsoleColors.YELLOW_BRIGHT + "\tINSERT: ");
        String option = scanner.nextLine();
        while (true) {
            if (option.equals("Q") || option.equals("q")) {
                break;
            } else if (option.equals("R") || option.equals("r")) {
                if (lines.isEmpty()) {
                    System.out.println(ConsoleColors.YELLOW_BRIGHT + "\tNothing to remove! \n\tType Q to go back to menu");
                    System.out.print(ConsoleColors.YELLOW_BRIGHT + "\tINSERT: ");
                    option = scanner.nextLine();

                } else {
                    System.out.println("Which task do You want to remove?");
                    System.out.print(ConsoleColors.YELLOW_BRIGHT + "\tINSERT: ");
                    String lineToRemove = scanner.nextLine();
                    removeLineFromFile(lineToRemove);

                    System.out.println(ConsoleColors.BLUE_BACKGROUND + "\t\t\tTASK SUCCESFULLY REMOVED");
                    break;
                }
            } else {
                System.out.println(ConsoleColors.RED_BACKGROUND_BRIGHT + "\t\t\t\tWRONG INPUT!");
                System.out.println(ConsoleColors.YELLOW_BRIGHT + "\tType R to remove a task, \n\tQ to go back to menu");
                System.out.print(ConsoleColors.YELLOW_BRIGHT + "\tINSERT: ");
                option = scanner.nextLine();
            }
        }

    }

    public static String printEntryScreen() {
        String option = "";
        System.out.println(ConsoleColors.YELLOW + "==============================================");
        System.out.print(ConsoleColors.YELLOW_BACKGROUND_BRIGHT);
        System.out.println(ConsoleColors.BLACK_BOLD + "\t\t\t\tTASK MANAGER");
//        System.out.println();
        System.out.println(ConsoleColors.YELLOW + "==============================================");
        System.out.print(ConsoleColors.YELLOW_BOLD_BRIGHT + "\tChoose an option:");
        System.out.print(ConsoleColors.YELLOW_BOLD_BRIGHT + "\t\t\t\tType:");
        System.out.println();

        System.out.print(ConsoleColors.WHITE_BRIGHT + "\tTask list ");
        System.out.print(ConsoleColors.BLUE + "\t\t\t\t\t\tLS");
        System.out.println();

        System.out.print(ConsoleColors.WHITE_BRIGHT + "\tAdd a task ");
        System.out.print(ConsoleColors.BLUE + "\t\t\t\t\t\tADD");
        System.out.println();

        System.out.print(ConsoleColors.WHITE_BRIGHT + "\tRemove a task ");
        System.out.print(ConsoleColors.BLUE + "\t\t\t\t\tRM");
        System.out.println();

        System.out.print(ConsoleColors.WHITE_BRIGHT + "\tExit Task Manager ");
        System.out.print(ConsoleColors.BLUE + "\t\t\t\tQ");
        System.out.println();
        System.out.println();
        System.out.print(ConsoleColors.YELLOW_BRIGHT + "\tINSERT: ");
        option = scanner.nextLine();

        return option;
    }

    public static void printPageScreen(String page) {
        System.out.println();

        System.out.print(ConsoleColors.YELLOW_BACKGROUND_BRIGHT);
        System.out.print(ConsoleColors.BLACK_BOLD + "\t\t\t\t  " + page);

    }

    public static void writeToFile(String[] task) {
        try {
            if (Files.notExists(path)) {
                Files.createFile(path);
            } else {
                String result = (String.join(", ", task)).concat("\n");
                Files.writeString(path, result, StandardOpenOption.APPEND);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
