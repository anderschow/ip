package anders.command;

import anders.collection.TaskList;
import anders.storage.Storage;
import anders.ui.Ui;

/** Changes the completion state of a selected task. */
public class MarkCommand extends Command {
    private final String taskNumber;
    private final boolean done;

    /** Creates a mark or unmark command. */
    public MarkCommand(String taskNumber, boolean done) {
        this.taskNumber = taskNumber;
        this.done = done;
    }
    /** Updates the selected task's completion state and saves the task list. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        try {
            int index = Integer.parseInt(taskNumber) - 1;
            if (index < 0 || index >= tasks.size()) {
                ui.showInvalidTaskNumber(tasks.size());
                return;
            }
            if (done) {
                tasks.get(index).markAsDone();
            } else {
                tasks.get(index).markAsNotDone();
            }
            storage.save(tasks);
            ui.showMarked(tasks.get(index), done);
        } catch (NumberFormatException e) {
            ui.showInvalidTaskNumberFormat();
        }
    }
}
