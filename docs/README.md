# Anders User Guide

Anders is a task manager that supports todos, deadlines, events, completion status, searching, and reusable tags.

## Command reference

Send one command at a time. Replace text in `<brackets>` with your own values,
leaving out the brackets.

| Command format | What it does | Example |
| --- | --- | --- |
| `todo <task>` | Add a task with no due date. | `todo read chapter 1` |
| `deadline <task> /by <date>` | Add a task with a due date. | `deadline report /by 2/10/2026 1800` |
| `event <task> /from <start> /to <end>` | Add an event with a start and end. | `event study /from 2/10/2026 1400 /to 2/10/2026 1600` |
| `list` | Show all tasks and their numbers. | `list` |
| `find <text>` | Search task descriptions. | `find chapter` |
| `mark <number>` | Mark a task as done. | `mark 2` |
| `unmark <number>` | Mark a task as not done. | `unmark 2` |
| `delete <number>` | Remove a task. | `delete 2` |
| `tag <number> #<tag>` | Add a tag to a task. | `tag 2 #school` |
| `untag <number> #<tag>` | Remove a tag from a task. | `untag 2 #school` |
| `find #<tag>` | Show tasks with that tag. | `find #school` |

Dates in these examples use **day/month/year**, with an optional 24-hour time:
`2/10/2026 1800` means **2 October 2026 at 6 pm**.

**Use task numbers from `list`.** Replace `<number>` with any number from 1 to the
current task count. With two tasks, either 1 or 2 is valid. Run `list` again after
deleting a task because the remaining tasks are renumbered.

## Adding tasks

Create an untagged todo with:

```text
todo read book
```

Add tags during creation with a trailing `/tags` clause:

```text
todo read book /tags #fun #school
deadline return book /by 2019-12-02 /tags #admin
event project meeting /from 2025-01-01 14:00 /to 2025-01-01 16:00 /tags #project #meeting
```

Tags must start with `#` and may contain letters, digits, hyphens, or underscores. Tags are case-insensitive and are stored in lowercase.

For example, the first command displays the task as:

```text
[T][ ] read book (tags: #fun #school)
```

## Managing tags

Add one or more tags to an existing task:

```text
tag 1 #fun #school
```

Remove one or more tags:

```text
untag 1 #school
```

The same tag can be used on any number of tasks. A tag is unique only within an individual task, so adding an existing tag to the same task does not create a duplicate.

## Finding tagged tasks

Use `find #tag` to find tasks with an exact tag. Matching is case-insensitive:

```text
find #FUN
```

The result includes every task with `#fun`, but not tasks with `#funny`. Ordinary `find` searches continue to search task descriptions only.

## Invalid tags

The following are invalid:

```text
fun
#
#fun!
#fun tag
```

Invalid tag commands are rejected without changing the task list. A creation-time `/tags` clause must contain at least one valid tag and must be the final clause in the command.

## Display format

Tags are displayed after the task-specific details:

```text
[T][ ] read book (tags: #fun)
[D][ ] return book (by: Dec 02 2019) (tags: #admin)
[E][ ] project meeting (from: Jan 01 2025 2.00 pm to: Jan 01 2025 4.00 pm) (tags: #project #meeting)
```

Tasks without tags keep the existing display format.

## Using the desktop window

Launch the JavaFX app with `gradlew.bat run` on Windows (or `./gradlew run`).

- Type a command and press **Enter** or click **Send**. Blank commands are ignored.
- Use **Tasks** to show your list, or **Commands** for formats, explanations, and examples. Both keep your unfinished input.
- Press **Up** and **Down** in the input to recall commands. Going past the newest command restores your draft.
- Right-click a message and choose **Copy message** to copy its text.
- Resize the window as needed. Replies wrap to fit, and long replies open at their beginning so you can read downward.
