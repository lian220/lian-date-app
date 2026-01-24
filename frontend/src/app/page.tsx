'use client';

import { useState } from 'react';
import RegionBottomSheet from '@/components/region/RegionBottomSheet';
import { Region } from '@/types/region';

export default function Home() {
  const [isBottomSheetOpen, setIsBottomSheetOpen] = useState(false);
  const [selectedRegion, setSelectedRegion] = useState<Region | null>(null);

  const handleRegionSelect = (region: Region) => {
    setSelectedRegion(region);
  };

  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-zinc-50 p-8 font-sans dark:bg-black">
      <main className="flex w-full max-w-2xl flex-col items-center gap-8">
        <div className="text-center">
          <h1 className="mb-4 text-4xl font-bold text-gray-900 dark:text-gray-100">
            데이트 조건 입력
          </h1>
          <p className="text-lg text-gray-600 dark:text-gray-400">
            원하는 데이트 지역을 선택해주세요
          </p>
        </div>

        {/* Region Selection Button */}
        <button
          onClick={() => setIsBottomSheetOpen(true)}
          className="flex w-full max-w-md items-center justify-between rounded-xl border border-gray-300 bg-white px-6 py-4 text-left shadow-sm transition-all hover:border-blue-400 hover:shadow-md dark:border-gray-700 dark:bg-gray-800 dark:hover:border-blue-500"
        >
          <div className="flex flex-col gap-1">
            <span className="text-sm font-medium text-gray-500 dark:text-gray-400">
              지역
            </span>
            <span className="text-lg font-semibold text-gray-900 dark:text-gray-100">
              {selectedRegion ? selectedRegion.name : '지역을 선택하세요'}
            </span>
            {selectedRegion && selectedRegion.keywords.length > 0 && (
              <div className="mt-1 flex flex-wrap gap-1.5">
                {selectedRegion.keywords.map(keyword => (
                  <span
                    key={keyword}
                    className="rounded-full bg-blue-100 px-2.5 py-0.5 text-xs font-medium text-blue-700 dark:bg-blue-900 dark:text-blue-200"
                  >
                    {keyword}
                  </span>
                ))}
              </div>
            )}
          </div>
          <svg
            className="h-6 w-6 text-gray-400"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M9 5l7 7-7 7"
            />
          </svg>
        </button>

        {/* Selected Region Info */}
        {selectedRegion && (
          <div className="w-full max-w-md rounded-lg border border-green-200 bg-green-50 p-4 dark:border-green-900 dark:bg-green-950">
            <div className="flex items-start gap-3">
              <svg
                className="mt-0.5 h-5 w-5 flex-shrink-0 text-green-600 dark:text-green-400"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"
                />
              </svg>
              <div>
                <p className="font-semibold text-green-900 dark:text-green-100">
                  선택 완료!
                </p>
                <p className="mt-1 text-sm text-green-700 dark:text-green-300">
                  <span className="font-medium">{selectedRegion.name}</span>{' '}
                  지역이 선택되었습니다.
                </p>
              </div>
            </div>
          </div>
        )}
      </main>

      {/* Region Bottom Sheet */}
      <RegionBottomSheet
        isOpen={isBottomSheetOpen}
        onClose={() => setIsBottomSheetOpen(false)}
        onSelect={handleRegionSelect}
        initialRegion={selectedRegion || undefined}
      />
    </div>
  );
}
