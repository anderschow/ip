package anders.ui;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import anders.collection.TaskList;
import anders.task.Deadline;
import anders.task.Task;
import anders.task.Todo;

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

    @Test
    public void showTaskList_deadline_preservesTypeAndDescriptionFormatting() {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("return book", "2019-12-02"));

        new Ui().showTaskList(tasks);

        assertTrue(capturedOutput.toString().contains("1.[D][ ] return book (by: Dec 02 2019)"));
    }

    @Test
    public void showTaskList_reusableTags_displaysTagsForEachTask() {
        TaskList tasks = new TaskList();
        Task firstTask = new Todo("read book");
        Task secondTask = new Todo("revise notes");
        firstTask.addTags(List.of("#fun"));
        secondTask.addTags(List.of("#fun", "#school"));
        tasks.add(firstTask);
        tasks.add(secondTask);

        new Ui().showTaskList(tasks);

        String output = capturedOutput.toString();
        assertTrue(output.contains("1.[T][ ] read book (tags: #fun)"));
        assertTrue(output.contains("2.[T][ ] revise notes (tags: #fun #school)"));
    }

    @Test
    public void showMatchingTasks_tagKeyword_matchesExactReusableTag() {
        TaskList tasks = new TaskList();
        Task matchingTask = new Todo("read book");
        Task secondMatchingTask = new Todo("revise notes");
        Task nonMatchingTask = new Todo("have fun");
        matchingTask.addTags(List.of("#fun"));
        secondMatchingTask.addTags(List.of("#FUN"));
        nonMatchingTask.addTags(List.of("#funny"));
        tasks.add(matchingTask);
        tasks.add(secondMatchingTask);
        tasks.add(nonMatchingTask);

        new Ui().showMatchingTasks(tasks, "#Fun");

        String output = capturedOutput.toString();
        assertTrue(output.contains("1.[T][ ] read book (tags: #fun)"));
        assertTrue(output.contains("2.[T][ ] revise notes (tags: #fun)"));
        assertTrue(!output.contains("3.[T][ ] have fun (tags: #funny)"));
    }

    public void showTaskList_taskWithoutTypePrefix_failsFastWithAssertion() {
        TaskList tasks = new TaskList();
        tasks.add(new Task("plain task"));

        assertThrows(AssertionError.class, () -> new Ui().showTaskList(tasks));
    }
}
