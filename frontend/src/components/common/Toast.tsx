'use client';

import { useEffect, useRef } from 'react';

/**
 * 토스트 타입
 */
type ToastType = 'success' | 'error' | 'info';

interface ToastProps {
  message: string;
  /** @deprecated Use conditional rendering instead */
  isVisible?: boolean;
  onClose: () => void;
  duration?: number;
  /** 토스트 타입 (success, error, info) */
  type?: ToastType;
}

/**
 * 토스트 타입별 스타일
 */
function getTypeStyles(type: ToastType) {
  switch (type) {
    case 'success':
      return {
        icon: (
          <svg
            className="h-4 w-4 text-green-400"
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
        ),
      };
    case 'error':
      return {
        icon: (
          <svg
            className="h-4 w-4 text-red-400"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M6 18L18 6M6 6l12 12"
            />
          </svg>
        ),
      };
    case 'info':
    default:
      return {
        icon: (
          <svg
            className="h-4 w-4 text-blue-400"
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
      };
  }
}

/**
 * 토스트 메시지 컴포넌트
 * 일정 시간 후 자동으로 사라지는 알림 메시지
 *
 * @example
 * // 새로운 사용법 (조건부 렌더링)
 * {toast && (
 *   <Toast
 *     message={toast.message}
 *     type={toast.type}
 *     onClose={() => setToast(null)}
 *   />
 * )}
 *
 * // 기존 사용법 (deprecated)
 * <Toast
 *   message="저장되었습니다"
 *   isVisible={showToast}
 *   onClose={() => setShowToast(false)}
 * />
 */
export default function Toast({
  message,
  isVisible = true,
  onClose,
  duration = 2000,
  type = 'success',
}: ToastProps) {
  const timerRef = useRef<ReturnType<typeof setTimeout> | null>(null);
  const onCloseRef = useRef(onClose);

  useEffect(() => {
    onCloseRef.current = onClose;
  }, [onClose]);

  useEffect(() => {
    if (isVisible) {
      timerRef.current = setTimeout(() => {
        onCloseRef.current();
      }, duration);
    }

    return () => {
      if (timerRef.current) {
        clearTimeout(timerRef.current);
      }
    };
  }, [isVisible, duration]);

  if (!isVisible) return null;

  const styles = getTypeStyles(type);

  return (
    <div className="fixed bottom-20 left-1/2 z-50 -translate-x-1/2 transform animate-fade-in-up">
      <div className="flex items-center gap-2 rounded-full bg-gray-800 px-4 py-2.5 shadow-lg dark:bg-gray-700">
        {styles.icon}
        <span className="text-sm font-medium text-white">{message}</span>
      </div>
    </div>
  );
}
