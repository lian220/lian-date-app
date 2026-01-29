'use client';

import { useEffect, useState, useCallback } from 'react';
import { Place } from '@/types/course';
import Toast from '@/components/common/Toast';
import { openKakaoMap } from '@/lib/kakaoMapLink';

interface PlaceDetailBottomSheetProps {
  isOpen: boolean;
  onClose: () => void;
  place: Place | null;
}

/**
 * 장소 상세 정보 바텀시트
 * 지도에서 마커 클릭 시 표시되는 장소 상세 정보
 */
export default function PlaceDetailBottomSheet({
  isOpen,
  onClose,
  place,
}: PlaceDetailBottomSheetProps) {
  const [showToast, setShowToast] = useState(false);

  useEffect(() => {
    if (isOpen) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = 'unset';
    }
    return () => {
      document.body.style.overflow = 'unset';
    };
  }, [isOpen]);

  const handleBackdropClick = (e: React.MouseEvent) => {
    if (e.target === e.currentTarget) {
      onClose();
    }
  };

  const handleCopyAddress = useCallback(async () => {
    if (!place) return;
    try {
      if (!navigator.clipboard) {
        throw new Error('Clipboard API not supported');
      }
      await navigator.clipboard.writeText(place.address);
      setShowToast(true);
    } catch (err) {
      console.error('주소 복사 실패:', err);
    }
  }, [place]);

  const handleCloseToast = useCallback(() => {
    setShowToast(false);
  }, []);

  if (!place) return null;

  return (
    <>
      {/* Backdrop */}
      <div
        className={`
          fixed inset-0 z-40 bg-black transition-opacity duration-300
          ${isOpen ? 'opacity-50' : 'pointer-events-none opacity-0'}
        `}
        onClick={handleBackdropClick}
      />

      {/* Bottom Sheet */}
      <div
        className={`
          fixed bottom-0 left-0 right-0 z-50 flex max-h-[70vh] flex-col
          rounded-t-3xl bg-white shadow-2xl transition-transform duration-300 dark:bg-gray-900
          ${isOpen ? 'translate-y-0' : 'translate-y-full'}
        `}
      >
        {/* Handle */}
        <div className="flex justify-center py-3">
          <div className="h-1.5 w-12 rounded-full bg-gray-300 dark:bg-gray-600" />
        </div>

        {/* Header */}
        <div className="px-6 pb-4">
          <div className="flex items-start justify-between">
            <div className="flex items-start gap-3">
              {/* 순서 번호 뱃지 */}
              <div className="flex h-8 w-8 flex-shrink-0 items-center justify-center rounded-full bg-indigo-500 font-bold text-white shadow-md">
                {place.order}
              </div>
              <div>
                <h2 className="text-xl font-bold text-gray-900 dark:text-gray-100">
                  {place.name}
                </h2>
                <p className="mt-1 text-sm text-gray-500 dark:text-gray-400">
                  {place.category}
                  {place.categoryDetail && ` · ${place.categoryDetail}`}
                </p>
              </div>
            </div>
            <button
              onClick={onClose}
              className="rounded-full p-2 hover:bg-gray-100 dark:hover:bg-gray-800"
              aria-label="닫기"
            >
              <svg
                className="h-6 w-6 text-gray-500 dark:text-gray-400"
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
            </button>
          </div>
        </div>

        {/* Content */}
        <div className="flex-1 space-y-4 overflow-y-auto px-6 pb-6">
          {/* 주소 - 클릭 시 복사 */}
          <button
            type="button"
            onClick={handleCopyAddress}
            className="group flex w-full items-start gap-2 rounded-lg p-3 text-left transition-colors hover:bg-gray-100 active:bg-gray-200 dark:hover:bg-gray-800 dark:active:bg-gray-700"
          >
            <svg
              className="mt-0.5 h-5 w-5 flex-shrink-0 text-gray-400"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"
              />
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"
              />
            </svg>
            <span className="flex-1 text-sm text-gray-600 dark:text-gray-400">
              {place.roadAddress || place.address}
            </span>
            <svg
              className="h-4 w-4 flex-shrink-0 text-gray-400 opacity-0 transition-opacity group-hover:opacity-100"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M8 16H6a2 2 0 01-2-2V6a2 2 0 012-2h8a2 2 0 012 2v2m-6 12h8a2 2 0 002-2v-8a2 2 0 00-2-2h-8a2 2 0 00-2 2v8a2 2 0 002 2z"
              />
            </svg>
          </button>

          {/* 전화번호 & 예상 비용 */}
          <div className="flex items-center justify-between rounded-lg bg-gray-50 p-4 dark:bg-gray-800">
            <div className="flex items-center gap-2">
              <svg
                className={`h-5 w-5 ${place.phone ? 'text-gray-500' : 'text-gray-300 dark:text-gray-600'}`}
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z"
                />
              </svg>
              {place.phone ? (
                <a
                  href={`tel:${place.phone}`}
                  className="text-sm font-medium text-blue-600 hover:underline dark:text-blue-400"
                >
                  {place.phone}
                </a>
              ) : (
                <span className="text-sm text-gray-400 dark:text-gray-500">
                  전화번호 없음
                </span>
              )}
            </div>
            <div className="flex items-center gap-2">
              <svg
                className="h-5 w-5 text-gray-500"
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
              <span className="text-sm font-semibold text-gray-900 dark:text-gray-100">
                {place.estimatedCost.toLocaleString()}원
              </span>
              <span className="text-xs text-gray-500">(1인)</span>
            </div>
          </div>

          {/* AI 추천 이유 */}
          <div className="rounded-lg bg-indigo-50 p-4 dark:bg-indigo-950">
            <div className="flex items-start gap-2">
              <span className="text-lg">💡</span>
              <div className="flex-1">
                <p className="text-xs font-semibold text-indigo-600 dark:text-indigo-400">
                  AI 추천 이유
                </p>
                <p className="mt-1 text-sm text-gray-700 dark:text-gray-300">
                  {place.recommendReason}
                </p>
              </div>
            </div>
          </div>

          {/* 예상 소요 시간 */}
          {place.estimatedDuration > 0 && (
            <div className="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400">
              <svg
                className="h-5 w-5 text-gray-400"
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
              <span>예상 소요 시간: {place.estimatedDuration}분</span>
              {place.recommendedTime && (
                <span className="text-indigo-600 dark:text-indigo-400">
                  · 추천 시간: {place.recommendedTime}
                </span>
              )}
            </div>
          )}
        </div>

        {/* Footer - 카카오맵 열기 버튼 */}
        <div className="border-t border-gray-200 px-6 py-4 dark:border-gray-700">
          <button
            type="button"
            onClick={() => openKakaoMap(place.placeId, place.kakaoPlaceUrl)}
            className="flex w-full items-center justify-center gap-2 rounded-lg bg-yellow-400 px-4 py-3 font-semibold text-gray-900 transition-colors hover:bg-yellow-500 active:bg-yellow-600"
          >
            <svg className="h-5 w-5" viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 17.93c-3.95-.49-7-3.85-7-7.93 0-.62.08-1.21.21-1.79L9 15v1c0 1.1.9 2 2 2v1.93zm6.9-2.54c-.26-.81-1-1.39-1.9-1.39h-1v-3c0-.55-.45-1-1-1H8v-2h2c.55 0 1-.45 1-1V7h2c1.1 0 2-.9 2-2v-.41c2.93 1.19 5 4.06 5 7.41 0 2.08-.8 3.97-2.1 5.39z" />
            </svg>
            카카오맵에서 보기
          </button>
        </div>
      </div>

      {/* 토스트 메시지 */}
      <Toast
        message="주소가 복사되었습니다"
        isVisible={showToast}
        onClose={handleCloseToast}
      />
    </>
  );
}
