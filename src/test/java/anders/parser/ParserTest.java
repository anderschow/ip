package anders.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import anders.AndersException;
import anders.command.AddCommand;
import anders.command.Command;
import anders.command.ExitCommand;
import anders.command.FindCommand;
import anders.command.MarkCommand;
import anders.task.Deadline;
import anders.task.Event;
import anders.task.Task;
import anders.task.Todo;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

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
        Task task = parser.parseTask("deadline return book /by 2/12/2019 1800");

        Deadline deadline = assertInstanceOf(Deadline.class, task);
        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), deadline.getBy());
        assertEquals("2/12/2019 1800", deadline.getByText());
        assertEquals(true, deadline.toString().contains("Dec 02 2019 6.00 pm"));
    }

    @Test
    public void parseTask_eventCommand_createsEventWithTimeRange() {
        Task task = parser.parseTask("event project meeting /from 2025-01-01 14:00 /to 2025-01-01 16:00");

        Event event = assertInstanceOf(Event.class, task);
        assertEquals("project meeting", event.getDescription());
        assertEquals(LocalDateTime.of(2025, 1, 1, 14, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2025, 1, 1, 16, 0), event.getTo());
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
    public void parse_commandWithExtraWhitespace_acceptsCommand() throws AndersException {
        String input = "  event   project meeting   /from   2025-01-01 14:00   /to   2025-01-01 16:00  ";
        Command command = Parser.parse(input);

        assertInstanceOf(AddCommand.class, command);
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

    @Test
    public void validate_invalidDeadline_hasHelpfulFormatMessage() {
        AndersException exception = assertThrows(AndersException.class,
                () -> parser.validate("deadline return book /by tomorrow"));

        assertTrue(exception.getMessage().contains("d/M/yyyy"));
    }
}
