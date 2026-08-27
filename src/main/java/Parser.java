/** Validates the command formats accepted by Anders. */
public class Parser {
    /** Throws an exception when the command is empty, malformed, or unsupported. */
    public void validate(String command) throws AndersException {
        if (command.isEmpty()) throw new AndersException("I don't know what that means. Please enter a command.");
        if (command.equals("todo") || command.matches("todo\\s+"))
            throw new AndersException("The description of a todo cannot be empty. Please include a description!");
        if (command.equals("deadline") || command.startsWith("deadline ")
                && !command.substring(9).contains(" /by "))
            throw new AndersException("A deadline needs a description and a /by value.");
        if (command.equals("event") || command.startsWith("event ")
                && (!command.substring(6).contains(" /from ") || !command.substring(6).contains(" /to ")))
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
}
