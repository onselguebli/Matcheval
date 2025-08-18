import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./dashboardRec.component').then(m => m.DashboardRecComponent),
    data: {
      title: $localize`DashboardRec`
    }
  }
];

