import cors from "cors";
import express from "express";
import { scheduleRouter } from "./routes/schedule.js";

const app = express();
app.use(cors());
app.use(express.json({ limit: "1mb" }));

const appSharedKey = process.env.APP_SHARED_KEY;

app.use((req, res, next) => {
  if (!appSharedKey) {
    next();
    return;
  }
  if (req.header("X-App-Key") !== appSharedKey) {
    res.status(401).json({ error: "unauthorized" });
    return;
  }
  next();
});

app.get("/health", (_req, res) => res.json({ status: "ok" }));
app.use("/api/v1/schedule", scheduleRouter);

const port = Number(process.env.PORT ?? 8787);
app.listen(port, () => {
  console.log(`AI planner proxy listening on :${port}`);
});
