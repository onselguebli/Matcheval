import { AfterContentInit, AfterViewInit, ChangeDetectorRef, Component, inject, OnInit, viewChild } from '@angular/core';
import { getStyle } from '@coreui/utils';
import { ChartjsComponent, ChartjsModule } from '@coreui/angular-chartjs';
import { RouterLink } from '@angular/router';
import { IconDirective } from '@coreui/icons-angular';
import {
  ButtonDirective,
  ColComponent,
  DropdownComponent,
  DropdownDividerDirective,
  DropdownItemDirective,
  DropdownMenuDirective,
  DropdownToggleDirective,
  RowComponent,
  TemplateIdDirective,
  WidgetStatAComponent
} from '@coreui/angular';
import { AdminService } from '../../../services/admin.service';
import { ChartConfiguration } from 'chart.js';
import { CommonModule } from '@angular/common';
import { StatService } from '../../../services/stat.service';

@Component({
  selector: 'app-widgets-dropdown',
  templateUrl: './widgets-dropdown.component.html',
  styleUrls: ['./widgets-dropdown.component.scss'],
  imports: [RowComponent, ColComponent, WidgetStatAComponent,ChartjsModule, CommonModule,TemplateIdDirective, ChartjsComponent]
})
export class WidgetsDropdownComponent implements OnInit {
 public monthlyLabels = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
  
  public data: ChartConfiguration['data'][] = [];
  public options: ChartConfiguration['options'][] = [];
  
  public stats = [
    { title: 'Utilisateurs', color: 'primary', data: [], borderColor: '#4e73df' },
    { title: 'Recruteurs', color: 'info', data: [], borderColor: '#1cc88a' },
    { title: 'Managers', color: 'warning', data: [], borderColor: '#fd7e14' },
    { title: 'Candidatures', color: 'danger', data: [], borderColor: '#e83e8c' }
  ];

  constructor(private statService: StatService) {}

  ngOnInit(): void {
    this.statService.getMonthlyStats().subscribe(data => {
      
      this.stats[0].data = data.totalUsers;
      this.stats[1].data = data.totalRecruteurs;
      this.stats[2].data = data.totalManagers;
      this.stats[3].data = data.totalCandidatures;

      this.prepareChartData();
    });
  }

  private prepareChartData(): void {
    this.stats.forEach(stat => {
      this.data.push({
        labels: this.monthlyLabels,
        datasets: [{
          data: stat.data,
          label: stat.title,
          borderColor: stat.borderColor,
          backgroundColor: 'rgba(255,255,255,0.1)',
          borderWidth: 1,
          fill: true,
          pointBackgroundColor: stat.borderColor,
          pointHoverBackgroundColor: '#fff',
          tension: 0.4
        }]
      });

      this.options.push({
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
        },
        elements: {
          line: {
            tension: 0.4
          },
          point: {
            radius: 0
          }
        }
      });
    });
  }

 getLastValue(data: number[]): number {
  return data.reduce((acc, curr) => acc + curr, 0); // somme annuelle
}
formatValue(value: number): string {
  if (value >= 1000) return (value / 1000).toFixed(1) + 'K';
  return value.toString();
}



  
}
