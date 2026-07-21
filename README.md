# Student Grade Management System

A console app for tracking student grades - built in Java, starting from a
simple Lab 1 version and refactored into something closer to a real,
maintainable project: proper class separation, custom exceptions, unit
tests, and a Git workflow to match.

## What's in the project

```
src/main/java/
  Main.java            - entry point, just starts ConsoleApp
  ConsoleApp.java      - the menu loop, wires everything together

  model/               - Student, Subject, Grade and their subtypes
  service/             - managers, calculators, printers, search
  service/importing/   - CSV parsing and bulk import
  exception/           - custom exceptions

src/test/java/         - mirrors the structure above
```

Each service class does one job - a manager stores data, a calculator
does the math, a printer formats the output. That split is what makes
most of this testable without a lot of setup.

## Features

1. Add a student (Regular or Honors)
2. View all students
3. Record a grade
4. View a student's grade report
5. Calculate GPA (4.0 scale, with class rank)
6. View class statistics (mean, median, mode, std dev, distribution)
7. Search students by ID, name, grade range, or type
8. Export a grade report to a text file
9. Bulk import grades from a CSV file

## Exceptions

Four custom exceptions, all extending a shared `GradeSystemException` base:
`StudentNotFoundException`, `InvalidGradeException`, `ReportExportException`,
and `InvalidFileFormatException`. Each one gets caught where it matters in
`ConsoleApp`, usually with a clear message and a chance to try again.

## Test coverage

Coverage is measured with JaCoCo - run `mvn test` and open
`target/site/jacoco/index.html` to see the report.

`Main` and `ConsoleApp` are left out of the coverage target on purpose.
They're just the console menu and input handling, not real logic - testing
them properly would mean simulating fake keyboard input rather than
testing anything meaningful. All the actual logic lives in `model/`,
`service/`, and `service/importing/`, and that's what's measured.

## Git workflow

- `main` - the stable, finished version
- `develop` - where everything gets integrated before it's considered done
- `feature/*`, `bugfix/*`, `docs/*` - one branch per piece of work

Commits use a simple prefix convention (`feat:`, `fix:`, `refactor:`,
`test:`, `docs:`, `chore:`) so the history is easy to scan.

See `CHANGELOG.md` for the full history of what was built and when.