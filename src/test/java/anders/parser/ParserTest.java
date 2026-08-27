package anders.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import anders.AndersException;
import anders.command.AddCommand;
import anders.command.ExitCommand;
import anders.command.FindCommand;
import anders.command.MarkCommand;
import anders.task.Deadline;
import anders.task.Event;
import anders.task.Task;
import anders.task.Todo;
import org.junit.jupiter.api.Test;

/** Tests command parsing and validation, which drive Anders' core input flow. */
public class ParserTest {

    private final Parser parser = new Parser();

    @Test
    public void parseTask_todoCommand_createsTodoWithDescription() {
        Task task = parser.parseTask("todo read book");

        assertInstanceOf(Todo.class, task);
        assertEquals("read book", task.getDescription());
    }

    @Test
    public void parseTask_deadlineCommand_createsDeadlineWithDueDate() {
        Task task = parser.parseTask("deadline return book /by 2019-12-02");

        Deadline deadline = assertInstanceOf(Deadline.class, task);
        assertEquals("return book", deadline.getDescription());
        assertEquals("2019-12-02", deadline.getByText());
    }

    @Test
    public void parseTask_eventCommand_createsEventWithTimeRange() {
        Task task = parser.parseTask("event project meeting /from 2025-01-01 14:00 /to 16:00");

        Event event = assertInstanceOf(Event.class, task);
        assertEquals("project meeting", event.getDescription());
        assertEquals("2025-01-01 14:00", event.getFromText());
        assertEquals("16:00", event.getToText());
    }

    @Test
    public void parse_supportedCommands_createsExpectedCommandTypes() throws AndersException {
        assertInstanceOf(ExitCommand.class, Parser.parse("bye"));
        assertInstanceOf(AddCommand.class, Parser.parse("todo read book"));
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
    }

    @Test
    public void commandWordAndArguments_commandWithArguments_areSeparated() {
        assertEquals("deadline", parser.commandWord("deadline return book /by Friday"));
        assertEquals("return book /by Friday", parser.arguments("deadline return book /by Friday"));
        assertEquals("list", parser.commandWord("list"));
        assertEquals("", parser.arguments("list"));
    }

    @Test
    public void validate_invalidCommands_throwHelpfulExceptions() {
        assertThrows(AndersException.class, () -> parser.validate(""));
        assertThrows(AndersException.class, () -> parser.validate("todo"));
        assertThrows(AndersException.class, () -> parser.validate("deadline return book"));
        assertThrows(AndersException.class, () -> parser.validate("event meeting /from 2pm"));
        assertThrows(AndersException.class, () -> parser.validate("unknown command"));
        assertThrows(AndersException.class, () -> parser.validate("find"));
    }
}
