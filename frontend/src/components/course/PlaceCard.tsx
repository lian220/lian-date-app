import { Place } from '@/types/course';

interface PlaceCardProps {
  place: Place;
  order: number; // 1, 2, 3
}

/**
 * 코스 내 개별 장소 카드 컴포넌트
 * 장소명, 카테고리, 주소, 예상 비용, AI 추천 이유 표시
 */
export default function PlaceCard({ place, order }: PlaceCardProps) {
  return (
    <div className="relative rounded-xl border border-gray-200 bg-white p-6 shadow-sm transition-shadow hover:shadow-md dark:border-gray-700 dark:bg-gray-800">
      {/* 순서 번호 뱃지 */}
      <div className="absolute -left-3 -top-3 flex h-8 w-8 items-center justify-center rounded-full bg-blue-600 font-bold text-white shadow-md dark:bg-blue-500">
        {order}
      </div>

      {/* 장소 정보 */}
      <div className="space-y-3">
        {/* 장소명 & 카테고리 */}
        <div>
          <h3 className="text-lg font-bold text-gray-900 dark:text-gray-100">
            {place.name}
          </h3>
          <p className="mt-1 text-sm text-gray-500 dark:text-gray-400">
            {place.category}
          </p>
        </div>

        {/* 주소 */}
        <div className="flex items-start gap-2">
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

        {/* 전화번호 */}
        <div className="flex items-center gap-2">
          <svg
            className={`h-4 w-4 flex-shrink-0 ${place.phone ? 'text-gray-400' : 'text-gray-300 dark:text-gray-600'}`}
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z"
            />
          </svg>
          {place.phone ? (
            <a
              href={`tel:${place.phone}`}
              className="text-sm text-blue-600 hover:text-blue-800 hover:underline dark:text-blue-400 dark:hover:text-blue-300"
              onClick={(e) => e.stopPropagation()}
            >
              {place.phone}
            </a>
          ) : (
            <span className="text-sm text-gray-400 dark:text-gray-500">
              전화번호 정보 없음
            </span>
          )}
        </div>

        {/* 예상 비용 */}
        <div className="flex items-center gap-2">
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
          <p className="text-sm font-semibold text-gray-900 dark:text-gray-100">
            {place.estimatedCost.toLocaleString()}원
          </p>
          <span className="text-xs text-gray-500 dark:text-gray-500">
            (1인 기준)
          </span>
        </div>

        {/* AI 추천 이유 */}
        <div className="rounded-lg bg-blue-50 p-3 dark:bg-blue-950">
          <div className="flex items-start gap-2">
            <span className="text-lg">💡</span>
            <div className="flex-1">
              <p className="text-xs font-semibold text-blue-600 dark:text-blue-400">
                AI 추천 이유
              </p>
              <p className="mt-1 text-sm text-gray-700 dark:text-gray-300">
                {place.recommendReason}
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
