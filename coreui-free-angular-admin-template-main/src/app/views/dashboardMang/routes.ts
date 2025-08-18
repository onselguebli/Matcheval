import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./dashboardMang.component').then(m => m.DashboardComponentMang),
    data: {
      title: $localize`Dashboard`
    }
  }
];

