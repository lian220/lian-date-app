/**
 * 코스 생성 요청 타입
 * POST /v1/courses
 */

// 백엔드 API DateType 열거형
export type ApiDateType =
  | 'CAFE_TOUR'
  | 'ACTIVITY'
  | 'CULTURE'
  | 'HEALING'
  | 'FOOD_TOUR';

// 백엔드 API Budget 열거형
export type ApiBudget =
  | 'UNDER_30K'
  | '30K_TO_50K'
  | '50K_TO_100K'
  | 'OVER_100K';

export interface CourseCreateRequest {
  regionId: string;
  dateType: ApiDateType;
  budget: ApiBudget;
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
  placeId: string;
  name: string;
  category: string;
  address: string;
  lat?: number;
  lng?: number;
  estimatedCost: number;
  estimatedTime?: number; // 분 단위
  reason: string; // AI 추천 이유
}

/**
 * 코스 생성 응답 타입
 */
export interface CourseCreateResponse {
  courseId: string;
  places: Place[];
  routes?: Route[]; // 장소 간 이동 정보 (places.length - 1 개)
  totalCost: number;
  estimatedTime: number;
  regenerationCount?: number; // 재생성 횟수 (1부터 시작)
}

/**
 * 코스 생성 에러 타입
 */
export interface CourseCreateError {
  message: string;
  code?: string;
  isTimeout?: boolean;
}
