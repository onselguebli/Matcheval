import { CommonModule, NgStyle } from '@angular/common';
import { ChangeDetectorRef, Component, DestroyRef, DOCUMENT, effect, inject, OnInit, Renderer2, signal, WritableSignal } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { StatService } from '../../services/stat.service';
import { AuthService } from '../../services/auth.service';
import { CountLabel, MonthlyCount, OfferTop, OverviewStats, StatRecService } from '../../services/stat-rec.service';
import { ChartjsModule } from '@coreui/angular-chartjs';
import { ChartData, ChartOptions } from 'chart.js';

@Component({
  templateUrl: 'dashboardRec.component.html',
  styleUrls: ['dashboardRec.component.scss'],
  imports: [ FormsModule,CommonModule, ReactiveFormsModule,ChartjsModule]
})
export class DashboardRecComponent implements OnInit {
constructor(public  authService: AuthService,private statRecService:StatRecService) {}
  statsPersonnelles: any = {};
   email!: string;

  overview?: OverviewStats;
  byStatus: CountLabel[] = [];
  byType: CountLabel[] = [];
  monthly: MonthlyCount[] = [];
  bySource: CountLabel[] = [];
  topOffers: OfferTop[] = [];

  year = new Date().getFullYear();
  loading = false;
  error?: string;
   private palette = ['#6366F1','#22C55E','#F59E0B','#EF4444','#06B6D4','#A855F7','#14B8A6','#F97316','#0EA5E9'];
  private soft(c: string, a = 0.15) { // rgba util
    const m = c.match(/^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i);
    if (!m) return c;
    const r = parseInt(m[1],16), g = parseInt(m[2],16), b = parseInt(m[3],16);
    return `rgba(${r},${g},${b},${a})`;
  }

 statusData: ChartData<'doughnut'> = { labels: [], datasets: [{ data: [] }] };
  sourceData: ChartData<'doughnut'> = { labels: [], datasets: [{ data: [] }] };
  monthlyData: ChartData<'line'> = { labels: [], datasets: [{ data: [], label: 'Candidatures' }] };
  typeData: ChartData<'bar'> = { labels: [], datasets: [{ data: [], label: 'Offres' }] };

  // (optionnel) options
  donutOptions: ChartOptions<'doughnut'> = { responsive: true, plugins: { legend: { position: 'bottom' } } };
  lineOptions: ChartOptions<'line'> = {
    responsive: true,
    plugins: { legend: { display: false } },
    scales: { y: { beginAtZero: true } }
  };
  barOptions: ChartOptions<'bar'> = { responsive: true, plugins: { legend: { display: false } }, scales: { y: { beginAtZero: true } } };
   ngOnInit(): void {
     this.email = this.authService.getRecruteurEmail() || '';
    this.getCandsBySiteTypeForRecruteur(); 
    this.loadAll();
  }

getTypeIcon(type: string): string {
  switch (type.toUpperCase()) {
    case 'IT': return 'bi bi-laptop';
    case 'MARKETING': return 'bi bi-megaphone';
    case 'FINANCE': return 'bi bi-cash-stack';
    case 'RH': return 'bi bi-people';
    case 'BUSINESS': return 'bi bi-briefcase';
    case 'AUTRE': return 'bi bi-grid-3x3-gap';
    default: return 'bi bi-question-circle';
  }
}

getCandsBySiteTypeForRecruteur(): void {
    if (this.authService.getUserRole() === 'RECRUITER') {
      const email = this.authService.getRecruteurEmail();
      if (email) {
        this.statRecService.getCandsBySiteTypeForRecruteur(email).subscribe({
          next: (data) => {
            this.statsPersonnelles = data;
          },
          error: (err) => {
            console.error('Error loading personal stats', err);
          }
        });
      }
    }
  }

  objectKeys(obj: any): string[] {
    return obj ? Object.keys(obj) : [];
  }
reloadMonthly() {
    this.statRecService.candidaturesMonthly(this.email, this.year).subscribe({
      next: v => {
        this.monthly = v;
        const border = this.palette[0];
        this.monthlyData = {
          labels: this.monthsLabels(),
          datasets: [{
            data: this.monthsCounts(),
            label: 'Candidatures',
            borderColor: border,
            backgroundColor: this.soft(border, .2),
            fill: true,
            tension: .35,
            pointRadius: 3
          }]
        };
      }
    });
  }

  loadAll() {
    this.loading = true; this.error = undefined;

    this.statRecService.overview(this.email).subscribe({ next: v => this.overview = v });

    this.statRecService.offersByStatus(this.email).subscribe({
      next: v => {
        this.byStatus = v;
        const colors = this.palette.slice(0, v.length);
        this.statusData = {
          labels: this.labelsFrom(v),
          datasets: [{ data: this.countsFrom(v), backgroundColor: colors, borderWidth: 1 }]
        };
      }
    });

    this.statRecService.offersByType(this.email).subscribe({
      next: v => {
        this.byType = v;
        const color = this.palette[2];
        this.typeData = {
          labels: this.labelsFrom(v),
          datasets: [{ data: this.countsFrom(v), label: 'Offres', backgroundColor: this.soft(color, .6) }]
        };
      }
    });

    this.statRecService.candidaturesMonthly(this.email, this.year).subscribe({
      next: () => this.reloadMonthly()
    });

    this.statRecService.candidaturesBySource(this.email, 90).subscribe({
      next: v => {
        this.bySource = v;
        const colors = this.palette.slice(0, v.length);
        this.sourceData = {
          labels: this.labelsFrom(v),
          datasets: [{ data: this.countsFrom(v), backgroundColor: colors, borderWidth: 1 }]
        };
      }
    });

    this.statRecService.topOffers(this.email, 5).subscribe({
      next: v => { this.topOffers = v; this.loading = false; },
      error: _ => { this.error = 'Erreur chargement stats'; this.loading = false; }
    });
  }

  // helpers (inchangés)
  private labelsFrom(items: CountLabel[]) { return items.map(i => i.label); }
  private countsFrom(items: CountLabel[]) { return items.map(i => i.count); }
  private monthsLabels() { return this.monthly.map(m => m.period); }
  private monthsCounts() { return this.monthly.map(m => m.count); }

  
}