import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CvMatchingServiceService } from '../../services/cv-matching-service.service';
import { AuthService } from '../../services/auth.service';
import { CheckedMatchDTO } from '../../models/CheckedMatchDTO';

type Group = { email: string; items: CheckedMatchDTO[]; open: boolean };
@Component({
  selector: 'app-checked-list-mang',
  imports: [CommonModule, FormsModule],
  templateUrl: './checked-list-mang.component.html',
  styleUrl: './checked-list-mang.component.scss'
})
export class CheckedListMangComponent implements OnInit {
 groups: Group[] = [];
  loading = false;
  error: string | null = null;
  filterRecruteurEmail: string = '';
  sortBy: 'date'|'score' = 'date';

  constructor(
    private svc: CvMatchingServiceService,
    private auth: AuthService
  ) {}

  ngOnInit(): void {
    this.load();
  }

  private groupByRecruteur(items: CheckedMatchDTO[]): Group[] {
    const map = new Map<string, CheckedMatchDTO[]>();
    items.forEach(it => {
      const key = it.recruteurEmail || 'inconnu';
      if (!map.has(key)) map.set(key, []);
      map.get(key)!.push(it);
    });
    const arr: Group[] = [];
    map.forEach((vals, key) => {
      arr.push({
        email: key,
        items: this.sort(vals),
        open: true
      });
    });
    // ordre alpha recruteur
    arr.sort((a,b) => a.email.localeCompare(b.email));
    return arr;
  }

  private sort(arr: CheckedMatchDTO[]): CheckedMatchDTO[] {
    const copy = [...arr];
    if (this.sortBy === 'score') {
      copy.sort((a,b) => (b.scoreOverall ?? 0) - (a.scoreOverall ?? 0));
    } else {
      copy.sort((a,b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
    }
    return copy;
  }

  load(): void {
    const managerEmail = this.auth.getRecruteurEmail();
    if (!managerEmail) { this.error = 'Email manager introuvable.'; return; }

    this.loading = true;
    const filter = this.filterRecruteurEmail?.trim() || undefined;
    this.svc.getCheckedForManager(managerEmail, filter).subscribe({
      next: data => {
        this.groups = this.groupByRecruteur(data);
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.error = 'Erreur de chargement';
        this.loading = false;
      }
    });
  }

  toggle(g: Group) { g.open = !g.open; }

  scoreClass(score: number|null|undefined): string {
    const s = (score ?? 0);
    if (s >= 0.8) return 'chip chip--excellent';
    if (s >= 0.6) return 'chip chip--good';
    if (s >= 0.4) return 'chip chip--avg';
    return 'chip chip--low';
  }

  onSortChange(v: any) {
    this.sortBy = v.target.value as any;
    this.groups = this.groups.map(g => ({ ...g, items: this.sort(g.items) }));
  }

  delete(id: number) {
    this.svc.deleteChecked(id).subscribe({
      next: () => this.load(),
      error: err => { console.error(err); this.error = 'Suppression impossible'; }
    });
  }
}
