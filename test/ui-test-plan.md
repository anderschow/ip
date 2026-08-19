# Console UI test plan

Each test case specifies its aim, commands, and expected output associated with each command.

```json
{
  "cases": [
    {
      "name": "add and list all task types",
      "aim": "Verify todo, deadline, event parsing, and polymorphic list display.",
      "commands": ["todo borrow book", "deadline return book /by Sunday", "event project meeting /from Mon 2pm /to 4pm", "list", "bye"],
      "expected": ["[T] borrow book", "[D] return book (by: Sunday)", "[E] project meeting (from: Mon 2pm to: 4pm)", "1.[T][ ] borrow book", "Bye! Keep learning"]
    },
    {
      "name": "handle invalid commands",
      "aim": "Verify empty todo, deadline, and event descriptions plus unknown commands produce helpful errors without ending the session.",
      "commands": ["", "todo", "deadline", "event", "mark", "unmark", "blah", "bye"],
      "expected": ["OOPS!!! I don't know what that means. Please enter a command.", "OOPS!!! The description of a todo cannot be empty.", "OOPS!!! A deadline needs a description and a /by value.", "OOPS!!! An event needs a description, /from value, and /to value.", "OOPS!!! Mark needs a task number.", "OOPS!!! Unmark needs a task number.", "OOPS!!! I don't know what that means.", "Bye! Keep learning"]
    },
    {
      "name": "invalid todo does not alter state",
      "aim": "Verify an empty todo is rejected and does not create a task before a valid todo is listed.",
      "commands": ["todo valid task", "todo", "list", "bye"],
      "expected": ["Now you have 1 tasks in the list.", "OOPS!!! The description of a todo cannot be empty.", "1.[T][ ] valid task", "Bye! Keep learning"]
    },
    {
      "name": "invalid deadline and event do not alter state",
      "aim": "Verify malformed deadline and event commands do not create phantom tasks between valid tasks.",
      "commands": ["deadline valid deadline /by Friday", "deadline missing by", "event valid event /from 2pm /to 3pm", "event missing times", "list", "bye"],
      "expected": ["Now you have 1 tasks in the list.", "OOPS!!! A deadline needs a description and a /by value.", "Now you have 2 tasks in the list.", "OOPS!!! An event needs a description, /from value, and /to value.", "1.[D][ ] valid deadline (by: Friday)", "Bye! Keep learning"]
    },
    {
      "name": "invalid mark commands do not alter completion state",
      "aim": "Verify valid marking works and invalid mark or unmark inputs do not change task completion state.",
      "commands": ["todo finish report", "mark 1", "mark abc", "mark 0", "unmark 1", "unmark xyz", "list", "bye"],
      "expected": ["Now you have 1 tasks in the list.", "[X] finish report", "Please provide a valid task number.", "Task number must be between 1 and 1.", "[ ] finish report", "Please provide a valid task number.", "1.[T][ ] finish report", "Bye! Keep learning"]
    }
  ]
}
```
