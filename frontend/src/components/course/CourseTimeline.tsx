'use client';

import { Place, Route } from '@/types/course';
import { formatDuration } from '@/lib/kakaoMap';
import RouteButton from './RouteButton';

interface CourseTimelineProps {
  places: Place[];
  routes?: Route[];
}

/**
 * 코스 타임라인 컴포넌트
 * 시간대별 코스 흐름을 수직 타임라인으로 표시
 */
export default function CourseTimeline({
  places,
  routes = [],
}: CourseTimelineProps) {
  return (
    <div className="w-full max-w-2xl">
      {/* 타임라인 */}
      <div className="relative space-y-0">
        {places.map((place, index) => (
          <div key={place.placeId} className="relative">
            {/* 장소 아이템 */}
            <div className="flex gap-4">
              {/* 타임라인 왼쪽: 순서 인디케이터 */}
              <div className="flex flex-col items-center">
                {/* 순서 번호 */}
                <div className="z-10 flex h-10 w-10 flex-shrink-0 items-center justify-center rounded-full bg-blue-600 font-bold text-white shadow-md dark:bg-blue-500">
                  {index + 1}
                </div>

                {/* 연결선 (마지막 장소 제외) */}
                {index < places.length - 1 && (
                  <div className="w-0.5 flex-1 bg-gray-300 dark:bg-gray-700" />
                )}
              </div>

              {/* 타임라인 오른쪽: 장소 정보 */}
              <div className="flex-1 pb-8">
                <div className="rounded-lg border border-gray-200 bg-white p-4 shadow-sm dark:border-gray-700 dark:bg-gray-800">
                  {/* 장소명 & 카테고리 */}
                  <div className="mb-3">
                    <h3 className="text-lg font-bold text-gray-900 dark:text-gray-100">
                      {place.name}
                    </h3>
                    <p className="text-sm text-gray-500 dark:text-gray-400">
                      {place.category}
                    </p>
                  </div>

                  {/* 주소 */}
                  <div className="mb-3 flex items-start gap-2">
                    <svg
                      className="mt-0.5 h-4 w-4 flex-shrink-0 text-gray-400"
                      fill="none"
                      viewBox="0 0 24 24"
                      stroke="currentColor"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"
                      />
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"
                      />
                    </svg>
                    <p className="text-sm text-gray-600 dark:text-gray-400">
                      {place.address}
                    </p>
                  </div>

                  {/* 비용 & 체류 시간 */}
                  <div className="flex flex-wrap gap-3">
                    <div className="flex items-center gap-1.5">
                      <svg
                        className="h-4 w-4 text-gray-400"
                        fill="none"
                        viewBox="0 0 24 24"
                        stroke="currentColor"
                      >
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth={2}
                          d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
                        />
                      </svg>
                      <span className="text-sm font-semibold text-gray-900 dark:text-gray-100">
                        {place.estimatedCost.toLocaleString()}원
                      </span>
                    </div>

                    {place.estimatedDuration && (
                      <div className="flex items-center gap-1.5">
                        <svg
                          className="h-4 w-4 text-gray-400"
                          fill="none"
                          viewBox="0 0 24 24"
                          stroke="currentColor"
                        >
                          <path
                            strokeLinecap="round"
                            strokeLinejoin="round"
                            strokeWidth={2}
                            d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"
                          />
                        </svg>
                        <span className="text-sm text-gray-600 dark:text-gray-400">
                          체류 {formatDuration(place.estimatedDuration)}
                        </span>
                      </div>
                    )}
                  </div>
                </div>
              </div>
            </div>

            {/* 이동 경로 버튼 (마지막 장소 제외) */}
            {index < places.length - 1 && routes[index] && (
              <div className="flex gap-4 pb-4">
                {/* 왼쪽 여백 (인디케이터 너비만큼) */}
                <div className="w-10 flex-shrink-0" />

                {/* 길찾기 버튼 */}
                <div className="flex-1">
                  <RouteButton
                    from={{
                      name: place.name,
                      lat: place.lat,
                      lng: place.lng,
                    }}
                    to={{
                      name: places[index + 1].name,
                      lat: places[index + 1].lat,
                      lng: places[index + 1].lng,
                    }}
                    transportType={routes[index].transportType}
                    duration={routes[index].duration}
                    distance={routes[index].distance}
                  />
                </div>
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}
