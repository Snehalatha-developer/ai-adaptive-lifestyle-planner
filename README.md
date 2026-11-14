# AI Adaptive Lifestyle Planner

An Android app that builds a personalized daily timetable from your lifestyle preferences and
adapts it on the fly through an AI chat assistant. Tell it *"move my workout to the evening"* or
*"I'm sick, clear my afternoon"* and it replans the rest of your day.

## Features

- **Onboarding profile** – wake/sleep times, office hours, commute, exercise and study targets,
  meal times, weekend preference, and lifestyle goals (exercise regularly, eat healthier,
  reduce screen time, …).
- **AI schedule generation** – creates a full day's plan from your profile via an AI backend,
  with an on-device rule-based generator as a fallback when the server is unreachable.
- **AI Planner chat** – conversational replanning of today's schedule, plus a quick-add form
  for ad-hoc tasks that the AI fits into the best available slot.
- **Today's schedule timeline** – tasks grouped by category (Health, Exercise, Meal, Study,
  Work, Personal) with current/upcoming/completed/skipped states that update with the clock.
- **Task tracking** – mark tasks complete or skip them.
- **Progress** – daily completion percentage and study minutes vs. target.
- **Settings** – edit your profile and regenerate your schedule at any time.

## Tech stack

| Layer | Technology |
|-------|------------|
| UI | Kotlin, Jetpack Compose, Material 3, Navigation Compose |
| Architecture | MVVM + Clean Architecture (domain use cases / repositories) |
| DI | Hilt |
| Persistence | Room (schedules & tasks), DataStore (user profile) |
| Networking | Retrofit, OkHttp, kotlinx.serialization |
| Backend | Node.js + TypeScript + Express proxy calling OpenAI (`/server`) |

## Project structure

```
app/src/main/java/com/example/aiplanner/
├── data/          # Room DB, DAOs, DataStore, Retrofit API, repository implementations
├── di/            # Hilt modules (database, network, repositories)
├── domain/        # Models, repository interfaces, use cases
├── navigation/    # Nav host, routes, bottom navigation
├── ui/            # Screens: onboarding, home, schedule, aiplanner, progress, settings
└── utils/         # Local schedule generator, time helpers
server/            # AI proxy server (keeps the OpenAI key off the device)
```

## Architecture

The app never talks to OpenAI directly. It calls a thin proxy server which holds the API key:

```
Android app ──(X-App-Key)──▶ server/ (Express) ──▶ OpenAI
```

Endpoints:

- `POST /api/v1/schedule/generate` – `{ profile, date }` → `{ tasks }`
- `POST /api/v1/schedule/replan` – `{ profile, currentTasks, now, instruction }` → `{ tasks, assistantMessage }`

## Getting started

### Prerequisites

- Android Studio (recent version with AGP 9 support), JDK 11+
- Android device or emulator running Android 7.0 (API 24) or higher
- Node.js 20+ and an OpenAI API key (for AI features)

### 1. Run the AI proxy server

```bash
cd server
cp .env.example .env      # set OPENAI_API_KEY and a random APP_SHARED_KEY
npm install
npm run dev               # listens on http://localhost:8787
```

See [`server/README.md`](server/README.md) for deployment options (Cloud Run, Docker, etc.).

### 2. Configure the app

Add the following to `local.properties` in the project root (this file is gitignored):

```properties
AI_BASE_URL=http://10.0.2.2:8787/
AI_APP_SHARED_KEY=<same value as APP_SHARED_KEY in server/.env>
```

`10.0.2.2` points the Android emulator at your machine's localhost. For a deployed server, use
its HTTPS URL.

### 3. Build and run

Open the project in Android Studio and run the `app` configuration, or:

```bash
./gradlew installDebug
```

The app still works without the server — schedules fall back to the local rule-based
generator, but AI chat replanning requires the server.

## Security notes

- Never commit `server/.env` or `local.properties`.
- The OpenAI key lives only on the server; the app authenticates with a shared `X-App-Key`.
