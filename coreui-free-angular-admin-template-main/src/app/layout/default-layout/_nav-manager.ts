import { INavData } from '@coreui/angular';

export const navItemsManager: INavData[] = [
  {
    name: 'Accueil Mangaer',
    url: '/manager-dashboard/dashboardMang',
    iconComponent: { name: 'cil-speedometer' },
   
  },
   {
    name: 'liste_selectionnées',
    url: '/manager-dashboard/ckecked-listMang',
   iconComponent: { name: 'cil-list' }
  },
  {
    name: 'Meetings',
    url: '/manager-dashboard/pages_manager/Meetings',
   iconComponent: { name: 'cil-people' }
  },
  {
    title: true,
    name: 'Theme'
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
