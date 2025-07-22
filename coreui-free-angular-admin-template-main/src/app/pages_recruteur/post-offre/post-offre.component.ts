import { Component } from '@angular/core';
import { OffreService } from '../../services/offre.service'; // Adjust the import path as necessary
import { OffreEmploi } from '../../models/OffreEmploi'; // Adjust the import
import { ToastService } from '../../services/toast.service';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-post-offre',
  imports: [ FormsModule,HttpClientModule,CommonModule],
  templateUrl: './post-offre.component.html',
  styleUrl: './post-offre.component.scss'
})
export class PostOffreComponent {
 offre: OffreEmploi = {
   titre: '',
   description: '',
   localisation: '',
   exigences: '',
   dateExpiration: '',
   datePublication:'', 
    statut: ''

 };

  constructor(private offreService: OffreService,
    private toastService: ToastService // Adjust the import path as necessary
  ) {}

  submitForm(form: any): void {
  if (form.invalid) {
    this.toastService.showError('Veuillez remplir tous les champs obligatoires');
    return;
  }

  this.offreService.publierOffre(this.offre).subscribe({
    next: () => this.toastService.showSuccess('Offre publiée avec succès'),
    error: err => this.toastService.showError('Erreur lors de la publication')
  });
}

}
