package anders.parser;
import anders.AndersException;
import anders.command.AddCommand;
import anders.command.Command;
import anders.command.DeleteCommand;
import anders.command.ExitCommand;
import anders.command.ListCommand;
import anders.command.MarkCommand;
import anders.task.Deadline;
import anders.task.Event;
import anders.task.Task;
import anders.task.Todo;
/** Validates the command formats accepted by Anders. */
public class Parser {
    /** Converts a validated command string into an executable command object. */
    public static Command parse(String command) throws AndersException {
        Parser parser = new Parser();
        parser.validate(command);
        String word = parser.commandWord(command);
        String args = parser.arguments(command);
        switch (word) {
        case "bye": return new ExitCommand();
        case "list": return new ListCommand();
        case "mark": return new MarkCommand(args, true);
        case "unmark": return new MarkCommand(args, false);
        case "delete": return new DeleteCommand(args);
        case "todo": case "deadline": case "event": return new AddCommand(parser.parseTask(command));
        default: throw new AndersException("I don't know what that means. Please try a supported command.");
        }
    }
    /** Returns the command keyword, separated from its arguments. */
    public String commandWord(String command) {
        int separator = command.indexOf(' ');
        return separator < 0 ? command : command.substring(0, separator);
    }

    /** Returns the text following the command keyword. */
    public String arguments(String command) {
        int separator = command.indexOf(' ');
        return separator < 0 ? "" : command.substring(separator + 1).trim();
    }

    /** Builds a task from a validated task-creation command. */
    public Task parseTask(String command) {
        String details = arguments(command);
        switch (commandWord(command)) {
        case "todo":
            return new Todo(details);
        case "deadline":
            int by = details.indexOf(" /by ");
            return new Deadline(details.substring(0, by).trim(),
                    details.substring(by + " /by ".length()).trim());
        case "event":
            int from = details.indexOf(" /from ");
            int to = details.indexOf(" /to ");
            return new Event(details.substring(0, from).trim(),
                    details.substring(from + " /from ".length(), to).trim(),
                    details.substring(to + " /to ".length()).trim());
        default:
            throw new IllegalArgumentException("Command does not create a task");
        }
    }
    /** Throws an exception when the command is empty, malformed, or unsupported. */
    public void validate(String command) throws AndersException {
        if (command.isEmpty()) throw new AndersException("I don't know what that means. Please enter a command.");
        if (command.equals("todo") || command.matches("todo\\s+"))
            throw new AndersException("The description of a todo cannot be empty. Please include a description!");
        if (command.equals("deadline") || command.startsWith("deadline ")
                && (!command.substring(9).contains(" /by ")
                || command.substring(9, command.indexOf(" /by ")).trim().isEmpty()
                || command.substring(command.indexOf(" /by ") + 5).trim().isEmpty()))
            throw new AndersException("A deadline needs a description and a /by value.");
        if (command.equals("event") || command.startsWith("event ")
                && !validEvent(command.substring(6)))
            throw new AndersException("An event needs a description, /from value, and /to value.");
        if (command.equals("mark") || command.matches("mark\\s+"))
            throw new AndersException("Mark needs a task number.");
        if (command.equals("unmark") || command.matches("unmark\\s+"))
            throw new AndersException("Unmark needs a task number.");
        if (command.equals("delete") || command.matches("delete\\s+"))
            throw new AndersException("Delete needs a task number.");
        boolean known = command.equals("bye") || command.equals("list")
                || command.matches("(mark|unmark|delete|todo|deadline|event)\\s+.+");
        if (!known) throw new AndersException("I don't know what that means. Please try a supported command.");
    }

    /** Checks that an event contains non-empty {@code /from} and {@code /to} values. */
    private boolean validEvent(String details) {
        int from = details.indexOf(" /from ");
        int to = details.indexOf(" /to ");
        return from > 0 && to > from
                && !details.substring(from + 7, to).trim().isEmpty()
                && !details.substring(to + 5).trim().isEmpty();
    }
}
