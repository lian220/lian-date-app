export type RegionType = 'seoul' | 'gyeonggi';

export interface Region {
  id: string;
  name: string;
  keywords: string[];
}

export interface RegionCategory {
  type: RegionType;
  label: string;
  regions: Region[];
}
