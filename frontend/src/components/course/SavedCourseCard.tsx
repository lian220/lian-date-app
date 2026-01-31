'use client';

import React from 'react';
import Image from 'next/image';
import { SavedCourse } from '@/types/course';

/**
 * SavedCourseCard 컴포넌트 Props
 */
interface SavedCourseCardProps {
  /** 저장된 코스 정보 */
  course: SavedCourse;
  /** 카드 클릭 핸들러 */
  onClick?: () => void;
  /** 삭제 버튼 클릭 핸들러 */
  onDelete?: () => void;
}

/**
 * 날짜 포맷 함수
 */
function formatDate(dateString: string): string {
  const date = new Date(dateString);
  const now = new Date();
  const diffMs = now.getTime() - date.getTime();
  const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));

  if (diffDays === 0) {
    return '오늘';
  } else if (diffDays === 1) {
    return '어제';
  } else if (diffDays < 7) {
    return `${diffDays}일 전`;
  } else if (diffDays < 30) {
    const weeks = Math.floor(diffDays / 7);
    return `${weeks}주 전`;
  } else {
    return date.toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  }
}

/**
 * 데이트 타입 한글 변환
 */
function getDateTypeLabel(dateType: string): string {
  const labels: Record<string, string> = {
    romantic: '로맨틱',
    activity: '액티비티',
    food: '맛집 탐방',
    culture: '문화/예술',
    healing: '힐링',
  };
  return labels[dateType] || dateType;
}

/**
 * 저장된 코스 카드 컴포넌트
 * 내 코스 목록에서 각 코스를 표시
 *
 * @example
 * <SavedCourseCard
 *   course={savedCourse}
 *   onClick={() => handleViewCourse(savedCourse.courseId)}
 *   onDelete={() => handleDeleteCourse(savedCourse.courseId)}
 * />
 */
export default function SavedCourseCard({
  course,
  onClick,
  onDelete,
}: SavedCourseCardProps) {
  // 첫 번째 장소 이미지 (있으면)
  const thumbnailPlace = course.places.find((p) => p.imageUrl);
  const thumbnailUrl = thumbnailPlace?.imageUrl;

  // 키보드 활성화 핸들러
  const handleKeyDown = (e: React.KeyboardEvent<HTMLDivElement>) => {
    if (!onClick) return;

    if (e.key === 'Enter' || e.key === ' ') {
      e.preventDefault();
      onClick();
    }
  };

  return (
    <div
      className="bg-white rounded-xl border border-gray-200 overflow-hidden hover:shadow-md transition-shadow dark:bg-gray-800 dark:border-gray-700"
      onClick={onClick}
      onKeyDown={handleKeyDown}
      role={onClick ? 'button' : undefined}
      tabIndex={onClick ? 0 : undefined}
    >
      <div className="flex">
        {/* 썸네일 영역 */}
        <div className="w-24 h-24 flex-shrink-0 bg-gray-100 dark:bg-gray-700 relative">
          {thumbnailUrl ? (
            <Image
              src={thumbnailUrl}
              alt={course.regionName}
              fill
              className="object-cover"
              sizes="96px"
              unoptimized
            />
          ) : (
            <div className="w-full h-full flex items-center justify-center">
              <svg
                className="w-8 h-8 text-gray-300 dark:text-gray-500"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={1.5}
                  d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"
                />
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={1.5}
                  d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"
                />
              </svg>
            </div>
          )}
        </div>

        {/* 정보 영역 */}
        <div className="flex-1 p-3 min-w-0">
          <div className="flex items-start justify-between gap-2">
            <div className="min-w-0">
              {/* 지역명 & 데이트 타입 */}
              <div className="flex items-center gap-2 mb-1">
                <span className="text-sm font-semibold text-gray-900 dark:text-gray-100 truncate">
                  {course.regionName}
                </span>
                <span className="flex-shrink-0 px-2 py-0.5 text-xs font-medium text-pink-600 bg-pink-50 rounded-full dark:text-pink-400 dark:bg-pink-900/30">
                  {getDateTypeLabel(course.dateType)}
                </span>
              </div>

              {/* 장소 목록 */}
              <p className="text-xs text-gray-500 dark:text-gray-400 truncate mb-1">
                {course.places.map((p) => p.name).join(' → ')}
              </p>

              {/* 메타 정보 */}
              <div className="flex items-center gap-3 text-xs text-gray-400 dark:text-gray-500">
                <span>{course.places.length}곳</span>
                <span>·</span>
                <span>{course.totalEstimatedCost.toLocaleString()}원</span>
                <span>·</span>
                <span>{formatDate(course.savedAt)}</span>
              </div>
            </div>

            {/* 삭제 버튼 */}
            {onDelete && (
              <button
                onClick={(e) => {
                  e.stopPropagation();
                  onDelete();
                }}
                className="p-1.5 text-gray-400 hover:text-red-500 hover:bg-red-50 rounded-full transition-colors dark:hover:bg-red-900/30"
                aria-label="코스 삭제"
              >
                <svg
                  className="w-4 h-4"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
                  />
                </svg>
              </button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
