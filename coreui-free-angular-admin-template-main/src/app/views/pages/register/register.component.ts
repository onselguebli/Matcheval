import { Component } from '@angular/core';
import { IconDirective } from '@coreui/icons-angular';
import { NgxIntlTelInputModule } from 'ngx-intl-tel-input';

import { ContainerComponent, RowComponent, ColComponent, TextColorDirective, CardComponent, CardBodyComponent, FormDirective, InputGroupComponent, InputGroupTextDirective, FormControlDirective, ButtonDirective, AlertComponent } from '@coreui/angular';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ReqRes } from '../../../models/ReqRes';
import { AdminService } from '../../../services/admin.service';
import { CommonModule } from '@angular/common';
import { cilCheck } from '@coreui/icons';
import { ToastService } from '../../../services/toast.service';

@Component({
    selector: 'app-register',
    templateUrl: './register.component.html',
    styleUrls: ['./register.component.scss'],
    imports: [ContainerComponent, NgxIntlTelInputModule, RowComponent, FormsModule, CommonModule, IconDirective, ReactiveFormsModule, ColComponent, TextColorDirective, CardComponent, CardBodyComponent, FormDirective, InputGroupComponent, InputGroupTextDirective, IconDirective, FormControlDirective, ButtonDirective]
})
export class RegisterComponent {

 registerForm: FormGroup;
 managers: any[] = [];
 showSuccess: boolean = false; 
 showError: boolean = false;
 icons = { cilCheck };
  constructor(private fb: FormBuilder, 
    private adminService: AdminService,
  private toastService: ToastService) {
   this.registerForm = this.fb.group({
  firstname: [''],
  lastname: [''],
  email: ['', [
    Validators.required,
    Validators.email
  ]],
  password: ['', [
    Validators.required,
    Validators.minLength(8),
    Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{8,}$/)
  ]],
  dnaiss: [''],
  civility: [null],
  phonenumber: ['', [
    Validators.minLength(8),
    Validators.maxLength(8),
    Validators.pattern(/^[0-9]{8}$/)
  ]],
  enabled: [null,Validators.required],
   managerId: [null] ,
  role: [null, Validators.required]
});


  }
ngOnInit(): void {
  this.adminService.getManagers().subscribe({
  next: (res) => {
    this.managers = res.listusers ?? [];
    console.log('Managers chargés :', this.managers); // pour vérifier
  },
  error: (err) => {
    console.error('Erreur chargement managers', err);
  }
});

  // Écoute les changements de rôle
  this.registerForm.get('role')?.valueChanges.subscribe((value) => {
    if (value === 'RECRUITER') {
      this.registerForm.get('managerId')?.setValidators([Validators.required]);
    } else {
      this.registerForm.get('managerId')?.clearValidators();
    }
    this.registerForm.get('managerId')?.updateValueAndValidity();
  });
}
  onSubmit() {
  if (this.registerForm.valid) {
    const user: ReqRes = this.registerForm.value;

    this.adminService.register(user).subscribe({
      next: (res) => {
        if (res.statusCode === 409 && res.message === 'Email déjà utilisé') {
          this.toastService.showWarning('Cet email 📧 est déjà utilisé !');
        } else if (res.statusCode === 200) {
          console.log('Utilisateur enregistré', res);
          this.toastService.showSuccess('Utilisateur ajouté avec succès !');
          this.registerForm.reset();
        } else {
          this.toastService.showError('Erreur inconnue lors de l’ajout');
        }
      },
      error: (err) => {
        console.error('Erreur serveur', err);
        this.toastService.showError('Erreur serveur lors de l’ajout');
      }
    });
  }
}


}
