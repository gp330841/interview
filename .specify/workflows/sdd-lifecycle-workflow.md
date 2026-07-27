# SDD Lifecycle Workflow Manual

This document outlines the 5-stage lifecycle for Spec-Driven Development.

```
+----------------+      +------------------+      +------------------+      +-------------------+      +-------------------+
| 1. Author Spec | ---> | 2. Validate Spec | ---> | 3. Generate Code | ---> | 4. Auto Verify    | ---> | 5. Audit & Refactor|
+----------------+      +------------------+      +------------------+      +-------------------+      +-------------------+
```

---

## Stage 1: Specification Authoring
- Identify feature requirements, LLD or HLD architecture.
- Select appropriate template from `.specify/templates/`.
- Fill all functional, non-functional, and data structure details. Zero placeholders.

## Stage 2: Spec Review & Validation
- Validate spec completeness against `.specify/standards/`.
- Ensure edge cases and boundary failure modes are explicitly specified.

## Stage 3: AI Code Generation
- Feed the spec into AI assistant using recipes from `.specify/prompts/prompt-playbook.md`.
- AI generates complete, production-ready code with no mock/stub placeholders.

## Stage 4: Automated Verification
- Run compiler (`mvn compile` / `javac`).
- Execute unit and integration tests (`mvn test`).

## Stage 5: Code Audit & Refactoring
- Audit generated code against `.specify/workflows/validation-checklist.md`.
- Use `.specify/prompts/refactoring-prompts.md` for clean code tuning.
