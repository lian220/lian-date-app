'use client';

import React, { useCallback, useEffect, useRef } from 'react';
import { MAX_COURSES } from '@/lib/courseStorage';

/**
 * StorageLimitAlert 컴포넌트 Props
 */
interface StorageLimitAlertProps {
  /** 모달 표시 여부 */
  isOpen: boolean;
  /** 닫기 핸들러 */
  onClose: () => void;
  /** 내 코스 보기 클릭 핸들러 */
  onViewMyCourses: () => void;
  /** 현재 저장된 코스 개수 */
  currentCount?: number;
}

/**
 * 저장 용량 초과 안내 모달 컴포넌트
 * 저장 공간이 가득 찼을 때 사용자에게 안내
 *
 * @example
 * <StorageLimitAlert
 *   isOpen={showLimitAlert}
 *   onClose={() => setShowLimitAlert(false)}
 *   onViewMyCourses={() => router.push('/my-courses')}
 *   currentCount={20}
 * />
 */
export default function StorageLimitAlert({
  isOpen,
  onClose,
  onViewMyCourses,
  currentCount = MAX_COURSES,
}: StorageLimitAlertProps) {
  const dialogRef = useRef<HTMLDivElement>(null);
  const previousActiveElement = useRef<HTMLElement | null>(null);

  // 포커스 관리
  useEffect(() => {
    if (isOpen) {
      previousActiveElement.current = document.activeElement as HTMLElement;
      dialogRef.current?.focus();
    } else if (previousActiveElement.current) {
      previousActiveElement.current.focus();
    }
  }, [isOpen]);

  // ESC 키로 닫기
  useEffect(() => {
    function handleKeyDown(e: KeyboardEvent) {
      if (e.key === 'Escape' && isOpen) {
        onClose();
      }
    }

    document.addEventListener('keydown', handleKeyDown);
    return () => document.removeEventListener('keydown', handleKeyDown);
  }, [isOpen, onClose]);

  // 모달 열릴 때 스크롤 방지
  useEffect(() => {
    if (isOpen) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = '';
    }
    return () => {
      document.body.style.overflow = '';
    };
  }, [isOpen]);

  if (!isOpen) return null;

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center p-4"
      role="alertdialog"
      aria-modal="true"
      aria-labelledby="alert-title"
      aria-describedby="alert-description"
      tabIndex={-1}
    >
      {/* 배경 오버레이 */}
      <div
        className="absolute inset-0 bg-black/50 transition-opacity"
        onClick={onClose}
        aria-hidden="true"
      />

      {/* 모달 본문 */}
      <div
        ref={dialogRef}
        className="relative bg-white rounded-2xl shadow-xl w-full max-w-sm mx-auto overflow-hidden animate-in zoom-in-95 duration-200"
      >
        <div className="p-6">
          {/* 아이콘 */}
          <div className="w-14 h-14 bg-orange-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <svg
              className="w-7 h-7 text-orange-500"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"
              />
            </svg>
          </div>

          {/* 제목 */}
          <h3
            id="alert-title"
            className="text-lg font-semibold text-gray-900 text-center mb-2"
          >
            저장 공간이 가득 찼어요
          </h3>

          {/* 설명 */}
          <p
            id="alert-description"
            className="text-sm text-gray-500 text-center mb-4"
          >
            최대 {MAX_COURSES}개까지 저장할 수 있어요.
            <br />
            새로운 코스를 저장하려면 기존 코스를 삭제해주세요.
          </p>

          {/* 저장 현황 표시 */}
          <div className="bg-gray-50 rounded-xl p-4 mb-4">
            <div className="flex items-center justify-between mb-2">
              <span className="text-sm text-gray-600">저장된 코스</span>
              <span className="text-sm font-medium text-gray-900">
                {currentCount} / {MAX_COURSES}
              </span>
            </div>
            <div className="h-2 bg-gray-200 rounded-full overflow-hidden">
              <div
                className="h-full bg-orange-500 rounded-full transition-all duration-300"
                style={{ width: `${MAX_COURSES > 0 ? (currentCount / MAX_COURSES) * 100 : 0}%` }}
              />
            </div>
          </div>
        </div>

        {/* 버튼 영역 */}
        <div className="flex border-t border-gray-100">
          <button
            onClick={onClose}
            className="flex-1 py-3.5 text-sm font-medium text-gray-700 hover:bg-gray-50 active:bg-gray-100 transition-colors"
          >
            닫기
          </button>
          <div className="w-px bg-gray-100" />
          <button
            onClick={onViewMyCourses}
            className="flex-1 py-3.5 text-sm font-medium text-pink-500 hover:bg-pink-50 active:bg-pink-100 transition-colors"
          >
            내 코스 보기
          </button>
        </div>
      </div>
    </div>
  );
}
