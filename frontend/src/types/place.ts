/**
 * 장소 큐레이션 정보
 */
export interface PlaceCurationInfo {
  /** 데이트 적합도 점수 (1-10점) */
  dateScore: number;
  /** 분위기 태그 (최대 3개) */
  moodTags: string[];
  /** 1인당 예상 가격대 (원) */
  priceRange: number;
  /** 추천 시간대 */
  bestTime: string;
  /** 한 줄 추천 이유 */
  recommendation: string;
}

/**
 * 장소 큐레이션 API 응답
 */
export interface PlaceCurationResponse {
  success: boolean;
  data: PlaceCurationInfo | null;
  error: {
    code: string;
    message: string;
  } | null;
}

/**
 * 장소 큐레이션 API 에러
 */
export interface PlaceCurationError {
  message: string;
  code?: string;
}
