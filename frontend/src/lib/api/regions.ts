import { Region } from '@/types/region';

/**
 * 모든 지역 조회 (백엔드에서 5분 캐싱)
 * @param city 선택적 도시 필터 ('seoul' 또는 'gyeonggi')
 * @returns 지역 목록
 */
export async function getRegions(city?: string): Promise<Region[]> {
  try {
    console.log(`[RegionAPI] Fetching regions${city ? ` for city: ${city}` : ''}...`);

    const baseUrl = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080/v1';
    const url = new URL(`${baseUrl}/regions`);
    if (city) {
      url.searchParams.append('city', city);
    }

    const response = await fetch(url.toString(), {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    });

    if (!response.ok) {
      throw new Error(`API Error: ${response.status} ${response.statusText}`);
    }

    const apiResponse = await response.json();
    const data: Region[] = apiResponse.data?.regions || [];

    console.log(`[RegionAPI] Retrieved ${data.length} regions`);
    return data;
  } catch (error) {
    console.error('[RegionAPI] Error fetching regions:', error);
    throw error;
  }
}

/**
 * 단일 지역 조회
 * @param regionId 지역 ID
 * @returns 지역 정보 또는 null
 */
export async function getRegionById(regionId: string): Promise<Region | null> {
  const regions = await getRegions();
  return regions.find((r) => r.id === regionId) || null;
}
