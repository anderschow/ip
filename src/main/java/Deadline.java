/**
 * Represents a task that must be completed by a specified time.
 */
public class Deadline extends Task {
    private final String by;

    /**
     * Creates a new unfinished deadline task.
     *
     * @param description the task description
     * @param by the deadline, kept as entered by the user
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    /** Returns the display text for this deadline task. */
    @Override
    public String toString() {
        return "[D] " + super.toString() + " (by: " + by + ")";
    }
}
