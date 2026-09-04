package anders.ui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import anders.collection.TaskList;
import anders.task.Todo;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests console messages produced by the user interface. */
public class UiTest {
    private final PrintStream originalOutput = System.out;
    private ByteArrayOutputStream capturedOutput;

    @BeforeEach
    public void setUp() {
        capturedOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOutput));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOutput);
    }

    @Test
    public void showMatchingTasks_noMatch_showsHelpfulMessage() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        new Ui().showMatchingTasks(tasks, "exam");

        assertTrue(capturedOutput.toString().contains("No matching tasks found."));
    }
}
