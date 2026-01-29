'use client';

import { useState, useCallback } from 'react';
import { Place } from '@/types/course';
import Toast from '@/components/common/Toast';
import { openKakaoMap } from '@/lib/kakaoMapLink';

interface PlaceCardProps {
  place: Place;
  order: number; // 1, 2, 3
}

/**
 * 코스 내 개별 장소 카드 컴포넌트
 * 장소명, 카테고리, 주소, 예상 비용, AI 추천 이유 표시
 * 주소 탭 시 클립보드 복사 기능 제공
 */
export default function PlaceCard({ place, order }: PlaceCardProps) {
  const [showToast, setShowToast] = useState(false);

  const handleCopyAddress = useCallback(async () => {
    try {
      if (!navigator.clipboard) {
        throw new Error('Clipboard API not supported');
      }
      await navigator.clipboard.writeText(place.address);
      setShowToast(true);
    } catch (err) {
      console.error('주소 복사 실패:', err);
    }
  }, [place.address]);

  const handleCloseToast = useCallback(() => {
    setShowToast(false);
  }, []);

  return (
    <div className="relative rounded-xl border border-gray-200 bg-white p-6 shadow-sm transition-shadow hover:shadow-md dark:border-gray-700 dark:bg-gray-800">
      {/* 순서 번호 뱃지 */}
      <div className="absolute -left-3 -top-3 flex h-8 w-8 items-center justify-center rounded-full bg-blue-600 font-bold text-white shadow-md dark:bg-blue-500">
        {order}
      </div>

      {/* 장소 정보 */}
      <div className="space-y-3">
        {/* 장소명 & 카테고리 */}
        <div>
          <h3 className="text-lg font-bold text-gray-900 dark:text-gray-100">
            {place.name}
          </h3>
          <p className="mt-1 text-sm text-gray-500 dark:text-gray-400">
            {place.category}
          </p>
        </div>

        {/* 주소 - 클릭 시 복사 */}
        <button
          type="button"
          onClick={handleCopyAddress}
          className="group flex w-full items-start gap-2 rounded-lg p-2 -ml-2 text-left transition-colors hover:bg-gray-100 active:bg-gray-200 dark:hover:bg-gray-700 dark:active:bg-gray-600"
          aria-label={`주소 복사: ${place.address}`}
        >
          {/* 위치 아이콘 */}
          <svg
            className="mt-0.5 h-4 w-4 flex-shrink-0 text-gray-400"
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
            {place.address}
          </span>
          {/* 복사 아이콘 */}
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

        {/* 전화번호 */}
        <div className="flex items-center gap-2">
          <svg
            className={`h-4 w-4 flex-shrink-0 ${place.phone ? 'text-gray-400' : 'text-gray-300 dark:text-gray-600'}`}
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
              className="text-sm text-blue-600 hover:text-blue-800 hover:underline dark:text-blue-400 dark:hover:text-blue-300"
              onClick={(e) => e.stopPropagation()}
            >
              {place.phone}
            </a>
          ) : (
            <span className="text-sm text-gray-400 dark:text-gray-500">
              전화번호 정보 없음
            </span>
          )}
        </div>

        {/* 예상 비용 */}
        <div className="flex items-center gap-2">
          <svg
            className="h-4 w-4 text-gray-400"
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
          <p className="text-sm font-semibold text-gray-900 dark:text-gray-100">
            {place.estimatedCost.toLocaleString()}원
          </p>
          <span className="text-xs text-gray-500 dark:text-gray-500">
            (1인 기준)
          </span>
        </div>

        {/* AI 추천 이유 */}
        <div className="rounded-lg bg-blue-50 p-3 dark:bg-blue-950">
          <div className="flex items-start gap-2">
            <span className="text-lg">💡</span>
            <div className="flex-1">
              <p className="text-xs font-semibold text-blue-600 dark:text-blue-400">
                AI 추천 이유
              </p>
              <p className="mt-1 text-sm text-gray-700 dark:text-gray-300">
                {place.recommendReason}
              </p>
            </div>
          </div>
        </div>

        {/* 카카오맵에서 보기 버튼 */}
        <button
          type="button"
          onClick={() => openKakaoMap(place.placeId, place.kakaoPlaceUrl)}
          className="flex w-full items-center justify-center gap-2 rounded-lg bg-yellow-400 px-4 py-3 font-semibold text-gray-900 transition-colors hover:bg-yellow-500 active:bg-yellow-600"
        >
          <svg
            className="h-5 w-5"
            viewBox="0 0 24 24"
            fill="currentColor"
          >
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 17.93c-3.95-.49-7-3.85-7-7.93 0-.62.08-1.21.21-1.79L9 15v1c0 1.1.9 2 2 2v1.93zm6.9-2.54c-.26-.81-1-1.39-1.9-1.39h-1v-3c0-.55-.45-1-1-1H8v-2h2c.55 0 1-.45 1-1V7h2c1.1 0 2-.9 2-2v-.41c2.93 1.19 5 4.06 5 7.41 0 2.08-.8 3.97-2.1 5.39z" />
          </svg>
          카카오맵에서 보기
        </button>
      </div>

      {/* 토스트 메시지 */}
      <Toast
        message="주소가 복사되었습니다"
        isVisible={showToast}
        onClose={handleCloseToast}
      />
    </div>
  );
}
