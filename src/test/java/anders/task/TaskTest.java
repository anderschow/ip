package anders.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests task completion, tags, and date/time formatting. */
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

    @Test
    public void event_dateOnlyEndpoints_preservesDateOnlyDisplayAndStorage() {
        Event event = new Event("trip", "2025-01-01", "2025-01-02");

        assertEquals("2025-01-01", event.getFromText());
        assertEquals("2025-01-02", event.getToText());
        assertEquals("[E] trip (from: Jan 01 2025 to: Jan 02 2025)", event.toString());
    }

    @Test
    public void event_explicitMidnightEndpoints_preservesBothTimes() {
        Event event = new Event("trip", "2025-01-01 0000", "2025-01-02 0000");

        assertEquals("2025-01-01 0000", event.getFromText());
        assertEquals("2025-01-02 0000", event.getToText());
        assertEquals("[E] trip (from: Jan 01 2025 12.00 am to: Jan 02 2025 12.00 am)", event.toString());
    }

    @Test
    public void event_onlyEndHasTime_preservesEndpointPrecision() {
        Event event = new Event("trip", "2025-01-01", "2025-01-02 1600");

        assertEquals("2025-01-01", event.getFromText());
        assertEquals("2025-01-02 1600", event.getToText());
        assertEquals("[E] trip (from: Jan 01 2025 to: Jan 02 2025 4.00 pm)", event.toString());
    }

    @Test
    public void event_onlyStartHasTime_preservesEndpointPrecision() {
        Event event = new Event("trip", "2025-01-01 1400", "2025-01-02");

        assertEquals("2025-01-01 1400", event.getFromText());
        assertEquals("2025-01-02", event.getToText());
        assertEquals("[E] trip (from: Jan 01 2025 2.00 pm to: Jan 02 2025)", event.toString());
    }
}
