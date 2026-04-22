# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

NASAPicOnSpring is a Kotlin Telegram bot that serves NASA Astronomy Picture of the Day (APOD) images. It is deployed as a **serverless function on Yandex Cloud**, triggered via a Yandex Message Queue webhook rather than a long-polling loop. The live bot is at https://t.me/NASAPic_bot.

## Commands

```bash
# Build
./gradlew build

# Run all tests
./gradlew test

# Run a single test class
./gradlew test --tests "space.maxkonkin.nasapicbot.NASAPicOnSpringBotTest"
```

There is no local run target — the application is designed to run as a Yandex Cloud serverless function. Use test suites to validate changes locally.

## Architecture

The app has two entry points, both implementing `YcFunction`:

- **`Handler`** — receives Telegram `Update` objects forwarded from a Yandex Message Queue trigger (webhook flow).
- **`TimerHandler`** — invoked by a Yandex Cloud scheduled trigger to push the daily APOD to users who have opted in.

**Request flow (webhook):**
```
Telegram → API Gateway → Yandex Message Queue → Handler.kt
  → NASAPicOnSpringBot (command routing)
  → NasaService (fetch/cache APOD via NasaApiClient + NasaRowTableRepository)
  → TranslateService (optional RU translation via Yandex Translate API)
  → TelegramClient (send response)
```

**Layers:**

| Package | Responsibility |
|---|---|
| `web/` | Entry points (`Handler`, `TimerHandler`) and bot command routing (`NASAPicOnSpringBot`) |
| `service/` | Business logic: `NasaService`, `UserService`, `TranslateService`, `MessageService` |
| `client/` | HTTP clients for NASA APOD API, Yandex Translate API, and Yandex Cloud trigger management |
| `repository/` | YDB (Yandex Database) access — user records and APOD cache (`EntityManager` owns the YDB session) |
| `config/` | Koin DI module definitions (`AppConfig`) and YAML config loaders |
| `model/` / `to/` | Domain models and API DTOs |

**Dependency Injection:** Koin. All wiring is in `AppConfig.kt`. Koin is started once per cold start inside `Handler.init {}`.

**Configuration:** Loaded from `src/main/resources/application-${PROFILE}.yaml` at startup. `PROFILE` is an environment variable (`dev`, `prod`, etc.). Localised user-facing strings live in `messages.yaml` and are accessed via `MessageService`.

**Database:** YDB is used for two tables — users (chat_id, language, schedule flag) and a NASA image cache (avoids redundant API + translation calls for the same date).

## Key Environment Variables

| Variable | Purpose |
|---|---|
| `PROFILE` | Selects the YAML config file (`application-${PROFILE}.yaml`) |
| `BOT_TOKEN` | Telegram bot token |
| `YA_API_TOKEN` | Yandex Cloud API key (used for Translate + Cloud SDK auth) |
| `DATABASE` | YDB endpoint/database path |

## Testing Conventions

Tests use JUnit 5 + MockK. All service/handler tests mock their dependencies via `mockk<>()` and use `every { } returns` / `verify { }`. Test files mirror the main source tree under `src/test/kotlin/`.
