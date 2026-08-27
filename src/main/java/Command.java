/** Represents one parsed user command. */
public abstract class Command {
    /** Executes this command using the supplied application services. */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws AndersException;
    /** @return whether this command ends the application */
    public boolean isExit() { return false; }
}
