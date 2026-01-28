// 백엔드 API 응답 형식에 맞춘 타입 정의
export type City = 'seoul' | 'gyeonggi';

export interface Region {
  id: string;
  name: string;
  city: City;
  description: string;
  keywords: string[];
  centerLat: number;
  centerLng: number;
}

export interface RegionsResponse {
  regions: Region[];
}

// 도시를 한글 이름으로 변환
export const cityToDisplay: Record<City, string> = {
  seoul: '서울',
  gyeonggi: '경기',
};
