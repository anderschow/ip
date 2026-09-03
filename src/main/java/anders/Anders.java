package anders;

import anders.collection.TaskList;
import anders.command.Command;
import anders.parser.Parser;
import anders.storage.Storage;
import anders.ui.Ui;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * The entry point for the Anders chatbot.
 *
 * <p>Tasks are kept in memory only and are lost when the program exits.</p>
 */
public class Anders {
    private final Storage storage;
    private final Ui ui;
    private final TaskList tasks;

    /** Creates an Anders session using the default storage file. */
    public Anders() {
        storage = new Storage("data/anders.txt");
        ui = new Ui();
        tasks = new TaskList(storage.load());
    }

    /** Processes one command and returns the output for display in a GUI. */
    public String getResponse(String input) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;
        try {
            System.setOut(new PrintStream(output));
            Command command = Parser.parse(input);
            command.execute(tasks, ui, storage);
            return output.toString().trim();
        } catch (AndersException e) {
            return "OOPS!!! " + e.getMessage();
        } finally {
            System.setOut(originalOutput);
        }
    }

    /** Starts Anders and processes commands until the user exits or input ends. */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage("data/anders.txt");

        TaskList tasks = new TaskList(storage.load());
        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            try {
                String fullCommand = ui.readCommand();
                ui.showSeparator();
                Command command = Parser.parse(fullCommand);
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (AndersException e) {
                ui.showError(e.getMessage());
            } finally {
                ui.showSeparator();
            }
        }
    }

}
