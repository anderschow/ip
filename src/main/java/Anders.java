import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.AtomicMoveNotSupportedException;
import java.util.Base64;

/**
 * The entry point for the Anders chatbot.
 *
 * <p>Tasks are kept in memory only and are lost when the program exits.</p>
 */
public class Anders {
    public static void main(String[] args) {
        String banner = "    _                 _                \n"
                + "   / \\   _ __   __| | ___ _ __ ___\n"
                + "  / _ \\ | '_ \\ / _` |/ _ \\ '__/ __|\n"
                + " / ___ \\| | | | (_| |  __/ |  \\__ \\\n"
                + "/_/   \\_\\_| |_|\\__,_|\\___|_|  |___/";
        String separator = "____________________________________________________________";

        System.out.println(separator);
        System.out.println(banner);
        System.out.println("Hello! I'm Anders, your friendly study companion.");
        System.out.println("What can I do for you today?");
        System.out.println(separator);

        List<Task> tasks = loadTasks();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            if (!scanner.hasNextLine()) {
                break;
            }
            String command = scanner.nextLine().trim();
            System.out.println(separator);

            try {
                validateCommand(command);
            } catch (AndersException e) {
                System.out.println("     OOPS!!! " + e.getMessage());
                System.out.println(separator);
                continue;
            }

            if (command.equals("bye")) {
                System.out.println("     Bye! Keep learning, and see you again soon!");
                System.out.println(separator);
                break;
            }

            if (command.equals("list")) {
                System.out.println("     Here are the tasks in your list:");
                for (int i = 0; i < tasks.size(); i++) {
                    System.out.println("     " + (i + 1) + "." + formatTask(tasks.get(i)));
                }
            } else if (command.startsWith("mark ")) {
                String taskNumber = command.substring("mark ".length()).trim();
                try {
                    int taskIndex = Integer.parseInt(taskNumber) - 1;
                    if (taskIndex >= 0 && taskIndex < tasks.size()) {
                        tasks.get(taskIndex).markAsDone();
                        saveTasks(tasks);
                        System.out.println("     Nice! I've marked this task as done:");
                        System.out.println("       [X] " + tasks.get(taskIndex).getDescription());
                    } else {
                        System.out.println("     Task number must be between 1 and " + tasks.size() + ".");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("     Please provide a valid task number.");
                }
            } else if (command.startsWith("unmark ")) {
                String taskNumber = command.substring("unmark ".length()).trim();
                try {
                    int taskIndex = Integer.parseInt(taskNumber) - 1;
                    if (taskIndex >= 0 && taskIndex < tasks.size()) {
                        tasks.get(taskIndex).markAsNotDone();
                        saveTasks(tasks);
                        System.out.println("     OK, I've marked this task as not done yet:");
                        System.out.println("       [ ] " + tasks.get(taskIndex).getDescription());
                    } else {
                        System.out.println("     Task number must be between 1 and " + tasks.size() + ".");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("     Please provide a valid task number.");
                }
            } else if (command.startsWith("delete ")) {
                deleteTask(tasks, command.substring("delete ".length()).trim());
            } else if (command.startsWith("todo ")) {
                tasks.add(new Todo(command.substring("todo ".length()).trim()));
                saveTasks(tasks);
                printTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
            } else if (command.startsWith("deadline ")) {
                String taskDetails = command.substring("deadline ".length()).trim();
                int bySeparator = taskDetails.indexOf(" /by ");
                if (bySeparator > 0) {
                    String description = taskDetails.substring(0, bySeparator).trim();
                    String by = taskDetails.substring(bySeparator + " /by ".length()).trim();
                    tasks.add(new Deadline(description, by));
                    saveTasks(tasks);
                    printTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
                } else {
                    System.out.println("     Deadline tasks must include /by.");
                }
            } else if (command.startsWith("event ")) {
                String taskDetails = command.substring("event ".length()).trim();
                int fromSeparator = taskDetails.indexOf(" /from ");
                int toSeparator = taskDetails.indexOf(" /to ");
                if (fromSeparator > 0 && toSeparator > fromSeparator) {
                    String description = taskDetails.substring(0, fromSeparator).trim();
                    String from = taskDetails.substring(fromSeparator + " /from ".length(), toSeparator).trim();
                    String to = taskDetails.substring(toSeparator + " /to ".length()).trim();
                    tasks.add(new Event(description, from, to));
                    saveTasks(tasks);
                    printTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
                } else {
                    System.out.println("     Event tasks must include /from and /to.");
                }
            } else {
                tasks.add(new Todo(command));
                printTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
            }
            System.out.println(separator);
        }
    }

    /** Deletes the task at the one-based number supplied by the user. */
    private static void deleteTask(List<Task> tasks, String taskNumber) {
        try {
            int taskIndex = Integer.parseInt(taskNumber) - 1;
            if (taskIndex >= 0 && taskIndex < tasks.size()) {
                Task removedTask = tasks.remove(taskIndex);
                saveTasks(tasks);
                System.out.println("     Noted. I've removed this task:");
                System.out.println("       " + formatTask(removedTask));
                System.out.println("     Now you have " + tasks.size() + " tasks in the list.");
            } else {
                System.out.println("     Task number must be between 1 and " + tasks.size() + ".");
            }
        } catch (NumberFormatException e) {
            System.out.println("     Please provide a valid task number.");
        }
    }

    private static final Path SAVE_FILE = Paths.get("data", "anders.txt");

    /** Loads saved tasks when the save file exists; otherwise starts with an empty list. */
    private static List<Task> loadTasks() {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(SAVE_FILE)) {
            return tasks;
        }
        try {
            for (String line : Files.readAllLines(SAVE_FILE, StandardCharsets.UTF_8)) {
                Task task = parseTask(line);
                if (task != null) {
                    tasks.add(task);
                }
            }
        } catch (IOException | SecurityException e) {
            System.out.println("     OOPS!!! I could not load the saved tasks.");
        }
        return tasks;
    }

    /** Writes the current task list to the relative save file. */
    private static void saveTasks(List<Task> tasks) {
        List<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            String type = task instanceof Deadline ? "D" : task instanceof Event ? "E" : "T";
            String line = "2|" + type + "|" + (task.isDone() ? "1" : "0") + "|"
                    + encode(task.getDescription());
            if (task instanceof Deadline deadline) {
                line += "|" + encode(deadline.getByText());
            } else if (task instanceof Event event) {
                line += "|" + encode(event.getFromText()) + "|" + encode(event.getToText());
            }
            lines.add(line);
        }
        try {
            Files.createDirectories(SAVE_FILE.getParent());
            Path temporaryFile = SAVE_FILE.resolveSibling(SAVE_FILE.getFileName() + ".tmp");
            Files.write(temporaryFile, lines, StandardCharsets.UTF_8);
            try {
                Files.move(temporaryFile, SAVE_FILE, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporaryFile, SAVE_FILE, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException | SecurityException e) {
            System.out.println("     OOPS!!! I could not save the tasks.");
        }
    }

    /** Parses one versioned save line, or the original human-readable format. */
    private static Task parseTask(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        try {
            String[] fields = line.split("\\|", -1);
            if (fields.length >= 4 && fields[0].equals("2")) {
                String type = fields[1];
                boolean done = parseStatus(fields[2]);
                String description = decode(fields[3]);
                Task task;
                if (type.equals("T") && fields.length == 4) {
                    task = new Todo(description);
                } else if (type.equals("D") && fields.length == 5) {
                    task = new Deadline(description, decode(fields[4]));
                } else if (type.equals("E") && fields.length == 6) {
                    task = new Event(description, decode(fields[4]), decode(fields[5]));
                } else {
                    return null;
                }
                if (done) {
                    task.markAsDone();
                }
                return task;
            }
            String[] legacy = line.split("\\s*\\|\\s*", -1);
            if (legacy.length < 3 || !(legacy[1].equals("0") || legacy[1].equals("1"))) {
                return null;
            }
            Task task = legacy[0].equals("T") && legacy.length == 3 ? new Todo(legacy[2])
                    : legacy[0].equals("D") && legacy.length == 4 ? new Deadline(legacy[2], legacy[3])
                    : legacy[0].equals("E") && legacy.length == 5
                    ? new Event(legacy[2], legacy[3], legacy[4]) : null;
            if (task != null && legacy[1].equals("1")) {
                task.markAsDone();
            }
            return task;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static boolean parseStatus(String status) {
        if (!status.equals("0") && !status.equals("1")) {
            throw new IllegalArgumentException("Invalid completion status");
        }
        return status.equals("1");
    }

    private static String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private static String decode(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }

    /** Returns the task's type, completion status, and display text. */
    private static String formatTask(Task task) {
        String typeIcon = task instanceof Deadline ? "D" : task instanceof Event ? "E" : "T";
        String taskText = task.toString();
        return "[" + typeIcon + "][" + task.getStatusIcon() + "] " + taskText.substring(4);
    }

    /** Prints the confirmation shown after adding a task. */
    private static void printTaskAdded(Task task, int taskCount) {
        System.out.println("     Got it. I've added this task:");
        System.out.println("       " + task);
        System.out.println("     Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Validates the command forms that require a description or a command keyword.
     *
     * @param command the raw user command
     * @throws AndersException when the command is not supported or is incomplete
     */
    private static void validateCommand(String command) throws AndersException {
        if (command.isEmpty()) {
            throw new AndersException("I don't know what that means. Please enter a command.");
        }
        if (command.equals("todo") || (command.startsWith("todo ")
                && command.substring("todo ".length()).trim().isEmpty())) {
            throw new AndersException("The description of a todo cannot be empty. Please include a description!");
        }
        if (command.equals("deadline")) {
            throw new AndersException("A deadline needs a description and a /by value.");
        }
        if (command.startsWith("deadline ")) {
            String details = command.substring("deadline ".length()).trim();
            int bySeparator = details.indexOf(" /by ");
            if (bySeparator <= 0 || details.substring(bySeparator + " /by ".length()).trim().isEmpty()) {
                throw new AndersException("A deadline needs a description and a /by value.");
            }
        }
        if (command.equals("event")) {
            throw new AndersException("An event needs a description, /from value, and /to value.");
        }
        if (command.startsWith("event ")) {
            String details = command.substring("event ".length()).trim();
            int fromSeparator = details.indexOf(" /from ");
            int toSeparator = details.indexOf(" /to ");
            if (fromSeparator <= 0 || toSeparator <= fromSeparator
                    || details.substring(fromSeparator + " /from ".length(), toSeparator).trim().isEmpty()
                    || details.substring(toSeparator + " /to ".length()).trim().isEmpty()) {
                throw new AndersException("An event needs a description, /from value, and /to value.");
            }
        }
        if (command.equals("mark") || (command.startsWith("mark ")
                && command.substring("mark ".length()).trim().isEmpty())) {
            throw new AndersException("Mark needs a task number.");
        }
        if (command.equals("unmark") || (command.startsWith("unmark ")
                && command.substring("unmark ".length()).trim().isEmpty())) {
            throw new AndersException("Unmark needs a task number.");
        }
        if (command.equals("delete") || (command.startsWith("delete ")
                && command.substring("delete ".length()).trim().isEmpty())) {
            throw new AndersException("Delete needs a task number.");
        }
        boolean knownCommand = command.equals("bye") || command.equals("list")
                || command.startsWith("mark ") || command.startsWith("unmark ")
                || command.startsWith("delete ")
                || command.startsWith("todo ") || command.startsWith("deadline ")
                || command.startsWith("event ");
        if (!knownCommand) {
            throw new AndersException("I don't know what that means. Please try a supported command.");
        }
    }
}
