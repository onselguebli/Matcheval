import { INavData } from '@coreui/angular';

export const navItemsRecruiter: INavData[] = [
  {
    name: 'Accueil Recruteur',
    url: '/recruiter-dashboard/dashboardRec',
    iconComponent: { name: 'cil-speedometer' },
   
  },
  {
    title: true,
    name: 'Theme'
  },
  {
    name: 'post_offre',
    url: '/recruiter-dashboard/pages_recruteur/post_offre',
   iconComponent: { name: 'cil-user-follow' }
  },
  {
    name: 'liste_candidatures',
    url: '/recruiter-dashboard/pages_recruteur/liste-candidatures',
   iconComponent: { name: 'cil-list' }
  },
  {
    name: 'liste_offres',
    url: '/recruiter-dashboard/pages_recruteur/liste-offre',
   iconComponent: { name: 'cil-list-rich' }
  },
  
  {
    name: 'Components',
    title: true
  },
  
  {
    title: true,
    name: 'Links',
    class: 'mt-auto'
  },
  {
    name: 'Docs',
    url: 'https://coreui.io/angular/docs/',
    iconComponent: { name: 'cil-description' },
    attributes: { target: '_blank' }
  }
];
