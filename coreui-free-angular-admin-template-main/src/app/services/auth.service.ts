import { Injectable } from '@angular/core';
import { INavData } from '@coreui/angular';
import { jwtDecode } from 'jwt-decode';
import { navItemsAdmin } from '../layout/default-layout/_nav-admin';
import { navItemsManager } from '../layout/default-layout/_nav-manager';
import { navItemsRecruiter } from '../layout/default-layout/_nav-recruiter';

export interface DecodedToken {
  sub: string;
  role: string;  // This will now contain "ROLE_ADMIN", "ROLE_MANAGER", etc.
  exp: number;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  getToken(): string | null {
    return localStorage.getItem('jwt');
  }

  // Extract raw role from token (with ROLE_ prefix)
  getRawRole(): string | null {
    const token = this.getToken();
    if (token) {
      const decoded = jwtDecode<DecodedToken>(token);
      return decoded.role;  // Returns "ROLE_ADMIN", "ROLE_MANAGER", etc.
    }
    return null;
  }
getRecruteurEmail(): string | null {
  const token = this.getToken();
  if (token) {
    const decoded = jwtDecode<DecodedToken>(token);
    return decoded.sub ?? null;  // supposition : sub = email
  }
  return null;
}

getManagerEmail(): string | null {
  const token = this.getToken();
  if (token) {
    const decoded = jwtDecode<DecodedToken>(token);
    
    // Si l'utilisateur est un manager, retourner son email
    if (decoded.role === 'ROLE_MANAGER') {
      return decoded.sub ?? null;
    }
    
    // Si vous avez besoin de gérer d'autres cas, ajoutez-les ici
  }
  return null;
}
getUsername(): string | null {
  const token = this.getToken();
  if (!token) return null;
  const decoded = jwtDecode<DecodedToken>(token);
  // si tu stockes le nom ailleurs, adapte : decoded.name / decoded.email / etc.
  return decoded.sub ?? null;
}

  // Get role without prefix for frontend logic
  getUserRole(): string | null {
    const role = this.getRawRole();
    return role ? role.replace('ROLE_', '') : null;  // Returns "ADMIN", "MANAGER", etc.
  }
 
  getNavItems(): INavData[] {
    const role = this.getUserRole();  // Use the unprefixed version
    
    switch (role) {
      case 'ADMIN':
        return navItemsAdmin;
      case 'MANAGER':
        return navItemsManager;
      case 'RECRUITER':
        return navItemsRecruiter;
      default:
        return [];
    }
  }

  getDashboardFor(role: string | null | undefined): string {
  switch (role) {
    case 'ADMIN':     return '/admin-dashboard/dashboard';
    case 'MANAGER':   return '/manager-dashboard/dashboard';
    case 'RECRUITER': return '/recruiter-dashboard/dashboard';
    default:          return '/login';
  }
}
  isLoggedIn(): boolean {
    return !!this.getToken();
  }
}