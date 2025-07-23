import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { IconDirective } from '@coreui/icons-angular';
import { ToastService } from '../../services/toast.service';
import { ToastBodyComponent, ToastComponent, ToastHeaderComponent } from '@coreui/angular'


@Component({
  selector: 'app-toast',
  standalone: true,
  template: `
    <div class="toast-container">
      <c-toast
        *ngFor="let toast of toastService.toasts"
        [autohide]="false"
        [fade]="true"
        [visible]="true"
        [color]="toast.type"
      >
        <c-toast-header>
          <svg cIcon [name]="getIcon(toast.type)" class="me-2"></svg>
          <strong class="me-auto">Notification</strong>
          <small class="text-muted ms-auto">just now</small>
        </c-toast-header>
        <c-toast-body>
          {{ toast.message }}
        </c-toast-body>
      </c-toast>
    </div>
  `,
  styleUrls: ['./toast.component1.scss'],
  imports: [
    CommonModule,
    IconDirective,
    ToastComponent,
    ToastHeaderComponent,
    ToastBodyComponent
  ]
})
export class ToastComponent1 {
  constructor(public toastService: ToastService) {}

  getIcon(type: string): string {
    switch (type) {
      case 'success':
        return 'cil-check-circle';
      case 'error':
        return 'cil-x-circle';
      case 'warning':
        return 'cil-warning';
      default:
        return 'cil-info';
    }
  }
}
