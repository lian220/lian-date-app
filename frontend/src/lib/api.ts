import { RegionsResponse } from '@/types/region';
import {
  CourseCreateRequest,
  CourseCreateResponse,
  CourseCreateError,
} from '@/types/course';

/**
 * 백엔드 API 기본 URL
 * TODO: 환경변수로 관리 (NEXT_PUBLIC_API_URL)
 */
const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

/**
 * API 요청 타임아웃 (밀리초)
 */
const API_TIMEOUT = 30000; // 30초

/**
 * 권역 목록 조회 API
 * GET /v1/regions
 *
 * @returns 권역 목록
 * @throws API 호출 실패 시 에러
 *
 * 의존성: [BE] 권역 목록 API (LAD-13) 완료 필요
 */
export async function fetchRegions(): Promise<RegionsResponse> {
  const response = await fetch(`${API_BASE_URL}/v1/regions`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  });

  if (!response.ok) {
    throw new Error(`Failed to fetch regions: ${response.statusText}`);
  }

  return response.json();
}

/**
 * 코스 생성 API
 * POST /v1/courses
 *
 * @param request 코스 생성 요청 데이터
 * @returns 생성된 코스 정보
 * @throws CourseCreateError API 호출 실패 또는 타임아웃 시 에러
 *
 * 의존성: [BE] 코스 생성 API (LAD-21) 완료 필요
 */
export async function createCourse(
  request: CourseCreateRequest
): Promise<CourseCreateResponse> {
  const controller = new AbortController();
  const timeoutId = setTimeout(() => controller.abort(), API_TIMEOUT);

  try {
    const response = await fetch(`${API_BASE_URL}/v1/courses`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(request),
      signal: controller.signal,
    });

    clearTimeout(timeoutId);

    if (!response.ok) {
      const error: CourseCreateError = {
        message: `코스 생성에 실패했습니다: ${response.statusText}`,
        code: response.status.toString(),
      };
      throw error;
    }

    return response.json();
  } catch (error) {
    clearTimeout(timeoutId);

    if (error instanceof Error && error.name === 'AbortError') {
      const timeoutError: CourseCreateError = {
        message: '요청 시간이 초과되었습니다. 다시 시도해주세요.',
        isTimeout: true,
      };
      throw timeoutError;
    }

    if ((error as CourseCreateError).message) {
      throw error;
    }

    const unknownError: CourseCreateError = {
      message: '알 수 없는 오류가 발생했습니다.',
    };
    throw unknownError;
  }
}

/**
 * 코스 재생성 API
 * POST /v1/courses/{courseId}/regenerate
 *
 * @param courseId 기존 코스 ID
 * @returns 재생성된 코스 정보
 * @throws CourseCreateError API 호출 실패 또는 타임아웃 시 에러
 *
 * 의존성: [BE] 코스 재생성 API (LAD-22) 완료 필요
 */
export async function regenerateCourse(
  courseId: string
): Promise<CourseCreateResponse> {
  const controller = new AbortController();
  const timeoutId = setTimeout(() => controller.abort(), API_TIMEOUT);

  try {
    const response = await fetch(
      `${API_BASE_URL}/v1/courses/${courseId}/regenerate`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        signal: controller.signal,
      }
    );

    clearTimeout(timeoutId);

    if (!response.ok) {
      const error: CourseCreateError = {
        message: `코스 재생성에 실패했습니다: ${response.statusText}`,
        code: response.status.toString(),
      };
      throw error;
    }

    return response.json();
  } catch (error) {
    clearTimeout(timeoutId);

    if (error instanceof Error && error.name === 'AbortError') {
      const timeoutError: CourseCreateError = {
        message: '요청 시간이 초과되었습니다. 다시 시도해주세요.',
        isTimeout: true,
      };
      throw timeoutError;
    }

    if ((error as CourseCreateError).message) {
      throw error;
    }

    const unknownError: CourseCreateError = {
      message: '알 수 없는 오류가 발생했습니다.',
    };
    throw unknownError;
  }
}
