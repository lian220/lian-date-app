/**
 * 카카오맵 딥링크 유틸리티
 * - 모바일: 카카오맵 앱으로 열기 시도, 실패 시 웹으로 폴백
 * - 데스크톱: 카카오맵 웹 페이지로 열기
 */

/** 카카오맵 웹 URL 베이스 */
const KAKAO_MAP_WEB_BASE = 'https://place.map.kakao.com';

/** 카카오맵 앱 스키마 */
const KAKAO_MAP_APP_SCHEME = 'kakaomap://place';

/** 앱 열기 타임아웃 (ms) - 앱이 없으면 이 시간 후 웹으로 폴백 */
const APP_OPEN_TIMEOUT = 1500;

/**
 * 모바일 환경인지 확인
 */
function isMobile(): boolean {
  if (typeof window === 'undefined') return false;
  return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(
    navigator.userAgent
  );
}

/**
 * 카카오맵 웹 URL 생성
 * @param placeId 카카오 장소 ID
 */
export function getKakaoMapWebUrl(placeId: string): string {
  return `${KAKAO_MAP_WEB_BASE}/${placeId}`;
}

/**
 * 카카오맵 앱 딥링크 URL 생성
 * @param placeId 카카오 장소 ID
 */
export function getKakaoMapAppUrl(placeId: string): string {
  return `${KAKAO_MAP_APP_SCHEME}?id=${placeId}`;
}

/**
 * 카카오맵에서 장소 열기
 * - 모바일: 앱으로 열기 시도, 실패 시 웹으로 폴백
 * - 데스크톱: 웹 페이지로 열기
 *
 * @param placeId 카카오 장소 ID
 * @param kakaoPlaceUrl 카카오 장소 URL (있으면 이 URL 사용)
 */
export function openKakaoMap(
  placeId: string,
  kakaoPlaceUrl?: string | null
): void {
  // 웹 URL 결정 (kakaoPlaceUrl이 유효하면 사용, 없으면 placeId로 생성)
  const webUrl =
    kakaoPlaceUrl && kakaoPlaceUrl.startsWith(KAKAO_MAP_WEB_BASE)
      ? kakaoPlaceUrl
      : getKakaoMapWebUrl(placeId);

  // 데스크톱: 바로 웹으로 열기
  if (!isMobile()) {
    window.open(webUrl, '_blank', 'noopener,noreferrer');
    return;
  }

  // 모바일: 앱으로 열기 시도
  const appUrl = getKakaoMapAppUrl(placeId);
  const startTime = Date.now();

  // 앱 열기 시도
  window.location.href = appUrl;

  // 앱이 열리지 않으면 웹으로 폴백
  setTimeout(() => {
    // 페이지가 아직 보이면 (앱이 열리지 않았으면) 웹으로 이동
    if (document.hidden) return;

    // 앱 열기 시도 후 너무 빨리 돌아왔으면 앱이 없는 것
    const elapsed = Date.now() - startTime;
    if (elapsed < APP_OPEN_TIMEOUT + 500) {
      // 모바일에서 window.open은 팝업 차단될 수 있으므로 location.href 사용
      window.location.href = webUrl;
    }
  }, APP_OPEN_TIMEOUT);
}
