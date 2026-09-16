# Anders

Meet **Anders**, your lantern keeper: a calm woodland companion who helps you
find a path through todos, deadlines, and events, one small step at a time.

- **A gentle voice:** "A new trail marker. I've added this task" and
  "One more light along the path. Task marked as done."
- **A woodland desk:** forest green, warm parchment, and amber accents, with a
  serif title and readable system text.
- **A little light:** original vector lantern and compass icons in the header,
  conversation, and window icon. No external images or font downloads are needed.

## Run

Use **JDK 25**. On Windows:

```text
gradlew.bat run
```

On other platforms, use `./gradlew run` after configuring the JavaFX dependencies
in `build.gradle` for that platform. The current build targets Windows.

In IntelliJ, open this directory as a Gradle project, select JDK 25 as the project
and Gradle JVM, then run `anders.Launcher`. The console entry point is
`anders.Anders`.

Try `todo read chapter 1`, then `list` and `mark 1`. **Commands** in the window
explains all supported commands. See the [user guide](docs/README.md) for details.

Existing tasks continue to use `data/anders.txt`, and the original `anders`
package and launcher remain compatible. Build a distributable with
`gradlew.bat shadowJar`; the output is `build/libs/anders.jar`.

## Verify

```text
gradlew.bat check
python .codex/skills/test-ui/scripts/run_ui_tests.py
```

Run the console UI script in an isolated copy of the project because its fixtures
replace `data/anders.txt`. The [UI test plan](test/ui-test-plan.md) describes the
console cases and GUI checks. JUnit also renders GUI previews to
`build/reports/gui/` for visual review.
