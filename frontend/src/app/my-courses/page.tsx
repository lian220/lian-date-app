'use client';

import React, { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { SavedCourse } from '@/types/course';
import {
  getCourses,
  deleteCourse,
  getSavedCount,
  MAX_COURSES,
} from '@/lib/courseStorage';
import EmptyState from '@/components/common/EmptyState';
import ConfirmDialog from '@/components/common/ConfirmDialog';
import BrowserStorageNotice from '@/components/common/BrowserStorageNotice';
import SavedCourseCard from '@/components/course/SavedCourseCard';
import Toast from '@/components/common/Toast';

/**
 * 내 코스 목록 페이지
 * 로컬 스토리지에 저장된 코스들을 표시
 */
export default function MyCoursesPage() {
  const router = useRouter();
  const [courses, setCourses] = useState<SavedCourse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [deleteTarget, setDeleteTarget] = useState<string | null>(null);
  const [toast, setToast] = useState<{
    message: string;
    type: 'success' | 'error' | 'info';
  } | null>(null);

  // 코스 목록 로드
  useEffect(() => {
    const loadCourses = () => {
      const savedCourses = getCourses();
      setCourses(savedCourses);
      setIsLoading(false);
    };

    loadCourses();
  }, []);

  // 코스 상세 보기 (추후 구현)
  const handleViewCourse = (courseId: string) => {
    // TODO: 코스 상세 페이지로 이동
    console.log('View course:', courseId);
    setToast({
      message: '코스 상세 보기는 준비 중이에요',
      type: 'info',
    });
  };

  // 코스 삭제 확인
  const handleDeleteClick = (courseId: string) => {
    setDeleteTarget(courseId);
  };

  // 코스 삭제 실행
  const handleDeleteConfirm = () => {
    if (!deleteTarget) return;

    const success = deleteCourse(deleteTarget);
    if (success) {
      setCourses((prev) => prev.filter((c) => c.courseId !== deleteTarget));
      setToast({
        message: '코스가 삭제되었어요',
        type: 'success',
      });
    } else {
      setToast({
        message: '삭제에 실패했어요',
        type: 'error',
      });
    }

    setDeleteTarget(null);
  };

  // 새 코스 만들기
  const handleCreateCourse = () => {
    router.push('/');
  };

  // 뒤로가기
  const handleBack = () => {
    router.back();
  };

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
      {/* 헤더 */}
      <header className="sticky top-0 z-10 bg-white border-b border-gray-200 dark:bg-gray-800 dark:border-gray-700">
        <div className="flex items-center justify-between px-4 h-14">
          <button
            onClick={handleBack}
            className="p-2 -ml-2 text-gray-600 hover:text-gray-900 dark:text-gray-400 dark:hover:text-gray-100"
            aria-label="뒤로가기"
          >
            <svg
              className="w-6 h-6"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M15 19l-7-7 7-7"
              />
            </svg>
          </button>

          <h1 className="text-lg font-semibold text-gray-900 dark:text-gray-100">
            내 코스
          </h1>

          {/* 저장 현황 */}
          <div className="text-sm text-gray-500 dark:text-gray-400">
            {getSavedCount()}/{MAX_COURSES}
          </div>
        </div>
      </header>

      {/* 메인 컨텐츠 */}
      <main className="px-4 py-4 max-w-2xl mx-auto">
        {isLoading ? (
          // 로딩 상태
          <div className="flex items-center justify-center py-16">
            <svg
              className="animate-spin h-8 w-8 text-pink-500"
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
          </div>
        ) : courses.length === 0 ? (
          // 빈 상태
          <EmptyState
            icon="bookmark"
            title="저장된 코스가 없어요"
            description="마음에 드는 데이트 코스를 저장해보세요. 언제든지 다시 확인할 수 있어요!"
            actionText="코스 만들기"
            onAction={handleCreateCourse}
          />
        ) : (
          // 코스 목록
          <div className="space-y-4">
            {/* 브라우저 저장 안내 배너 */}
            <BrowserStorageNotice className="mb-4" />

            {/* 코스 카드 목록 */}
            <div className="space-y-3">
              {courses.map((course) => (
                <SavedCourseCard
                  key={course.courseId}
                  course={course}
                  onClick={() => handleViewCourse(course.courseId)}
                  onDelete={() => handleDeleteClick(course.courseId)}
                />
              ))}
            </div>

            {/* 새 코스 만들기 버튼 */}
            <div className="pt-4">
              <button
                onClick={handleCreateCourse}
                className="w-full py-3 text-sm font-medium text-pink-500 border border-pink-200 rounded-xl hover:bg-pink-50 transition-colors dark:border-pink-800 dark:hover:bg-pink-900/30"
              >
                + 새 코스 만들기
              </button>
            </div>
          </div>
        )}
      </main>

      {/* 삭제 확인 다이얼로그 */}
      <ConfirmDialog
        isOpen={deleteTarget !== null}
        onClose={() => setDeleteTarget(null)}
        onConfirm={handleDeleteConfirm}
        title="코스를 삭제할까요?"
        description="삭제된 코스는 복구할 수 없어요"
        confirmText="삭제"
        cancelText="취소"
        variant="danger"
      />

      {/* 토스트 메시지 */}
      {toast && (
        <Toast
          message={toast.message}
          type={toast.type}
          onClose={() => setToast(null)}
        />
      )}
    </div>
  );
}
