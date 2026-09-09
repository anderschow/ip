# Anders User Guide

Anders is a task manager that supports todos, deadlines, events, completion status, searching, and reusable tags.

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
