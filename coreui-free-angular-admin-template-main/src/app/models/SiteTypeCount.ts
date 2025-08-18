export interface SiteTypeCount {
  site: string;
  typeOffre: string;  // or use a TypeScript enum if you have defined one
  count: number;
}

export type SiteTypeCountResponse = SiteTypeCount[];