import { CommonModule, NgStyle } from '@angular/common';
import { ChangeDetectorRef, Component, DestroyRef, DOCUMENT, effect, inject, OnInit, Renderer2, signal, WritableSignal } from '@angular/core';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ChartOptions } from 'chart.js';
import {
  AvatarComponent,
  ButtonDirective,
  ButtonGroupComponent,
  CardBodyComponent,
  CardComponent,
  CardFooterComponent,
  CardHeaderComponent,
  ColComponent,
  FormCheckLabelDirective,
  GutterDirective,
  PaginationComponent,
  ProgressComponent,
  RowComponent,
  TableDirective
} from '@coreui/angular';
import { ChartjsComponent } from '@coreui/angular-chartjs';
import { IconDirective } from '@coreui/icons-angular';
import { Pagination1Component } from "../../pagination-1/pagination-1.component";
import { WidgetsBrandComponent } from '../widgets/widgets-brand/widgets-brand.component';
import { WidgetsDropdownComponent } from '../widgets/widgets-dropdown/widgets-dropdown.component';
import { DashboardChartsData, IChartProps } from './dashboard-charts-data';
import { AdminService } from '../../services/admin.service';
import { WidgetsDropdownComponentNew } from '../widgets/widgets-dropdown-new/widgets-dropdown.componentNew';
import { StatService } from '../../services/stat.service';
import { TrafficDashboardDTO } from '../../models/TrafficDashboar';
import { SiteTypeCount } from '../../models/SiteTypeCount';
import { AuthService } from '../../services/auth.service';
import { StatRecService } from '../../services/stat-rec.service';

interface IUser {
  name: string;
  state: string;
  registered: string;
  country: string;
  usage: number;
  period: string;
  payment: string;
  activity: string;
  avatar: string;
  status: string;
  color: string;
}

@Component({
  templateUrl: 'dashboardRec.component.html',
  styleUrls: ['dashboardRec.component.scss'],
  imports: [WidgetsDropdownComponent,CardComponent, Pagination1Component, FormsModule,CommonModule, CardBodyComponent, RowComponent, ColComponent, ButtonDirective, IconDirective, ReactiveFormsModule, ButtonGroupComponent, FormCheckLabelDirective, ChartjsComponent, NgStyle, CardFooterComponent, GutterDirective, ProgressComponent, WidgetsBrandComponent, CardHeaderComponent, TableDirective, AvatarComponent]
})
export class DashboardRecComponent implements OnInit {
constructor(private statService: StatService,private cdRef: ChangeDetectorRef,public  authService: AuthService,private statRecService:StatRecService) {}
  userRoles: string[] = [];
  offresByType: { type: string, count: number }[] = [];
  userCounts: number[] = [];
  userYears: number[] = [];
  userCountsPerYear: number[] = [];
  userStatusLabels: string[] = [];
  userStatusCounts: number[] = [];
  userCivilityLabels: string[] = [];
  userCivilityCounts: number[] = [];
  cvsTodayCount = 0;
  recruteurLabels: string[] = [];
recruteurCounts: number[] = [];
public totalCVs = 0;
public totalOffres = 0;
public totalRecruteurs = 0;
activeUsersToday: number = 0;
stats: any[] = [];
statsSorted: any[] = [];
maxOffres: number = 0;
searchTerm: string = '';
filteredStats: any[] = [];
currentPage: number = 1;
itemsPerPage: number = 8;



statsGlobales: any = {};
  statsPersonnelles: any = {};
  affichage: 'global' | 'personnel' = 'global';
  
////////////*** lfou9 jdyyyd ******** *////////////

  readonly #destroyRef: DestroyRef = inject(DestroyRef);
  readonly #document: Document = inject(DOCUMENT);
  readonly #renderer: Renderer2 = inject(Renderer2);
  readonly #chartsData: DashboardChartsData = inject(DashboardChartsData);

  public users: IUser[] = [
    {
      name: 'Yiorgos Avraamu',
      state: 'New',
      registered: 'Jan 1, 2021',
      country: 'Us',
      usage: 50,
      period: 'Jun 11, 2021 - Jul 10, 2021',
      payment: 'Mastercard',
      activity: '10 sec ago',
      avatar: './assets/images/avatars/1.jpg',
      status: 'success',
      color: 'success'
    },
    {
      name: 'Avram Tarasios',
      state: 'Recurring ',
      registered: 'Jan 1, 2021',
      country: 'Br',
      usage: 10,
      period: 'Jun 11, 2021 - Jul 10, 2021',
      payment: 'Visa',
      activity: '5 minutes ago',
      avatar: './assets/images/avatars/2.jpg',
      status: 'danger',
      color: 'info'
    },
    {
      name: 'Quintin Ed',
      state: 'New',
      registered: 'Jan 1, 2021',
      country: 'In',
      usage: 74,
      period: 'Jun 11, 2021 - Jul 10, 2021',
      payment: 'Stripe',
      activity: '1 hour ago',
      avatar: './assets/images/avatars/3.jpg',
      status: 'warning',
      color: 'warning'
    },
    {
      name: 'Enéas Kwadwo',
      state: 'Sleep',
      registered: 'Jan 1, 2021',
      country: 'Fr',
      usage: 98,
      period: 'Jun 11, 2021 - Jul 10, 2021',
      payment: 'Paypal',
      activity: 'Last month',
      avatar: './assets/images/avatars/4.jpg',
      status: 'secondary',
      color: 'danger'
    },
    {
      name: 'Agapetus Tadeáš',
      state: 'New',
      registered: 'Jan 1, 2021',
      country: 'Es',
      usage: 22,
      period: 'Jun 11, 2021 - Jul 10, 2021',
      payment: 'ApplePay',
      activity: 'Last week',
      avatar: './assets/images/avatars/5.jpg',
      status: 'success',
      color: 'primary'
    },
    {
      name: 'Friderik Dávid',
      state: 'New',
      registered: 'Jan 1, 2021',
      country: 'Pl',
      usage: 43,
      period: 'Jun 11, 2021 - Jul 10, 2021',
      payment: 'Amex',
      activity: 'Yesterday',
      avatar: './assets/images/avatars/6.jpg',
      status: 'info',
      color: 'dark'
    }
  ];

  public mainChart: IChartProps = { type: 'line' };
  public mainChartRef: WritableSignal<any> = signal(undefined);
  
  public chart: Array<IChartProps> = [];
  public trafficRadioGroup = new FormGroup({
    trafficRadio: new FormControl('Month')
  });

 
   

  initCharts(): void {
  this.mainChartRef()?.stop();
  this.setChartStyles(); // ✅ Forcer le rafraîchissement visuel
}

  handleChartRef($chartRef: any) {
    if ($chartRef) {
      this.mainChartRef.set($chartRef);
    }
  }

  updateChartOnColorModeChange() {
    const unListen = this.#renderer.listen(this.#document.documentElement, 'ColorSchemeChange', () => {
      this.setChartStyles();
    });

    this.#destroyRef.onDestroy(() => {
      unListen();
    });
  }

  setChartStyles() {
    if (this.mainChartRef()) {
      setTimeout(() => {
        const options: ChartOptions = { ...this.mainChart.options };
        const scales = this.#chartsData.getScales();
        this.mainChartRef().options.scales = { ...options.scales, ...scales };
        this.mainChartRef().update();
      });
    }
  }

  //***************jdyyyd te3iii */
   ngOnInit(): void {
    this.loadUsersByCivility();
    this.getCVsToday();
    this.getCandsBySiteType();
    this.getCandsBySiteTypeForRecruteur('');
    this.getOffresByType();
    this.loadUsersByStatus();
    this.getOffresParRecruteur();
    this.getActiveUsersToday();
    this.loadUserStats();
    this.loadUsersPerYear();
    this.loadRecruteursPerManagerStats();
    this.loadUsersByStatus();
    this.initCharts();
    this.updateChartOnColorModeChange();
     this.setTrafficPeriod('Month');
  }

   loadUserStats() {
    this.statService.getUserStatsByRole().subscribe({
      next: data => {
        this.userRoles = Object.keys(data);
        this.userCounts = Object.values(data);
      },
      error: err => {
        console.error('Erreur chargement statistiques', err);
      }
    });
  }
  loadUsersPerYear() {
  this.statService.getUsersPerYear().subscribe({
    next: (data) => {
      this.userYears = Object.keys(data).map(y => +y);
      this.userCountsPerYear = Object.values(data);
    },
    error: (err) => {
      console.error("Erreur chargement stats par année", err);
    }
  });
}
loadUsersByStatus() {
  this.statService.getUsersByStatus().subscribe({
    next: (data) => {
      this.userStatusLabels = Object.keys(data);
      this.userStatusCounts = Object.values(data);
    },
    error: (err) => {
      console.error("Erreur stats status", err);
    }
  });
}

loadUsersByCivility() {
  this.statService.getUsersByCivility().subscribe({
    next: (data) => {
      this.userCivilityLabels = Object.keys(data);
      this.userCivilityCounts = Object.values(data);
    },
    error: (err) => console.error("Erreur stats civilité", err)
  });
}

loadRecruteursPerManagerStats(): void {
  this.statService.getRecruteursPerManagerStats().subscribe({
    next: (data) => {
      this.recruteurLabels = Object.keys(data);
      this.recruteurCounts = Object.values(data);
    },
    error: (err) => console.error('Erreur chargement stats recruteurs par manager', err)
  });
}
recruteursChartOptions: any = {
  responsive: true,
  plugins: {
    legend: { display: false },
    tooltip: {
      callbacks: {
        label: function (ctx: any) {
          return ctx.raw + ' recruteur(s)';
        }
      }
    }
  },
  scales: {
    x: {
      ticks: { maxRotation: 90, minRotation: 45 },
      title: { display: true, text: 'Email du manager' }
    },
    y: {
      beginAtZero: true,
      title: { display: true, text: 'Nombre de recruteurs' }
    }
  }
};
setTrafficPeriod(value: string): void {
 
  this.trafficRadioGroup.setValue({ trafficRadio: value });

  this.statService.getTrafficStats(value).subscribe((data) => {
  

    this.mainChart = { ...this.buildMainChart(data) }; 
    this.totalCVs = data.totalCVs;
    this.totalOffres = data.totalOffres;
    this.totalRecruteurs = data.totalRecruteurs;
    this.initCharts();
  });
}
buildMainChart(data: TrafficDashboardDTO): IChartProps {
  return {
    type: 'line',
    data: {
      labels: data.labels,
      datasets: [
        {
          label: 'CVs',
          backgroundColor: 'rgba(0,123,255,0.1)',
          borderColor: 'rgba(0,123,255,1)',
          data: data.nbCVs,
          fill: false,
          tension: 0.4
        },
        {
          label: 'Offres',
          backgroundColor: 'rgba(40,167,69,0.1)',
          borderColor: 'rgba(40,167,69,1)',
          data: data.nbOffres,
          fill: false,
          tension: 0.4
        },
        {
          label: 'Recruteurs',
          backgroundColor: 'rgba(255,193,7,0.1)',
          borderColor: 'rgba(255,193,7,1)',
          data: data.nbRecruteurs,
          fill: false,
          tension: 0.4
        }
      ]
    },
    options: {
      responsive: true,
      scales: {
        x: {
          display: true,
          title: { display: true, text: 'Période' }
        },
        y: {
          beginAtZero: true,
          title: { display: true, text: 'Nombre' }
        }
      }
    }
  };
}
  getActiveUsersToday() {
    this.statService.getActiveUsersToday().subscribe({
      next: (count) => {
        this.activeUsersToday = count;
      },
      error: (err) => {
        console.error('Erreur chargement utilisateurs actifs aujourd\'hui', err);
      }
    });
  }
  getCVsToday() {
    this.statService.getCVsToday().subscribe({
      next: (count) => {
        this.cvsTodayCount = count;
      },
      error: (err) => {
        console.error('Erreur chargement CVs aujourd\'hui', err);
      }
    });
  }
 getOffresParRecruteur() {
  this.statService.getOffresParRecruteur().subscribe({
    next: (data) => {
      this.statsSorted = [...data].sort((a, b) => b.nombreOffres - a.nombreOffres);
      this.maxOffres = this.statsSorted[0]?.nombreOffres || 0;
        this.filterStats(); 
    },
    error: (err) => {
      console.error('Erreur chargement stats', err);
      this.statsSorted = [];
      this.maxOffres = 0;
    }
  });
}
filterStats() {
  this.currentPage = 1;
  this.filteredStats = this.searchTerm
    ? this.statsSorted.filter(stat =>
        stat.recruteurEmail.toLowerCase().includes(this.searchTerm.toLowerCase())
      )
    : [...this.statsSorted];
}

get paginatedStats(): any[] {
  const start = (this.currentPage - 1) * this.itemsPerPage;
  return this.filteredStats.slice(start, start + this.itemsPerPage);
}

onPageChanged(page: number): void {
  this.currentPage = page;
}

getOffresByType() {
  this.statService.getOffresByType().subscribe({
    next: (data) => {
      this.offresByType = Object.entries(data).map(([type, count]) => ({ type, count }));
      this.offresByType.sort((a, b) => b.count - a.count);
    },
    error: (err) => {
      console.error('Erreur chargement offres par type', err);
      this.offresByType = [];
    }
  });
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

  getCandsBySiteType() {
    this.statService.getCandsBySiteTypeGlobal().subscribe({
       next: (data) => {
         this.statsGlobales = data;
      },
      error: (err) => {
        console.error('Error loading global stats', err);
       
      }
    });
  }

  getCandsBySiteTypeForRecruteur(email: string) {
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

  // Pour pouvoir utiliser Object.keys() dans le template
objectKeys(obj: any): string[] {
  return obj ? Object.keys(obj) : [];
}

  
}
