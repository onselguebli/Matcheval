// src/app/components/checked-list/checked-list.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CvMatchingServiceService } from '../../services/cv-matching-service.service';
import { AuthService } from '../../services/auth.service';
import { FormsModule } from '@angular/forms';
import { CheckedMatchDTO } from 'src/app/models/CheckedMatchDTO';

@Component({
  selector: 'app-checked-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './checked-list.component.html',
  styleUrls: ['./checked-list.component.scss']
})
export class CheckedListComponent implements OnInit {
   items: CheckedMatchDTO[] = [];
  loading = false;
  error: string | null = null;

  isManager = false;
  managerEmail: string | null = null;
  recruteurEmailFilter: string | null = null;

  constructor(
    private matchingService: CvMatchingServiceService,
    private auth: AuthService
  ) {}

  ngOnInit(): void {
    const role = this.auth.getUserRole();
    this.isManager = role === 'MANAGER';

    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = null;

    if (this.isManager) {
      this.managerEmail = this.auth.getRecruteurEmail(); // sub = email
      if (!this.managerEmail) { this.error = 'Email manager introuvable.'; this.loading = false; return; }

      this.matchingService.getCheckedForManager(this.managerEmail, this.recruteurEmailFilter || undefined)
        .subscribe({
          next: data => { this.items = data; this.loading = false; },
          error: err => { console.error(err); this.error = 'Erreur de chargement'; this.loading = false; }
        });

    } else {
      const email = this.auth.getRecruteurEmail();
      if (!email) { this.error = 'Email recruteur introuvable.'; this.loading = false; return; }

      this.matchingService.getCheckedForRecruteur(email)
        .subscribe({
          next: data => { this.items = data; this.loading = false; },
          error: err => { console.error(err); this.error = 'Erreur de chargement'; this.loading = false; }
        });
    }
  }

  delete(itemId: number): void {
    this.matchingService.deleteChecked(itemId).subscribe({
      next: () => this.load(),
      error: err => { console.error(err); this.error = 'Suppression impossible'; }
    });
  }
}
