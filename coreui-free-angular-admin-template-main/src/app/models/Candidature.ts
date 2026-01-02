// src/app/models/candidature.model.ts
export interface candidature {
  id: number;
  dateSoumission: string;
  statut: string;
  commentaire: string;
  candidatNom: string;
  candidatPrenom: string;
  candidatEmail: string;
  cvOriginalFilename?: string;
  hasCvText?: boolean;
  offreId?: number;
  offreTitre?: string;
}