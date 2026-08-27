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
}
