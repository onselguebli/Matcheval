import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivate,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root',
})
export class RoleGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {
    const expectedRole = route.data['expectedRole'];
    
    // Cas 1: Page inexistante (expectedRole est undefined)
    if (expectedRole === undefined) {
      if (this.authService.isLoggedIn()) {
        // Rediriger vers le dashboard approprié si connecté
        const userRole = this.authService.getUserRole();
        this.router.navigate([this.authService.getDashboardFor(userRole)]);
      } else {
        // Rediriger vers login si non connecté
        this.router.navigate(['/login']);
      }
      return false;
    }

    // Cas 2: Utilisateur non connecté
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return false;
    }

    const userRole = this.authService.getUserRole();

    // Cas 3: Rôle incorrect
    if (!userRole || userRole !== expectedRole) {
      // Rediriger vers le dashboard approprié
      this.router.navigate([this.authService.getDashboardFor(userRole)]);
      return false;
    }

    return true;
  }
}