package anders.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/** Represents a task that must be completed by a specified time. */
public class Deadline extends Task {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("d/M/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("d/M/yyyy HHmm");
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");
    private static final DateTimeFormatter DISPLAY_TIME_FORMAT = DateTimeFormatter.ofPattern("h.mm a", Locale.ENGLISH);
    private final LocalDateTime by;
    private final boolean hasTime;
    private final DateTimeFormatter persistenceFormat;

    /**
     * Creates a new unfinished deadline task.
     *
     * @param description the task description
     * @param by the deadline in d/M/yyyy or d/M/yyyy HHmm format
     */
    public Deadline(String description, String by) {
        super(description);
        LocalDateTime parsedDateTime;
        boolean hasTime;
        DateTimeFormatter format;
        try {
            parsedDateTime = LocalDateTime.parse(by, DATE_TIME_FORMAT);
            hasTime = true;
            format = DATE_TIME_FORMAT;
        } catch (DateTimeParseException e) {
            format = DATE_FORMAT;
            try {
                parsedDateTime = LocalDate.parse(by, format).atStartOfDay();
            } catch (DateTimeParseException invalidLocalFormat) {
                format = DateTimeFormatter.ISO_LOCAL_DATE;
                parsedDateTime = LocalDate.parse(by, format).atStartOfDay();
            }
            hasTime = false;
        }
        this.by = parsedDateTime;
        this.hasTime = hasTime;
        this.persistenceFormat = format;
    }

    /** Returns the typed deadline date. */
    public LocalDateTime getBy() {
        return by;
    }

    /** Returns the display text for this deadline task. */
    @Override
    public String toString() {
        String time = by.format(DISPLAY_TIME_FORMAT).toLowerCase(Locale.ROOT);
        String display = by.format(DISPLAY_FORMAT) + (hasTime ? " " + time : "");
        return "[D] " + super.toString() + " (by: " + display + ")";
    }

    /** Returns the canonical value used for persistence. */
    public String getByText() {
        return by.format(persistenceFormat);
    }
}
