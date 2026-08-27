
/**
 * The entry point for the Anders chatbot.
 *
 * <p>Tasks are kept in memory only and are lost when the program exits.</p>
 */
public class Anders {
    private static final Storage STORAGE = new Storage("data/anders.txt");
    private static final Parser PARSER = new Parser();
    private static final Ui UI = new Ui();
    public static void main(String[] args) {
        UI.showWelcome();

        TaskList tasks = new TaskList(STORAGE.load());
        while (true) {
            if (!UI.hasNextCommand()) {
                break;
            }
            String command = UI.readCommand();
            UI.showSeparator();

            try {
                PARSER.validate(command);
            } catch (AndersException e) {
                UI.showError(e.getMessage());
                UI.showSeparator();
                continue;
            }

            String commandWord = PARSER.commandWord(command);
            if (commandWord.equals("bye")) {
                UI.showGoodbye();
                UI.showSeparator();
                break;
            }

            if (commandWord.equals("list")) {
                UI.showTaskList(tasks);
            } else if (commandWord.equals("mark")) {
                String taskNumber = PARSER.arguments(command);
                try {
                    int taskIndex = Integer.parseInt(taskNumber) - 1;
                    if (taskIndex >= 0 && taskIndex < tasks.size()) {
                        tasks.get(taskIndex).markAsDone();
                        saveTasks(tasks);
                        UI.showMarked(tasks.get(taskIndex), true);
                    } else {
                        UI.showInvalidTaskNumber(tasks.size());
                    }
                } catch (NumberFormatException e) {
                    UI.showInvalidTaskNumberFormat();
                }
            } else if (commandWord.equals("unmark")) {
                String taskNumber = PARSER.arguments(command);
                try {
                    int taskIndex = Integer.parseInt(taskNumber) - 1;
                    if (taskIndex >= 0 && taskIndex < tasks.size()) {
                        tasks.get(taskIndex).markAsNotDone();
                        saveTasks(tasks);
                        UI.showMarked(tasks.get(taskIndex), false);
                    } else {
                        UI.showInvalidTaskNumber(tasks.size());
                    }
                } catch (NumberFormatException e) {
                    UI.showInvalidTaskNumberFormat();
                }
            } else if (commandWord.equals("delete")) {
                deleteTask(tasks, PARSER.arguments(command));
            } else if (commandWord.equals("todo") || commandWord.equals("deadline")
                    || commandWord.equals("event")) {
                tasks.add(PARSER.parseTask(command));
                saveTasks(tasks);
                UI.showTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
            }
            UI.showSeparator();
        }
    }

    /** Deletes the task at the one-based number supplied by the user. */
    private     static void deleteTask(TaskList tasks, String taskNumber) {
        try {
            int taskIndex = Integer.parseInt(taskNumber) - 1;
            if (taskIndex >= 0 && taskIndex < tasks.size()) {
                Task removedTask = tasks.remove(taskIndex);
                saveTasks(tasks);
                UI.showDeleted(removedTask, tasks.size());
            } else {
                UI.showInvalidTaskNumber(tasks.size());
            }
        } catch (NumberFormatException e) {
            UI.showInvalidTaskNumberFormat();
        }
    }

    /** Writes the current task list to the relative save file. */
    private static void saveTasks(TaskList tasks) {
        STORAGE.save(tasks);
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
