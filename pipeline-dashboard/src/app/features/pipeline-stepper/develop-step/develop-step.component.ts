import { Component, EventEmitter, Input, Output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StageState } from '../../../core/pipeline-state.model';
import { DevelopResponse, GeneratedFile } from '../../../api/models';
import { StageStatusBadgeComponent } from '../stage-status-badge.component';
import { ElapsedTimerComponent } from '../elapsed-timer.component';
import { StageErrorComponent } from '../stage-error.component';

@Component({
  selector: 'app-develop-step',
  standalone: true,
  imports: [CommonModule, StageStatusBadgeComponent, ElapsedTimerComponent, StageErrorComponent],
  templateUrl: './develop-step.component.html',
})
export class DevelopStepComponent {
  @Input({ required: true }) stage!: StageState<DevelopResponse>;
  @Output() retry = new EventEmitter<void>();

  readonly expanded = signal(false);
  readonly selectedFile = signal<GeneratedFile | null>(null);

  toggle(): void {
    if (this.stage.status === 'DONE') {
      this.expanded.update((v) => !v);
    }
  }

  selectFile(file: GeneratedFile): void {
    this.selectedFile.update((current) => (current === file ? null : file));
  }
}
