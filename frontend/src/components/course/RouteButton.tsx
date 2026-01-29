'use client';

import { TransportType } from '@/types/course';
import {
  openKakaoMapRoute,
  formatDuration,
  formatDistance,
  getTransportIcon,
  getTransportLabel,
} from '@/lib/kakaoMap';

interface Location {
  name: string;
  lat: number;
  lng: number;
}

interface RouteButtonProps {
  from: Location;
  to: Location;
  transportType: TransportType;
  duration: number; // 분 단위
  distance: number; // 미터 단위
}

/**
 * 이동 경로 버튼 컴포넌트
 * - 예상 이동 시간 표시
 * - 클릭 시 카카오맵 길찾기 실행
 */
export default function RouteButton({
  from,
  to,
  transportType,
  duration,
  distance,
}: RouteButtonProps) {
  const handleClick = () => {
    openKakaoMapRoute(from, to, transportType);
  };

  return (
    <button
      onClick={handleClick}
      className="group flex w-full items-center justify-between rounded-lg bg-blue-50 px-4 py-3 transition-all hover:bg-blue-100 active:bg-blue-200 dark:bg-blue-950 dark:hover:bg-blue-900 dark:active:bg-blue-800"
      aria-label={`${from.name}에서 ${to.name}까지 ${getTransportLabel(transportType)} ${formatDuration(duration)} 길찾기`}
    >
      {/* 왼쪽: 이동 정보 */}
      <div className="flex items-center gap-3">
        <span className="text-2xl" role="img" aria-hidden="true">
          {getTransportIcon(transportType)}
        </span>
        <div className="text-left">
          <p className="text-sm font-medium text-blue-700 dark:text-blue-300">
            {getTransportLabel(transportType)} {formatDuration(duration)}
          </p>
          <p className="text-xs text-blue-600 dark:text-blue-400">
            약 {formatDistance(distance)}
          </p>
        </div>
      </div>

      {/* 오른쪽: 길찾기 버튼 */}
      <div className="flex items-center gap-1.5 rounded-full bg-blue-600 px-3 py-1.5 text-white transition-colors group-hover:bg-blue-700 dark:bg-blue-500 dark:group-hover:bg-blue-600">
        <svg
          className="h-4 w-4"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
          aria-hidden="true"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth={2}
            d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7"
          />
        </svg>
        <span className="text-xs font-semibold">길찾기</span>
      </div>
    </button>
  );
}
