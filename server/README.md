# DailyTimetable AI proxy

Thin backend that holds the OpenAI API key and serves two endpoints the
Android app calls instead of talking to OpenAI directly (so the key never
ships inside the APK).

## Endpoints

- `POST /api/v1/schedule/generate` — `{ profile, date }` -> `{ tasks }`
- `POST /api/v1/schedule/replan` — `{ profile, currentTasks, now, instruction }`
  -> `{ tasks, assistantMessage }`

Both require header `X-App-Key: <APP_SHARED_KEY>` if `APP_SHARED_KEY` is set.

## Local development

```bash
cp .env.example .env
# edit .env: set OPENAI_API_KEY and a random APP_SHARED_KEY
npm install
npm run dev
```

Server listens on `http://localhost:8787` (configurable via `PORT`).

Quick manual test:

```bash
curl -X POST http://localhost:8787/api/v1/schedule/generate \
  -H "Content-Type: application/json" \
  -H "X-App-Key: $APP_SHARED_KEY" \
  -d '{
    "date": "2026-11-10",
    "profile": {
      "name": "Snehalatha",
      "wakeUpTime": "06:30",
      "sleepTime": "23:00",
      "officeStartTime": "10:00",
      "officeEndTime": "19:00",
      "commuteMinutes": 30,
      "exerciseMinutes": 45,
      "studyTargetMinutes": 60,
      "breakfastTime": "08:00",
      "lunchTime": "13:00",
      "dinnerTime": "20:30",
      "weekendPreference": "RELAXED",
      "goals": ["Exercise regularly"]
    }
  }'
```

From an Android emulator pointed at this local server, use base URL
`http://10.0.2.2:8787/`.

## Deploying

Any container host works. Example with Google Cloud Run:

```bash
gcloud run deploy dailytimetable-ai-proxy \
  --source . \
  --region <your-region> \
  --allow-unauthenticated \
  --set-env-vars OPENAI_API_KEY=sk-...,OPENAI_MODEL=gpt-4o-mini,APP_SHARED_KEY=...
```

Or build and push the included `Dockerfile` to Render/Fly.io/any Docker host.
Set `OPENAI_API_KEY`, `OPENAI_MODEL`, and `APP_SHARED_KEY` as environment
variables on the host — never commit `.env`.

After deploying, put the resulting HTTPS URL and the `APP_SHARED_KEY` into the
Android app's `local.properties` as `AI_BASE_URL` and `AI_APP_SHARED_KEY`.
