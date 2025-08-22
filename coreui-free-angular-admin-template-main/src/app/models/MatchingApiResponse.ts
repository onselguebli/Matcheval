import { MatchingResult } from "./MatchingResult";

export interface MatchingApiResponse {
  job_received: {
    title: string;
    description: string;
    skills_required: string[];
    min_years_experience: number;
    location: string;
    languages_required: string[];
  };
  results: MatchingResult[];
}
