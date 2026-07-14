import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PipelineState, StageKey, StageStatus } from '../../core/pipeline-state.model';
import { JiraStepComponent } from './jira-step/jira-step.component';
import { DevelopStepComponent } from './develop-step/develop-step.component';
import { GithubStepComponent } from './github-step/github-step.component';

@Component({
  selector: 'app-pipeline-stepper',
  standalone: true,
  imports: [CommonModule, JiraStepComponent, DevelopStepComponent, GithubStepComponent],
  templateUrl: './pipeline-stepper.component.html',
})
export class PipelineStepperComponent {
  @Input({ required: true }) state!: PipelineState;
  @Output() retryFrom = new EventEmitter<StageKey>();

  connectorClass(status: StageStatus): string {
    switch (status) {
      case 'DONE':
        return 'bg-signal';
      case 'FAILED':
        return 'bg-danger';
      case 'IN_PROGRESS':
        return 'rail-flow';
      default:
        return 'bg-rail';
    }
  }
}
