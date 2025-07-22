export interface OffreEmploi {
  id?: number;
  titre: string;
  description?: string;
  exigences?: string;
  dateExpiration: string; 
  datePublication: string; 
  statut: string;
  localisation?: string;
}
