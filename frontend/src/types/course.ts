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
  estimatedTime?: number;
  reason: string; // AI 추천 이유
}

/**
 * 코스 생성 응답 타입
 */
export interface CourseCreateResponse {
  courseId: string;
  places: Place[];
  totalCost: number;
  estimatedTime: number;
}

/**
 * 코스 생성 에러 타입
 */
export interface CourseCreateError {
  message: string;
  code?: string;
  isTimeout?: boolean;
}
