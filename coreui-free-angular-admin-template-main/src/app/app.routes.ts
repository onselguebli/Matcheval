import { Routes } from '@angular/router';
import { RoleGuard } from '../app/guards/role.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  },
 
  {
    path: 'manager-dashboard',
    loadComponent: () =>
      import('./layout/manager-lay/manager-layout/manager-layout.component').then((c) => c.ManagerLayoutComponent),
    canActivate: [RoleGuard],
    data: { expectedRole: 'MANAGER' },
    children: [
      {
        path: 'ckecked-listMang',
      loadComponent: () => import('./checkedlist/checked-list-mang/checked-list-mang.component').then(m => m.CheckedListMangComponent)
      },
      {
      path: 'pages_manager',
      loadChildren: () => import('./pages_manager/routes').then(m => m.routes)
    },
   {
      path: 'dashboardMang',
      loadChildren: () => import('./views/dashboardMang/routes').then(m => m.routes)
    }
  ]
  },
  {
    path: 'recruiter-dashboard',
    loadComponent: () =>
      import('./layout/recruiter-lay/recruiter-layout/recruiter-layout.component').then((c) => c.RecruiterLayoutComponent),
    canActivate: [RoleGuard],
    data: { expectedRole: 'RECRUITER' },
    children: [
      {
      path: 'pages_recruteur',
      loadChildren: () => import('./pages_recruteur/routes').then(m => m.routes)
    },
    {
      path: 'dashboardRec',
      loadChildren: () => import('./views/dashboardRec/routes').then(m => m.routes)
    },
    {
      path: 'ckecked-list',
      loadComponent: () => import('./checkedlist/checked-list/checked-list.component').then(m => m.CheckedListComponent)
    },
    ]
  },
  {
    path: 'admin-dashboard',
    loadComponent: () =>
      import('./layout/admin-lay/admin-layout/admin-layout.component').then((c) => c.AdminLayoutComponent),
    canActivate: [RoleGuard],
    data: { expectedRole: 'ADMIN' },
     children: [
    {
      path: 'pages',
      loadChildren: () => import('./views/pages/routes').then(m => m.routes)
      
    },
    {
      path: 'dashboard',
      loadChildren: () => import('./views/dashboard/routes').then(m => m.routes)
      
    },
    {
      path: 'pages_admin',
      loadChildren: () => import('./pages_admin/routes').then(m => m.routes)
      
    },
   
      {
        path: 'pages',
        loadChildren: () => import('./views/pages/routes').then((m) => m.routes)
      }
    ]
  
  },
 
  {
    path: '404',
    loadComponent: () => import('./views/pages/page404/page404.component').then(m => m.Page404Component),
    data: {
      title: 'Page 404'
    }
  },
  {
    path: '500',
    loadComponent: () => import('./views/pages/page500/page500.component').then(m => m.Page500Component),
    data: {
      title: 'Page 500'
    }
  },
  {
    path: 'login',
    loadComponent: () => import('./views/pages/login/login.component').then(m => m.LoginComponent),
    data: {
      title: 'Login Page'
    }
  },
  
  { 
    path: '**', 
    redirectTo: '/404' 
  }
];
