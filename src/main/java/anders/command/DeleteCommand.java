package anders.command;
import anders.AndersException;
import anders.collection.TaskList;
import anders.storage.Storage;
import anders.ui.Ui;
import anders.task.Task;
/** Deletes a selected task. */
public class DeleteCommand extends Command {
    private final String taskNumber;
    /** Creates a delete command. */
    public DeleteCommand(String taskNumber) { this.taskNumber = taskNumber; }
    @Override public void execute(TaskList tasks, Ui ui, Storage storage) {
        try {
            int index = Integer.parseInt(taskNumber) - 1;
            if (index < 0 || index >= tasks.size()) { ui.showInvalidTaskNumber(tasks.size()); return; }
            Task removed = tasks.remove(index); storage.save(tasks); ui.showDeleted(removed, tasks.size());
        } catch (NumberFormatException e) { ui.showInvalidTaskNumberFormat(); }
    }
}
