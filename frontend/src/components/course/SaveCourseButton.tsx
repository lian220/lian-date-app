'use client';

import React, { useState, useCallback, useMemo, useSyncExternalStore } from 'react';
import { useRouter } from 'next/navigation';
import { CourseCreateResponse } from '@/types/course';
import {
  saveCourse,
  isSaved,
  canSave,
  getSavedCount,
} from '@/lib/courseStorage';
import StorageLimitAlert from '@/components/common/StorageLimitAlert';
import Toast from '@/components/common/Toast';

/**
 * SaveCourseButton 컴포넌트 Props
 */
interface SaveCourseButtonProps {
  /** 저장할 코스 정보 */
  course: CourseCreateResponse;
  /** 버튼 크기 */
  size?: 'sm' | 'md' | 'lg';
  /** 전체 너비 사용 여부 */
  fullWidth?: boolean;
  /** 추가 CSS 클래스 */
  className?: string;
}

/**
 * localStorage 저장 상태 변경을 위한 이벤트
 */
const STORAGE_CHANGE_EVENT = 'courseStorageChange';

/**
 * localStorage 저장 상태 구독을 위한 store 생성
 */
function createSavedStore(courseId: string) {
  return {
    getSnapshot(): boolean {
      if (typeof window === 'undefined') return false;
      return isSaved(courseId);
    },
    getServerSnapshot(): boolean {
      return false;
    },
    subscribe(callback: () => void): () => void {
      const handleStorageChange = () => callback();
      window.addEventListener('storage', handleStorageChange);
      window.addEventListener(STORAGE_CHANGE_EVENT, handleStorageChange);
      return () => {
        window.removeEventListener('storage', handleStorageChange);
        window.removeEventListener(STORAGE_CHANGE_EVENT, handleStorageChange);
      };
    },
  };
}

/**
 * 저장 상태 변경 알림
 */
function notifyStorageChange(): void {
  if (typeof window !== 'undefined') {
    window.dispatchEvent(new Event(STORAGE_CHANGE_EVENT));
  }
}

/**
 * 코스 저장 버튼 컴포넌트
 * 코스를 로컬 스토리지에 저장하는 기능을 제공
 *
 * @example
 * <SaveCourseButton course={courseData} />
 */
export default function SaveCourseButton({
  course,
  size = 'md',
  fullWidth = false,
  className = '',
}: SaveCourseButtonProps) {
  const router = useRouter();
  const [showLimitAlert, setShowLimitAlert] = useState(false);
  const [toast, setToast] = useState<{
    message: string;
    type: 'success' | 'error' | 'info';
  } | null>(null);

  // useSyncExternalStore를 사용하여 저장 상태 구독
  const savedStore = useMemo(
    () => createSavedStore(course.courseId),
    [course.courseId]
  );
  const saved = useSyncExternalStore(
    savedStore.subscribe,
    savedStore.getSnapshot,
    savedStore.getServerSnapshot
  );

  // 저장 핸들러
  const handleSave = useCallback(() => {
    // 이미 저장된 경우
    if (isSaved(course.courseId)) {
      setToast({
        message: '이미 저장된 코스예요',
        type: 'info',
      });
      return;
    }

    // 저장 공간 확인
    if (!canSave()) {
      setShowLimitAlert(true);
      return;
    }

    // 저장 실행
    const result = saveCourse(course);

    if (result.success) {
      notifyStorageChange();
      setToast({
        message: '코스가 저장되었어요!',
        type: 'success',
      });
    } else {
      setToast({
        message: result.message || '저장에 실패했어요',
        type: 'error',
      });
    }
  }, [course]);

  // 내 코스 보기로 이동
  const handleViewMyCourses = useCallback(() => {
    setShowLimitAlert(false);
    router.push('/my-courses');
  }, [router]);

  // 사이즈별 스타일
  const sizeStyles = {
    sm: 'px-4 py-2 text-sm',
    md: 'px-6 py-3 text-base',
    lg: 'px-8 py-4 text-lg',
  };

  return (
    <>
      <button
        onClick={handleSave}
        className={`
          ${sizeStyles[size]}
          ${fullWidth ? 'w-full' : ''}
          font-semibold rounded-lg transition-all duration-200
          flex items-center justify-center gap-2
          ${
            saved
              ? 'bg-gray-100 text-gray-500 cursor-default dark:bg-gray-800 dark:text-gray-400'
              : 'bg-pink-500 text-white hover:bg-pink-600 active:bg-pink-700 dark:bg-pink-600 dark:hover:bg-pink-700'
          }
          ${className}
        `}
        disabled={saved}
      >
        {saved ? (
          <>
            <svg
              className="w-5 h-5"
              fill="currentColor"
              viewBox="0 0 24 24"
            >
              <path d="M5 5a2 2 0 012-2h10a2 2 0 012 2v16l-7-3.5L5 21V5z" />
            </svg>
            저장됨
          </>
        ) : (
          <>
            <svg
              className="w-5 h-5"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M5 5a2 2 0 012-2h10a2 2 0 012 2v16l-7-3.5L5 21V5z"
              />
            </svg>
            이 코스 저장하기
          </>
        )}
      </button>

      {/* 용량 초과 알림 */}
      <StorageLimitAlert
        isOpen={showLimitAlert}
        onClose={() => setShowLimitAlert(false)}
        onViewMyCourses={handleViewMyCourses}
        currentCount={getSavedCount()}
      />

      {/* 토스트 메시지 */}
      {toast && (
        <Toast
          message={toast.message}
          type={toast.type}
          onClose={() => setToast(null)}
        />
      )}
    </>
  );
}
