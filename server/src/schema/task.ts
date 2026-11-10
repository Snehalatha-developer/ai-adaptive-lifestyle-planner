import { z } from "zod";

const timeString = z
  .string()
  .regex(/^([01]\d|2[0-3]):[0-5]\d$/, "expected HH:mm");

export const TaskCategorySchema = z.enum([
  "HEALTH",
  "EXERCISE",
  "MEAL",
  "STUDY",
  "WORK",
  "PERSONAL",
]);

export const TaskStatusSchema = z.enum([
  "PENDING",
  "IN_PROGRESS",
  "COMPLETED",
  "SKIPPED",
  "RESCHEDULED",
]);

export const WeekendPreferenceSchema = z.enum([
  "SAME_AS_WEEKDAY",
  "RELAXED",
  "FULL_REST",
]);

export const UserProfileSchema = z.object({
  name: z.string(),
  wakeUpTime: timeString,
  sleepTime: timeString,
  officeStartTime: timeString,
  officeEndTime: timeString,
  commuteMinutes: z.number().int().min(0),
  exerciseMinutes: z.number().int().min(0),
  studyTargetMinutes: z.number().int().min(0),
  breakfastTime: timeString,
  lunchTime: timeString,
  dinnerTime: timeString,
  weekendPreference: WeekendPreferenceSchema,
  goals: z.array(z.string()),
});

export const ScheduleTaskSchema = z.object({
  id: z.string(),
  title: z.string().min(1),
  description: z.string().nullable(),
  category: TaskCategorySchema,
  date: z.string(),
  startTime: timeString,
  endTime: timeString,
  notificationEnabled: z.boolean(),
  status: TaskStatusSchema,
});

export const GenerateRequestSchema = z.object({
  profile: UserProfileSchema,
  date: z.string(),
});

export const GenerateResponseSchema = z.object({
  tasks: z.array(ScheduleTaskSchema),
});

export const ReplanRequestSchema = z.object({
  profile: UserProfileSchema,
  currentTasks: z.array(ScheduleTaskSchema),
  now: timeString,
  instruction: z.string().min(1),
});

export const ReplanResponseSchema = z.object({
  tasks: z.array(ScheduleTaskSchema),
  assistantMessage: z.string(),
});

export type ScheduleTask = z.infer<typeof ScheduleTaskSchema>;
export type UserProfile = z.infer<typeof UserProfileSchema>;

function toMinutes(time: string): number {
  const [h, m] = time.split(":").map(Number);
  return h * 60 + m;
}

/** Returns a human-readable reason if `tasks` has overlapping or malformed entries, else null. */
export function findScheduleViolation(tasks: ScheduleTask[]): string | null {
  const sorted = [...tasks].sort((a, b) => toMinutes(a.startTime) - toMinutes(b.startTime));
  for (const task of sorted) {
    if (toMinutes(task.endTime) <= toMinutes(task.startTime)) {
      return `Task "${task.title}" has endTime <= startTime`;
    }
  }
  for (let i = 1; i < sorted.length; i++) {
    const prev = sorted[i - 1];
    const curr = sorted[i];
    if (toMinutes(curr.startTime) < toMinutes(prev.endTime)) {
      return `Task "${curr.title}" overlaps with "${prev.title}"`;
    }
  }
  return null;
}
