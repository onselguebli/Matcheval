import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class ToastService {
  toasts: { type: 'success' | 'error' | 'warning'; message: string }[] = [];

  showSuccess(message: string) {
    this.show('success', message);
  }

  showError(message: string) {
    this.show('error', message);
  }

  showWarning(message: string) {
    this.show('warning', message);
  }

  private show(type: 'success' | 'error' | 'warning', message: string) {
    this.toasts.push({ type, message });

    // Auto-remove after 4 seconds
    setTimeout(() => {
      this.toasts = this.toasts.filter(t => t.message !== message);
    }, 6000);
  }
}
