import { RegionsResponse } from '@/types/region';

/**
 * 백엔드 API 기본 URL
 * TODO: 환경변수로 관리 (NEXT_PUBLIC_API_URL)
 */
const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8000';

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
