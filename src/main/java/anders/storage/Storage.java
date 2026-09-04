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
            Task task = tasks.get(i);
            String type = task instanceof Deadline ? "D" : task instanceof Event ? "E" : "T";
            String line = "2|" + type + "|" + (task.isDone() ? "1" : "0") + "|" + encode(task.getDescription());
            if (task instanceof Deadline d) {
                line += "|" + encode(d.getByText());
            }
            if (task instanceof Event e) {
                line += "|" + encode(e.getFromText()) + "|" + encode(e.getToText());
            }
            lines.add(line);
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
            Task task;
            if (fields.length >= 4 && fields[0].equals("2")) {
                if (!fields[2].equals("0") && !fields[2].equals("1")) {
                    return null;
                }
                task = fields[1].equals("T") && fields.length == 4 ? new Todo(decode(fields[3]))
                        : fields[1].equals("D") && fields.length == 5
                        ? new Deadline(decode(fields[3]), decode(fields[4]))
                        : fields[1].equals("E") && fields.length == 6
                        ? new Event(decode(fields[3]), decode(fields[4]), decode(fields[5])) : null;
                if (task != null && fields[2].equals("1")) {
                    task.markAsDone();
                }
                return task;
            }
            fields = line.split("\\s*\\|\\s*", -1);
            if (fields.length < 3 || !(fields[1].equals("0") || fields[1].equals("1"))) {
                return null;
            }
            task = fields[0].equals("T") && fields.length == 3 ? new Todo(fields[2])
                    : fields[0].equals("D") && fields.length == 4 ? new Deadline(fields[2], fields[3])
                    : fields[0].equals("E") && fields.length == 5
                    ? new Event(fields[2], fields[3], fields[4]) : null;
            if (task != null && fields[1].equals("1")) {
                task.markAsDone();
            }
            return task;
        } catch (IllegalArgumentException e) {
            return null;
        }
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
