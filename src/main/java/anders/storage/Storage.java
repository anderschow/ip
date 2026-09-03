package anders.storage;
import anders.collection.TaskList;
import anders.task.Deadline;
import anders.task.Event;
import anders.task.Task;
import anders.task.Todo;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

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
            String[] f = line.split("\\|", -1);
            Task task;
            if (f.length >= 4 && f[0].equals("2")) {
                if (!f[2].equals("0") && !f[2].equals("1")) {
                    return null;
                }
                task = f[1].equals("T") && f.length == 4 ? new Todo(decode(f[3]))
                        : f[1].equals("D") && f.length == 5 ? new Deadline(decode(f[3]), decode(f[4]))
                        : f[1].equals("E") && f.length == 6
                        ? new Event(decode(f[3]), decode(f[4]), decode(f[5])) : null;
                if (task != null && f[2].equals("1")) {
                    task.markAsDone();
                }
                return task;
            }
            f = line.split("\\s*\\|\\s*", -1);
            if (f.length < 3 || !(f[1].equals("0") || f[1].equals("1"))) {
                return null;
            }
            task = f[0].equals("T") && f.length == 3 ? new Todo(f[2])
                    : f[0].equals("D") && f.length == 4 ? new Deadline(f[2], f[3])
                    : f[0].equals("E") && f.length == 5 ? new Event(f[2], f[3], f[4]) : null;
            if (task != null && f[1].equals("1")) {
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
