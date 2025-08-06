import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { UserserviceService } from '../../services/userservice.service';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-liste-candidatures',
  imports: [CommonModule,FormsModule],
  templateUrl: './liste-candidatures.component.html',
  styleUrl: './liste-candidatures.component.scss'
})
export class ListeCandidaturesComponent implements OnInit {
  offres: any[] = []; 
  selectedCandidatureId: number | null = null;
selectedCandidature: any = null;

  constructor(private userService: UserserviceService,
    private authService: AuthService ,
    private toast: ToastService 
  ) {}

  ngOnInit(): void {
    this.loadCandidatures();
  }

  loadCandidatures(): void {
  const recruteurEmail = this.authService.getRecruteurEmail();

 if (recruteurEmail !== null) {
  this.userService.getOffresWithCandidaturesByRecruteur(recruteurEmail).subscribe({
    next: (data) => {
      this.offres = data;
    },
    error: (err) => {
      console.error('Error loading candidatures', err);
    }
  });
} else {
  console.error('Recruiter email is null. User may not be authenticated.');
}
  }

showDetails(candidatureId: number): void {
  this.selectedCandidatureId = candidatureId;
  this.userService.getCandidatureById(candidatureId).subscribe({
    next: (data) => this.selectedCandidature = data,
    error: (err) => console.error('Erreur chargement candidature', err)
  });
}

closeDetails(): void {
  this.selectedCandidatureId = null;
  this.selectedCandidature = null;
}
updateStatut(): void {
  if (this.selectedCandidatureId && this.selectedCandidature?.statut) {
    const dto = {
      statut: this.selectedCandidature.statut
      // you can also send other fields like commentaire if needed
    };

    this.userService.updateCandidatureStatut(this.selectedCandidatureId, dto).subscribe({
      next: (updated) => {
        this.toast.showSuccess('Statut mis à jour avec succès');
        this.closeDetails();
        this.loadCandidatures();  // Refresh the list
      },
      error: (err) => {
        this.toast.showError('Erreur lors de la mise à jour du statut');
        console.error('Erreur lors de la mise à jour du statut', err);
      }
    });
  }
}


}
