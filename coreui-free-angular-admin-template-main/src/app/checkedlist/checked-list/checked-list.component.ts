// src/app/components/checked-list/checked-list.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CvMatchingServiceService } from '../../services/cv-matching-service.service';
import { AuthService } from '../../services/auth.service';
import { FormsModule } from '@angular/forms';
import { CheckedMatchDTO } from '../../models/CheckedMatchDTO';

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
  query = '';            // recherche locale (candidat, email, offre)
  sortBy: 'date'|'score' = 'date';

  constructor(
    private svc: CvMatchingServiceService,
    private auth: AuthService
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    const email = this.auth.getRecruteurEmail();
    if (!email) { this.error = 'Email recruteur introuvable.'; return; }

    this.loading = true;
    this.svc.getCheckedForRecruteur(email).subscribe({
      next: data => {
        this.items = this.sort(data);
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.error = 'Erreur de chargement';
        this.loading = false;
      }
    });
  }

  sort(arr: CheckedMatchDTO[]): CheckedMatchDTO[] {
    const copy = [...arr];
    if (this.sortBy === 'score') {
      copy.sort((a,b) => (b.scoreOverall ?? 0) - (a.scoreOverall ?? 0));
    } else {
      copy.sort((a,b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
    }
    return copy;
  }

  onSortChange(v: any) {
    this.sortBy = v.target.value as any;
    this.items = this.sort(this.items);
  }

  get filtered(): CheckedMatchDTO[] {
    const q = this.query.trim().toLowerCase();
    if (!q) return this.items;
    return this.items.filter(it =>
      (it.offreTitre || '').toLowerCase().includes(q) ||
      (it.candidatNom || '').toLowerCase().includes(q) ||
      (it.candidatPrenom || '').toLowerCase().includes(q) ||
      (it.candidatEmail || '').toLowerCase().includes(q)
    );
  }

  scoreClass(score: number|null|undefined): string {
    const s = (score ?? 0);
    if (s >= 0.8) return 'chip chip--excellent';
    if (s >= 0.6) return 'chip chip--good';
    if (s >= 0.4) return 'chip chip--avg';
    return 'chip chip--low';
  }

  delete(id: number) {
    this.svc.deleteChecked(id).subscribe({
      next: () => this.load(),
      error: err => { console.error(err); this.error = 'Suppression impossible'; }
    });
  }
}
