import { Component, Input, OnChanges, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OverallStatus } from '../../core/pipeline-state.model';

@Component({
  selector: 'app-pipeline-summary',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pipeline-summary.component.html',
})
export class PipelineSummaryComponent implements OnChanges, OnDestroy {
  @Input() startedAt: Date | null = null;
  @Input() finishedAt: Date | null = null;
  @Input() totalTokens: number | null = null;
  @Input({ required: true }) status!: OverallStatus;

  elapsedDisplay = '0.0s';
  private intervalId: ReturnType<typeof setInterval> | null = null;

  ngOnChanges(): void {
    this.clearTimer();
    this.tick();
    if (this.startedAt && !this.finishedAt) {
      this.intervalId = setInterval(() => this.tick(), 200);
    }
  }

  ngOnDestroy(): void {
    this.clearTimer();
  }

  get statusClass(): string {
    switch (this.status) {
      case 'Running':
        return 'text-amber';
      case 'Completed':
        return 'text-signal';
      case 'Failed':
        return 'text-danger';
      default:
        return 'text-ink-faint';
    }
  }

  private tick(): void {
    if (!this.startedAt) {
      this.elapsedDisplay = '0.0s';
      return;
    }
    const end = this.finishedAt ?? new Date();
    const seconds = Math.max(0, (end.getTime() - this.startedAt.getTime()) / 1000);
    this.elapsedDisplay = `${seconds.toFixed(1)}s`;
  }

  private clearTimer(): void {
    if (this.intervalId) {
      clearInterval(this.intervalId);
      this.intervalId = null;
    }
  }
}
