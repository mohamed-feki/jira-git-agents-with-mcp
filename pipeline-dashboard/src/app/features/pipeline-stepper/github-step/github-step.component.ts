import { Component, EventEmitter, Input, Output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StageState } from '../../../core/pipeline-state.model';
import { PushResponse } from '../../../api/models';
import { StageStatusBadgeComponent } from '../stage-status-badge.component';
import { ElapsedTimerComponent } from '../elapsed-timer.component';
import { StageErrorComponent } from '../stage-error.component';

@Component({
  selector: 'app-github-step',
  standalone: true,
  imports: [CommonModule, StageStatusBadgeComponent, ElapsedTimerComponent, StageErrorComponent],
  templateUrl: './github-step.component.html',
})
export class GithubStepComponent {
  @Input({ required: true }) stage!: StageState<PushResponse>;
  @Output() retry = new EventEmitter<void>();

  readonly expanded = signal(false);

  toggle(): void {
    if (this.stage.status === 'DONE') {
      this.expanded.update((v) => !v);
    }
  }

  shortSha(sha: string): string {
    return sha.slice(0, 7);
  }
}
