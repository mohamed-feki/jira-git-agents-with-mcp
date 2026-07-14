import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StageStatus } from '../../core/pipeline-state.model';

@Component({
  selector: 'app-stage-status-badge',
  standalone: true,
  imports: [CommonModule],
  template: `
    <span
      class="flex h-9 w-9 shrink-0 items-center justify-center rounded-full border font-mono text-sm"
      [ngClass]="{
        'border-rail text-ink-faint': status === 'NOT_STARTED',
        'border-amber text-amber animate-pulse-ring': status === 'IN_PROGRESS',
        'border-signal bg-signal/10 text-signal': status === 'DONE',
        'border-danger bg-danger/10 text-danger': status === 'FAILED'
      }"
      [attr.aria-label]="statusLabel"
    >
      @switch (status) {
        @case ('NOT_STARTED') { {{ index }} }
        @case ('IN_PROGRESS') {
          <span class="block h-3.5 w-3.5 animate-spin rounded-full border-2 border-amber border-t-transparent"></span>
        }
        @case ('DONE') { ✓ }
        @case ('FAILED') { ✕ }
      }
    </span>
  `,
})
export class StageStatusBadgeComponent {
  @Input({ required: true }) status!: StageStatus;
  @Input({ required: true }) index!: number;

  get statusLabel(): string {
    switch (this.status) {
      case 'NOT_STARTED':
        return 'Not started';
      case 'IN_PROGRESS':
        return 'In progress';
      case 'DONE':
        return 'Done';
      case 'FAILED':
        return 'Failed';
    }
  }
}
