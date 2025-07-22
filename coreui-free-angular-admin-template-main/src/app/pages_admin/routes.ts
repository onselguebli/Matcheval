import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    data: {
      title: 'pages_admins' // 🔰 Titre global pour ce groupe de routes
    },
    children: [
      {
        path: 'listusers',
        loadComponent: () => import('./listusers/listusers.component').then(m => m.ListusersComponent),
        data: {
          title: 'listusers' 
        }
      },
      {
        path: 'updateuser/:id',
        loadComponent: () => import('./updateuser/updateuser.component').then(m => m.UpdateuserComponent  ),
        data: {
          title: 'updateuser' 
        }
      }
    ]
  }
];
