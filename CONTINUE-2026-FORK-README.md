# Continue for IntelliJ 2026.x

Community fork of [continuedev/continue](https://github.com/continuedev/continue) with
**API-, Laufzeit- und Build-Fixes**, damit das JetBrains-Plugin auf
**IntelliJ IDEA 2026.1 / 2026.2** (und anderen JetBrains-IDEs derselben Generation)
kompiliert, installiert **und läuft**.

> ⚠️ **Upstream-Status:** `continuedev/continue` ist **read-only / nicht mehr aktiv
> gepflegt** (letzter Release 2.0.0, Fokus liegt auf der CLI). Dieser Fork ist aktuell
> (September 2026) der Weg zu einem funktionierenden JetBrains-Plugin für die 2026.x-Linie.

## Status

- ✅ **Verifiziert auf IntelliJ IDEA 2026.2.1 (Windows x64)** — Plugin startet, Chat-UI rendert
- ✅ Native Binaries für **linux-x64** und **win32-x64** gebaut und getestet
- ✅ Release-Asset `continue-intellij-extension-1.0.68.zip` enthält beide Plattformen

## Was dieser Fork ändert

Gegenüber upstream `v1.0.68`:

### API-Kompatibilität (Kotlin/Java)

| Datei | Änderung |
|-------|----------|
| `build.gradle.kts` | `jvmToolchain(17)` → `jvmToolchain(21)` (Java 17 nicht überall verfügbar) |
| `continue/ConfigJsonSchemaProviderFactory.kt` | deaktiviert — `com.jetbrains.jsonSchema.extension.*` in neueren Plattformen entfernt |
| `continue/ConfigRcJsonSchemaProviderFactory.kt` | deaktiviert — gleicher Grund |
| `editor/InlineEditPanel.kt` | `UIUtil.HIDE_EDITOR_FROM_DATA_CONTEXT_PROPERTY` entfernt (Konstante in 2025.2+ entfernt) |
| `error/ContinueErrorSubmitter.kt` | auf `IdeaLoggingEvent` migriert (`IdeaReportingEvent` entfernt) |

### Laufzeit (2026.2)

| Datei | Änderung |
|-------|----------|
| `META-INF/plugin.xml` | `<depends optional="true">com.intellij.modules.jcef</depends>` ergänzt. JCEF wurde in 2026.2 in ein separates gebündeltes Plugin ausgelagert — ohne diese Dependency wirft der Browser-Service `NoClassDefFoundError: com/intellij/ui/jcef/JBCefApp`. |

### Binary-Build (Node)

| Datei | Änderung |
|-------|----------|
| `binary/build.js` | esbuild-`alias`-Block ergänzt, damit die Workspace-Pakete (`core`, `@continuedev/*`) beim Bundling aufgelöst werden |
| `binary/pkgJson/{linux,win32}-x64/package.json` | `node18-*` → `node24-*` (der Code nutzt `node:sqlite`, ein Node-22+-Built-in) |
| `binary/utils/bundle-binary.js` | `npx pkg` → `npx @yao-pkg/pkg` (das alte `pkg` 5.x bündelt nur Node 18) |

## Build

Der vollständige Build ist mehrstufig (Reihenfolge wichtig):

```bash
# 0) Repo klonen
git clone https://github.com/theWatcherNineteen83/continue-intellij-2026.git
cd continue-intellij-2026

# 1) Workspace-Pakete bauen
node ./scripts/build-packages.js

# 2) Core installieren + bauen
cd core && npm ci && npm run build && cd ..

# 3) GUI bauen (React → webview/assets) — WICHTIG, sonst leeres Chat-Fenster
cd gui && npm ci && npm run build && cd ..
# gui/dist → extensions/intellij/src/main/resources/webview/ kopieren
# (offiziell via: cd extensions/vscode && npm ci && npm run prepackage)

# 4) Binary bauen (lädt node24-Base-Binary via @yao-pkg/pkg)
cd binary && npm ci && npm run build && cd ..

# 5) Plugin bauen
cd extensions/intellij
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./gradlew buildPlugin
```

Artifact: `extensions/intellij/build/distributions/continue-intellij-extension-1.0.68.zip`

> **Hinweis npm-Version:** Schritt 3 (`gui`) braucht **npm 10** — npm 9.2.0 scheitert
> an den verschachtelten `file:`-Dependencies (`ENOENT .../node_modules/packages/fetch/package.json`).

## Install

**Aus dem Release (empfohlen):**
1. Release-Asset laden:
   https://github.com/theWatcherNineteen83/continue-intellij-2026/releases/download/v1.0.68-2026/continue-intellij-extension-1.0.68.zip
2. IntelliJ IDEA 2026.x → **Settings / Preferences**
3. **Plugins** → ⚙ → **Install Plugin from Disk…** → ZIP auswählen
4. IDE neu starten

**Aus dem Build (Entwicklung):**
```bash
cd extensions/intellij
./gradlew runIde
```

## Ollama-Anbindung (lokal, z. B. miniedi)

Der Config-Key für den Ollama-Provider heißt `apiBase` (nicht `baseUrl`).
`AUTODETECT` sucht nur auf `localhost:11434` — bei Remote-Ollama muss `apiBase`
explizit gesetzt und das Modell fest angegeben werden.

```yaml
name: Main Config
version: 1.0.0
schema: v1
models:
  - name: Qwen 3.8 27B
    provider: ollama
    model: qwen3.8:27b
    apiBase: http://192.168.22.204:11436
```

- Kein API-Key nötig (Ollama läuft ohne Auth).
- Alternative zu Ollama: `provider: openai` mit `apiBase: http://host:11436/v1` + `apiKey` (beliebig) + `model: qwen3.8:27b`.
- Für Embedding/RAG: zusätzlich `provider: ollama`, `model: nomic-embed-text`, `apiBase: http://host:11438`, Role `embed`.

## Bekannte Einschränkungen

- **JSON-Schema-Autovervollständigung** für `config.json` / `.continuerc.json` ist
  **nicht verfügbar** (upstream-API entfernt). Bearbeiten funktioniert, nur die
  Auto-Complete-Hints fehlen.
- Kompiliert gegen **SDK 2025.2.6** (höchstes von JetBrains als Maven-Dependency
  veröffentlichtes Artifact). `sinceBuild=241` erlaubt Installation auf 2026.x.
- Nur **linux-x64** und **win32-x64** als Binaries gebaut (kein macOS/ARM).

## Lizenz

Apache-2.0 (upstream). Siehe `LICENSE`.

## Mitmachen

Forks, PRs und Issues willkommen. Der Fork existiert, weil upstream nicht mehr
aktiv gepflegt wird — wer eine der entfernten APIs wieder auf der 2026.x-Plattform
zum Laufen bringt, ist herzlich eingeladen, einen Patch zu schicken.
