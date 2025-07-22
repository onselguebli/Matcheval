import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AdminService } from '../../services/admin.service';
import { ToastService } from '../../services/toast.service';
import { ActivatedRoute } from '@angular/router';
import { User } from '../../models/User';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-updateuser',
  templateUrl: './updateuser.component.html',
  styleUrls: ['./updateuser.component.scss'],
  standalone: true,
  imports: [ReactiveFormsModule,CommonModule] // Ajoute ReactiveFormsModule dans le module principal ou ici si standalone
})
export class UpdateuserComponent implements OnInit {
  @Input() userId!: number;
@Output() formClosed = new EventEmitter<boolean>();
  editForm: FormGroup;
  

  constructor(
    private fb: FormBuilder,
    private adminService: AdminService,
    private toastService: ToastService,
   
  ) {
    this.editForm = this.fb.group({
      firstname: [''],
      lastname: [''],
      email: ['',[Validators.required, Validators.email]],
      dnaiss: [''],
      civility: [null],
      phonenumber: ['',[
    
    Validators.pattern(/^[0-9]{8}$/) // exactement 8 chiffres
  ]],
      role: ['', Validators.required],
      enabled: [null,Validators.required]
    });
  }

  ngOnInit(): void {
  
  this.adminService.getUserById(this.userId).subscribe({
  next: (res) => {
    const user = res.user; 
    this.editForm.patchValue({
      firstname: user.firstname,
      lastname: user.lastname,
      email: user.email,
      dnaiss: user.dnaiss?.substring(0, 10),
      civility: user.civility,
      phonenumber: user.phonenumber,
      role: user.role,
      enabled: user.enabled
    });
  },
  error: err => {
    this.toastService.showError('Erreur chargement utilisateur');
    console.error(err);
  }
});
}


  onSubmit(): void {
    if (this.editForm.invalid) {
    this.editForm.markAllAsTouched();
    return;
  }
    const formValue = this.editForm.value;

    const updatedFields: Partial<User> = {};
    for (const key in formValue) {
  const typedKey = key as keyof User;
  if (formValue[typedKey] !== null && formValue[typedKey] !== '') {
    updatedFields[typedKey] = formValue[typedKey];
  }
}


    this.adminService.updateUser(this.userId, updatedFields).subscribe({
      next: res => {
        this.toastService.showSuccess(' Utilisateur mis à jour avec succès');
        this.formClosed.emit(true);
      },
      error: err => {
        this.toastService.showError(' Erreur lors de la mise à jour');
        console.error(err);
      }
    });
  }
}
