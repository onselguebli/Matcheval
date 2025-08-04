import {
  AfterContentInit,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  Input
} from '@angular/core';
import { ChartjsComponent } from '@coreui/angular-chartjs';
import { IconDirective } from '@coreui/icons-angular';
import { ColComponent, RowComponent, WidgetStatDComponent } from '@coreui/angular';
import { ChartData } from 'chart.js';
import { StatService } from '../../../services/stat.service';
import { SiteStatsDTO } from '../../../models/SiteStatsDTO';

type BrandData = {
  icon: string
  values: any[]
  capBg?: any
  color?: string
  labels?: string[]
  data: ChartData
}

@Component({
  selector: 'app-widgets-brand',
  templateUrl: './widgets-brand.component.html',
  styleUrls: ['./widgets-brand.component.scss'],
  changeDetection: ChangeDetectionStrategy.Default,
  standalone: true,
  imports: [RowComponent, ColComponent, WidgetStatDComponent, IconDirective, ChartjsComponent]
})
export class WidgetsBrandComponent implements AfterContentInit {

  @Input() withCharts: boolean = true;

  chartOptions = {
    elements: {
      line: {
        tension: 0.4
      },
      point: {
        radius: 0,
        hitRadius: 10,
        hoverRadius: 4,
        hoverBorderWidth: 3
      }
    },
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: false
      }
    },
    scales: {
      x: {
        display: false
      },
      y: {
        display: false
      }
    }
  };

  labels = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul'];
  datasets = {
    borderWidth: 2,
    fill: true
  };
  colors = {
    backgroundColor: 'rgba(255,255,255,.1)',
    borderColor: 'rgba(255,255,255,.55)',
    pointHoverBackgroundColor: '#fff',
    pointBackgroundColor: 'rgba(255,255,255,.55)'
  };

  brandData: BrandData[] = [];

  constructor(
    private statService: StatService,
    private cdr: ChangeDetectorRef
  ) {}

  ngAfterContentInit(): void {
    this.statService.getStatsBySite().subscribe((sites: SiteStatsDTO[]) => {
      this.brandData = sites.map((site, index) => ({
        icon: this.getIconByIndex(index),
        values: [
          { title: 'offres', value: site.nombreOffres.toString() },
          { title: 'candidatures', value: site.nombreCandidatures.toString() }
        ],
        capBg: this.getCapBgByIndex(index),
        labels: [...this.labels],
        data: {
          labels: [...this.labels],
          datasets: [{
            ...this.datasets,
            data: this.generateRandomData(),
            label: site.nomSite,
            ...this.colors
          }]
        }
      }));
      this.cdr.detectChanges();
    });
  }

  getIconByIndex(index: number): string {
    const icons = ['cibFacebook', 'cibTwitter', 'cib-linkedin', 'cilCalendar'];
    return icons[index % icons.length];
  }

  getCapBgByIndex(index: number): any {
    const colors = ['#3b5998', '#00aced', '#4875b4', 'var(--cui-warning)'];
    return { '--cui-card-cap-bg': colors[index % colors.length] };
  }

  generateRandomData(): number[] {
    return Array.from({ length: 7 }, () => Math.floor(Math.random() * 100));
  }
}
