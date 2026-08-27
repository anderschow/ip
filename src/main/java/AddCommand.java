/** Adds a newly parsed task to the task list. */
public class AddCommand extends Command {
    private final Task task;
    /** Creates an add command for the supplied task. */
    public AddCommand(Task task) { this.task = task; }
    @Override public void execute(TaskList tasks, Ui ui, Storage storage) {
        tasks.add(task); storage.save(tasks); ui.showTaskAdded(task, tasks.size());
    }
}
