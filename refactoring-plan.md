# HttpExecutor Refactoring Plan

## Current Issues
- `HttpExecutor.execute()` directly uses `System.out.println()` for output (lines 43-45, 54)
- Tests capture `System.out` via `captureOutput()` method to verify printed content
- The method returns `void`, making it hard to test the response handling separately

## Proposed Refactoring Plan

1. **Create ResponsePrinter class** - A new class that handles all output formatting and printing using a configurable `PrintStream` (defaulting to `System.out` for backward compatibility, but injectable for testing)

2. **Modify HttpExecutor.execute()** - Change return type from `void` to `HttpResponse<String>` to return the actual response object

3. **Update HttpExecutor** - Inject `ResponsePrinter` instance and use it for all output operations instead of direct `System.out` calls

4. **Refactor tests** - Update `JCurlExecutorTest` to assert on the returned `HttpResponse` instead of captured output strings

5. **Create ResponsePrinter tests** - New test class to verify output formatting logic independently

## ResponsePrinter Design
- Constructor accepts `PrintStream` (for testability)
- Methods: `printHeaders(HttpResponse)`, `printBody(String)`, `printStatusLine(HttpResponse)`
- Handles the conditional printing based on `verbose`/`includeHeaders` flags

This approach separates concerns: `HttpExecutor` handles HTTP logic and returns responses, `ResponsePrinter` handles output formatting, and tests can verify each independently.