# Claude AI Code Review Configuration

## Purpose
This document defines how the Claude AI agent reviews code changes in the
`user-api` Spring Boot project. The agent runs as part of the **AI Review**
stage of the CI/CD pipeline and is the final gate before deployment.

## Project Context
- **Language:** Java 21
- **Framework:** Spring Boot 3.x
- **Build:** Maven
- **Architecture:** Layered (controller → service → repository → entity)

## Responsibilities
The Claude agent MUST review the diff against the following criteria:

1. **Java Code Quality**
   - Adherence to SOLID principles.
   - No code smells (long methods, deep nesting, duplicated logic, etc.).
   - Proper use of Lombok annotations and avoidance of getters/setters in
     business logic.
   - Constructor injection only — flag any field/setter injection.
   - Use of `final` for fields where appropriate.

2. **Architecture**
   - Layered architecture is preserved (controller → service → repository).
   - No business logic in controllers.
   - No persistence concerns leaking into the DTO layer.
   - Proper transaction boundaries (`@Transactional` on service methods).

3. **Security**
   - No hardcoded secrets, credentials, or tokens.
   - Inputs are validated.
   - No SQL injection risk.
   - No sensitive data in logs.
   - Error responses do not leak stack traces to clients.

4. **Test Coverage**
   - Every new public method in the service or controller layer MUST have
     a corresponding unit test.
   - Tests must follow Arrange-Act-Assert structure.
   - Tests must use Mockito for dependencies and AssertJ for assertions.
   - JaCoCo coverage MUST be ≥ 85% line coverage.

5. **Maintainability**
   - Clear and consistent naming.
   - Javadoc on public APIs.
   - No dead code or commented-out blocks.
   - SLF4J logging used everywhere (never `System.out.println`).

6. **Dependency Risks**
   - New dependencies must be from a trusted source.
   - New dependencies must have a current version and a clear license.
   - No snapshot or unstable dependencies in production code.

7. **Coding Standards**
   - Code must compile under Java 21.
   - Maven build MUST succeed with no warnings.
   - Spotless / Checkstyle rules are respected (when configured).

## Decision Logic
The agent MUST return exactly one of two decisions:

- **PASS** — All criteria are satisfied. The pipeline can proceed to
  deployment.
- **FAIL** — At least one criterion is violated. The pipeline MUST stop
  and surface the findings to the developer.

The agent's response MUST include:
- The decision (PASS / FAIL).
- A bulleted list of findings with file:line references and severity.
- Specific, actionable remediation suggestions for every FAIL finding.

## Operating Notes
- The agent is invoked via the `claude-ai-action` GitHub Action.
- The agent does not have write access — it only reviews and reports.
- The agent SHOULD read `review-agent.md` for its runtime configuration.
