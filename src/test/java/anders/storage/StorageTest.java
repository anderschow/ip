package anders.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import anders.collection.TaskList;
import anders.task.Deadline;
import anders.task.Event;
import anders.task.Task;
import anders.task.Todo;

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
                "2|D|0|cmV0dXJuIGJvb2s=|MjAxOS0xMi0wMg==",
                "2|E|0|cHJvamVjdCBtZWV0aW5n|MjAyNS0wMS0wMSAxNDowMA==|MjAyNS0wMS0wMSAxNjowMA=="));

        List<Task> tasks = new Storage(file.toString()).load();

        assertEquals(3, tasks.size());
        assertEquals("read | book", tasks.get(0).getDescription());
        assertTrue(tasks.get(0).isDone());
        assertEquals("2019-12-02", ((Deadline) tasks.get(1)).getByText());
        assertEquals("2025-01-01 1400", ((Event) tasks.get(2)).getFromText());
        assertEquals("2025-01-01 1600", ((Event) tasks.get(2)).getToText());
    }

    @Test
    public void load_taggedRecords_reconstructsReusableTags() throws Exception {
        Path file = temporaryDirectory.resolve("tagged-tasks.txt");
        Files.write(file, List.of(
                "3|T|0|cmVhZCBib29r|I2Z1bg==",
                "3|T|1|cmV2aXNlIG5vdGVz|I2Z1bg==",
                "3|D|0|cmV0dXJuIGJvb2s=|MjAxOS0xMi0wMg==|I2FkbWlu"));

        List<Task> tasks = new Storage(file.toString()).load();

        assertEquals(3, tasks.size());
        assertEquals(List.of("#fun"), tasks.get(0).getTags());
        assertEquals(List.of("#fun"), tasks.get(1).getTags());
        assertEquals(List.of("#admin"), tasks.get(2).getTags());
        assertTrue(tasks.get(1).isDone());
    }

    @Test
    public void load_legacyRecords_reconstructsTaskTypesAndStatus() throws Exception {
        Path file = temporaryDirectory.resolve("legacy-tasks.txt");
        Files.write(file, List.of(
                "T | 1 | read book",
                "D | 0 | return book | 2/12/2019",
                "E | 0 | project meeting | 2025-01-01 1400 | 2025-01-01 1600"));

        List<Task> tasks = new Storage(file.toString()).load();

        assertEquals(3, tasks.size());
        assertTrue(tasks.get(0).isDone());
        assertEquals("2/12/2019", ((Deadline) tasks.get(1)).getByText());
        assertEquals("2025-01-01 1400", ((Event) tasks.get(2)).getFromText());
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
        todo.addTags(List.of("#fun"));
        tasks.add(todo);
        tasks.add(new Deadline("return book", "2019-12-02"));
        tasks.add(new Event("meeting", "2025-01-01 14:00", "2025-01-01 16:00"));

        new Storage(file.toString()).save(tasks);

        List<Task> loaded = new Storage(file.toString()).load();
        assertEquals(3, loaded.size());
        assertEquals("read | book", loaded.get(0).getDescription());
        assertTrue(loaded.get(0).isDone());
        assertEquals(List.of("#fun"), loaded.get(0).getTags());
        assertEquals("2019-12-02", ((Deadline) loaded.get(1)).getByText());
        assertEquals("2025-01-01 1400", ((Event) loaded.get(2)).getFromText());
        assertTrue(Files.readAllLines(file).get(0).startsWith("3|T|1|"));
    }
}
