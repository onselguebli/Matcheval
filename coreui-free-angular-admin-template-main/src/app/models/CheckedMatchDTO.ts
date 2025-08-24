//pour afficher les cv cochés dans la liste des cv cochés
export interface CheckedMatchDTO {
  id: number;
  offreId: number | null;
  offreTitre: string | null;

  candidatureId: number | null;
  candidatNom: string | null;
  candidatPrenom: string | null;
  candidatEmail: string | null;

  recruteurEmail: string | null;
  scoreOverall: number | null;
  filenameSnapshot: string | null;
  createdAt: string; // ISO string
}