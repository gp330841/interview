# Spec-Driven Development (SDD) Framework

Welcome to the Spec-Driven Development (`.specify`) directory. This framework establishes a systematic, specification-first methodology for software development and AI-assisted engineering within this repository.

---

## 📁 Folder Structure

```
.specify/
├── README.md                      # SDD framework entry point & guide
├── config.json                    # Workspace SDD configurations
├── templates/                     # Standardized spec templates
│   ├── feature-spec-template.md   # Functional feature specs
│   ├── lld-spec-template.md       # Low-Level Design specs
│   ├── hld-spec-template.md       # High-Level Architecture specs
│   ├── api-spec-template.md       # REST/gRPC API contracts
│   └── bugfix-spec-template.md    # RCA & Bug fix specs
├── standards/                     # Engineering & architectural rules
│   ├── coding-standards.md        # Clean code, SOLID & Java standards
│   ├── api-design-guidelines.md   # REST conventions & error wrapping
│   └── security-and-resilience.md # Resilience & security guidelines
├── workflows/                     # SDD lifecycle & quality gates
│   ├── sdd-lifecycle-workflow.md  # 5-step SDD process
│   └── validation-checklist.md   # Definition of Done compliance
├── prompts/                       # AI Prompt Playbooks
│   ├── prompt-playbook.md         # Spec creation & code gen prompts
│   └── refactoring-prompts.md     # Code cleanup & optimization prompts
└── specs/                         # Pre-filled Production Specifications
    ├── distributed-search-spec.md # Distributed Document Search Engine
    ├── rate-limiter-spec.md       # Distributed Rate Limiter Service
    ├── lru-cache-spec.md          # Concurrent Thread-Safe LRU Cache
    ├── url-shortener-spec.md      # Key Generator & URL Shortener
    └── notification-service-spec.md # Event-Driven Notification System
```

---

## 🚀 SDD Core Workflow

1. **Spec First**: Always author or select a spec from `.specify/specs/` or create one using `.specify/templates/`.
2. **Review & Validate**: Validate specs against `.specify/standards/` and `.specify/workflows/validation-checklist.md`.
3. **AI Code Generation**: Use `.specify/prompts/prompt-playbook.md` recipes to generate or update code.
4. **Automated Verification**: Run build and test suites to verify implementation against the specification.
5. **Audit & Refactor**: Use `.specify/prompts/refactoring-prompts.md` for post-implementation code review.
