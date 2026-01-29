'use client';

import { useState } from 'react';
import { CourseCreateResponse } from '@/types/course';
import PlaceCard from './PlaceCard';
import CourseTimeline from './CourseTimeline';
import CourseMapView from './CourseMapView';

interface CourseResultProps {
  course: CourseCreateResponse;
  onNewCourse?: () => void;
  onRegenerateCourse?: () => void;
  isRegenerating?: boolean;
}

type ViewMode = 'card' | 'timeline' | 'map';

/**
 * 코스 생성 결과 표시 컴포넌트
 * 카드 뷰와 타임라인 뷰 전환 가능
 */
export default function CourseResult({
  course,
  onNewCourse,
  onRegenerateCourse,
  isRegenerating = false,
}: CourseResultProps) {
  const [viewMode, setViewMode] = useState<ViewMode>('card');

  return (
    <div className="flex w-full max-w-2xl flex-col gap-6">
      {/* 헤더 */}
      <div className="text-center">
        <div className="mb-2 inline-flex items-center gap-2 rounded-full bg-green-100 px-4 py-2 dark:bg-green-900">
          <svg
            className="h-5 w-5 text-green-600 dark:text-green-400"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M5 13l4 4L19 7"
            />
          </svg>
          <span className="text-sm font-semibold text-green-700 dark:text-green-300">
            코스 생성 완료
          </span>
        </div>
        <h2 className="mt-3 text-2xl font-bold text-gray-900 dark:text-gray-100">
          AI가 추천하는 데이트 코스
          {course.regenerationCount && course.regenerationCount > 1 && (
            <span className="ml-2 text-lg text-blue-600 dark:text-blue-400">
              ({course.regenerationCount}번째 추천)
            </span>
          )}
        </h2>
        <p className="mt-2 text-sm text-gray-600 dark:text-gray-400">
          각 장소는 선택하신 조건에 맞춰 최적화되었습니다
        </p>
      </div>

      {/* 뷰 모드 전환 탭 */}
      <div className="flex gap-1 rounded-lg border border-gray-200 bg-white p-1 dark:border-gray-700 dark:bg-gray-800">
        <button
          onClick={() => setViewMode('card')}
          className={`flex flex-1 items-center justify-center gap-1.5 rounded-md px-3 py-2 text-sm font-semibold transition-colors ${
            viewMode === 'card'
              ? 'bg-blue-600 text-white dark:bg-blue-500'
              : 'text-gray-600 hover:bg-gray-100 dark:text-gray-400 dark:hover:bg-gray-700'
          }`}
        >
          <svg
            className="h-4 w-4"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"
            />
          </svg>
          카드
        </button>
        <button
          onClick={() => setViewMode('timeline')}
          className={`flex flex-1 items-center justify-center gap-1.5 rounded-md px-3 py-2 text-sm font-semibold transition-colors ${
            viewMode === 'timeline'
              ? 'bg-blue-600 text-white dark:bg-blue-500'
              : 'text-gray-600 hover:bg-gray-100 dark:text-gray-400 dark:hover:bg-gray-700'
          }`}
        >
          <svg
            className="h-4 w-4"
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
          타임라인
        </button>
        <button
          onClick={() => setViewMode('map')}
          className={`flex flex-1 items-center justify-center gap-1.5 rounded-md px-3 py-2 text-sm font-semibold transition-colors ${
            viewMode === 'map'
              ? 'bg-blue-600 text-white dark:bg-blue-500'
              : 'text-gray-600 hover:bg-gray-100 dark:text-gray-400 dark:hover:bg-gray-700'
          }`}
        >
          <svg
            className="h-4 w-4"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7"
            />
          </svg>
          지도
        </button>
      </div>

      {/* 컨텐츠: 카드 뷰, 타임라인 뷰, 지도 뷰 */}
      {viewMode === 'card' && (
        <div className="space-y-4">
          {course.places.map((place, index) => (
            <div key={place.placeId} className="relative">
              <PlaceCard place={place} order={index + 1} />

              {/* 순서 화살표 (마지막 카드 제외) */}
              {index < course.places.length - 1 && (
                <div className="flex justify-center py-2">
                  <svg
                    className="h-6 w-6 text-blue-400 dark:text-blue-500"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M19 14l-7 7m0 0l-7-7m7 7V3"
                    />
                  </svg>
                </div>
              )}
            </div>
          ))}
        </div>
      )}
      {viewMode === 'timeline' && (
        <CourseTimeline places={course.places} routes={course.routes} />
      )}
      {viewMode === 'map' && <CourseMapView course={course} />}

      {/* 총 예상 비용 */}
      <div className="rounded-xl border-2 border-blue-200 bg-blue-50 p-6 dark:border-blue-800 dark:bg-blue-950">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="flex h-12 w-12 items-center justify-center rounded-full bg-blue-600 dark:bg-blue-500">
              <svg
                className="h-6 w-6 text-white"
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
            </div>
            <div>
              <p className="text-sm text-gray-600 dark:text-gray-400">
                총 예상 비용 (1인 기준)
              </p>
              <p className="text-2xl font-bold text-gray-900 dark:text-gray-100">
                {course.totalEstimatedCost.toLocaleString()}원
              </p>
            </div>
          </div>
        </div>
      </div>

      {/* 액션 버튼 */}
      <div className="flex gap-3">
        {onNewCourse && (
          <button
            onClick={onNewCourse}
            className="flex-1 rounded-lg border border-gray-300 px-6 py-3 font-semibold text-gray-700 transition-colors hover:bg-gray-50 dark:border-gray-600 dark:text-gray-300 dark:hover:bg-gray-800"
          >
            다른 코스 추천받기
          </button>
        )}
        {onRegenerateCourse && (
          <button
            onClick={onRegenerateCourse}
            disabled={isRegenerating}
            className="flex-1 rounded-lg border border-blue-600 px-6 py-3 font-semibold text-blue-600 transition-colors hover:bg-blue-50 disabled:cursor-not-allowed disabled:opacity-50 dark:border-blue-500 dark:text-blue-400 dark:hover:bg-blue-950"
          >
            {isRegenerating ? (
              <span className="flex items-center justify-center gap-2">
                <svg
                  className="h-5 w-5 animate-spin"
                  fill="none"
                  viewBox="0 0 24 24"
                >
                  <circle
                    className="opacity-25"
                    cx="12"
                    cy="12"
                    r="10"
                    stroke="currentColor"
                    strokeWidth="4"
                  />
                  <path
                    className="opacity-75"
                    fill="currentColor"
                    d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                  />
                </svg>
                재생성 중...
              </span>
            ) : (
              '다른 코스 보기'
            )}
          </button>
        )}
        <button
          className="flex-1 rounded-lg bg-blue-600 px-6 py-3 font-semibold text-white transition-colors hover:bg-blue-700 dark:bg-blue-500 dark:hover:bg-blue-600"
          onClick={() =>
            alert('코스 저장 기능은 추후 구현 예정입니다 (LAD-5)')
          }
        >
          이 코스 저장하기
        </button>
      </div>
    </div>
  );
}
