export interface Task {
  id: number;
  name: string;
  description?: string;
  deviceId: string;
  deviceOperation?: string;
  hour: number;
  minute: number;
  executionDays: string;
  executionCommand: string;
  executeUntil: number;
  createdAt: number;
  updatedAt: number;
}

export const compareTasks = (t1: Task, t2: Task): number => {
  const result = t1.hour - t2.hour;
  if (result === 0) {
    const result2 = t1.minute - t2.minute;
    return taskComparatorResult(result2);
  } else {
    return taskComparatorResult(result);
  }
};

const taskComparatorResult = (result: number): number => {
  if (result > 0) {
    return 1;
  } else if (result < 0) {
    return -1;
  } else {
    return 0;
  }
};
