# Study Guide: Claude Code in Action & Exam Prep

This study guide covers the technical architecture, configurations, and core workflows of **Claude Code**—Anthropic's agentic command-line developer assistant. It targets the domains tested in the **Claude Certified Architect – Foundations (CCA-F)** exam.

---

## 1. CLI Commands & Session Management

Claude Code runs directly in the terminal, interacting with your local directory structure, git history, and shell utilities.

### Basic CLI Commands
When inside an interactive `claude` session, you can use the following slash commands:

| Command | Action | Exam/Use Case Context |
| :--- | :--- | :--- |
| `/help` | Display help menu and available slash commands. | General usage support. |
| `/clear` | Clear the current conversation history/context. | Use when starting a completely different task to save tokens and prevent context pollution. |
| `/search <query>` | Search codebase using grep/ripgrep under the hood. | Semantic and literal matches. |
| `/add <file>` | Add specific files to the current context window. | Explicitly feeding relevant files to Claude. |
| `/remove <file>` | Remove specific files from the context window. | Token conservation / pruning context. |
| `/config` | View and edit user/session configuration. | Customizing terminal behaviors and model parameters. |
| `/bug` | File a bug report. | Troubleshooting tool issues. |
| `/quit` or `/exit` | Exit the current Claude Code session. | Closing interactive session. |

---

## 2. Context Management: `CLAUDE.md` Architecture

A key concept in managing developer context is `CLAUDE.md`. This file resides in the root of the project (and optionally subdirectories) to guide Claude Code on how to build, test, and style code inside this repository.

### Core Sections of a `CLAUDE.md` File:
```markdown
# Project Guidelines

## Build Commands
- Build project: `mvn clean install` or `npm run build`
- Build specific modules: `mvn -pl :sub-module compile`

## Test Commands
- Run all tests: `npm test` or `mvn test`
- Run single test: `mvn test -Dtest=MyTestClassName`

## Coding Style Guidelines
- Language version: Java 21, Python 3.11
- Naming conventions: CamelCase for variables, SCREAMING_SNAKE_CASE for constants
- Framework rules: Always prefer record classes over standard POJOs for DTOs
- Imports structure: Organize alphabetically, group standard libraries first
```

### Path-Specific Rules
If you have different rules for different subfolders, you can place nested `CLAUDE.md` files in subdirectories (e.g., `ai/CLAUDE.md`). Claude Code combines these, prioritizing the closest config to the file being edited.

---

## 3. Workflow Control: Plan Mode vs. Direct Execution

Claude Code works by executing multi-step loops. Understanding the lifecycle of these loops is crucial.

*   **Plan Mode:** Useful for complex architectural refactoring. Claude drafts a markdown plan (like `implementation_plan.md`) describing modifications first, then seeks explicit user approval.
*   **Direct Execution:** Running single commands or short questions without entering Plan Mode. Useful for direct, simple tweaks.
*   **Command Approvals (Safety Controls):**
    *   **Low-risk actions:** Reading files, directory listings (executed automatically).
    *   **High-risk actions:** Modifying files, executing terminal scripts, making web requests. These prompt the user: `[y/N]` to approve execution.
    *   *Exam Tip:* Understand how sandboxing works. Standard sandbox restricts network access and external directory writes. Bypassing sandbox mode allows broader access but poses higher security risk and requires explicit developer confirmation.

---

## 4. Model Context Protocol (MCP) Integration

The **Model Context Protocol (MCP)** is an open standard that allows developers to build secure, modular connectors (servers) that expose data sources and tools to LLMs.

```
┌─────────────┐             ┌────────────┐             ┌─────────────┐
│ Claude Code │  ◄────────  │ MCP Client │  ◄────────  │ MCP Server  │
└─────────────┘             └────────────┘             └─────────────┘
                                                              │
                                                ┌─────────────┼─────────────┐
                                                ▼             ▼             ▼
                                           [Databases]     [APIs]    [Local Files]
```

*   **Capabilities of MCP:** Exposing databases, custom build servers, Slack channels, GitHub issues, or search engines.
*   **Configuration:** Configured in `claude.json` or custom system configs. Exposing tools to Claude enables it to query live services on your behalf.
*   **Security:** MCP servers run locally or via authorized remote tokens, respecting local sandboxing rules.

---

## 5. Advanced CI/CD & Hook System

You can run Claude Code in non-interactive environments:
*   **Git Hooks:** Integrating Claude to run validation checks before commit (`pre-commit` hooks) or automatically format code description during commits.
*   **GitHub Actions / CI/CD:** Auto-analyzing Pull Request diffs, running tests, and posting code review comments back directly on GitHub.
