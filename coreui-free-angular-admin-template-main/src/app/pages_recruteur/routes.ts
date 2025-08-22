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
      {
        path: 'liste-candidatures',
        loadComponent: () => import('./liste-candidatures/liste-candidatures.component').then(m => m.ListeCandidaturesComponent),
        data: {
          title: 'liste-candidatures' 
        }
      },
      {
        path: 'liste-offre',
        loadComponent: () => import('./list-offre/list-offre.component').then(m => m.ListOffreComponent),
        data: {
          title: 'liste-offres' 
        }
      },
      {
        path: 'cv-matching',
        loadComponent: () => import('./cv-matching/cv-matching.component').then(m => m.CvMatchingComponent),
        data: {
          title: 'cv-matching' 
        }
      },
    ]
  }
];
