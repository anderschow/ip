package anders.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/** Represents a task scheduled between a start time and an end time. */
public class Event extends Task {
    private final LocalDateTime from;
    private final LocalDateTime to;
    private final String originalFrom;
    private final String originalTo;
    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm");

    /**
     * Creates a new unfinished event task.
     *
     * @param description the task description
     * @param from the event start time, kept as entered by the user
     * @param to the event end time, kept as entered by the user
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = parseDateTime(from);
        this.to = parseDateTime(to);
        this.originalFrom = from;
        this.originalTo = to;
    }

    /** @return the event start text */
    public LocalDateTime getFrom() {
        return from;
    }

    /** @return the event end text */
    public LocalDateTime getTo() {
        return to;
    }

    /** Returns the display text for this event task. */
    @Override
    public String toString() {
        String fromText = from == null ? originalFrom : from.format(DISPLAY_FORMAT);
        String toText = to == null ? originalTo : to.format(DISPLAY_FORMAT);
        return "[E] " + super.toString() + " (from: " + fromText + " to: " + toText + ")";
    }

    /** @return the original start value for persistence and legacy free-form times */
    public String getFromText() {
        return originalFrom;
    }

    /** @return the original end value for persistence and legacy free-form times */
    public String getToText() {
        return originalTo;
    }

    private static LocalDateTime parseDateTime(String value) {
        try {
            return LocalDateTime.parse(value, INPUT_FORMAT);
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }
}
