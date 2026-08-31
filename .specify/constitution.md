# Repository Constitution

Purpose
- Define standard behaviours and expectations for this monorepo.
- Provide a single source of truth so contributors follow consistent rules.

Scope
- Applies to all code, packages, services, tooling, and CI pipelines in this repository.

Monorepo layout and conventions
- Keep packages or services in clearly named top-level folders (e.g., /packages, /services).
- Use a single dependency management approach (workspaces: pnpm/yarn/lerna, or language-native monorepo tooling). Document chosen tool in README.
- Each package should include a concise README describing its public API, usage, and owner(s).

Branching and PRs
- Work on feature branches named <type>/<short-description> (e.g., feat/auth-improv).
- Open small, focused pull requests with a clear description and motivation.
- Include CHANGELOG or release notes when behavior or public API changes.

Code style and quality
- Follow existing formatting and linting rules. Run linters/formatters locally before committing.
- Keep commits atomic and well-described.

Testing policy — PROHIBITED
- Do NOT add, commit, or maintain any kind of test files in this repository. This includes unit tests, integration tests, end-to-end tests, snapshot tests, and any test-related scripts or fixtures.
- Test file patterns to avoid: *(spec|test|__tests__|.spec.|.test.)* — if detected in a PR they should be removed.
- CI must not be used to run tests for this repository. If CI detects test files, the PR should be rejected and the author asked to remove them.
- Exceptions: Only an explicit written approval by repository owners/maintainers may temporarily allow test files; approval must be recorded in the PR description and referenced in the issue tracking the change.

Enforcement
- Reviewers are expected to block PRs that introduce tests.
- Use pre-commit and CI checks where possible to detect test files and fail the build or block merge.
- If tests are required for a particular workflow, discuss with maintainers and obtain written approval.

Ownership and support
- Maintain a CODEOWNERS file or mention owners in READMEs for each package.
- For process exceptions (including tests), open an issue and tag the repository owners for approval.

License and attribution
- Keep repository license consistent and documented in LICENSE at the root.

Amendments
- This constitution may be amended by repository owners with a documented changelog entry. Significant changes should be communicated to contributors.

--
By contributing to this repository you agree to follow these conventions and the explicit prohibition on adding tests unless an approved exception exists.