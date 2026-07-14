import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PipelineFormComponent } from './features/pipeline-form/pipeline-form.component';
import { PipelineStepperComponent } from './features/pipeline-stepper/pipeline-stepper.component';
import { PipelineSummaryComponent } from './features/pipeline-summary/pipeline-summary.component';
import { PipelineInputs, PipelineService } from './core/pipeline.service';
import { StageKey } from './core/pipeline-state.model';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, PipelineFormComponent, PipelineStepperComponent, PipelineSummaryComponent],
  templateUrl: './app.component.html',
})
export class AppComponent {
  private readonly pipeline = inject(PipelineService);

  readonly state = this.pipeline.state;
  readonly isRunning = this.pipeline.isRunning;
  readonly runStartedAt = this.pipeline.runStartedAt;
  readonly runFinishedAt = this.pipeline.runFinishedAt;
  readonly totalTokens = this.pipeline.totalTokens;
  readonly status = this.pipeline.status;

  onRunPipeline(inputs: PipelineInputs): void {
    void this.pipeline.run(inputs);
  }

  onRetryFrom(stage: StageKey): void {
    void this.pipeline.retryFrom(stage);
  }
}
