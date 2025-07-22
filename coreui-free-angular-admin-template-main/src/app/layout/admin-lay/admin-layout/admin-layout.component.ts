import { Component, OnInit } from '@angular/core';
import { ContainerComponent, ShadowOnScrollDirective, SidebarBrandComponent, SidebarComponent, SidebarFooterComponent, SidebarHeaderComponent, SidebarNavComponent, SidebarToggleDirective, SidebarTogglerDirective } from '@coreui/angular';
import { DefaultFooterComponent } from '../../default-layout/default-footer/default-footer.component';
import { DefaultHeaderComponent } from '../../default-layout/default-header/default-header.component';
import { IconDirective } from '@coreui/icons-angular';
import { RouterLink, RouterOutlet } from '@angular/router';
import { NgScrollbar } from 'ngx-scrollbar';
import { navItems } from '../../default-layout/_nav';
import { AuthService } from '../../../services/auth.service';



@Component({
  selector: 'app-admin-layout',
  imports: [SidebarComponent,
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
    ShadowOnScrollDirective],
  templateUrl: './admin-layout.component.html',
  styleUrl: './admin-layout.component.scss'
})
export class AdminLayoutComponent implements OnInit {
  
  public navItems = [...navItems];
  constructor(private authService: AuthService) {  }
   ngOnInit(): void {
    this.navItems = this.authService.getNavItems();
  }

}
