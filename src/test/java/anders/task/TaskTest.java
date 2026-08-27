package anders.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
