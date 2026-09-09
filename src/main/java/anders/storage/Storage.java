package anders.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import anders.collection.TaskList;
import anders.task.Deadline;
import anders.task.Event;
import anders.task.Task;
import anders.task.Todo;

/** Handles loading tasks from and saving tasks to a file. */
public class Storage {
    private static final String CURRENT_FORMAT_VERSION = "2";
    private static final String TODO_TYPE = "T";
    private static final String DEADLINE_TYPE = "D";
    private static final String EVENT_TYPE = "E";
    private static final String NOT_DONE_STATUS = "0";
    private static final String DONE_STATUS = "1";

    private final Path file;

    /** Creates storage backed by {@code filePath}. */
    public Storage(String filePath) {
        file = Path.of(filePath);
    }

    /** Loads valid tasks from the file, ignoring malformed records. */
    public List<Task> load() {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(file)) {
            return tasks;
        }
        try {
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                Task task = parse(line);
                if (task != null) {
                    tasks.add(task);
                }
            }
        } catch (IOException | SecurityException e) {
            // Ignore unreadable storage and start with no tasks.
        }
        return tasks;
    }

    /** Saves the current tasks in the versioned encoded format. */
    public void save(TaskList tasks) {
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            lines.add(serialize(tasks.get(i)));
        }
        try {
            Files.createDirectories(file.getParent());
            Path temporary = file.resolveSibling(file.getFileName() + ".tmp");
            Files.write(temporary, lines, StandardCharsets.UTF_8);
            Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException | SecurityException e) {
            // Ignore save failures; the in-memory task list remains usable.
        }
    }

    /** Converts one saved record into a task, or returns {@code null} if invalid. */
    private static Task parse(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        try {
            String[] fields = line.split("\\|", -1);
            if (fields.length >= 4 && CURRENT_FORMAT_VERSION.equals(fields[0])) {
                return parseVersionedRecord(fields);
            }

            return parseLegacyRecord(line);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static String serialize(Task task) {
        StringBuilder line = new StringBuilder(CURRENT_FORMAT_VERSION)
                .append('|').append(getType(task))
                .append('|').append(task.isDone() ? DONE_STATUS : NOT_DONE_STATUS)
                .append('|').append(encode(task.getDescription()));
        if (task instanceof Deadline deadline) {
            line.append('|').append(encode(deadline.getByText()));
        } else if (task instanceof Event event) {
            line.append('|').append(encode(event.getFromText()))
                    .append('|').append(encode(event.getToText()));
        }
        return line.toString();
    }

    private static String getType(Task task) {
        if (task instanceof Deadline) {
            return DEADLINE_TYPE;
        }
        if (task instanceof Event) {
            return EVENT_TYPE;
        }
        return TODO_TYPE;
    }

    private static Task parseVersionedRecord(String[] fields) {
        if (!isValidStatus(fields[2])) {
            return null;
        }
        Task task = createTask(fields[1], fields, 3, true);
        return applyStatus(task, fields[2]);
    }

    private static Task parseLegacyRecord(String line) {
        String[] fields = line.split("\\s*\\|\\s*", -1);
        if (fields.length < 3 || !isValidStatus(fields[1])) {
            return null;
        }
        Task task = createTask(fields[0], fields, 2, false);
        return applyStatus(task, fields[1]);
    }

    private static boolean isValidStatus(String status) {
        return NOT_DONE_STATUS.equals(status) || DONE_STATUS.equals(status);
    }

    private static Task createTask(String type, String[] fields, int dataStart, boolean isEncoded) {
        switch (type) {
            case TODO_TYPE:
                if (fields.length != dataStart + 1) {
                    return null;
                }
                return new Todo(readField(fields[dataStart], isEncoded));
            case DEADLINE_TYPE:
                if (fields.length != dataStart + 2) {
                    return null;
                }
                return new Deadline(readField(fields[dataStart], isEncoded),
                        readField(fields[dataStart + 1], isEncoded));
            case EVENT_TYPE:
                if (fields.length != dataStart + 3) {
                    return null;
                }
                return new Event(readField(fields[dataStart], isEncoded),
                        readField(fields[dataStart + 1], isEncoded), readField(fields[dataStart + 2], isEncoded));
            default:
                return null;
        }
    }

    private static String readField(String value, boolean isEncoded) {
        return isEncoded ? decode(value) : value;
    }

    private static Task applyStatus(Task task, String status) {
        if (task != null && DONE_STATUS.equals(status)) {
            task.markAsDone();
        }
        return task;
    }

    /** Encodes a task field so separators and Unicode characters are preserved. */
    private static String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    /** Decodes a previously encoded task field. */
    private static String decode(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }
}
