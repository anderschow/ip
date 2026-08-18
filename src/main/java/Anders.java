import java.util.Scanner;

/**
 * The entry point for the Anders chatbot.
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

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String command = scanner.nextLine();
            System.out.println(separator);

            if (command.equals("bye")) {
                System.out.println("     Bye! Keep learning, and see you again soon!");
                System.out.println(separator);
                break;
            }

            System.out.println("     " + command);
            System.out.println(separator);
        }
    }
}
