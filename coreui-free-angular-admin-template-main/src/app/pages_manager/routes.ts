import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    data: {
      title: 'pages_manager' // 🔰 Titre global pour ce groupe de routes
    },
    children: [
      {
        path: 'Meetings',
        loadComponent: () => import('./meeting-room/meeting-room.component').then(m => m.MeetingRoomComponent),
        data: {
          title: 'Meetings' 
        }
      },
     
    ]
  }
];
