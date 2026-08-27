package anders.command;

import anders.AndersException;
import anders.collection.TaskList;
import anders.storage.Storage;
import anders.ui.Ui;

/** Ends the Anders session. */
public class ExitCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}

