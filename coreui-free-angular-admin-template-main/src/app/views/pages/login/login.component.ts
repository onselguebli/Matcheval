import { Component } from '@angular/core';
import { NgStyle } from '@angular/common';
import { IconDirective } from '@coreui/icons-angular';
import { ContainerComponent, RowComponent, ColComponent, CardGroupComponent, TextColorDirective, CardComponent, CardBodyComponent, FormDirective, InputGroupComponent, InputGroupTextDirective, FormControlDirective, ButtonDirective } from '@coreui/angular';
import { UserserviceService } from '../../../services/userservice.service'; // Adjust the import path as necessary
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../services/auth.service'; // Adjust the import path as necessary
import { ToastService } from '../../../services/toast.service'; // Adjust the import path as necessary
@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.scss'],
    imports: [ContainerComponent, RowComponent, ColComponent, CardGroupComponent, TextColorDirective, CardComponent, CardBodyComponent, FormDirective, InputGroupComponent, InputGroupTextDirective, IconDirective, FormControlDirective, ButtonDirective, NgStyle,FormsModule]
})
export class LoginComponent {
email: string = '';
password: string = '';
constructor(private userService: UserserviceService, 
  private router : Router,
private authService: AuthService,
private toastservice: ToastService ) {}

 onLogin() {
    this.userService.login(this.email, this.password).subscribe({
      next: (res: any) => {
        // 1. Stocker le token
        localStorage.setItem('jwt', res.token);

        // 2. Lire le rôle via AuthService
        const role = this.authService.getUserRole();
        console.log('User role:', role);
        // 3. Rediriger selon le rôle
        if (role === 'ADMIN') {
          this.router.navigate(['/admin-dashboard']);
          this.toastservice.showSuccess('Connexion réussie, bienvenue Admin!');
        } else if (role === 'MANAGER') {
          this.router.navigate(['/manager-dashboard']);
          this.toastservice.showSuccess('Connexion réussie, bienvenue Manager!');
        } else if (role === 'RECRUITER') {
          this.router.navigate(['/recruiter-dashboard']);
          this.toastservice.showSuccess('Connexion réussie, bienvenue Recruiter!');
        } else {
          this.router.navigate(['/unauthorized']);
        }
      },
      error: (err) => {
        this.toastservice.showError('Login failed:');
        alert('Login failed: ' + (err.error?.message || 'Unknown error'));
      }
    });
  }
}
