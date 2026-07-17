# Changelog

All notable changes to this project are documented here.
Format loosely follows [Keep a Changelog](https://keepachangelog.com/), grouped by phase/branch.

## [Unreleased]

### Phase 0 — Project Setup
- Converted project to Maven layout (`src/main/java`, `src/test/java`)
- Added `pom.xml` with JUnit 5 (Jupiter) and Mockito dependencies
- Added JaCoCo plugin for test coverage reporting
- Initialized Git repository with `main` and `develop` branches
- Added `.gitignore` and this `CHANGELOG.md`

### Phase 1 — SOLID Refactor (feature/solid-refactor)
- Split `GradeManager` (SRP violation: storage + calculation + printing) into:
    - `GradeManager` — storage/CRUD only
    - `GradeAverageCalculator` — pure calculation over `List<Grade>`
    - `GradeReportPrinter` — console formatting only
- Split `StudentManager` (same violation) into:
    - `StudentManager` — storage/CRUD only
    - `StudentAverageCalculator` — pure calculation over `List<Student>`
    - `StudentReportPrinter` — console formatting only
- Extracted `ConsoleApp` from `Main`: replaced static fields (managers, subjects,
  scanner) with constructor-injected instance fields. `Main` is now a 3-line
  entry point.
- No behavior changes — console output is identical to Lab 1/2 baseline.


### Phase 2 — Exception Handling (feature/exception-handling)
- Added `exception` package with a checked-exception hierarchy:
  - `GradeSystemException` — abstract base for all custom exceptions
  - `StudentNotFoundException` — thrown on an unknown student ID lookup
  - `InvalidGradeException` — thrown for non-numeric or out-of-range grade input
- `StudentManager.findStudent()` now throws `StudentNotFoundException` instead
  of silently returning `null`.
- `ConsoleApp` replaced its null-check-and-bail pattern with proper
  `try`/`catch` retry loops:
  - `promptForExistingStudent()` — shared by Record Grade and View Grade
    Report, catches `StudentNotFoundException`, shows the available student
    IDs, and asks "Try again? (Y/N)"
  - `promptForGrade()` / `parseGrade()` — catches `InvalidGradeException`
    for both non-numeric and out-of-range input, with the same retry prompt
- No behavior changes to successful paths — only failure handling changed.

### Phase 3 — Unit Tests on Core (feature/unit-tests-core)
- Added JUnit 5 test suites for the classes made testable by the Phase 1
  SRP split:
  - `GradeAverageCalculatorTest`, `StudentAverageCalculatorTest` — pure
    calculation logic, including null/empty edge cases
  - `GradeTest` — includes a `@ParameterizedTest` covering all letter-grade
    boundaries (A/B/C/D/F) via `@CsvSource`
  - `HonorsStudentTest` — honors eligibility threshold (85%) and
    re-evaluation as grades change
  - `RegularStudentTest` — passing threshold (50%) and grade validation
  - `StudentManagerTest`, `GradeManagerTest` — storage, lookup, and the new
    `StudentNotFoundException`
  - `StudentReportPrinterTest` — first Mockito-based test, isolating the
    printer from real `StudentManager`/`StudentAverageCalculator`
- Fixed `pom.xml`: removed a duplicate `maven-surefire-plugin` declaration,
  corrected `exec-maven-plugin`'s `mainClass` to `Main`, and added
  `-XX:+EnableDynamicAgentLoading -Xshare:off` to quiet harmless JVM
  warnings triggered by Mockito's dynamic agent loading.


### Phase 4 — New Features

#### feature/gpa-calculator
- Added `GPACalculator`: converts percentage grades to the 4.0 GPA scale
  (`toGpaPoints`, `toLetterGrade`) and computes cumulative GPA across a
  student's grades.
- Added `GPAReportPrinter`: prints per-subject GPA breakdown, cumulative
  GPA, overall letter grade, class rank, and a short performance analysis
  (comparing against the 3.5 GPA threshold and class average).
- Wired into `ConsoleApp` as new menu option **5. Calculate Student GPA**
  (existing Exit option shifted from 5 to 6).
- Unit tests: `GPACalculatorTest` (parameterized boundary tests for both
  GPA points and letter grades, plus cumulative-GPA and rounding tests).


#### feature/class-statistics
- Added `ClassStatisticsCalculator`: mean, median, mode, standard deviation,
  highest/lowest, grade distribution buckets (A/B/C/D/F), per-subject
  averages, and Regular vs. Honors student averages.
- Added `ClassStatisticsPrinter`: renders the distribution as a bar chart,
  prints the statistical summary, subject performance breakdown (Core vs.
  Elective, with per-subject detail), and student type comparison.
- Wired into `ConsoleApp` as new menu option **6. View Class Statistics**
  (Exit shifted from 6 to 7).
- Unit tests: `ClassStatisticsCalculatorTest` covering all calculations,
  including edge cases (empty lists, ties in mode, boundary scores for
  distribution buckets).


#### feature/search-students
- Added `StudentSearchService`: search by exact student ID, partial
  case-insensitive name match, average-grade range (inclusive), or
  student type (Regular/Honors).
- Wired into `ConsoleApp` as new menu option **7. Search Students**, with
  a sub-menu for the four search modes (Exit shifted from 7 to 8).
- Unit tests: `StudentSearchServiceTest` covering all four search modes,
  including no-match and partial-match cases.



#### feature/export-report
- Added `ReportExportException` for file-write failures.
- Added `ReportGenerator`: builds summary and detailed report text as a
  `String`, independent of where it ends up (console, file, etc.).
- Added `FileExporter`: writes report text to `reports/{filename}.txt`,
  creating the directory if needed, wrapping `IOException` into
  `ReportExportException`.
- Wired into `ConsoleApp` as new menu option **8. Export Grade Report**,
  supporting summary-only, detailed-only, or both (Exit shifted from 8 to 9).
- Unit tests: `ReportGeneratorTest` (summary/detailed content, empty-grades
  case) and `FileExporterTest` (real file I/O against a temp directory).

#### feature/bulk-import
- Added `InvalidFileFormatException` for malformed or unreadable CSV files.
- Moved CSV/import-specific classes into a `service.importing` subpackage
  to keep `service` from growing unbounded: `CSVGradeRecord`,
  `ImportFailure`, `BulkImportResult`, `CSVParser`, `BulkImportService`.
- `CSVParser`: reads and validates CSV structure only (header, column count) -
  parsing is fully separated from business validation.
- `BulkImportService`: validates each row (student exists, subject known,
  subject type matches, grade in range) and applies valid rows to the real
  `StudentManager`/`GradeManager`. Invalid rows are skipped, not thrown -
  the whole import continues and reports failures per row.
- Wired into `ConsoleApp` as new menu option **9. Bulk Import Grades**,
  reading from `imports/{filename}.csv` and writing a dated log file to
  `imports/import_log_YYYYMMDD.txt` (Exit shifted from 9 to 10).
- Unit tests: `CSVParserTest` (structure validation, malformed rows, blank
  lines) and `BulkImportServiceTest` (unknown student, out-of-range grade,
  unknown subject, subject-type mismatch, mixed success/failure files).