import { CommonModule } from '@angular/common';
import { Component} from '@angular/core';
import { AlertComponent } from '@coreui/angular';
import { IconDirective } from '@coreui/icons-angular';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-toast',
  standalone: true,
  template: `
    <div class="toast-container">
      <div *ngFor="let toast of toastService.toasts" class="toast-{{ toast.type }}">
        <c-alert [color]="toast.type" class="d-flex align-items-center">
          <svg cIcon [name]="getIcon(toast.type)" class="me-2"></svg>
          <div>{{ toast.message }}</div>
        </c-alert>
      </div>
    </div>
  `,
  styleUrls: ['./toast.component.scss'],
  imports: [CommonModule,AlertComponent,IconDirective]
})
export class ToastComponent {
  constructor(public toastService: ToastService) {}

  getIcon(type: string): string {
    switch (type) {
      case 'success': return 'cil-check-circle';
      case 'error': return 'cil-x-circle';
      case 'warning': return 'cil-warning';
      default: return 'cil-info';
    }
  }
}
