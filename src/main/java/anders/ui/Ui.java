package anders.ui;

import anders.collection.TaskList;
import anders.task.Deadline;
import anders.task.Event;
import anders.task.Task;

import java.util.Locale;
import java.util.Scanner;

/** Handles console input for Anders. */
public class Ui {
    private final Scanner scanner;

    /** Creates a console user interface reading from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Returns whether another command is available. */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** Reads and trims the next user command. */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /** Prints the startup greeting. */
    public void showWelcome() {
        showSeparator();
        System.out.println("    _                 _                \n"
                + "   / \\   _ __   _| | ___ _ __ ___\n"
                + "  / _ \\ | '_ \\ / _` |/ _ \\ '__/ __|\n"
                + " / ___ \\| | | | (_| |  __/ |  \\__ \\\n"
                + "/_/   \\_\\_| |_|\\__,_|\\___|_|  |___/");
        System.out.println("Hello! I'm Anders, your friendly study companion.");
        System.out.println("What can I do for you today?");
        showSeparator();
    }

    /** Prints the command separator. */
    public void showSeparator() {
        System.out.println("____________________________________________________________");
    }

    /** Prints an error message. */
    public void showError(String message) {
        System.out.println("     OOPS!!! " + message);
    }

    /** Prints a goodbye message. */
    public void showGoodbye() {
        System.out.println("     Bye! Keep learning, and see you again soon!");
    }

    /** Displays all tasks in the list. */
    public void showTaskList(TaskList tasks) {
        System.out.println("     Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println("     " + (i + 1) + "." + formatTask(tasks.get(i)));
        }
    }

    /** Displays tasks whose descriptions contain the supplied keyword, or a no-match message. */
    public void showMatchingTasks(TaskList tasks, String keyword) {
        System.out.println("     Here are the matching tasks in your list:");
        String searchText = keyword.toLowerCase(Locale.ROOT);
        boolean hasMatch = false;
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.getDescription().toLowerCase(Locale.ROOT).contains(searchText)) {
                hasMatch = true;
                System.out.println("     " + (i + 1) + "." + formatTask(task));
            }
        }
        if (!hasMatch) {
            System.out.println("     No matching tasks found.");
        }
    }

    /** Displays a task-added confirmation. */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println("     Got it. I've added this task:");
        System.out.println("       " + task);
        System.out.println("     Now you have " + taskCount + " tasks in the list.");
    }

    /** Displays a task status change confirmation. */
    public void showMarked(Task task, boolean done) {
        System.out.println(done ? "     Nice! I've marked this task as done:"
                : "     OK, I've marked this task as not done yet:");
        System.out.println("       [" + (done ? "X" : " ") + "] " + task.getDescription());
    }

    /** Displays a deletion confirmation. */
    public void showDeleted(Task task, int remaining) {
        System.out.println("     Noted. I've removed this task:");
        System.out.println("       " + formatTask(task));
        System.out.println("     Now you have " + remaining + " tasks in the list.");
    }

    /** Displays an invalid task-number message. */
    public void showInvalidTaskNumber(int count) {
        System.out.println("     Task number must be between 1 and " + count + ".");
    }

    /** Displays a non-numeric task-number message. */
    public void showInvalidTaskNumberFormat() {
        System.out.println("     Please provide a valid task number.");
    }

    /** Formats a task with its type and completion icons for console display. */
    private String formatTask(Task task) {
        String typeIcon = task instanceof Deadline ? "D" : task instanceof Event ? "E" : "T";
        String taskText = task.toString();
        return "[" + typeIcon + "][" + task.getStatusIcon() + "] " + taskText.substring(4);
    }
}
