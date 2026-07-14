import { Component, EventEmitter, Input, OnDestroy, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Subscription } from 'rxjs';
import { PipelineInputs } from '../../core/pipeline.service';

const TICKET_KEY_PATTERN = /^[A-Z][A-Z0-9]+-\d+$/;
const REPO_PATTERN = /^[\w.-]+\/[\w.-]+$/;

@Component({
  selector: 'app-pipeline-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './pipeline-form.component.html',
})
export class PipelineFormComponent implements OnDestroy {
  @Input() disabled = false;
  @Output() runPipeline = new EventEmitter<PipelineInputs>();

  private readonly fb = inject(FormBuilder);
  private autoBranch = '';
  private sub: Subscription;

  readonly form = this.fb.group({
    ticketKey: ['', [Validators.required, Validators.pattern(TICKET_KEY_PATTERN)]],
    repository: ['', [Validators.required, Validators.pattern(REPO_PATTERN)]],
    branch: ['', [Validators.required]],
  });

  constructor() {
    this.sub = this.form.controls.ticketKey.valueChanges.subscribe((ticketKey) => {
      const branchControl = this.form.controls.branch;
      const current = branchControl.value ?? '';
      // Only auto-fill the branch while the user hasn't diverged from our suggestion.
      if (current === '' || current === this.autoBranch) {
        const suggestion = ticketKey ? `feature/${ticketKey}` : '';
        this.autoBranch = suggestion;
        branchControl.setValue(suggestion, { emitEvent: false });
      }
    });
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  submit(): void {
    if (this.form.invalid || this.disabled) {
      this.form.markAllAsTouched();
      return;
    }
    const { ticketKey, repository, branch } = this.form.getRawValue();
    this.runPipeline.emit({
      ticketKey: ticketKey!.trim(),
      repository: repository!.trim(),
      branch: branch!.trim(),
    });
  }
}
