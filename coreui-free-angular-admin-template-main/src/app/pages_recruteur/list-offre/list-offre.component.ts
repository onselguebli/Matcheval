import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service'; // Adjust the import path as necessary
import { UserserviceService } from '../../services/userservice.service'; // Adjust the import path as necessary
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-list-offre',
  imports: [FormsModule,CommonModule],
  templateUrl: './list-offre.component.html',
  styleUrl: './list-offre.component.scss'
})
export class ListOffreComponent implements OnInit {
 offresRecruteur: any[] = [];
selectedOffre: any = null;
constructor(private userService: UserserviceService, private authService: AuthService,private toast:ToastService) {}



  ngOnInit(): void {
    this.loadOffres(); 
    
  }
loadOffres(): void {
  const email = this.authService.getRecruteurEmail();
  if (email) {
    this.userService.getOffresByRecruteur(email).subscribe({
      next: (data) => this.offresRecruteur = data,
      error: () => this.toast.showError("Erreur chargement offres")
    });
  }
}

editOffre(offre: any): void {
  this.selectedOffre = {
    ...offre,
    datePublication: this.formatDateForInput(offre.datePublication),
    dateExpiration: this.formatDateForInput(offre.dateExpiration)
  };
}

get offresActives() {
    return this.offresRecruteur.filter(o => (o.statut||'').toLowerCase() === 'active');
  }
  get offresInactives() {
    return this.offresRecruteur.filter(o => (o.statut||'').toLowerCase() === 'inactive');
  }

  statusClass(o:any) {
    const s = (o.statut||'').toLowerCase();
    return {
      'card-active': s === 'active',
      'card-inactive': s === 'inactive'
    };
  }

saveOffre(): void {
  if (!this.selectedOffre) return;

  const dto = {
    id: this.selectedOffre.id,
    titre: this.selectedOffre.titre,
    description: this.selectedOffre.description,
    exigences: this.selectedOffre.exigences,
    datePublication: this.selectedOffre.datePublication,
    dateExpiration: this.selectedOffre.dateExpiration,
    statut: this.selectedOffre.statut,
    localisation: this.selectedOffre.localisation,
    typeOffre: this.selectedOffre.typeOffre, // "IT", "Business", etc.
    recruteurEmail: this.authService.getRecruteurEmail()
  };

  this.userService.updateOffre(this.selectedOffre.id, dto).subscribe({
    next: () => {
      this.toast.showSuccess("Offre mise à jour !");
      this.selectedOffre = null;
      this.loadOffres();
    },
    error: (err) => console.error("Erreur mise à jour", err)
  });
}


cancelEdit(): void {
  this.selectedOffre = null;
}

private formatDateForInput(dateStr: string | Date): string {
  const date = new Date(dateStr);
  return date.toISOString().split('T')[0]; // => "YYYY-MM-DD"
}

}
