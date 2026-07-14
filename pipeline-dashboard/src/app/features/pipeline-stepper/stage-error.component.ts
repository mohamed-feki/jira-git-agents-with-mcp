import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-stage-error',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="flex flex-col gap-3 rounded-sm border border-danger/40 bg-danger/10 p-3">
      <p class="font-mono text-xs leading-relaxed text-danger">{{ message }}</p>
      <button
        type="button"
        (click)="retry.emit()"
        class="self-start rounded-sm border border-danger px-3 py-1.5 font-display text-xs font-semibold tracking-wide text-danger transition-colors hover:bg-danger hover:text-void"
      >
        Retry from this step
      </button>
    </div>
  `,
})
export class StageErrorComponent {
  @Input({ required: true }) message!: string | null;
  @Output() retry = new EventEmitter<void>();
}
