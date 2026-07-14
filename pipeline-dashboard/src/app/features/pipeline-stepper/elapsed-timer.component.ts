import { Component, Input, OnChanges, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-elapsed-timer',
  standalone: true,
  imports: [CommonModule],
  template: `<span class="font-mono text-xs text-amber">{{ display }}</span>`,
})
export class ElapsedTimerComponent implements OnChanges, OnDestroy {
  @Input({ required: true }) since!: Date | null;
  @Input() until: Date | null = null;

  display = '0.0s';
  private intervalId: ReturnType<typeof setInterval> | null = null;

  ngOnChanges(): void {
    this.clearTimer();
    this.tick();
    if (this.since && !this.until) {
      this.intervalId = setInterval(() => this.tick(), 100);
    }
  }

  ngOnDestroy(): void {
    this.clearTimer();
  }

  private tick(): void {
    if (!this.since) {
      this.display = '0.0s';
      return;
    }
    const end = this.until ?? new Date();
    const seconds = Math.max(0, (end.getTime() - this.since.getTime()) / 1000);
    this.display = `${seconds.toFixed(1)}s`;
  }

  private clearTimer(): void {
    if (this.intervalId) {
      clearInterval(this.intervalId);
      this.intervalId = null;
    }
  }
}
