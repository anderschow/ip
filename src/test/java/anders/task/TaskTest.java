package anders.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests the completion-status behavior of {@link Task}. */
public class TaskTest {

    @Test
    public void getStatusIcon_newTask_returnsIncompleteIcon() {
        Task task = new Task("read book");

        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void getStatusIcon_completedTask_returnsCompleteIcon() {
        Task task = new Task("read book");
        task.markAsDone();

        assertEquals("X", task.getStatusIcon());
    }

    @Test
    public void getStatusIcon_uncompletedTask_returnsIncompleteIcon() {
        Task task = new Task("read book");
        task.markAsDone();
        task.markAsNotDone();

        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void getStatusIcon_repeatedStatusChanges_returnsCurrentIcon() {
        Task task = new Task("read book");

        task.markAsNotDone();
        assertEquals(" ", task.getStatusIcon());

        task.markAsDone();
        task.markAsDone();
        assertEquals("X", task.getStatusIcon());

        task.markAsNotDone();
        task.markAsNotDone();
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void markAsDone_newTask_marksTaskAsDone() {
        Task task = new Task("read book");

        task.markAsDone();

        assertTrue(task.isDone());
    }

    @Test
    public void markAsNotDone_completedTask_marksTaskAsNotDone() {
        Task task = new Task("read book");
        task.markAsDone();

        task.markAsNotDone();

        assertFalse(task.isDone());
    }

    @Test
    public void getDescription_task_returnsOriginalDescription() {
        Task task = new Task("read book");

        assertEquals("read book", task.getDescription());
    }

    @Test
    public void isDone_newTask_returnsFalse() {
        Task task = new Task("read book");

        assertFalse(task.isDone());
    }

    @Test
    public void toString_task_returnsDescription() {
        Task task = new Task("read book");

        assertEquals("read book", task.toString());
    }

    @Test
    public void addTags_reusesTagsAcrossTasks_butAvoidsDuplicatesWithinTask() {
        Task firstTask = new Todo("read book");
        Task secondTask = new Todo("revise notes");

        assertTrue(firstTask.addTags(List.of("#Fun", "#school", "#fun")));
        assertFalse(firstTask.addTags(List.of("#FUN")));
        assertTrue(secondTask.addTags(List.of("#fun")));

        assertEquals(List.of("#fun", "#school"), firstTask.getTags());
        assertEquals(List.of("#fun"), secondTask.getTags());
    }

    @Test
    public void removeTags_existingAndMissingTags_updatesOnlySelectedTask() {
        Task firstTask = new Todo("read book");
        Task secondTask = new Todo("revise notes");
        firstTask.addTags(List.of("#fun", "#school"));
        secondTask.addTags(List.of("#fun"));

        assertTrue(firstTask.removeTags(List.of("#school", "#missing")));

        assertEquals(List.of("#fun"), firstTask.getTags());
        assertEquals(List.of("#fun"), secondTask.getTags());
    }

    @Test
    public void addTags_invalidTag_failsWithoutChangingExistingTags() {
        Task task = new Todo("read book");
        task.addTags(List.of("#fun"));

        assertThrows(IllegalArgumentException.class, () -> task.addTags(List.of("#school", "fun!")));
        assertEquals(List.of("#fun"), task.getTags());
    }

    @Test
    public void toString_taggedTodo_includesTags() {
        Task task = new Todo("read book");
        task.addTags(List.of("#fun"));

        assertEquals("[T] read book (tags: #fun)", task.toString());
    }

    @Test
    public void toString_deadlineWithTime_usesTwelveHourClock() {
        Deadline deadline = new Deadline("read notes", "5/9/2026 2030");

        assertTrue(deadline.toString().contains("8.30 pm"));
    }

    @Test
    public void event_endBeforeStart_failsFastWithAssertion() {
        assertThrows(AssertionError.class, () -> new Event("meeting", "2025-01-01 16:00", "2025-01-01 14:00"));
    }
}
