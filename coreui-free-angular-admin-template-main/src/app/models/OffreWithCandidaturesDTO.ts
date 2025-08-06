export interface OffreWithCandidaturesDTO {
  id: number;
  titre: string;
  candidatures: {
    id: number;
    candidatNom: string;
    candidatPrenom: string;
    dateSoumission: string;
  }[];
}