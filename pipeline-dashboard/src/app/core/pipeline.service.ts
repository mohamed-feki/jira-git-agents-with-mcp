import { Injectable, computed, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { JiraAgentService } from '../api/jira-agent.service';
import { DeveloperAgentService } from '../api/developer-agent.service';
import { GithubAgentService } from '../api/github-agent.service';
import { ApiErrorResponse, FileEntry } from '../api/models';
import {
  PipelineState,
  StageKey,
  emptyStage,
  initialPipelineState,
  overallStatus,
} from './pipeline-state.model';

export interface PipelineInputs {
  ticketKey: string;
  repository: string;
  branch: string;
}

@Injectable({ providedIn: 'root' })
export class PipelineService {
  private readonly _state = signal<PipelineState>(initialPipelineState());
  private readonly _inputs = signal<PipelineInputs | null>(null);
  private readonly _runStartedAt = signal<Date | null>(null);
  private readonly _runFinishedAt = signal<Date | null>(null);

  readonly state = this._state.asReadonly();
  readonly inputs = this._inputs.asReadonly();
  readonly runStartedAt = this._runStartedAt.asReadonly();
  readonly runFinishedAt = this._runFinishedAt.asReadonly();

  readonly status = computed(() => overallStatus(this._state()));

  readonly isRunning = computed(() => this.status() === 'Running');

  readonly totalTokens = computed(() => {
    const develop = this._state().develop;
    const total = develop.result?.tokenUsage?.totalTokens;
    return typeof total === 'number' ? total : null;
  });

  constructor(
    private readonly jira: JiraAgentService,
    private readonly developer: DeveloperAgentService,
    private readonly github: GithubAgentService,
  ) {}

  async run(inputs: PipelineInputs): Promise<void> {
    this._inputs.set(inputs);
    this._state.set(initialPipelineState());
    this._runStartedAt.set(new Date());
    this._runFinishedAt.set(null);

    const jiraResult = await this.runJiraStage(inputs);
    if (!jiraResult) {
      this._runFinishedAt.set(new Date());
      return;
    }

    const developResult = await this.runDevelopStage(jiraResult);
    if (!developResult) {
      this._runFinishedAt.set(new Date());
      return;
    }

    await this.runGithubStage(inputs, jiraResult.ticketKey, developResult.files);
    this._runFinishedAt.set(new Date());
  }

  /** Re-runs the given stage and every stage after it, reusing prior results where needed. */
  async retryFrom(stage: StageKey): Promise<void> {
    const inputs = this._inputs();
    if (!inputs) return;

    if (stage === 'jira') {
      this._state.update((s) => ({ ...s, jira: emptyStage(), develop: emptyStage(), github: emptyStage() }));
      const jiraResult = await this.runJiraStage(inputs);
      if (!jiraResult) return;
      const developResult = await this.runDevelopStage(jiraResult);
      if (!developResult) return;
      await this.runGithubStage(inputs, jiraResult.ticketKey, developResult.files);
      return;
    }

    if (stage === 'develop') {
      const jiraResult = this._state().jira.result;
      if (!jiraResult) return;
      this._state.update((s) => ({ ...s, develop: emptyStage(), github: emptyStage() }));
      const developResult = await this.runDevelopStage(jiraResult);
      if (!developResult) return;
      await this.runGithubStage(inputs, jiraResult.ticketKey, developResult.files);
      return;
    }

    if (stage === 'github') {
      const jiraResult = this._state().jira.result;
      const developResult = this._state().develop.result;
      if (!jiraResult || !developResult) return;
      this._state.update((s) => ({ ...s, github: emptyStage() }));
      await this.runGithubStage(inputs, jiraResult.ticketKey, developResult.files);
      return;
    }
  }

  private async runJiraStage(inputs: PipelineInputs) {
    this.markInProgress('jira');
    try {
      const result = await firstValueFrom(this.jira.readTicket(inputs.ticketKey));
      console.log(result);
      
      this.markDone('jira', result);
      return result;
    } catch (err) {
      this.markFailed('jira', this.extractErrorMessage(err));
      return null;
    }
  }

  private async runDevelopStage(jiraResult: { summary: string; description: string; acceptanceCriteria: string }) {
    this.markInProgress('develop');
    try {
      const result = await firstValueFrom(
        this.developer.develop({
          summary: jiraResult.summary,
          description: jiraResult.description,
          acceptanceCriteria: jiraResult.acceptanceCriteria,
        }),
      );
      this.markDone('develop', result);
      return result;
    } catch (err) {
      this.markFailed('develop', this.extractErrorMessage(err));
      return null;
    }
  }

  private async runGithubStage(inputs: PipelineInputs, ticketKey: string, files: FileEntry[]) {
    this.markInProgress('github');
    try {
      const result = await firstValueFrom(
        this.github.push({
          repository: inputs.repository,
          branch: inputs.branch,
          commitMessage: `${ticketKey}: automated changes from pipeline`,
          files,
        }),
      );
      this.markDone('github', result);
      return result;
    } catch (err) {
      this.markFailed('github', this.extractErrorMessage(err));
      return null;
    }
  }

  private markInProgress(stage: StageKey) {
    this._state.update((s) => ({
      ...s,
      [stage]: { ...emptyStage(), status: 'IN_PROGRESS', startedAt: new Date() },
    }));
  }

  private markDone<T>(stage: StageKey, result: T) {
    this._state.update((s) => ({
      ...s,
      [stage]: { ...s[stage], status: 'DONE', finishedAt: new Date(), result, errorMessage: null },
    }));
  }

  private markFailed(stage: StageKey, message: string) {
    this._state.update((s) => ({
      ...s,
      [stage]: { ...s[stage], status: 'FAILED', finishedAt: new Date(), errorMessage: message },
    }));
  }

  private extractErrorMessage(err: unknown): string {
    if (err instanceof HttpErrorResponse) {
      const body = err.error as ApiErrorResponse | undefined;
      if (body && typeof body.message === 'string') {
        return body.message;
      }
      if (err.status === 0) {
        return 'Could not reach the backend service. Is it running and is CORS configured for this origin?';
      }
      return `Request failed with status ${err.status} ${err.statusText}`.trim();
    }
    return 'An unexpected error occurred.';
  }
}
