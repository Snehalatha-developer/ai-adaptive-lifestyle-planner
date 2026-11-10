import { Router } from "express";
import { GenerateRequestSchema, ReplanRequestSchema } from "../schema/task.js";
import { generateSchedule, replanSchedule, ScheduleGenerationError } from "../openai/client.js";

export const scheduleRouter = Router();

scheduleRouter.post("/generate", async (req, res) => {
  const parsed = GenerateRequestSchema.safeParse(req.body);
  if (!parsed.success) {
    res.status(400).json({ error: "invalid_request", details: parsed.error.flatten() });
    return;
  }

  try {
    const result = await generateSchedule(parsed.data.profile, parsed.data.date);
    res.json(result);
  } catch (err) {
    if (err instanceof ScheduleGenerationError) {
      res.status(422).json({ error: "schedule_generation_failed", message: err.message });
      return;
    }
    console.error("generate failed", err);
    res.status(502).json({ error: "upstream_error" });
  }
});

scheduleRouter.post("/replan", async (req, res) => {
  const parsed = ReplanRequestSchema.safeParse(req.body);
  if (!parsed.success) {
    res.status(400).json({ error: "invalid_request", details: parsed.error.flatten() });
    return;
  }

  try {
    const result = await replanSchedule(
      parsed.data.profile,
      parsed.data.currentTasks,
      parsed.data.now,
      parsed.data.instruction
    );
    res.json(result);
  } catch (err) {
    if (err instanceof ScheduleGenerationError) {
      res.status(422).json({ error: "schedule_generation_failed", message: err.message });
      return;
    }
    console.error("replan failed", err);
    res.status(502).json({ error: "upstream_error" });
  }
});
