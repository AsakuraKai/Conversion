# Sidebar Deduplication Roadmap

**Goal**: Remove sidebar entries that also live in Settings, keeping them Settings-only while preserving navigation coverage and tests.

**Scope**
- Only routes already present in the Settings tab.
- Affects sidebar models, view model state, drawer UI, nav host wiring, badges, deep links, and tests.

**Principles**
- One source of truth for route visibility (sidebar vs Settings-only).
- No broken deep links; Settings remains the entry point for these routes.
- Maintain permission gating where the feature stays (Settings path).

## Phases

### Phase 0: Alignment (0.5 day)
- Confirm the list of Settings-only routes (owner: PM/UI). Export final sidebar ordering.
- Capture in a short checklist to avoid reintroductions.

### Phase 1: Source-of-Truth Update (0.5 day)
- Add a `visibility` flag to the navigation route model (e.g., Sidebar, SettingsOnly).
- Update `NavigationRoute` mapping to tag Settings-only routes accordingly.
- Ensure badge metadata tolerates hidden routes.

### Phase 2: ViewModel & Data Flow (0.5 day)
- In `SidebarNavigationViewModel`, filter out SettingsOnly routes when building state.
- Remove badge counters and auto-collapse hooks for hidden routes.
- Keep state persistence keys stable; default selection must remain valid.

### Phase 3: UI & Nav Wiring (1 day)
- Update sidebar item list in `CollapsibleNavigationDrawer`/`CollapsibleSidebarLayout` to use the filtered list.
- Adjust dividers/sections and tooltips to match the new list.
- In `ConversionNavHost`, ensure Settings remains the navigation path for these routes; add redirects if any deep link previously targeted the sidebar path.
- Verify permission prompts now trigger from Settings entry points only.

### Phase 4: Tests (0.5-1 day)
- Unit: update `NavigationRouteTest`, `SidebarNavigationViewModelTest`, badge-related tests.
- UI/Instrumentation: adjust navigation flow tests so removed items are absent; ensure Settings flows still work.
- Add a guard test that asserts no Settings-only route is exposed in the sidebar list.

### Phase 5: Docs & Handoff (0.5 day)
- Update sidebar docs and the implementation index with the new menu structure and rationale.
- Note the visibility flag and checklist in developer docs.

### Phase 6: Verification (0.5 day)
- Manual pass on phone/tablet, light/dark; confirm no duplicates and Settings entries still function.
- Sanity-check deep links and back stack behavior.

## Deliverables
- Navigation model updated with visibility flag.
- Sidebar UI reflects pruned list; Settings retains full functionality for those routes.
- Tests updated with guard coverage against reintroducing duplicates.
- Documentation updated in roadmap and usage guides.

## Risks & Mitigations
- **Deep-link regressions**: add redirect/guard in `ConversionNavHost` and test deep links.
- **Badge/state drift**: ensure hidden routes are excluded from badge/state builders.
- **Permission gaps**: move gating logic to Settings entry points where needed.

## Acceptance Criteria
- Sidebar shows no items that also exist in Settings.
- All Settings-only routes remain reachable from Settings and via deep links.
- Nav/VM/tests/docs updated; builds and nav tests pass.
