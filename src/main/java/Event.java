/**
 * Represents a task scheduled between a start time and an end time.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Creates a new unfinished event task.
     *
     * @param description the task description
     * @param from the event start time, kept as entered by the user
     * @param to the event end time, kept as entered by the user
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /** @return the event start text */
    public String getFrom() {
        return from;
    }

    /** @return the event end text */
    public String getTo() {
        return to;
    }

    /** Returns the display text for this event task. */
    @Override
    public String toString() {
        return "[E] " + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
