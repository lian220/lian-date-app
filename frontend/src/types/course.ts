/**
 * 코스 생성 요청 타입
 * POST /v1/courses
 */
export interface CourseCreateRequest {
  regionId: string;
  dateType: string; // Date type code: "romantic", "activity", "food", "culture", "healing"
  budget: string; // Budget range format: "0-30000", "30000-50000", "50000-100000", "100000-"
  specialRequest?: string;
}

/**
 * 이동 수단 타입
 */
export type TransportType = 'WALK' | 'CAR' | 'PUBLIC_TRANSIT';

/**
 * 장소 간 이동 정보
 */
export interface Route {
  transportType: TransportType;
  distance: number; // 미터 단위
  duration: number; // 분 단위
}

/**
 * 장소 정보
 */
export interface Place {
  order: number;
  placeId: string;
  name: string;
  category: string;
  categoryDetail?: string;
  address: string;
  roadAddress?: string;
  lat: number;
  lng: number;
  phone?: string;
  estimatedCost: number;
  estimatedDuration: number; // 분 단위
  recommendedTime?: string;
  recommendReason: string; // AI 추천 이유
  imageUrl?: string;
  kakaoPlaceUrl?: string;
}

/**
 * 코스 생성 응답 타입
 */
export interface CourseCreateResponse {
  courseId: string;
  regionId: string;
  regionName: string;
  dateType: string;
  budget: string;
  totalEstimatedCost: number;
  places: Place[];
  routes?: Route[]; // 장소 간 이동 정보 (places.length - 1 개)
  regenerationCount?: number; // 재생성 횟수 (1부터 시작)
  createdAt: string;
}

/**
 * 코스 생성 에러 타입
 */
export interface CourseCreateError {
  message: string;
  code?: string;
  isTimeout?: boolean;
}
