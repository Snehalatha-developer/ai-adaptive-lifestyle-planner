import OpenAI from "openai";
import { zodResponseFormat } from "openai/helpers/zod";
import {
  GenerateResponseSchema,
  ReplanResponseSchema,
  findScheduleViolation,
  type ScheduleTask,
  type UserProfile,
} from "../schema/task.js";
import { generateSystemPrompt, replanSystemPrompt } from "./prompts.js";

const openai = new OpenAI({ apiKey: process.env.OPENAI_API_KEY });
const MODEL = process.env.OPENAI_MODEL ?? "gpt-4o-mini";

class ScheduleGenerationError extends Error {}

async function requestJson<T extends { tasks: ScheduleTask[] }>(
  systemPrompt: string,
  userPrompt: string,
  responseFormat: ReturnType<typeof zodResponseFormat>,
  attempt = 1
): Promise<T> {
  const completion = await openai.beta.chat.completions.parse({
    model: MODEL,
    messages: [
      { role: "system", content: systemPrompt },
      { role: "user", content: userPrompt },
    ],
    response_format: responseFormat,
  });

  const parsed = completion.choices[0]?.message.parsed as T | undefined;
  if (!parsed) {
    throw new ScheduleGenerationError("Model returned no parsable content");
  }

  const violation = findScheduleViolation(parsed.tasks);
  if (violation) {
    if (attempt >= 2) {
      throw new ScheduleGenerationError(`AI output invalid after retry: ${violation}`);
    }
    return requestJson(
      systemPrompt,
      `${userPrompt}\n\nYour previous attempt was invalid: ${violation}. Fix it and try again.`,
      responseFormat,
      attempt + 1
    );
  }

  return parsed;
}

export async function generateSchedule(
  profile: UserProfile,
  date: string
): Promise<{ tasks: ScheduleTask[] }> {
  return requestJson(
    generateSystemPrompt(),
    `Date: ${date}\nUser profile (JSON): ${JSON.stringify(profile)}`,
    zodResponseFormat(GenerateResponseSchema, "schedule"),
  );
}

export async function replanSchedule(
  profile: UserProfile,
  currentTasks: ScheduleTask[],
  now: string,
  instruction: string
): Promise<{ tasks: ScheduleTask[]; assistantMessage: string }> {
  const userPrompt =
    `Current time: ${now}\n` +
    `User profile (JSON): ${JSON.stringify(profile)}\n` +
    `Current schedule (JSON): ${JSON.stringify(currentTasks)}\n` +
    `Instruction: ${instruction}`;
  return requestJson(replanSystemPrompt(), userPrompt, zodResponseFormat(ReplanResponseSchema, "replan"));
}

export { ScheduleGenerationError };
