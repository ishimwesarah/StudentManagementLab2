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

<!--
### Phase 2 — Exception Handling (feature/exception-handling)
- ...

### Phase 3 — Unit Tests on Core (feature/unit-tests-core)
- ...

### Phase 4 — New Features
- feature/gpa-calculator
- feature/class-statistics
- feature/search-students
- feature/export-report
- feature/bulk-import
-->