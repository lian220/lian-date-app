import { RegionsResponse } from '@/types/region';
import {
  CourseCreateRequest,
  CourseCreateResponse,
  CourseCreateError,
} from '@/types/course';
import {
  PlaceCurationResponse,
  PlaceCurationInfo,
  PlaceCurationError,
} from '@/types/place';

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
 * 세션 ID 생성 (UUID v4)
 */
function generateSessionId(): string {
  if (typeof window !== 'undefined') {
    // 브라우저 환경에서는 sessionStorage 사용
    let sessionId: string | null = sessionStorage.getItem('session-id');
    if (!sessionId) {
      sessionId = crypto.randomUUID();
      sessionStorage.setItem('session-id', sessionId);
    }
    return sessionId;
  }
  // 서버 사이드에서는 매번 새로운 ID 생성
  return crypto.randomUUID();
}

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
        'X-Session-Id': generateSessionId(),
      },
      body: JSON.stringify(request),
      signal: controller.signal,
    });

    clearTimeout(timeoutId);

    if (!response.ok) {
      try {
        const body = await response.json();
        const error: CourseCreateError = {
          message: body.error?.message || body.message || `코스 생성에 실패했습니다: ${response.statusText}`,
          code: body.error?.code || body.code || response.status.toString(),
        };
        throw error;
      } catch {
        // JSON 파싱 실패 시 기본 에러
        const error: CourseCreateError = {
          message: `코스 생성에 실패했습니다: ${response.statusText}`,
          code: response.status.toString(),
        };
        throw error;
      }
    }

    const body = await response.json();
    return body.data;
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
          'X-Session-Id': generateSessionId(),
        },
        signal: controller.signal,
      }
    );

    clearTimeout(timeoutId);

    if (!response.ok) {
      try {
        const body = await response.json();
        const error: CourseCreateError = {
          message: body.error?.message || body.message || `코스 재생성에 실패했습니다: ${response.statusText}`,
          code: body.error?.code || body.code || response.status.toString(),
        };
        throw error;
      } catch {
        // JSON 파싱 실패 시 기본 에러
        const error: CourseCreateError = {
          message: `코스 재생성에 실패했습니다: ${response.statusText}`,
          code: response.status.toString(),
        };
        throw error;
      }
    }

    const body = await response.json();
    return body.data;
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
 * 장소 큐레이션 조회 API
 * GET /api/v1/places/{placeId}/curation
 *
 * @param placeId 장소 ID (카카오 장소 ID)
 * @returns 장소 큐레이션 정보
 * @throws PlaceCurationError API 호출 실패 시 에러
 *
 * 의존성: [BE] 장소 큐레이션 API (LAD-26) 완료
 */
export async function fetchPlaceCuration(
  placeId: string
): Promise<PlaceCurationInfo> {
  try {
    const response = await fetch(
      `${API_BASE_URL}/api/v1/places/${placeId}/curation`,
      {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      }
    );

    if (!response.ok) {
      try {
        const body: PlaceCurationResponse = await response.json();
        const error: PlaceCurationError = {
          message:
            body.error?.message ||
            `장소 큐레이션 조회에 실패했습니다: ${response.statusText}`,
          code: body.error?.code || response.status.toString(),
        };
        throw error;
      } catch {
        // JSON 파싱 실패 시 기본 에러
        const error: PlaceCurationError = {
          message: `장소 큐레이션 조회에 실패했습니다: ${response.statusText}`,
          code: response.status.toString(),
        };
        throw error;
      }
    }

    const body: PlaceCurationResponse = await response.json();
    if (!body.data) {
      throw new Error('큐레이션 데이터가 없습니다');
    }
    return body.data;
  } catch (error) {
    if ((error as PlaceCurationError).message) {
      throw error;
    }

    const unknownError: PlaceCurationError = {
      message: '알 수 없는 오류가 발생했습니다.',
    };
    throw unknownError;
  }
}
