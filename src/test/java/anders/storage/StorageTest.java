package anders.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import anders.collection.TaskList;
import anders.task.Deadline;
import anders.task.Event;
import anders.task.Task;
import anders.task.Todo;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests persistence of tasks, including encoding and legacy records. */
public class StorageTest {

    @TempDir
    private Path temporaryDirectory;

    @Test
    public void load_missingFile_returnsEmptyList() {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt").toString());

        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void load_encodedRecords_reconstructsTasksAndStatus() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.write(file, List.of(
                "2|T|1|cmVhZCB8IGJvb2s=",
                "2|D|0|cmV0dXJuIGJvb2s=|SnVuZSA2dGg=",
                "2|E|0|cHJvamVjdCBtZWV0aW5n|QXVnIDZ0aCAycG0=|NHBt"));

        List<Task> tasks = new Storage(file.toString()).load();

        assertEquals(3, tasks.size());
        assertEquals("read | book", tasks.get(0).getDescription());
        assertTrue(tasks.get(0).isDone());
        assertEquals("June 6th", ((Deadline) tasks.get(1)).getByText());
        assertEquals("Aug 6th 2pm", ((Event) tasks.get(2)).getFromText());
        assertEquals("4pm", ((Event) tasks.get(2)).getToText());
    }

    @Test
    public void load_malformedRecords_ignoresInvalidEntries() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.write(file, List.of(
                "2|T|0|dmFsaWQ=",
                "not a valid record",
                "2|X|0|YmFkIHR5cGU=",
                "2|T|9|aW52YWxpZCBzdGF0dXM="));

        List<Task> tasks = new Storage(file.toString()).load();

        assertEquals(1, tasks.size());
        assertEquals("valid", tasks.get(0).getDescription());
        assertFalse(tasks.get(0).isDone());
    }

    @Test
    public void save_tasks_writesEncodedVersionedRecords() throws Exception {
        Path file = temporaryDirectory.resolve("nested").resolve("tasks.txt");
        TaskList tasks = new TaskList();
        Task todo = new Todo("read | book");
        todo.markAsDone();
        tasks.add(todo);
        tasks.add(new Deadline("return book", "June 6th"));
        tasks.add(new Event("meeting", "Aug 6th 2pm", "4pm"));

        new Storage(file.toString()).save(tasks);

        List<Task> loaded = new Storage(file.toString()).load();
        assertEquals(3, loaded.size());
        assertEquals("read | book", loaded.get(0).getDescription());
        assertTrue(loaded.get(0).isDone());
        assertEquals("June 6th", ((Deadline) loaded.get(1)).getByText());
        assertEquals("Aug 6th 2pm", ((Event) loaded.get(2)).getFromText());
    }
}
