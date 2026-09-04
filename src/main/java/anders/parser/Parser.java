package anders.parser;

import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import anders.AndersException;
import anders.command.AddCommand;
import anders.command.Command;
import anders.command.DeleteCommand;
import anders.command.ExitCommand;
import anders.command.FindCommand;
import anders.command.ListCommand;
import anders.command.MarkCommand;
import anders.task.Deadline;
import anders.task.Event;
import anders.task.Task;
import anders.task.Todo;

/** Validates the command formats accepted by Anders. */
public class Parser {
    private static final Pattern DEADLINE_PATTERN = Pattern.compile("(.+?)\\s+/by\\s+(.+)");
    private static final Pattern EVENT_PATTERN = Pattern.compile("(.+?)\\s+/from\\s+(.+?)\\s+/to\\s+(.+)");

    /** Converts a validated command string into an executable command object. */
    public static Command parse(String command) throws AndersException {
        Parser parser = new Parser();
        parser.validate(command);
        String word = parser.commandWord(command);
        String args = parser.arguments(command);
        switch (word) {
            case "bye":
                return new ExitCommand();
            case "list":
                return new ListCommand();
            case "mark":
                return new MarkCommand(args, true);
            case "unmark":
                return new MarkCommand(args, false);
            case "delete":
                return new DeleteCommand(args);
            case "find":
                return new FindCommand(args);
            case "todo":
            case "deadline":
            case "event":
                return new AddCommand(parser.parseTask(command));
            default:
                throw new AndersException("I don't know what that means. Please try a supported command.");
        }
    }
    /** Returns the command keyword, separated from its arguments. */
    public String commandWord(String command) {
        String trimmedCommand = command.trim();
        int separator = firstWhitespaceIndex(trimmedCommand);
        return separator < 0 ? trimmedCommand : trimmedCommand.substring(0, separator);
    }

    /** Returns the text following the command keyword. */
    public String arguments(String command) {
        String trimmedCommand = command.trim();
        int separator = firstWhitespaceIndex(trimmedCommand);
        return separator < 0 ? "" : trimmedCommand.substring(separator).trim();
    }

    /** Builds a task from a validated task-creation command. */
    public Task parseTask(String command) {
        String details = arguments(command);
        switch (commandWord(command)) {
            case "todo":
                return new Todo(details);
            case "deadline": {
                Matcher deadlineMatcher = DEADLINE_PATTERN.matcher(details);
                if (!deadlineMatcher.matches()) {
                    throw new IllegalArgumentException("Invalid deadline format");
                }
                return new Deadline(deadlineMatcher.group(1).trim(), deadlineMatcher.group(2).trim());
            }
            case "event": {
                Matcher eventMatcher = EVENT_PATTERN.matcher(details);
                if (!eventMatcher.matches()) {
                    throw new IllegalArgumentException("Invalid event format");
                }
                return new Event(eventMatcher.group(1).trim(), eventMatcher.group(2).trim(),
                        eventMatcher.group(3).trim());
            }
            default:
                throw new IllegalArgumentException("Command does not create a task");
        }
    }

    /** Validates that a command is non-empty, well-formed, and supported. */
    public void validate(String command) throws AndersException {
        String trimmedCommand = command.trim();
        if (trimmedCommand.isEmpty()) {
            throw new AndersException("I don't know what that means. Please enter a command.");
        }
        String word = commandWord(trimmedCommand);
        String args = arguments(trimmedCommand);
        switch (word) {
            case "todo":
                if (args.isEmpty()) {
                    throw new AndersException("The description of a todo cannot be empty. Please include a "
                            + "description!");
                }
                break;
            case "deadline":
                if (!isValidDeadline(args)) {
                    throw new AndersException("A deadline needs a description and a /by value.");
                }
                try {
                    parseTask(trimmedCommand);
                } catch (DateTimeParseException e) {
                    throw new AndersException("A deadline must use d/M/yyyy, d/M/yyyy HHmm, or yyyy-MM-dd format.");
                }
                break;
            case "event":
                if (!isValidEvent(args)) {
                    throw new AndersException("An event needs a description, /from value, and /to value.");
                }
                try {
                    parseTask(trimmedCommand);
                } catch (DateTimeParseException e) {
                    throw new AndersException("Event dates must use yyyy-MM-dd or d/M/yyyy, optionally followed "
                            + "by HHmm.");
                }
                break;
            case "mark":
                if (args.isEmpty()) {
                    throw new AndersException("Mark needs a task number.");
                }
                break;
            case "unmark":
                if (args.isEmpty()) {
                    throw new AndersException("Unmark needs a task number.");
                }
                break;
            case "delete":
                if (args.isEmpty()) {
                    throw new AndersException("Delete needs a task number.");
                }
                break;
            case "find":
                if (args.isEmpty()) {
                    throw new AndersException("Find needs a keyword.");
                }
                break;
            case "bye":
            case "list":
                if (!args.isEmpty()) {
                    throw new AndersException("I don't know what that means. Please try a supported command.");
                }
                break;
            default:
                throw new AndersException("I don't know what that means. Please try a supported command.");
        }
    }

    private boolean isValidDeadline(String details) {
        Matcher deadlineMatcher = DEADLINE_PATTERN.matcher(details);
        return deadlineMatcher.matches()
                && !deadlineMatcher.group(1).trim().isEmpty()
                && !deadlineMatcher.group(2).trim().isEmpty();
    }

    /** Checks that an event contains non-empty {@code /from} and {@code /to} values. */
    private boolean isValidEvent(String details) {
        Matcher eventMatcher = EVENT_PATTERN.matcher(details);
        return eventMatcher.matches()
                && !eventMatcher.group(1).trim().isEmpty()
                && !eventMatcher.group(2).trim().isEmpty()
                && !eventMatcher.group(3).trim().isEmpty();
    }

    private static int firstWhitespaceIndex(String command) {
        for (int i = 0; i < command.length(); i++) {
            if (Character.isWhitespace(command.charAt(i))) {
                return i;
            }
        }
        return -1;
    }
}
