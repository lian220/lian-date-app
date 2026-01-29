'use client';

import { useEffect, useRef } from 'react';

interface ToastProps {
  message: string;
  isVisible: boolean;
  onClose: () => void;
  duration?: number;
}

/**
 * 토스트 메시지 컴포넌트
 * 일정 시간 후 자동으로 사라지는 알림 메시지
 */
export default function Toast({
  message,
  isVisible,
  onClose,
  duration = 2000,
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

  return (
    <div className="fixed bottom-20 left-1/2 z-50 -translate-x-1/2 transform animate-fade-in-up">
      <div className="flex items-center gap-2 rounded-full bg-gray-800 px-4 py-2.5 shadow-lg dark:bg-gray-700">
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
        <span className="text-sm font-medium text-white">{message}</span>
      </div>
    </div>
  );
}
