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

### Test Coverage & Tooling
- Fixed `pom.xml`: JaCoCo's `argLine` property was being overwritten by a
  hardcoded surefire `argLine`, silently disabling coverage collection.
  Now appended via `@{argLine}` instead of replacing it.
- Excluded `Main`/`ConsoleApp` from the JaCoCo coverage target (console
  I/O/wiring layer, not business logic - see README's "Test Coverage"
  section for the full rationale). Overall coverage on the remaining
  scope: ~63% (service.importing 98%, model 61%, service 57%).
- `FileExporter`'s target directory is now constructor-injected rather
  than hardcoded, so tests can point it at a temp directory directly
  instead of relying on changing `user.dir` (which `java.nio.file`
  ignores at runtime since it caches the working directory at JVM
  startup).

### Documentation
- Added JavaDoc (`@param`/`@return`/`@throws`) to public methods across
  `model/` (Student, Subject, Grade) and `service/` (StudentManager,
  GradeManager, GradeAverageCalculator, StudentAverageCalculator,
  GPACalculator, ClassStatisticsCalculator, StudentSearchService,
  CSVParser, BulkImportService).

### Test Coverage Improvements
- Added `GradeReportPrinterTest`, `GPAReportPrinterTest`,
  `ClassStatisticsPrinterTest` - the three printer classes that previously
  only had manual/console verification, closing the largest remaining
  coverage gap in the `service` package.
### CI/CD Pipeline (ci/github-actions)
- Added `.github/workflows/ci.yml`: a GitHub Actions workflow that
  automatically runs `mvn clean test` on every push and pull request
  targeting `main` or `develop`, giving automated build/test
  verification without any manual step.
- Uploads the JaCoCo coverage report as a downloadable CI artifact on
  every run, so coverage can be inspected without regenerating it
  locally.
- Note: a static analysis tool (e.g. SpotBugs) was considered but not
  added - the graded Lab 2 rubric does not list it as a requirement
  (it only appears in the broader course objectives), and adding a new
  dependency purely for that wasn't judged worth the added complexity.

### CI Workflow Refinement (chore/ci-simplify)
- Removed the JaCoCo coverage report upload step from
  `.github/workflows/ci.yml`. Coverage is still generated on every CI
  run (via `mvn clean test`, which triggers JaCoCo's configured
  `prepare-agent`/`report` goals) - this change only stops it from
  being saved as a downloadable artifact, since that wasn't being used.
- CI now does exactly one thing: check out the code, set up JDK 17,
  and run the full test suite on every push/PR to `main` or `develop`.

## Lab 3 - Advanced Features

### Exportable Interface (feature/exporter-interface)
- Added `Exportable` interface in `model/` (alongside `Gradable`) — the
  first of the three interfaces (`Searchable`, `Exportable`, `Calculable`)
  originally requested back in Lab 2 and deliberately deferred at the
  time, since each concern only had one implementer then. `Exportable`
  now has three genuine implementers, which is exactly the situation
  where an interface earns its keep.
- `CsvGradeExporter` — writes grades in the same column format
  `CSVParser` expects on import, so exported data can be re-imported
  without a format mismatch.
- `JsonGradeExporter` — hand-built JSON (no new dependency), with proper
  string escaping and no trailing-comma bug.
- `BinaryGradeExporter` — uses Java's built-in object serialization
  (`ObjectOutputStream`/`ObjectInputStream`). Made `Grade` and `Subject`
  implement `Serializable`, with an explicit `serialVersionUID` on each.
- Wired into `ConsoleApp` as new menu option **10. Multi-Format Export**,
  looping over a `List<Exportable>` with zero per-format branching logic
  — adding a fourth format later requires no changes to this method.
- Unit tests: `CsvGradeExporterTest`, `JsonGradeExporterTest`, and
  `BinaryGradeExporterTest` (the last including a full serialize/
  deserialize round-trip test, proving real objects survive the export
  and can be read back intact).

### Searchable Interface (feature/searchable-interface)
- Added `Searchable` interface in `model/` (alongside `Gradable` and
  `Exportable`) - deliberately narrow, exposing only `search(String
  query)`, the one operation genuinely shared across every search
  strategy. Grade-range and type-based filtering stay as extra methods
  on the concrete classes that support them, rather than being forced
  onto every implementer - Interface Segregation applied directly.
- `StudentSearchService` now implements `Searchable`, delegating
  `search()` to its existing name-based partial matching. All four
  original search methods remain unchanged and fully usable.
- Added `RegexStudentSearchService` - a second, genuinely different
  implementer, matching student name/email against a regex pattern
  (`java.util.regex.Pattern`/`Matcher`), with a clear `IllegalArgumentException`
  for malformed patterns rather than letting a cryptic internal error
  leak through.
- Wired into `ConsoleApp`'s existing search sub-menu as new option
  **5. By Pattern (regex on name/email)**.
- Unit tests: `StudentSearchServiceTest` (updated for the new `search()`
  method) and `RegexStudentSearchServiceTest`, including a dedicated
  test proving both implementations are genuinely interchangeable
  through the shared `Searchable` type - the actual point of the
  interface, not just asserted but verified.
### Calculable Interface (feature/calculable-interface)
- Added `Calculable<T>` in `model/` — the third and last of the three
  interfaces (`Searchable`, `Exportable`, `Calculable`) originally
  requested back in Lab 2. Unlike the other two, this one is generic:
  `<T>` is a placeholder type filled in by whoever implements it, since
  the project's calculators don't share an identical method shape (they
  operate on different data types - `Grade` vs `Student`).
- `GradeAverageCalculator` implements `Calculable<Grade>`, delegating to
  its existing `calculateOverallAverage()`.
- `StudentAverageCalculator` implements `Calculable<Student>` -
  genuine proof the generic design works across two unrelated types with
  full compile-time type safety, no casting, no `Object`.
- `GPACalculator` also implements `Calculable<Grade>` - a second,
  different implementer with the same `T` as `GradeAverageCalculator`,
  demonstrating the interface describes a shared *shape* of operation,
  not one fixed formula.
- `ClassStatisticsCalculator` deliberately does NOT implement
  `Calculable` - it exposes several different summary statistics
  (mean, median, mode, standard deviation), and picking just one to
  satisfy a single `calculate()` method would be arbitrary and
  misleading, the same "don't force a fit" discipline applied when
  scoping `Searchable`.
- Unit tests: `CalculableTest` with two cases — one proving
  `Calculable<Grade>` and `Calculable<Student>` both work correctly
  through the same interface call, and one proving two different
  `Calculable<Grade>` implementers (`GradeAverageCalculator` vs
  `GPACalculator`) genuinely diverge on the same input data.

### HashMap-Backed Student Lookup (feature/hashmap-lookup)
- `StudentManager` now maintains a `HashMap<String, Student>` alongside
  its existing array, keyed by lowercase student ID.
- `findStudent()` changed from an O(n) linear scan through the array to
  an O(1) average-case HashMap lookup.
- The array remains the source of truth for `getAllStudents()` and
  `getAllStudentIds()` - HashMap does not guarantee iteration order, and
  registration order is what callers of those methods expect.
- Unit test added confirming lookup time stays roughly flat as the
  roster grows, rather than scaling linearly - a directional sanity
  check, not a strict benchmark, given natural JVM timing noise.
### TreeMap-Based GPA Rankings (feature/treemap-rankings)
- Added `GpaRankingService`, using `TreeMap<Double, List<Student>>` keyed
  by GPA - students sharing an identical GPA are grouped under the same
  key rather than silently overwriting each other, which a plain
  `TreeMap<Double, Student>` would have allowed.
- `buildRankingMap()` provides a continuously sorted view of the class
  by GPA. `getTopStudents(count)` walks the map in descending order for
  a leaderboard. `calculateRank(student)` correctly accounts for tied
  groups - if two students share the top GPA, the next distinct GPA
  is ranked third, not second.
- `GPAReportPrinter` no longer maintains its own rank calculation -
  it now delegates to `GpaRankingService`, removing a duplicated piece
  of logic that previously lived only inside the printer.
- Unit tests: `GpaRankingServiceTest`, including a dedicated test for
  the tied-group ranking edge case.

