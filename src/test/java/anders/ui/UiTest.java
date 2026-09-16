package anders.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
        assertFalse(output.contains("3.[T][ ] have fun (tags: #funny)"));
    }

    @Test
    public void showTaskList_taskWithoutTypePrefix_failsFastWithAssertion() {
        TaskList tasks = new TaskList();
        tasks.add(new Task("plain task"));

        assertThrows(AssertionError.class, () -> new Ui().showTaskList(tasks));
    }

    @Test
    public void showMatchingTasks_descriptionKeyword_matchesCaseInsensitively() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("revise notes"));
        tasks.add(new Todo("read book"));

        new Ui().showMatchingTasks(tasks, "BOOK");

        String output = capturedOutput.toString();
        assertTrue(output.contains("2.[T][ ] read book"));
        assertFalse(output.contains("revise notes"));
        assertFalse(output.contains("No matching tasks found."));
    }

    @Test
    public void showMarked_bothStatuses_displaysMatchingMessageAndIcon() {
        Task task = new Todo("read book");
        task.addTags(List.of("#fun"));
        Ui ui = new Ui();

        task.markAsDone();
        ui.showMarked(task, true);
        assertEquals("Nice! I've marked this task as done:" + System.lineSeparator()
                + "       [X] read book (tags: #fun)", capturedOutput.toString().trim());

        capturedOutput.reset();
        task.markAsNotDone();
        ui.showMarked(task, false);
        assertEquals("OK, I've marked this task as not done yet:" + System.lineSeparator()
                + "       [ ] read book (tags: #fun)", capturedOutput.toString().trim());
    }

    @Test
    public void showTagUpdate_eachOutcome_displaysMatchingConfirmation() {
        Task task = new Todo("read book");
        Ui ui = new Ui();

        ui.showTagUpdate(task, true, true);
        assertTrue(capturedOutput.toString().contains("Nice! I've tagged this task:"));

        capturedOutput.reset();
        ui.showTagUpdate(task, true, false);
        assertTrue(capturedOutput.toString().contains("This task already has these tags:"));

        capturedOutput.reset();
        ui.showTagUpdate(task, false, true);
        assertTrue(capturedOutput.toString().contains("OK, I've removed these tags from this task:"));

        capturedOutput.reset();
        ui.showTagUpdate(task, false, false);
        assertTrue(capturedOutput.toString().contains("This task does not have these tags:"));
    }
}
