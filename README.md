# AI Adaptive Lifestyle Planner

> A full-stack, AI-powered Android app that plans your day and replans it when life happens.

![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white)
![Android](https://img.shields.io/badge/Android-API%2024%2B-3DDC84?logo=android&logoColor=white)
![Hilt](https://img.shields.io/badge/DI-Hilt-orange)
![Room](https://img.shields.io/badge/DB-Room-blue)
![TypeScript](https://img.shields.io/badge/TypeScript-3178C6?logo=typescript&logoColor=white)
![Node.js](https://img.shields.io/badge/Node.js-339933?logo=nodedotjs&logoColor=white)
![OpenAI](https://img.shields.io/badge/OpenAI-API-412991?logo=openai&logoColor=white)

**Author:** Snehalatha Murra · [GitHub](https://github.com/Snehalatha-developer)
<!-- Add your LinkedIn / portfolio link here, e.g. · [LinkedIn](https://www.linkedin.com/in/your-profile) -->

---

## Overview

Most planners give you a fixed timetable that breaks the moment your day changes. This app
learns your routine (sleep, office hours, commute, meals, exercise and study goals), generates a
personalized daily schedule with AI, and lets you **replan in plain English**:

> *"Move my workout to the evening"* · *"I'm sick, clear my afternoon"* · *"Add 30 min of reading"*

The AI rewrites the rest of your day around the change, and the app tracks what you actually
complete.

I designed and built the whole thing myself: the **Android client** (Kotlin, Jetpack Compose) and
the **backend AI service** (Node.js, TypeScript, OpenAI).

## Screenshots

<!-- Add screenshots to a /docs/screenshots folder and uncomment:
| Onboarding | Home | Schedule | AI Planner | Progress |
|---|---|---|---|---|
| ![](docs/screenshots/onboarding.png) | ![](docs/screenshots/home.png) | ![](docs/screenshots/schedule.png) | ![](docs/screenshots/ai_planner.png) | ![](docs/screenshots/progress.png) |
-->
_Coming soon._

## Key features

- **Personalized onboarding:** captures wake/sleep times, office hours, commute, meal times,
  exercise and study targets, weekend style, and lifestyle goals.
- **AI schedule generation:** builds a full, conflict-free day from the user's profile.
- **Conversational replanning:** a chat screen where natural-language requests reshape the rest
  of today, plus a quick-add form that slots ad-hoc tasks into the best free time.
- **Live timeline:** tasks show as *current*, *upcoming*, *completed* or *skipped* and update
  with the clock.
- **Progress tracking:** daily completion rate and study minutes against your target.
- **Offline fallback:** if the AI service is unreachable, a local rule-based generator still
  produces a usable schedule.

## Engineering highlights

| Area | What I did |
|------|------------|
| **Architecture** | Clean Architecture with MVVM: `domain` (models, repository interfaces, use cases), `data` (Room, DataStore, Retrofit), and `ui` (Compose screens + ViewModels exposing `StateFlow`). |
| **Secure AI integration** | The OpenAI key never ships in the APK. The app calls my own proxy server, which authenticates requests with a shared `X-App-Key` header. |
| **Reliable LLM output** | Responses are constrained with **OpenAI structured outputs** (Zod schema → JSON schema). The server also validates schedule rules (e.g. no overlaps) and **automatically retries with the error fed back to the model** before returning a clean `422`. |
| **Input validation** | Every API request is validated with **Zod** (e.g. `HH:mm` time formats, enums, non-negative durations) and returns structured `400` errors. |
| **Graceful degradation** | AI generation returns a `Result`. On failure the app keeps the existing schedule and falls back to the on-device generator. |
| **Persistence** | **Room** stores schedules and tasks (with exported schemas for migrations). **DataStore** stores the user profile. |
| **Dependency injection** | **Hilt** modules for the database, network and repositories keep components testable and loosely coupled. |
| **Modern Android** | 100% Jetpack Compose, Material 3, Navigation Compose, Kotlin Coroutines/Flow, kotlinx.serialization, and `java.time` via core library desugaring on API 24+. |
| **Deployable backend** | A TypeScript Express service with a `Dockerfile`, ready for Cloud Run, Render or Fly.io. |

## Architecture

```
┌─────────────────────────── Android app ───────────────────────────┐
│  Compose UI  ──▶  ViewModels (StateFlow)  ──▶  Use cases          │
│                                                   │               │
│                                         Repository interfaces     │
│                                                   │               │
│        Room (tasks) · DataStore (profile) · Retrofit API client   │
└───────────────────────────────────────────────────┼───────────────┘
                                                    │ HTTPS + X-App-Key
                                    ┌───────────────▼───────────────┐
                                    │  Node.js / Express proxy      │
                                    │  Zod validation · retry logic │
                                    └───────────────┬───────────────┘
                                                    │ structured outputs
                                                ┌───▼───┐
                                                │OpenAI │
                                                └───────┘
```

**API**

| Method | Endpoint | Request → Response |
|--------|----------|--------------------|
| `POST` | `/api/v1/schedule/generate` | `{ profile, date }` → `{ tasks }` |
| `POST` | `/api/v1/schedule/replan` | `{ profile, currentTasks, now, instruction }` → `{ tasks, assistantMessage }` |

## Tech stack

**Android:** Kotlin · Jetpack Compose · Material 3 · Navigation Compose · Hilt · Room · DataStore ·
Retrofit · OkHttp · Coroutines & Flow · kotlinx.serialization

**Backend:** Node.js 20 · TypeScript · Express · OpenAI SDK · Zod · Docker

## Project structure

```
app/src/main/java/com/example/aiplanner/
├── data/          # Room DB & DAOs, DataStore, Retrofit API, repository implementations
├── di/            # Hilt modules
├── domain/        # Models, repository interfaces, use cases
├── navigation/    # NavHost, routes, bottom navigation
├── ui/            # onboarding · home · schedule · aiplanner · progress · settings
└── utils/         # Local schedule generator, time helpers
server/src/
├── routes/        # Express endpoints
├── openai/        # Prompts, OpenAI client, validation + retry
└── schema/        # Zod request/response schemas
```

## Run it locally

**Prerequisites:** Android Studio (AGP 9), JDK 11+, Node.js 20+, an OpenAI API key.

**1. Start the AI server**

```bash
cd server
cp .env.example .env      # set OPENAI_API_KEY and a random APP_SHARED_KEY
npm install
npm run dev               # http://localhost:8787
```

**2. Configure the app** in `local.properties` (gitignored):

```properties
AI_BASE_URL=http://10.0.2.2:8787/
AI_APP_SHARED_KEY=<same value as APP_SHARED_KEY>
```

**3. Build and run** from Android Studio, or with `./gradlew installDebug`.

The app runs without the server too. Schedules then come from the local generator, but AI chat
replanning needs the server. See [`server/README.md`](server/README.md) for deployment.

## Roadmap

- Task reminders with notifications
- Weekly and monthly progress analytics
- Unit and UI test coverage for use cases and screens
- Calendar sync

## Contact

**Snehalatha Murra**: I'm open to Android and full-stack opportunities.
GitHub: [@Snehalatha-developer](https://github.com/Snehalatha-developer)
<!-- LinkedIn: https://www.linkedin.com/in/your-profile -->
