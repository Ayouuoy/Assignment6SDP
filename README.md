# Advanced File System — Visitor Pattern

Assignment 06 — Software Design Patterns  
Astana IT University

## Description

This project implements an in-memory file system using the Visitor Design Pattern in Java.

The file system contains files and directories and supports three operations:

- **TotalSizeVisitor** — calculates the total size of all files.
- **SearchVisitor** — searches for files by name and minimum size.
- **StatisticsVisitor** — counts files, directories, and maximum depth.

## Technologies

- Java 17
- Maven
- JUnit 5

## How to Run

Run the application:

```bash
mvn exec:java
```

Run all tests:

```bash
mvn clean test
```

Run with custom search parameters:

```bash
mvn exec:java "-Dexec.args=.java 500"
```

## Example Output

```text
Root: workspace
Total size: 2240 bytes
Files: 6
Directories: 7
Maximum depth: 3
Search: .log, minimum 100 bytes
reports/run.log
src/engine/trace.log
```

## Testing

The project includes 17 JUnit 5 tests covering Visitor behavior, file system operations, tree integrity, and edge cases.

## Design

TreeWalker handles tree traversal. Each element uses `accept()` to call the correct Visitor method through double dispatch.

New visitors can be added without changing the existing element classes.
