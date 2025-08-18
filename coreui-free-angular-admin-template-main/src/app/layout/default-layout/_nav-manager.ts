import { INavData } from '@coreui/angular';

export const navItemsManager: INavData[] = [
  {
    name: 'Accueil Manager',
    url: '/dashboard',
    iconComponent: { name: 'cil-speedometer' },
    badge: {
      color: 'info',
      text: ''
    
    }
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
