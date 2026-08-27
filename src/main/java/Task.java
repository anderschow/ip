/**
 * Represents a task in the task list.
 */
public class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates a new unfinished task.
     *
     * @param description the task description
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /** Marks this task as done. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not done. */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the symbol used to display this task's completion status.
     *
     * @return {@code X} for a completed task, otherwise a space
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** @return the task description */
    public String getDescription() {
        return description;
    }

    /** @return whether this task is completed */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the task description for subclasses to reuse in their display text.
     *
     * @return the task description
     */
    @Override
    public String toString() {
        return description;
    }
}
