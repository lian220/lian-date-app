// 백엔드 API 응답 형식에 맞춘 타입 정의
export type AreaType = 'SEOUL' | 'GYEONGGI';

export interface Region {
  id: string;
  name: string;
  areaType: AreaType;
  keywords: string[];
}

export interface RegionsResponse {
  regions: Region[];
}
