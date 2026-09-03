# Continue for IntelliJ 2026.x

Community fork of [continuedev/continue](https://github.com/continuedev/continue) with
**API-compatibility fixes** so the IntelliJ plugin compiles and installs on
**IntelliJ IDEA 2026.1 / 2026.2** (and other JetBrains IDEs of the same generation).

> ⚠️ **Status of upstream:** `continuedev/continue` is **read-only / no longer
> actively maintained**. Continue's official focus is the CLI now. This fork is
> the only way to get a working JetBrains plugin for the 2026.x platform line
> as of September 2026.

## What this fork changes

Compared to upstream `v1.0.68`:

| File | Change |
|------|--------|
| `build.gradle.kts` | `jvmToolchain(17)` → `jvmToolchain(21)` (Java 17 runtime not always available for builds) |
| `continue/ConfigJsonSchemaProviderFactory.kt` | Disabled — `com.jetbrains.jsonSchema.extension.*` API removed in newer platform (JSON schema autocomplete for `config.json` is not available) |
| `continue/ConfigRcJsonSchemaProviderFactory.kt` | Disabled — same reason (`.continuerc.json`) |
| `editor/InlineEditPanel.kt` | Removed `UIUtil.HIDE_EDITOR_FROM_DATA_CONTEXT_PROPERTY` (constant removed in 2025.2+, was a cosmetic hint) |
| `error/ContinueErrorSubmitter.kt` | Migrated to `IdeaLoggingEvent` (`IdeaReportingEvent` removed; base `submit()` no longer abstract) |

Everything else is upstream. No runtime code was changed beyond the above.

## Build

```bash
git clone https://github.com/theWatcherNineteen83/continue-intellij-2026.git
cd continue-intellij-2026/extensions/intellij
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./gradlew buildPlugin
```

Artifact: `build/distributions/continue-intellij-extension-1.0.68.zip`

## Install

**Option A — from disk (recommended):**
1. IntelliJ IDEA 2026.x → **Settings / Preferences**
2. **Plugins** → ⚙ (gear icon) → **Install Plugin from Disk…**
3. Select `continue-intellij-extension-1.0.68.zip`
4. Restart IDE

**Option B — run in development:**
```bash
cd extensions/intellij
./gradlew runIde
```

## Using with a local Ollama (e.g. miniedi :11436)

After first launch:

1. **Continue** side panel → ⚙ **Configure**
2. **Models** tab → Add a **Provider**:
   - Type: `OpenAI Compatible` (works with any `/v1/chat/completions` endpoint)
   - Base URL: `http://miniedi:11436/v1` (or your Ollama host)
   - API Key: `ollama` (any non-empty string)
   - Model: `qwen3.8:27b`
3. Add a **Role**: `chat`, `edit`, `embed` (use a local embedding model for RAG)
4. **Test** connection → done

## Known limitations

- JSON schema autocomplete for `config.json` / `.continuerc.json` is **not
  available** (upstream API removed). Editing those files still works fine;
  you just lose the auto-complete hints.
- Compiled against SDK 2025.2.6 (the highest artifact JetBrains has published
  as a Maven dependency at time of build). `sinceBuild=241` means it installs
  on 2026.x, but **runtime behavior on 2026.2.1 has not been verified in a
  real IDE session** — the build is headless. If anything misbehaves, open an
  issue here (or file upstream).

## License

Apache-2.0 (upstream). See `LICENSE`.

## Contributing

Forks, PRs, and issues welcome. This fork exists because upstream is no longer
actively maintained; if you can get any of the removed APIs working again on
the 2026.x platform, please send a patch.
