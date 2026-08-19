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
    }
  ]
}
```
