import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CvMatchingServiceService } from '../../services/cv-matching-service.service';
import { UserserviceService } from '../../services/userservice.service';
import { AuthService } from '../../services/auth.service';
import { OffreEmploi } from '../../models/OffreEmploi';
import { lastValueFrom } from 'rxjs';
import { MatchingResult } from '../../models/MatchingResult';
import { Candidature } from '../../models/candidature';



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
  linkedCvs: Candidature[] = [];
  loadingLinkedCvs = false; 
    selectedMap: Record<string, { candidatureId: number; scoreOverall?: number|null; filenameSnapshot?: string|null }> = {};


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
    if (!this.hasId(this.selectedOffre)) {
      this.error = 'Veuillez sélectionner une offre d\'emploi';
      return;
    }
    if (this.selectedOffre) {
      this.loadLinkedCvs(this.selectedOffre.id);
    } else {
      this.linkedCvs = [];
    }
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

  private convertToCSV(results: any[]): string {
  const headers = [
    'Nom', 'Prénom', 'Email', 'Fichier', 'Score global', 
    'Match titre', 'Match compétences', 'Match expérience', 
    'Match localisation', 'Match langues', 'Compétences trouvées', 
    'Compétences manquantes', 'Téléphone', 'Expérience (ans)', 'Localisation'
  ];

  const rows = results.map(result => [
    result.candidatNom || '',
    result.candidatPrenom || '',
    result.cv_extracted?.email || result.candidatEmail || 'N/A',
    result.filename || 'N/A',
    result.score_overall ? (result.score_overall * 100).toFixed(1) + '%' : 'N/A',
    result.criteria?.title_match ? (result.criteria.title_match * 100).toFixed(1) + '%' : 'N/A',
    result.criteria?.skills_match ? (result.criteria.skills_match * 100).toFixed(1) + '%' : 'N/A',
    result.criteria?.experience_match ? (result.criteria.experience_match * 100).toFixed(1) + '%' : 'N/A',
    result.criteria?.location_match ? (result.criteria.location_match * 100).toFixed(1) + '%' : 'N/A',
    result.criteria?.languages_match ? (result.criteria.languages_match * 100).toFixed(1) + '%' : 'N/A',
    result.cv_extracted?.skills?.join(', ') || 'N/A',
    result.missing_skills?.join(', ') || 'N/A',
    result.cv_extracted?.phone || 'N/A',
    result.cv_extracted?.years_experience?.toString() || 'N/A',
    result.cv_extracted?.location || 'N/A'
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

  
  // Nouvelle méthode: Charger les CVs liés à l'offre
  loadLinkedCvs(offreId: number): void {
    this.loadingLinkedCvs = true;
    this.matchingService.getCandidaturesForOffre(offreId).subscribe({
      next: (candidatures: Candidature[]) => {
        this.linkedCvs = candidatures;
        this.loadingLinkedCvs = false;
      },
      error: (error: any) => {
        console.error('Erreur chargement CVs liés:', error);
        this.error = 'Erreur lors du chargement des CVs liés';
        this.loadingLinkedCvs = false;
      }
    });
  }

  // Nouvelle méthode: Analyser un CV spécifique lié
  // Nouvelle méthode: Analyser un CV spécifique lié
analyzeLinkedCv(candidatureId: number): void {
  if (!this.hasId(this.selectedOffre)) {
    this.error = 'Veuillez sélectionner une offre d\'emploi';
    return;
  }

  this.loading = true;
  this.matchingService.getCvMatchDetails(this.selectedOffre.id, candidatureId).subscribe({
    next: (result: any) => {
      // Ajouter le résultat à l'affichage
      this.matchingResults = [result, ...this.matchingResults];
      this.loading = false;
      this.success = 'Analyse du CV terminée';
    },
    error: (error: any) => {
      console.error('Erreur analyse CV lié:', error);
      this.error = 'Erreur lors de l\'analyse du CV';
      this.loading = false;
    }
  });
}

  // Nouvelle méthode: Analyser tous les CVs liés
  analyzeAllLinkedCvs(): void {
    if (!this.hasId(this.selectedOffre)) {
      this.error = 'Veuillez sélectionner une offre d\'emploi';
      return;
    }
    this.loading = true;
    this.matchingService.matchAllCvsForOffre(this.selectedOffre.id).subscribe({
      next: (results: MatchingResult[]) => {
        this.matchingResults = results;
        this.loading = false;
        this.success = `Analyse terminée pour ${results.length} CV(s) liés`;
      },
      error: (error: any) => {
        console.error('Erreur analyse CVs liés:', error);
        this.error = 'Erreur lors de l\'analyse des CVs liés';
        this.loading = false;
      }
    });
  }
  // Ajoutez ces méthodes dans votre composant
clearUploadedFiles(): void {
  this.selectedFiles = [];
  const fileInput = document.getElementById('cvFiles') as HTMLInputElement;
  if (fileInput) fileInput.value = '';
  this.success = 'Fichiers uploadés effacés';
  setTimeout(() => this.success = null, 3000);
}

clearResults(): void {
  this.matchingResults = [];
  this.success = 'Résultats effacés';
  setTimeout(() => this.success = null, 3000);
}
//la partie de selectionner les cvs scorés
// toggle checkbox
  toggleSelect(result: any, idx: number): void {
    // on ne coche QUE les CV liés (BDD) qui ont un candidatureId
    if (!result.candidatureId) { return; }
    const key = String(result.candidatureId);
    if (this.selectedMap[key]) {
      delete this.selectedMap[key];
    } else {
      this.selectedMap[key] = {
        candidatureId: result.candidatureId,
        scoreOverall: result.score_overall ?? null,
        filenameSnapshot: result.filename ?? null
      };
    }
  }
async checkSelected(): Promise<void> {
  if (!this.hasId(this.selectedOffre)) {
    this.error = 'Veuillez sélectionner une offre d\'emploi';
    return;
  }

  const offreId = this.selectedOffre?.id;
  if (typeof offreId !== 'number') {
    this.error = 'Offre invalide (id manquant).';
    return;
  }

  const recruteurEmail = this.authService.getRecruteurEmail();
  if (!recruteurEmail) {
    this.error = 'Impossible de déterminer votre email (token manquant).';
    return;
  }

  const items = Object.values(this.selectedMap);
  if (items.length === 0) {
    this.error = 'Sélection vide (seuls les CV liés peuvent être cochés).';
    return;
  }

  try {
    this.loading = true;
    const calls = items.map(v =>
      this.matchingService.addCheckedMatch({
        offreId, // <-- maintenant c'est un number certain
        candidatureId: Number(v.candidatureId),
        recruteurEmail,
        scoreOverall: v.scoreOverall ?? null,
        filenameSnapshot: v.filenameSnapshot ?? null
      })
    );
    await Promise.all(calls.map(o => lastValueFrom(o)));
    this.success = 'Sélection enregistrée ✅';
    this.selectedMap = {};
  } catch (e:any) {
    console.error(e);
    this.error = 'Erreur lors de l’enregistrement de la sélection';
  } finally {
    this.loading = false;
  }
}

}