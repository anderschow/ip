
/**
 * The entry point for the Anders chatbot.
 *
 * <p>Tasks are kept in memory only and are lost when the program exits.</p>
 */
public class Anders {
    private static final Storage STORAGE = new Storage("data/anders.txt");
    private static final Ui UI = new Ui();
    public static void main(String[] args) {
        UI.showWelcome();

        TaskList tasks = new TaskList(STORAGE.load());
        boolean isExit = false;
        while (!isExit && UI.hasNextCommand()) {
            try {
                String fullCommand = UI.readCommand();
                UI.showSeparator();
                Command command = Parser.parse(fullCommand);
                command.execute(tasks, UI, STORAGE);
                isExit = command.isExit();
            } catch (AndersException e) {
                UI.showError(e.getMessage());
            } finally {
                UI.showSeparator();
            }
        }
    }

}
