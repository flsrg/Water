# AGENTS.md

Guidance for Codex agents working in this repository.

## Project Context

This project is a Kotlin Multiplatform water reminder app with Compose UI for Android and iOS targets.

Current project shape is a newly created KMP app with:

- `androidApp` for the Android host.
- `iosApp` for the iOS host.
- `shared` for common Kotlin and Compose code.
- `config/detekt/detekt.yml` for Detekt configuration.

The intended architecture is multi-module, Kotlin Multiplatform, Compose, and MVI.

## Code Navigation: CodeGraph and Graphify First

Use repository intelligence tools before broad filesystem search.

1. Start substantial codebase tasks with CodeGraph:
   - `codegraph_status` to confirm the index is present and fresh.
   - `codegraph_files` for project layout.
   - `codegraph_search` for classes, functions, routes, screens, ViewModels, repositories, and modules.
   - `codegraph_explore` for understanding related code across files.
   - `codegraph_callers`, `codegraph_callees`, and `codegraph_impact` before changing a shared symbol or tracing behavior.
2. Use Graphify after CodeGraph for project-wide architecture, relationship, and documentation questions:
   - If `graphify-out/graph.json` exists, prefer `graphify query "<question>"` for architecture and relationship searches.
   - Do not rebuild or update Graphify automatically unless the user asks for it or the task clearly requires it.
3. Use `rg`, direct file reads, Gradle commands, and other shell tools only after CodeGraph/Graphify are unavailable, stale, insufficient, or when exact current file contents are needed for editing.
4. Avoid broad `find`, `ls -R`, grep loops, or repeated file reads when CodeGraph can answer the question.

## Relevant Codex Skills

Use these existing Codex skills when the task matches their domain. Open the skill instructions before acting.

- `android-module-structure`: module layout, dependency direction, feature modules, core modules, build-logic decisions.
- `android-gradle-logic`: Gradle convention plugins, version catalogs, build-logic setup.
- `android-presentation-mvi`: ViewModels, screen state, actions, one-time events, Root/Screen composable split, UI models.
- `android-navigation`: type-safe Compose navigation, route objects, feature nav graphs, cross-feature callbacks.
- `android-data-layer`: data sources, repositories, DTOs, mappers, Ktor, Room, offline-first patterns.
- `android-error-handling`: typed `Result`, `Error`, `DataError`, `EmptyResult`, and result helper extensions.
- `android-coroutines`: structured concurrency, Flow/StateFlow, lifecycle safety, cancellation, dispatcher decisions.
- `android-testing`: ViewModel, repository, Flow, Compose UI, fake dependencies, and robot-pattern tests.
- `edge-to-edge`: Android system bar, IME, and inset issues.
- `compose-performance-audit`: recomposition, jank, slow rendering, and Compose performance review.
- `test-android-apps:android-emulator-qa`: emulator validation, screenshots, UI inspection, logcat.
- `graphify`: architecture and project relationship queries when a graph exists or when the user asks to build one.

## Architecture Direction

Prefer feature-layered modularization as the app grows.

Target module layout:

```text
:androidApp or :app
:shared
:build-logic
:core:domain
:core:data
:core:presentation
:core:design-system
:feature:<name>:domain
:feature:<name>:data
:feature:<name>:presentation
```

Dependency rules:

- Domain modules are pure Kotlin/KMP and must not depend on data or presentation.
- Presentation depends on its own feature domain plus shared core presentation/design-system modules.
- Data depends on its own feature domain plus shared core data/domain modules.
- Feature modules must not depend on other feature modules.
- Cross-feature shared contracts belong in `core:domain`; shared UI utilities belong in `core:presentation` or `core:design-system`.
- Host app modules wire features together.

For this water reminder app, likely feature areas include reminders/schedule, hydration tracking, onboarding/settings, notifications, and history/statistics. Keep each feature self-contained until sharing is justified.

## Presentation and MVI

Use MVI for screens:

- `State`: a single immutable data class for all renderable state.
- `Action`: a sealed interface for user/system intents.
- `Event`: a sealed interface for one-time effects like navigation and snackbars.
- `ViewModel`: exposes read-only `StateFlow<State>` and event `Flow`; processes actions through `onAction`.

Compose structure:

- Keep `FeatureRoot` and `FeatureScreen` in the same file when practical.
- `Root` owns ViewModel collection and event observation.
- `Screen` receives only `state` and `onAction`, no ViewModel.
- Use UI models suffixed with `Ui` when domain data needs presentation formatting.
- Use `UiText` for strings that can come from resources or need localization.

## Data and Errors

- Domain defines models and data source/repository interfaces.
- Data defines DTOs, entities, mappers, and implementations.
- Keep DTOs/entities separate from domain models.
- Prefer data source names for single-source classes, e.g. `RoomReminderDataSource`.
- Use repository names only when coordinating multiple sources, e.g. `OfflineFirstReminderRepository`.
- Do not suffix implementations with `Impl`; name them for what they wrap or how they behave.
- Use typed `Result<T, E>` / `EmptyResult<E>` for expected failures.
- Do not throw exceptions for expected failures; map them into typed errors at the owning layer.

## Coroutines and Flow

- Use structured concurrency.
- Do not expose mutable flows publicly.
- ViewModels should expose state/events, not suspend functions for the UI to call.
- Preserve cancellation; do not swallow `CancellationException`.
- Inject dispatchers only where it improves testability or where a class performs non-main work and is directly unit tested. Follow the more specific local pattern when existing code has one.

## Navigation

- Use type-safe Compose navigation with `@Serializable` route objects.
- Define one nav graph per feature in that feature's presentation module.
- Assemble feature graphs in the host app module.
- Use callbacks for cross-feature navigation to keep features decoupled.
- Pass IDs through routes, not complex domain objects.

## Testing

Add focused tests when behavior changes, especially for:

- ViewModel state transitions and events.
- Domain use cases and validation.
- Repository/data-source behavior with non-trivial mapping or persistence.
- Compose screens with meaningful conditional UI.

Prefer fakes over mocks for repositories and data sources. Use Turbine for Flow/StateFlow tests and Compose test robots for complex UI flows.

## Static Checks and Token Budget

Do not run Detekt, ktlint/Catalynt, Android lint, or broad static-analysis checks automatically after every change.

Run static checks only when:

- The user explicitly asks for them.
- The task is specifically about lint/static analysis.
- You changed the corresponding configuration.
- A targeted check is needed to verify a suspected issue.

When verification is useful, prefer the narrowest relevant Gradle task or test first. Save full static-check sweeps for intentional cleanup passes.

## Editing Preferences

- Keep edits scoped to the requested behavior.
- Follow existing naming, package, and module conventions.
- Prefer version catalogs and convention plugins for dependency/build changes.
- Do not introduce cross-feature dependencies.
- Do not add abstractions until there is real duplication or module-boundary pressure.
- Do not overwrite user changes. If the worktree is dirty, preserve unrelated edits.

