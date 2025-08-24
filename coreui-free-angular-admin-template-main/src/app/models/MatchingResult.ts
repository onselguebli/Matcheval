export interface MatchingResult {
  filename: string;
  score_overall: number;
  criteria: {
    title_match: number;
    skills_match: number;
    experience_match: number;
    location_match: number;
    languages_match: number;
  };
  missing_skills: string[];
  extra_skills: string[];
  cv_extracted: {
    filename?: string;
    detected_language?: string | null;
    email?: string | null;
    phone?: string | null;
    skills: string[];
    languages: string[];
    years_experience?: number | null;
    location?: string | null;
    raw_text_chars: number;
    
  };
  candidatNom?: string;
  candidatPrenom?: string;
  candidatEmail?: string;
  candidatureId?: number;
  error?: string; // Ajouter cette propriété
}
