import { DevelopResponse, PushResponse } from '../api/models';

export type StageStatus = 'NOT_STARTED' | 'IN_PROGRESS' | 'DONE' | 'FAILED';

export interface StageState<TResult = unknown> {
  status: StageStatus;
  startedAt: Date | null;
  finishedAt: Date | null;
  result: TResult | null;
  errorMessage: string | null;
}

export interface PipelineState {
  jira: StageState<any>;
  develop: StageState<DevelopResponse>;
  github: StageState<PushResponse>;
}

export type StageKey = keyof PipelineState;

export function emptyStage<T>(): StageState<T> {
  return { status: 'NOT_STARTED', startedAt: null, finishedAt: null, result: null, errorMessage: null };
}

export function initialPipelineState(): PipelineState {
  return {
    jira: emptyStage(),
    develop: emptyStage(),
    github: emptyStage(),
  };
}

export type OverallStatus = 'Not started' | 'Running' | 'Completed' | 'Failed';

export function overallStatus(state: PipelineState): OverallStatus {
  const stages = [state.jira, state.develop, state.github];
  if (stages.some((s) => s.status === 'FAILED')) return 'Failed';
  if (stages.some((s) => s.status === 'IN_PROGRESS')) return 'Running';
  if (stages.every((s) => s.status === 'DONE')) return 'Completed';
  return 'Not started';
}
