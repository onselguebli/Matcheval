import { INavData } from '@coreui/angular';

export const navItemsAdmin: INavData[] = [
  {
    name: 'Dashboard',
    url: '/admin-dashboard/dashboard',
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
    title: true,
    name: 'Utilisateurs '
  },
 {
    name: 'Register',
    url: '/admin-dashboard/pages/register',
   iconComponent: { name: 'cil-user-follow' }
  },
  {
    name: 'Listes d utilisateurs',
    url: '/admin-dashboard/pages_admin/listusers',
   iconComponent: { name: 'cil-list' }
  },
  {
    name: 'Typography',
    url: '/admin-dashboard/theme/typography',
    linkProps: { fragment: 'headings' },
    iconComponent: { name: 'cil-pencil' }
  },
  {
    name: 'Components',
    title: true
  },
  
  {
    name: 'Widgets',
    url: '/admin-dashboard/widgets',
    iconComponent: { name: 'cil-calculator' },
    badge: {
      color: 'info',
      text: 'NEW'
    }
  },
  {
    title: true,
    name: 'Extras'
  },
  {
    name: 'Pages',
    url: '/login',
    iconComponent: { name: 'cil-star' },
    children: [
      {
        name: 'Error 404',
        url: '/404',
        icon: 'nav-icon-bullet'
      },
      {
        name: 'Error 500',
        url: '/500',
        icon: 'nav-icon-bullet'
      }
    ]
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
