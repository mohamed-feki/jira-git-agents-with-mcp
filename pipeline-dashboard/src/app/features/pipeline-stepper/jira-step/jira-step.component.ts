import { Component, EventEmitter, Input, Output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StageState } from '../../../core/pipeline-state.model';
import { StageStatusBadgeComponent } from '../stage-status-badge.component';
import { ElapsedTimerComponent } from '../elapsed-timer.component';
import { StageErrorComponent } from '../stage-error.component';

@Component({
  selector: 'app-jira-step',
  standalone: true,
  imports: [CommonModule, StageStatusBadgeComponent, ElapsedTimerComponent, StageErrorComponent],
  templateUrl: './jira-step.component.html',
})
export class JiraStepComponent {
  @Input({ required: true }) stage!: StageState<any>;
  @Output() retry = new EventEmitter<void>();

  readonly expanded = signal(false);

  toggle(): void {
    if (this.stage.status === 'DONE') {
      this.expanded.update((v) => !v);
    }
  }
}
