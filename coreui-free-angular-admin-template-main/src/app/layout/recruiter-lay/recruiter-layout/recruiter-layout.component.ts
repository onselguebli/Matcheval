import { Component, OnInit } from '@angular/core';
import { RouterOutlet, RouterLink } from '@angular/router';
import { ContainerComponent, ShadowOnScrollDirective, SidebarBrandComponent, SidebarComponent, SidebarFooterComponent, SidebarHeaderComponent, SidebarNavComponent, SidebarToggleDirective, SidebarTogglerDirective } from '@coreui/angular';
import { IconDirective } from '@coreui/icons-angular';
import { NgScrollbar } from 'ngx-scrollbar';
import { DefaultFooterComponent } from '../../default-layout/default-footer/default-footer.component';
import { DefaultHeaderComponent } from '../../default-layout/default-header/default-header.component';
import { navItems } from '../../default-layout/_nav';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-recruiter-layout',
  imports: [
      SidebarComponent,
    SidebarHeaderComponent,
    SidebarBrandComponent,
    SidebarNavComponent,
    SidebarFooterComponent,
    SidebarToggleDirective,
    SidebarTogglerDirective,
    ContainerComponent,
    DefaultFooterComponent,
    DefaultHeaderComponent,
    IconDirective,
    NgScrollbar,
    RouterOutlet,
    RouterLink,
    ShadowOnScrollDirective
  ],
  templateUrl: './recruiter-layout.component.html',
  styleUrl: './recruiter-layout.component.scss'
})
export class RecruiterLayoutComponent implements OnInit {
 public navItems = [...navItems];
  constructor(private authService: AuthService) {  }
    ngOnInit(): void {
     this.navItems = this.authService.getNavItems();
   }
}
