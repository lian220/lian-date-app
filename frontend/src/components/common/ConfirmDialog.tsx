'use client';

import React, { useEffect, useRef } from 'react';

/**
 * 다이얼로그 변형 타입
 */
type DialogVariant = 'danger' | 'warning' | 'info';

/**
 * ConfirmDialog 컴포넌트 Props
 */
interface ConfirmDialogProps {
  /** 다이얼로그 표시 여부 */
  isOpen: boolean;
  /** 닫기 핸들러 */
  onClose: () => void;
  /** 확인 핸들러 */
  onConfirm: () => void;
  /** 제목 */
  title: string;
  /** 설명 문구 */
  description?: string;
  /** 확인 버튼 텍스트 */
  confirmText?: string;
  /** 취소 버튼 텍스트 */
  cancelText?: string;
  /** 다이얼로그 변형 (danger, warning, info) */
  variant?: DialogVariant;
  /** 확인 버튼 로딩 상태 */
  isLoading?: boolean;
}

/**
 * 변형에 따른 스타일 반환
 */
function getVariantStyles(variant: DialogVariant) {
  switch (variant) {
    case 'danger':
      return {
        icon: (
          <svg
            className="w-6 h-6 text-red-500"
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
        ),
        iconBg: 'bg-red-100',
        confirmButton:
          'bg-red-500 hover:bg-red-600 active:bg-red-700 text-white',
      };
    case 'warning':
      return {
        icon: (
          <svg
            className="w-6 h-6 text-yellow-500"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
            />
          </svg>
        ),
        iconBg: 'bg-yellow-100',
        confirmButton:
          'bg-yellow-500 hover:bg-yellow-600 active:bg-yellow-700 text-white',
      };
    case 'info':
    default:
      return {
        icon: (
          <svg
            className="w-6 h-6 text-blue-500"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
            />
          </svg>
        ),
        iconBg: 'bg-blue-100',
        confirmButton:
          'bg-blue-500 hover:bg-blue-600 active:bg-blue-700 text-white',
      };
  }
}

/**
 * 확인 다이얼로그 컴포넌트
 * 삭제 등 중요한 액션 전에 사용자 확인을 받을 때 사용
 *
 * @example
 * <ConfirmDialog
 *   isOpen={showDeleteDialog}
 *   onClose={() => setShowDeleteDialog(false)}
 *   onConfirm={handleDelete}
 *   title="코스를 삭제할까요?"
 *   description="삭제된 코스는 복구할 수 없어요"
 *   confirmText="삭제"
 *   variant="danger"
 * />
 */
export default function ConfirmDialog({
  isOpen,
  onClose,
  onConfirm,
  title,
  description,
  confirmText = '확인',
  cancelText = '취소',
  variant = 'info',
  isLoading = false,
}: ConfirmDialogProps) {
  const dialogRef = useRef<HTMLDivElement>(null);
  const styles = getVariantStyles(variant);

  // ESC 키로 닫기
  useEffect(() => {
    function handleKeyDown(e: KeyboardEvent) {
      if (e.key === 'Escape' && isOpen && !isLoading) {
        onClose();
      }
    }

    document.addEventListener('keydown', handleKeyDown);
    return () => document.removeEventListener('keydown', handleKeyDown);
  }, [isOpen, isLoading, onClose]);

  // 다이얼로그 열릴 때 스크롤 방지
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
      role="dialog"
      aria-modal="true"
      aria-labelledby="dialog-title"
      aria-describedby="dialog-description"
    >
      {/* 배경 오버레이 */}
      <div
        className="absolute inset-0 bg-black/50 transition-opacity"
        onClick={!isLoading ? onClose : undefined}
        aria-hidden="true"
      />

      {/* 다이얼로그 본문 */}
      <div
        ref={dialogRef}
        className="relative bg-white rounded-2xl shadow-xl w-full max-w-sm mx-auto overflow-hidden animate-in zoom-in-95 duration-200"
      >
        <div className="p-6">
          {/* 아이콘 */}
          <div
            className={`w-12 h-12 ${styles.iconBg} rounded-full flex items-center justify-center mx-auto mb-4`}
          >
            {styles.icon}
          </div>

          {/* 제목 */}
          <h3
            id="dialog-title"
            className="text-lg font-semibold text-gray-900 text-center mb-2"
          >
            {title}
          </h3>

          {/* 설명 */}
          {description && (
            <p id="dialog-description" className="text-sm text-gray-500 text-center">{description}</p>
          )}
        </div>

        {/* 버튼 영역 */}
        <div className="flex border-t border-gray-100">
          <button
            onClick={onClose}
            disabled={isLoading}
            className="flex-1 py-3.5 text-sm font-medium text-gray-700 hover:bg-gray-50 active:bg-gray-100 transition-colors disabled:opacity-50"
          >
            {cancelText}
          </button>
          <div className="w-px bg-gray-100" />
          <button
            onClick={onConfirm}
            disabled={isLoading}
            className={`flex-1 py-3.5 text-sm font-medium transition-colors disabled:opacity-50 ${styles.confirmButton}`}
            data-testid="confirm-dialog-confirm-button"
          >
            {isLoading ? (
              <span className="flex items-center justify-center gap-2">
                <svg
                  className="animate-spin h-4 w-4"
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
                처리 중...
              </span>
            ) : (
              confirmText
            )}
          </button>
        </div>
      </div>
    </div>
  );
}
