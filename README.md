# VOID Launcher — Minimal Dark Android Launcher

A premium, distraction-free Android home replacement that prioritizes focus, speed, simplicity, and privacy.

## Features

- **Text-First Home Screen**: Pure minimalist interface featuring a responsive digital clock, formatted date, optional muted focus subtitle, and text-only favorite app names (no app icons or badge counts on Home).
- **Monochrome App Drawer**: Fast grid featuring automatic grayscale/monochrome icon processing, custom grid columns (3, 4, 5), configurable icon sizing (42dp, 48dp, 56dp), and long-press contextual controls.
- **Instant Local Search**: Fast, zero-latency local app search with 4-tier ranking (exact name match, prefix match, word-prefix match, substring match) and locale-aware tie breaking.
- **Customizable Favorites**: Reorder favorites via quick sorting dialog or contextual menu, add/remove favorites directly from search or app drawer.
- **Robust Persistence**: Backed by Jetpack DataStore Preferences for settings and ordered favorite component identities. Reconciles favorites automatically on package uninstall.
- **First-Run Onboarding**: 3-step setup explaining the philosophy, suggesting default essentials, and guiding the user to select VOID Launcher as their default Home app.
- **Privacy & Battery First**: 100% offline, zero data collection or telemetry, and zero persistent background services.

## Architecture

- **Language & Runtime**: Kotlin exclusively with Jetpack Compose.
- **Architecture Pattern**: MVVM with Repository pattern and Unidirectional Data Flow.
- **State Management**: Kotlin Coroutines, StateFlow, and immutable UI state models.
- **Data Persistence**: Jetpack DataStore Preferences.
- **Package Management**: Real `PackageManager` queries, dynamic `BroadcastReceiver` for package install/uninstall/change, and `RoleManager` default home integration.
