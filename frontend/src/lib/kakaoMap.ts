import { TransportType } from '@/types/course';

/**
 * 카카오맵 길찾기 관련 유틸리티 함수
 */

interface Location {
  name: string;
  lat: number;
  lng: number;
}

/**
 * TransportType을 카카오맵 API 파라미터로 변환
 */
function getKakaoTransportType(type: TransportType): string {
  switch (type) {
    case 'WALK':
      return 'FOOT';
    case 'CAR':
      return 'CAR';
    case 'PUBLIC_TRANSIT':
      return 'PUBLICTRANSIT';
    default:
      return 'FOOT';
  }
}

/**
 * 카카오맵 앱 길찾기 URL 생성
 * - 모바일 앱이 설치된 경우 앱으로 열림
 * @see https://apis.map.kakao.com/web/guide/#urlscheme
 */
export function getKakaoMapAppUrl(
  from: Location,
  to: Location,
  transportType: TransportType = 'WALK'
): string {
  const transport = getKakaoTransportType(transportType);
  return `kakaomap://route?sp=${from.lat},${from.lng}&ep=${to.lat},${to.lng}&by=${transport}`;
}

/**
 * 카카오맵 웹 길찾기 URL 생성
 * - 모바일/PC 웹 브라우저에서 열림
 * @see https://apis.map.kakao.com/web/guide/#urlscheme
 */
export function getKakaoMapWebUrl(from: Location, to: Location): string {
  // 카카오맵 웹 길찾기 URL 형식
  // https://map.kakao.com/link/from/{출발지명},{lat},{lng}/to/{도착지명},{lat},{lng}
  const fromEncoded = encodeURIComponent(from.name);
  const toEncoded = encodeURIComponent(to.name);
  return `https://map.kakao.com/link/from/${fromEncoded},${from.lat},${from.lng}/to/${toEncoded},${to.lat},${to.lng}`;
}

/**
 * 카카오맵 길찾기 열기
 * - 모바일: 앱이 설치되어 있으면 앱으로, 없으면 웹으로 이동
 * - PC: 웹으로 이동
 */
export function openKakaoMapRoute(
  from: Location,
  to: Location,
  transportType: TransportType = 'WALK'
): void {
  const isMobile = /Android|iPhone|iPad|iPod/i.test(navigator.userAgent);

  if (isMobile) {
    // 모바일: 앱 스킴으로 시도, 실패시 웹으로 fallback
    const appUrl = getKakaoMapAppUrl(from, to, transportType);
    const webUrl = getKakaoMapWebUrl(from, to);

    // 앱 실행 시도
    const startTime = Date.now();
    window.location.href = appUrl;

    // 앱이 열리지 않으면 (2초 후에도 페이지가 그대로면) 웹으로 이동
    setTimeout(() => {
      // 앱이 열렸으면 이 코드는 실행되지 않음 (페이지 이탈)
      // 앱이 안 열렸으면 웹으로 이동
      if (Date.now() - startTime < 2500) {
        window.open(webUrl, '_blank');
      }
    }, 2000);
  } else {
    // PC: 웹으로 새 탭에서 열기
    const webUrl = getKakaoMapWebUrl(from, to);
    window.open(webUrl, '_blank');
  }
}

/**
 * 이동 시간 포맷 (분 → "N분" 또는 "N시간 M분")
 */
export function formatDuration(minutes: number): string {
  if (minutes < 60) {
    return `${minutes}분`;
  }
  const hours = Math.floor(minutes / 60);
  const mins = minutes % 60;
  return mins > 0 ? `${hours}시간 ${mins}분` : `${hours}시간`;
}

/**
 * 거리 포맷 (미터 → "Nm" 또는 "N.Nkm")
 */
export function formatDistance(meters: number): string {
  if (meters >= 1000) {
    return `${(meters / 1000).toFixed(1)}km`;
  }
  return `${meters}m`;
}

/**
 * 이동 수단 아이콘 반환
 */
export function getTransportIcon(type: TransportType): string {
  switch (type) {
    case 'WALK':
      return '🚶';
    case 'CAR':
      return '🚗';
    case 'PUBLIC_TRANSIT':
      return '🚇';
    default:
      return '🚶';
  }
}

/**
 * 이동 수단 라벨 반환
 */
export function getTransportLabel(type: TransportType): string {
  switch (type) {
    case 'WALK':
      return '도보';
    case 'CAR':
      return '차량';
    case 'PUBLIC_TRANSIT':
      return '대중교통';
    default:
      return '이동';
  }
}
