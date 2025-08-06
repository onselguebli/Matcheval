import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from 'src/app/services/auth.service';
import { UserserviceService } from 'src/app/services/userservice.service';

@Component({
  selector: 'app-list-offre',
  imports: [FormsModule,CommonModule],
  templateUrl: './list-offre.component.html',
  styleUrl: './list-offre.component.scss'
})
export class ListOffreComponent {
 offresRecruteur: any[] = [];

selectedOffre: any = null;
constructor(private userService: UserserviceService, private authService: AuthService) {}
loadOffres(): void {
  const email = this.authService.getRecruteurEmail();
  if (email) {
    this.userService.getOffresByRecruteur(email).subscribe({
      next: (data) => this.offresRecruteur = data,
      error: (err) => console.error("Erreur chargement offres", err)
    });
  }
}

editOffre(offre: any): void {
  this.selectedOffre = { ...offre }; // clone
}

saveOffre(): void {
  if (!this.selectedOffre) return;
  this.userService.updateOffre(this.selectedOffre.id, this.selectedOffre).subscribe({
    next: () => {
      alert("✅ Offre mise à jour !");
      this.selectedOffre = null;
      this.loadOffres();
    },
    error: (err) => console.error("❌ Erreur mise à jour", err)
  });
}

cancelEdit(): void {
  this.selectedOffre = null;
}


}
