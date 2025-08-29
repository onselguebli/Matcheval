import { CommonModule, NgStyle } from '@angular/common';
import { ChangeDetectorRef, Component, DestroyRef, DOCUMENT, effect, inject, OnInit, Renderer2, signal, WritableSignal } from '@angular/core';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ChartData, ChartOptions } from 'chart.js';

import {  ChartjsModule } from '@coreui/angular-chartjs';
import { AuthService } from '../../services/auth.service';
import { OverviewManager, OfferTop, MeetingDto, RecruiterPipeline, StatMangService } from '../../services/stat-mang.service';

@Component({
  templateUrl: 'dashboardMang.component.html',
  styleUrls: ['dashboardMang.component.scss'],
  imports: [CommonModule, FormsModule, ChartjsModule]
})
export class DashboardComponentMang implements OnInit {
 email!: string;
  year = new Date().getFullYear();
  rangeDays = 90;

  overview?: OverviewManager;
  topOffers: OfferTop[] = [];
  meetings: MeetingDto[] = [];
  pipeline: RecruiterPipeline[] = [];

  donutStatus: ChartData<'doughnut'> = { labels: [], datasets: [{ data: [] }] };
  donutSources: ChartData<'doughnut'> = { labels: [], datasets: [{ data: [] }] };
  lineMonthly: ChartData<'line'> = { labels: [], datasets: [{ data: [], label: 'Candidatures', fill: true, tension: .35 }] };
  barByRecruiter: ChartData<'bar'> = { labels: [], datasets: [{ data: [], label: 'Candidatures (derniers jours)' }] };
  barPipeline: ChartData<'bar'> = { labels: [], datasets: [] };

  donutOpt: ChartOptions<'doughnut'> = { responsive:true, plugins:{ legend:{ position:'bottom' }}, cutout: '68%' };
  lineOpt: ChartOptions<'line'> = { responsive:true, plugins:{ legend:{ display:false }}, scales:{ y:{ beginAtZero:true } } };
  barOpt: ChartOptions<'bar'> = { responsive:true, plugins:{ legend:{ position:'bottom'}}, scales:{ y:{ beginAtZero:true } } };

  private palette = ['#6366F1','#22C55E','#F59E0B','#EF4444','#06B6D4','#A855F7','#14B8A6','#F97316','#0EA5E9'];
  private soft(c: string, a=.25){ const n=c.replace('#',''); const r=parseInt(n.slice(0,2),16), g=parseInt(n.slice(2,4),16), b=parseInt(n.slice(4,6),16); return `rgba(${r},${g},${b},${a})`; }

  constructor(private auth: AuthService, private api: StatMangService) {}

  ngOnInit(): void {
    this.email = this.auth.getManagerEmail?.() || this.auth.getRecruteurEmail() || '';
    this.load();
  }

  reloadMonthly(){
    this.api.monthly(this.email, this.year).subscribe(ms => {
      this.lineMonthly = {
        labels: ms.map(m=>m.period),
        datasets: [{
          data: ms.map(m=>m.count),
          label:'Candidatures',
          borderColor:this.palette[0],
          backgroundColor:this.soft(this.palette[0]),
          fill:true, tension:.35
        }]
      };
    });
  }

  load(){
    this.api.overview(this.email).subscribe(o => this.overview = o);

    this.reloadMonthly();

    this.api.sources(this.email, this.rangeDays).subscribe(v => {
      const colors = this.palette.slice(0, v.length);
      this.donutSources = { labels: v.map(x=>x.label), datasets: [{ data: v.map(x=>x.count), backgroundColor: colors }] };
    });

    this.api.byRecruiter(this.email, this.rangeDays).subscribe(v => {
      this.barByRecruiter = { labels: v.map(x=>x.label), datasets: [{ data: v.map(x=>x.count), label:`Candidatures (${this.rangeDays}j)`, backgroundColor: this.soft(this.palette[2], .6) }] };
    });

    this.api.pipeline(this.email, this.rangeDays).subscribe(v => {
      this.pipeline = v;
      const labels = v.map(x=>x.recruiter);
      const dsPending  = { label:'En attente', data: v.map(x=>x.pending),  backgroundColor: this.soft('#F59E0B', .6) };
      const dsAccepted = { label:'Acceptées',  data: v.map(x=>x.accepted), backgroundColor: this.soft('#22C55E', .6) };
      const dsRejected = { label:'Refusées',   data: v.map(x=>x.rejected), backgroundColor: this.soft('#EF4444', .6) };
      this.barPipeline = { labels, datasets: [dsPending, dsAccepted, dsRejected] } as any;
    });

    this.api.topOffers(this.email, 5).subscribe(v => this.topOffers = v);
    this.api.upcoming(this.email, 14).subscribe(v => this.meetings = v);
  }

  trackByIdx(i: number){ return i; }
}
