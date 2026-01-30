'use client';

import React, { useState, useCallback, useSyncExternalStore } from 'react';

/**
 * 로컬 스토리지 키 (배너 닫힘 상태 저장용)
 */
const NOTICE_DISMISSED_KEY = 'dateclick_storage_notice_dismissed';

/**
 * localStorage dismissed 상태 구독을 위한 store
 */
const dismissedStore = {
  getSnapshot(): boolean {
    if (typeof window === 'undefined') return false;
    try {
      return localStorage.getItem(NOTICE_DISMISSED_KEY) === 'true';
    } catch {
      return false;
    }
  },
  getServerSnapshot(): boolean {
    return false;
  },
  subscribe(callback: () => void): () => void {
    window.addEventListener('storage', callback);
    return () => window.removeEventListener('storage', callback);
  },
};

/**
 * BrowserStorageNotice 컴포넌트 Props
 */
interface BrowserStorageNoticeProps {
  /** 추가 CSS 클래스 */
  className?: string;
}

/**
 * 브라우저 저장 안내 배너 컴포넌트
 * 내 코스 목록 화면 상단에 표시되는 안내 메시지
 *
 * @example
 * <BrowserStorageNotice />
 */
export default function BrowserStorageNotice({
  className = '',
}: BrowserStorageNoticeProps) {
  const isDismissed = useSyncExternalStore(
    dismissedStore.subscribe,
    dismissedStore.getSnapshot,
    dismissedStore.getServerSnapshot
  );
  const [isAnimating, setIsAnimating] = useState(false);
  const [localDismissed, setLocalDismissed] = useState(false);

  // 닫기 핸들러
  const handleDismiss = useCallback(() => {
    setIsAnimating(true);

    // 애니메이션 후 실제로 숨김
    setTimeout(() => {
      setLocalDismissed(true);
      setIsAnimating(false);

      // localStorage에 dismissed 상태 저장
      try {
        localStorage.setItem(NOTICE_DISMISSED_KEY, 'true');
      } catch {
        // localStorage 저장 실패 무시
      }
    }, 200);
  }, []);

  // dismissed 상태면 렌더링하지 않음
  if (isDismissed || localDismissed) return null;

  return (
    <div
      className={`
        bg-blue-50 border border-blue-100 rounded-xl p-4
        transition-all duration-200
        ${isAnimating ? 'opacity-0 transform -translate-y-2' : 'opacity-100'}
        ${className}
      `}
      role="status"
      aria-live="polite"
    >
      <div className="flex items-start gap-3">
        {/* 정보 아이콘 */}
        <div className="flex-shrink-0 mt-0.5">
          <svg
            className="w-5 h-5 text-blue-500"
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
        </div>

        {/* 안내 텍스트 */}
        <div className="flex-1 min-w-0">
          <p className="text-sm text-blue-800 font-medium mb-1">
            브라우저에 저장됩니다
          </p>
          <p className="text-xs text-blue-600 leading-relaxed">
            저장된 코스는 이 브라우저에만 보관돼요.
            <br />
            브라우저 데이터를 삭제하면 저장된 코스도 함께 삭제될 수 있어요.
          </p>
        </div>

        {/* 닫기 버튼 */}
        <button
          onClick={handleDismiss}
          className="flex-shrink-0 p-1 text-blue-400 hover:text-blue-600 hover:bg-blue-100 rounded-full transition-colors"
          aria-label="안내 닫기"
        >
          <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M6 18L18 6M6 6l12 12"
            />
          </svg>
        </button>
      </div>
    </div>
  );
}

/**
 * 배너 dismissed 상태 초기화 (테스트/디버깅용)
 */
export function resetStorageNotice(): void {
  try {
    localStorage.removeItem(NOTICE_DISMISSED_KEY);
  } catch {
    // localStorage 접근 실패 무시
  }
}
