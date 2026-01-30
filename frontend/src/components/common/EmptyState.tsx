'use client';

import React from 'react';

/**
 * 빈 상태 아이콘 타입
 */
type EmptyStateIcon = 'heart' | 'bookmark' | 'search' | 'folder';

/**
 * EmptyState 컴포넌트 Props
 */
interface EmptyStateProps {
  /** 아이콘 타입 */
  icon?: EmptyStateIcon;
  /** 제목 */
  title: string;
  /** 설명 문구 */
  description?: string;
  /** 액션 버튼 텍스트 */
  actionText?: string;
  /** 액션 버튼 클릭 핸들러 */
  onAction?: () => void;
  /** 추가 CSS 클래스 */
  className?: string;
}

/**
 * 아이콘 렌더링 컴포넌트
 */
function EmptyIcon({ type }: { type: EmptyStateIcon }) {
  const iconClass = 'w-16 h-16 text-gray-300';

  switch (type) {
    case 'heart':
      return (
        <svg
          className={iconClass}
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth={1.5}
            d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"
          />
        </svg>
      );
    case 'bookmark':
      return (
        <svg
          className={iconClass}
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth={1.5}
            d="M5 5a2 2 0 012-2h10a2 2 0 012 2v16l-7-3.5L5 21V5z"
          />
        </svg>
      );
    case 'search':
      return (
        <svg
          className={iconClass}
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth={1.5}
            d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
          />
        </svg>
      );
    case 'folder':
    default:
      return (
        <svg
          className={iconClass}
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth={1.5}
            d="M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-6l-2-2H5a2 2 0 00-2 2z"
          />
        </svg>
      );
  }
}

/**
 * 빈 상태 UI 컴포넌트
 * 저장된 코스가 없을 때 표시되는 안내 화면
 *
 * @example
 * <EmptyState
 *   icon="bookmark"
 *   title="저장된 코스가 없어요"
 *   description="마음에 드는 데이트 코스를 저장해보세요"
 *   actionText="코스 만들기"
 *   onAction={() => router.push('/')}
 * />
 */
export default function EmptyState({
  icon = 'bookmark',
  title,
  description,
  actionText,
  onAction,
  className = '',
}: EmptyStateProps) {
  return (
    <div
      className={`flex flex-col items-center justify-center py-16 px-6 ${className}`}
    >
      {/* 아이콘 */}
      <div className="mb-4">
        <EmptyIcon type={icon} />
      </div>

      {/* 제목 */}
      <h3 className="text-lg font-medium text-gray-700 mb-2 text-center">
        {title}
      </h3>

      {/* 설명 */}
      {description && (
        <p className="text-sm text-gray-500 text-center max-w-xs mb-6">
          {description}
        </p>
      )}

      {/* 액션 버튼 */}
      {actionText && onAction && (
        <button
          onClick={onAction}
          className="px-6 py-2.5 bg-pink-500 text-white text-sm font-medium rounded-full hover:bg-pink-600 active:bg-pink-700 transition-colors"
        >
          {actionText}
        </button>
      )}
    </div>
  );
}
