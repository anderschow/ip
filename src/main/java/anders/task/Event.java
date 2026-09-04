package anders.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/** Represents a task scheduled between a start time and an end time. */
public class Event extends Task {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
    private static final DateTimeFormatter LEGACY_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter INPUT_DATE_FORMAT = DateTimeFormatter.ofPattern("d/M/yyyy");
    private static final DateTimeFormatter INPUT_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("d/M/yyyy HHmm");
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");
    private static final DateTimeFormatter DISPLAY_TIME_FORMAT = DateTimeFormatter.ofPattern("h.mm a", Locale.ENGLISH);
    private final LocalDateTime from;
    private final LocalDateTime to;
    private final boolean fromHasTime;
    private final boolean toHasTime;

    /**
     * Creates a new unfinished event task.
     *
     * @param description the task description
     * @param from the event start date/time in yyyy-MM-dd or yyyy-MM-dd HHmm format
     * @param to the event end date/time in yyyy-MM-dd or yyyy-MM-dd HHmm format
     */
    public Event(String description, String from, String to) {
        super(description);
        ParsedDateTime parsedFrom = parseDateTime(from);
        ParsedDateTime parsedTo = parseDateTime(to);
        this.from = parsedFrom.value;
        this.to = parsedTo.value;
        this.fromHasTime = parsedFrom.hasTime;
        this.toHasTime = parsedTo.hasTime;
    }

    /** @return the event start date and time */
    public LocalDateTime getFrom() {
        return from;
    }

    /** @return the event end date and time */
    public LocalDateTime getTo() {
        return to;
    }

    /** Returns the display text for this event task. */
    @Override
    public String toString() {
        String fromText = formatForDisplay(from, fromHasTime);
        String toText = formatForDisplay(to, toHasTime);
        return "[E] " + super.toString() + " (from: " + fromText + " to: " + toText + ")";
    }

    /** Returns the canonical start value used for persistence. */
    public String getFromText() {
        return from.format(fromHasTime ? DATE_TIME_FORMAT : DATE_FORMAT);
    }

    /** Returns the canonical end value used for persistence. */
    public String getToText() {
        return to.format(toHasTime ? DATE_TIME_FORMAT : DATE_FORMAT);
    }

    private static String formatForDisplay(LocalDateTime dateTime, boolean hasTime) {
        String date = dateTime.format(DISPLAY_DATE_FORMAT);
        String time = dateTime.format(DISPLAY_TIME_FORMAT).toLowerCase(Locale.ROOT);
        return date + (hasTime ? " " + time : "");
    }

    private static ParsedDateTime parseDateTime(String value) {
        try {
            return new ParsedDateTime(LocalDateTime.parse(value, DATE_TIME_FORMAT), true);
        } catch (DateTimeParseException e) {
            try {
                return new ParsedDateTime(LocalDateTime.parse(value, LEGACY_DATE_TIME_FORMAT), true);
            } catch (DateTimeParseException legacyFormatError) {
                try {
                    return new ParsedDateTime(LocalDateTime.parse(value, INPUT_DATE_TIME_FORMAT), true);
                } catch (DateTimeParseException slashDateTimeError) {
                    try {
                        return new ParsedDateTime(LocalDate.parse(value, DATE_FORMAT).atStartOfDay(), false);
                    } catch (DateTimeParseException isoDateError) {
                        return new ParsedDateTime(LocalDate.parse(value, INPUT_DATE_FORMAT).atStartOfDay(), false);
                    }
                }
            }
        }
    }

    private record ParsedDateTime(LocalDateTime value, boolean hasTime) {
    }
}
