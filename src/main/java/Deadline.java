/**
 * Represents a task that must be completed by a specified time.
 */
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Deadline extends Task {
    private final LocalDate by;
    private final String originalBy;
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");

    /**
     * Creates a new unfinished deadline task.
     *
     * @param description the task description
     * @param by the deadline in yyyy-MM-dd format (legacy free-form values are also accepted)
     */
    public Deadline(String description, String by) {
        super(description);
        LocalDate parsedDate = null;
        try {
            parsedDate = LocalDate.parse(by);
        } catch (DateTimeParseException ignored) {
            // Keep accepting dates saved by earlier versions of Anders.
        }
        this.by = parsedDate;
        this.originalBy = by;
    }

    /** @return the deadline text */
    public LocalDate getBy() {
        return by;
    }

    /** Returns the display text for this deadline task. */
    @Override
    public String toString() {
        String displayDate = by == null ? originalBy : by.format(DISPLAY_FORMAT);
        return "[D] " + super.toString() + " (by: " + displayDate + ")";
    }

    /** @return the original value, used for persistence and legacy free-form dates */
    public String getByText() {
        return originalBy;
    }
}
