import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CvMatchingServiceService } from '../../services/cv-matching-service.service';
import { UserserviceService } from '../../services/userservice.service';
import { AuthService } from '../../services/auth.service';
import { OffreEmploi } from '../../models/OffreEmploi';
import { lastValueFrom } from 'rxjs';
import { MatchingResult } from '../../models/MatchingResult';


@Component({
  selector: 'app-cv-matching',
  imports: [CommonModule, FormsModule],
  templateUrl: './cv-matching.component.html',
  styleUrl: './cv-matching.component.scss'
})
export class CvMatchingComponent implements OnInit {
  offres: OffreEmploi[] = [];
  selectedOffre: OffreEmploi | null = null;
  selectedFiles: File[] = [];
  matchingResults: MatchingResult[] = [];
  loading = false;
  uploading = false;
  loadingOffres = false;
  error: string | null = null;
  success: string | null = null;

  constructor(
    private matchingService: CvMatchingServiceService,
    private userService: UserserviceService,
    private authService: AuthService
  ) {}

  hasId(obj: any): obj is { id: number } {
    return obj && typeof obj.id === 'number';
  }

  ngOnInit(): void {
    this.loadOffres();
  }

  loadOffres(): void {
    this.loadingOffres = true;
    this.error = null;
    
    const recruteurEmail = this.authService.getRecruteurEmail();
    
    if (!recruteurEmail) {
      this.error = 'Utilisateur non connecté ou non autorisé';
      this.loadingOffres = false;
      return;
    }

    this.userService.getOffresByRecruteur(recruteurEmail).subscribe({
      next: (offres: OffreEmploi[]) => {
        this.offres = offres;
        this.loadingOffres = false;
      },
      error: (error: any) => {
        console.error('Erreur chargement offres:', error);
        this.error = 'Erreur lors du chargement des offres';
        this.loadingOffres = false;
      }
    });
  }

  onOffreSelected(event: any): void {
    const offreId = event.target.value;
    this.selectedOffre = this.offres.find(o => o.id === Number(offreId)) || null;
  }

  onFileSelected(event: any): void {
    const files: FileList = event.target.files;
    this.selectedFiles = Array.from(files);
    
    this.success = `${this.selectedFiles.length} fichier(s) sélectionné(s)`;
    setTimeout(() => this.success = null, 3000);
  }

  async uploadAndMatch(): Promise<void> {
    if (!this.hasId(this.selectedOffre)) {
      this.error = 'Veuillez sélectionner une offre d\'emploi';
      return;
    }
    
    if (this.selectedFiles.length === 0) {
      this.error = 'Veuillez sélectionner au moins un CV';
      return;
    }

    this.uploading = true;
    this.loading = true;
    this.error = null;
    this.matchingResults = [];
    const offreId = this.selectedOffre.id;

    try {
      const uploadPromises = this.selectedFiles.map(file => 
        lastValueFrom(this.matchingService.matchUploadedCv(offreId, file))
      );

      const results = await Promise.all(uploadPromises);
      this.matchingResults = results.filter(result => result !== undefined);
      this.success = `Analyse terminée pour ${this.matchingResults.length} CV(s)`;
    } catch (error: any) {
      console.error('Erreur matching:', error);
      this.error = error.message || 'Erreur lors de l\'analyse des CVs';
    } finally {
      this.uploading = false;
      this.loading = false;
    }
  }

  matchExistingCvs(): void {
    if (!this.hasId(this.selectedOffre)) {
      this.error = 'Veuillez sélectionner une offre d\'emploi';
      return;
    }

    this.loading = true;
    this.error = null;
    const offreId = this.selectedOffre.id;
    
    this.matchingService.matchAllCvsForOffre(offreId).subscribe({
      next: (results: MatchingResult[]) => {
        this.matchingResults = results;
        this.loading = false;
        this.success = `Analyse terminée pour ${results.length} CV(s) existants`;
      },
      error: (error: any) => {
        console.error('Erreur matching CVs existants:', error);
        this.error = 'Erreur lors de l\'analyse des CVs existants';
        this.loading = false;
      }
    });
  }

  downloadResults(): void {
    if (this.matchingResults.length === 0) {
      this.error = 'Aucun résultat à exporter';
      return;
    }

    const csvContent = this.convertToCSV(this.matchingResults);
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);
    
    link.setAttribute('href', url);
    link.setAttribute('download', `matching-results-${new Date().toISOString().split('T')[0]}.csv`);
    link.style.visibility = 'hidden';
    
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }

  private convertToCSV(results: MatchingResult[]): string {
    const headers = [
      'Nom du fichier', 'Score global', 'Match titre', 'Match compétences',
      'Match expérience', 'Match localisation', 'Match langues',
      'Compétences trouvées', 'Compétences manquantes', 'Email',
      'Téléphone', 'Expérience (ans)', 'Localisation'
    ];

    const rows = results.map(result => [
      result.filename,
      (result.score_overall * 100).toFixed(1) + '%',
      (result.criteria.title_match * 100).toFixed(1) + '%',
      (result.criteria.skills_match * 100).toFixed(1) + '%',
      (result.criteria.experience_match * 100).toFixed(1) + '%',
      (result.criteria.location_match * 100).toFixed(1) + '%',
      (result.criteria.languages_match * 100).toFixed(1) + '%',
      result.cv_extracted.skills.join(', '),
      result.missing_skills.join(', '),
      result.cv_extracted.email || 'N/A',
      result.cv_extracted.phone || 'N/A',
      result.cv_extracted.years_experience?.toString() || 'N/A',
      result.cv_extracted.location || 'N/A'
    ]);

    return [headers, ...rows]
      .map(row => row.map(field => `"${field}"`).join(','))
      .join('\n');
  }

  getScoreClass(score: number): string {
    if (score >= 0.8) return 'score-excellent';
    if (score >= 0.6) return 'score-good';
    if (score >= 0.4) return 'score-average';
    return 'score-poor';
  }

  clearSelection(): void {
    this.selectedOffre = null;
    this.selectedFiles = [];
    this.matchingResults = [];
    this.error = null;
    this.success = null;
    
    const selectElement = document.getElementById('offreSelect') as HTMLSelectElement;
    if (selectElement) selectElement.value = '';
    
    const fileInput = document.getElementById('cvFiles') as HTMLInputElement;
    if (fileInput) fileInput.value = '';
  }
}