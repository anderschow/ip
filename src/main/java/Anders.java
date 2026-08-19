import java.util.Scanner;

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

        Task[] tasks = new Task[100];
        int taskCount = 0;
        Scanner scanner = new Scanner(System.in);
        while (true) {
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
                for (int i = 0; i < taskCount; i++) {
                    System.out.println("     " + (i + 1) + "." + formatTask(tasks[i]));
                }
            } else if (command.startsWith("mark ")) {
                String taskNumber = command.substring("mark ".length()).trim();
                try {
                    int taskIndex = Integer.parseInt(taskNumber) - 1;
                    if (taskIndex >= 0 && taskIndex < taskCount) {
                        tasks[taskIndex].markAsDone();
                        System.out.println("     Nice! I've marked this task as done:");
                        System.out.println("       [X] " + tasks[taskIndex].getDescription());
                    } else {
                        System.out.println("     Task number must be between 1 and " + taskCount + ".");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("     Please provide a valid task number.");
                }
            } else if (command.startsWith("unmark ")) {
                String taskNumber = command.substring("unmark ".length()).trim();
                try {
                    int taskIndex = Integer.parseInt(taskNumber) - 1;
                    if (taskIndex >= 0 && taskIndex < taskCount) {
                        tasks[taskIndex].markAsNotDone();
                        System.out.println("     OK, I've marked this task as not done yet:");
                        System.out.println("       [ ] " + tasks[taskIndex].getDescription());
                    } else {
                        System.out.println("     Task number must be between 1 and " + taskCount + ".");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("     Please provide a valid task number.");
                }
            } else if (taskCount < tasks.length && command.startsWith("todo ")) {
                tasks[taskCount] = new Todo(command.substring("todo ".length()).trim());
                printTaskAdded(tasks[taskCount], taskCount + 1);
                taskCount++;
            } else if (taskCount < tasks.length && command.startsWith("deadline ")) {
                String taskDetails = command.substring("deadline ".length()).trim();
                int bySeparator = taskDetails.indexOf(" /by ");
                if (bySeparator > 0) {
                    String description = taskDetails.substring(0, bySeparator).trim();
                    String by = taskDetails.substring(bySeparator + " /by ".length()).trim();
                    tasks[taskCount] = new Deadline(description, by);
                    printTaskAdded(tasks[taskCount], taskCount + 1);
                    taskCount++;
                } else {
                    System.out.println("     Deadline tasks must include /by.");
                }
            } else if (taskCount < tasks.length && command.startsWith("event ")) {
                String taskDetails = command.substring("event ".length()).trim();
                int fromSeparator = taskDetails.indexOf(" /from ");
                int toSeparator = taskDetails.indexOf(" /to ");
                if (fromSeparator > 0 && toSeparator > fromSeparator) {
                    String description = taskDetails.substring(0, fromSeparator).trim();
                    String from = taskDetails.substring(fromSeparator + " /from ".length(), toSeparator).trim();
                    String to = taskDetails.substring(toSeparator + " /to ".length()).trim();
                    tasks[taskCount] = new Event(description, from, to);
                    printTaskAdded(tasks[taskCount], taskCount + 1);
                    taskCount++;
                } else {
                    System.out.println("     Event tasks must include /from and /to.");
                }
            } else if (taskCount < tasks.length) {
                tasks[taskCount] = new Todo(command);
                taskCount++;
                printTaskAdded(tasks[taskCount - 1], taskCount);
            } else {
                System.out.println("     You have reached the maximum of 100 tasks.");
            }
            System.out.println(separator);
        }
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
        boolean knownCommand = command.equals("bye") || command.equals("list")
                || command.startsWith("mark ") || command.startsWith("unmark ")
                || command.startsWith("todo ") || command.startsWith("deadline ")
                || command.startsWith("event ");
        if (!knownCommand) {
            throw new AndersException("I don't know what that means. Please try a supported command.");
        }
    }
}
