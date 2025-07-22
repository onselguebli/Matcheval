import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    data: {
      title: 'pages_recruteur' // 🔰 Titre global pour ce groupe de routes
    },
    children: [
      {
        path: 'post_offre',
        loadComponent: () => import('./post-offre/post-offre.component').then(m => m.PostOffreComponent),
        data: {
          title: 'post_offre' 
        }
      },
    ]
  }
];
