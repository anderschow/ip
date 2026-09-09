package anders.command;

import java.util.Arrays;
import java.util.List;

import anders.collection.TaskList;
import anders.storage.Storage;
import anders.task.Task;
import anders.ui.Ui;

/** Adds or removes tags from a selected task. */
public class TagCommand extends Command {
    private final String taskNumberText;
    private final List<String> tags;
    private final boolean adding;

    /** Creates a command that adds or removes the supplied tags. */
    public TagCommand(String arguments, boolean adding) {
        String[] fields = arguments.trim().split("\\s+");
        this.taskNumberText = fields[0];
        this.tags = List.copyOf(Arrays.asList(Arrays.copyOfRange(fields, 1, fields.length)));
        this.adding = adding;
    }

    /** Applies the tag changes to the selected task and saves successful changes. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        try {
            int index = Integer.parseInt(taskNumberText) - 1;
            if (index < 0 || index >= tasks.size()) {
                ui.showInvalidTaskNumber(tasks.size());
                return;
            }
            Task task = tasks.get(index);
            boolean changed = adding ? task.addTags(tags) : task.removeTags(tags);
            if (changed) {
                storage.save(tasks);
            }
            ui.showTagUpdate(task, adding, changed);
        } catch (NumberFormatException e) {
            ui.showInvalidTaskNumberFormat();
        }
    }
}
