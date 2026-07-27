# Prompt Playbook for Spec-Driven Development

This playbook contains curated prompt templates for working with AI assistants in an SDD workflow.

---

## 1. Prompt Recipe: Generating Code from Specification

```markdown
Role: Senior Staff Software Engineer
Task: Generate production-grade Java implementation for the following specification.

Specification Document:
[Paste Specification markdown here]

Rules:
1. Follow Java 17+ best practices and SOLID principles.
2. Ensure full thread-safety for all concurrent operations.
3. Write clean, readable code with zero placeholders, stubs, or TODO comments.
4. Include robust exception handling and SLF4J logging.
5. Provide unit tests covering happy path and edge cases.
```

---

## 2. Prompt Recipe: Generating Specification from Architecture Idea

```markdown
Role: Principal Systems Architect
Task: Create a Low-Level Design Specification using .specify/templates/lld-spec-template.md.

System Description:
[Describe system concept here]

Output Requirements:
- Complete class diagrams and interface definitions.
- Detailed data structures and time/space complexity analysis.
- Concurrency model and error handling strategy.
- Zero placeholder sections.
```

---

## 3. Prompt Recipe: Test Suite Generation

```markdown
Role: QA Automation & Engineering Specialist
Task: Generate JUnit 5 unit tests for the provided specification and Java class.

Target Class: [ClassName]
Specification: [Path to spec file]

Requirements:
- Use AssertJ and Mockito.
- Cover boundary conditions, concurrency races, and negative exception cases.
```
