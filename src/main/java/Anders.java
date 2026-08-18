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

        String[] tasks = new String[100];
        boolean[] completed = new boolean[100];
        int taskCount = 0;
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String command = scanner.nextLine();
            System.out.println(separator);

            if (command.equals("bye")) {
                System.out.println("     Bye! Keep learning, and see you again soon!");
                System.out.println(separator);
                break;
            }

            if (command.equals("list")) {
                System.out.println("     Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    String status = completed[i] ? "X" : " ";
                    System.out.println("     " + (i + 1) + ".[" + status + "] " + tasks[i]);
                }
            } else if (command.startsWith("mark ")) {
                String taskNumber = command.substring("mark ".length()).trim();
                try {
                    int taskIndex = Integer.parseInt(taskNumber) - 1;
                    if (taskIndex >= 0 && taskIndex < taskCount) {
                        completed[taskIndex] = true;
                        System.out.println("     Nice! I've marked this task as done:");
                        System.out.println("       [X] " + tasks[taskIndex]);
                    } else {
                        System.out.println("     Task number must be between 1 and " + taskCount + ".");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("     Please provide a valid task number.");
                }
            } else if (taskCount < tasks.length) {
                tasks[taskCount] = command;
                taskCount++;
                System.out.println("     added: " + command);
            } else {
                System.out.println("     You have reached the maximum of 100 tasks.");
            }
            System.out.println(separator);
        }
    }
}
