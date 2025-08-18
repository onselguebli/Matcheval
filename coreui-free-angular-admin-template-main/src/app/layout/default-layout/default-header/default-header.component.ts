import { CommonModule, NgTemplateOutlet } from '@angular/common';
import { Component, computed, inject, input } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { UserserviceService } from '../../../services/userservice.service'; 
import {
  AvatarComponent,
  BadgeComponent,
  BreadcrumbRouterComponent,
  ColorModeService,
  ContainerComponent,
  DropdownComponent,
  DropdownDividerDirective,
  DropdownHeaderDirective,
  DropdownItemDirective,
  DropdownMenuDirective,
  DropdownToggleDirective,
  HeaderComponent,
  HeaderNavComponent,
  HeaderTogglerDirective,
  NavItemComponent,
  NavLinkDirective,
  SidebarToggleDirective
} from '@coreui/angular';

import { IconDirective } from '@coreui/icons-angular';
import { AuthService } from '../../../services/auth.service';

@Component({
    selector: 'app-default-header',
    templateUrl: './default-header.component.html',
  imports: [ContainerComponent, HeaderTogglerDirective, CommonModule, SidebarToggleDirective, IconDirective, HeaderNavComponent, NavItemComponent, NavLinkDirective, RouterLink, RouterLinkActive, NgTemplateOutlet, BreadcrumbRouterComponent, DropdownComponent, DropdownToggleDirective, AvatarComponent, DropdownMenuDirective, DropdownHeaderDirective, DropdownItemDirective, BadgeComponent, DropdownDividerDirective]
})
export class DefaultHeaderComponent extends HeaderComponent {

  readonly #colorModeService = inject(ColorModeService);
  readonly colorMode = this.#colorModeService.colorMode;
 dashboardLink = '/login';
  displayName: string | null = null;
  displayRole: string | null = null;
  readonly colorModes = [
    { name: 'light', text: 'Light', icon: 'cilSun' },
    { name: 'dark', text: 'Dark', icon: 'cilMoon' },
    { name: 'auto', text: 'Auto', icon: 'cilContrast' }
  ];
onLogout(): void {
  this.usersService.logout();
}
  readonly icons = computed(() => {
    const currentMode = this.colorMode();
    return this.colorModes.find(mode => mode.name === currentMode)?.icon ?? 'cilSun';
  });

  constructor(
    private usersService: UserserviceService,     // (minuscule au début par convention)
    private auth: AuthService                     // <— injecte AuthService
  ) {
    super();
    const role = this.auth.getUserRole();         // 'ADMIN' | 'MANAGER' | 'RECRUITER' | null
    this.dashboardLink = this.auth.getDashboardFor(role);
    this.displayRole = role;
    this.displayName = this.auth.getUsername();
  }


  sidebarId = input('sidebar1');



}
