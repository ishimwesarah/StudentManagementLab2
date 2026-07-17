# Student Grade Management System

A console application for managing student grades, built with core OOP
principles (encapsulation, inheritance, polymorphism, abstraction, composition)
and hardened with SOLID principles, custom exception handling, and JUnit 5 /
Mockito test coverage.

## Requirements

- JDK 17 or later
- Maven 3.8+ (or use IntelliJ's bundled Maven via the Maven tool window)

## Project Structure

```
src/main/java/
  Main.java                    # Entry point
  ConsoleApp.java               # Console menu loop, wiring (constructor-injected dependencies)
  model/                        # Domain entities
    Student.java (abstract), RegularStudent.java, HonorsStudent.java
    Subject.java (abstract), CoreSubject.java, ElectiveSubject.java
    Grade.java, Gradable.java (interface)
  service/                      # Operations on domain entities
    StudentManager.java, GradeManager.java        # storage/CRUD
    StudentAverageCalculator.java, GradeAverageCalculator.java   # pure calculation
    StudentReportPrinter.java, GradeReportPrinter.java            # console formatting
    GPACalculator.java, GPAReportPrinter.java
    ClassStatisticsCalculator.java, ClassStatisticsPrinter.java
    StudentSearchService.java
    ReportGenerator.java, FileExporter.java
  service/importing/            # CSV bulk-import (kept separate to avoid
                                 # bloating the service package)
    CSVParser.java, CSVGradeRecord.java
    BulkImportService.java, BulkImportResult.java, ImportFailure.java
  exception/                    # Custom checked exceptions
    GradeSystemException.java (abstract base)
    StudentNotFoundException.java, InvalidGradeException.java
    ReportExportException.java, InvalidFileFormatException.java

src/test/java/
  model/                         # Mirrors src/main/java structure
  service/
  service/importing/
```

## Build

```bash
mvn clean compile
```

Or in IntelliJ: open the **Maven** tool window (right edge) → Lifecycle → double-click **compile**.

## Run the Application

```bash
mvn compile exec:java
```

Or in IntelliJ: right-click `Main.java` → **Run**.

## Run Tests

```bash
mvn test
```

Or in IntelliJ: right-click `src/test/java` → **Run 'All Tests'**.

Currently: 125+ tests, all passing, covering the model, service, and
service.importing packages.

## Test Coverage

Coverage is measured with JaCoCo. After running `mvn test`, open the report:

```
target/site/jacoco/index.html
```

**Scope note:** `Main` and `ConsoleApp` are excluded from the coverage
target. They are the console I/O and menu-wiring layer - testing them
meaningfully would require feeding fake `System.in` input rather than
testing real logic, and the Phase 1 SOLID refactor already moved all
actual business logic out of them into `model/`, `service/`, and
`service.importing/`, which are the packages measured and targeted for
coverage.

## Features

1. **Add Student** - Regular (50% passing) or Honors (60% passing, honors
   eligibility tracking)
2. **View Students** - Full roster with averages, status, and honors
   eligibility
3. **Record Grade** - Core or Elective subjects, with retry-on-invalid-input
   handling
4. **View Grade Report** - Per-student grade history with core/elective/
   overall averages
5. **Calculate Student GPA** - Converts percentages to a 4.0 GPA scale,
   including per-subject breakdown and class rank
6. **View Class Statistics** - Mean, median, mode, standard deviation, grade
   distribution, subject and Regular-vs-Honors comparisons
7. **Search Students** - By ID, partial name match, grade range, or student
   type
8. **Export Grade Report** - Summary and/or detailed report to a `.txt` file
   under `reports/`
9. **Bulk Import Grades** - Import multiple grades from a CSV file under
   `imports/`, with per-row validation and a dated import log

## Exception Handling

Custom checked exceptions (all extending `GradeSystemException`) replace
silent failures and generic exception handling:

- `StudentNotFoundException` - unknown student ID lookup
- `InvalidGradeException` - non-numeric or out-of-range grade input
- `ReportExportException` - file-write failure during export
- `InvalidFileFormatException` - malformed or unreadable CSV during import

Each is caught at the point of use in `ConsoleApp` with a clear error
message and, where it makes sense, a "Try again? (Y/N)" retry prompt.

## Git Workflow

This project follows a feature-branch workflow:

- `main` - production-ready code only
- `develop` - integration branch, features merge here first
- `feature/*` - one branch per feature/refactor (e.g. `feature/gpa-calculator`)
- `bugfix/*` - bug fixes found during development

Commits follow the [Conventional Commits](https://www.conventionalcommits.org/)
format: `feat: ...`, `fix: ...`, `refactor: ...`, `test: ...`, `docs: ...`,
`chore: ...`.

See `CHANGELOG.md` for a phase-by-phase history of what's been built.