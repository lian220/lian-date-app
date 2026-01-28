'use client';

import { useState, useEffect, useMemo } from 'react';
import { Region } from '@/types/region';
import { getRegions } from '@/lib/api/regions';
import { filterRegions } from '@/lib/search';
import RegionTabs from './RegionTabs';
import RegionCard from './RegionCard';

interface RegionBottomSheetProps {
  isOpen: boolean;
  onClose: () => void;
  onSelect?: (region: Region) => void;
  initialRegion?: Region;
}

export default function RegionBottomSheet({
  isOpen,
  onClose,
  onSelect,
  initialRegion,
}: RegionBottomSheetProps) {
  const [activeTab, setActiveTab] = useState<'seoul' | 'gyeonggi'>('seoul');
  const [selectedRegion, setSelectedRegion] = useState<Region | null>(
    initialRegion || null
  );
  const [searchQuery, setSearchQuery] = useState('');
  const [regions, setRegions] = useState<Region[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // 지역 데이터 로드 (컴포넌트 마운트 시 한 번만)
  useEffect(() => {
    const loadRegions = async () => {
      try {
        setIsLoading(true);
        setError(null);
        const data = await getRegions();
        setRegions(data);
      } catch (err) {
        console.error('Failed to load regions:', err);
        setError('지역 정보를 불러올 수 없습니다');
      } finally {
        setIsLoading(false);
      }
    };

    loadRegions();
  }, []);

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

  const handleRegionSelect = (region: Region) => {
    setSelectedRegion(region);
    onSelect?.(region);
  };

  const handleBackdropClick = (e: React.MouseEvent) => {
    if (e.target === e.currentTarget) {
      onClose();
    }
  };

  // 탭별 권역 필터링 및 검색어 필터링 (useMemo로 성능 최적화)
  const currentRegions = useMemo(() => {
    // 1. 탭별 필터링
    const regionsByTab = regions.filter(
      region => region.city === activeTab
    );

    // 2. 검색어 필터링
    return filterRegions(regionsByTab, searchQuery);
  }, [regions, activeTab, searchQuery]);

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
          fixed bottom-0 left-0 right-0 z-50 flex max-h-[85vh] flex-col
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
          <div className="flex items-center justify-between">
            <h2 className="text-xl font-bold text-gray-900 dark:text-gray-100">
              지역 선택
            </h2>
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

        {/* Tabs */}
        <RegionTabs activeTab={activeTab} onTabChange={setActiveTab} />

        {/* Search Input */}
        <div className="px-6 pt-4">
          <div className="relative">
            <input
              type="text"
              value={searchQuery}
              onChange={e => setSearchQuery(e.target.value)}
              placeholder="권역명이나 키워드로 검색 (예: 성수, 강남)"
              className="w-full rounded-lg border border-gray-300 bg-white px-4 py-3 pl-10 text-sm placeholder-gray-400 focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-500/20 dark:border-gray-600 dark:bg-gray-800 dark:text-gray-100 dark:placeholder-gray-500 dark:focus:border-blue-400"
            />
            <svg
              className="absolute left-3 top-1/2 h-5 w-5 -translate-y-1/2 text-gray-400 dark:text-gray-500"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
              />
            </svg>
            {searchQuery && (
              <button
                onClick={() => setSearchQuery('')}
                className="absolute right-3 top-1/2 -translate-y-1/2 rounded-full p-1 hover:bg-gray-100 dark:hover:bg-gray-700"
                aria-label="검색어 지우기"
              >
                <svg
                  className="h-4 w-4 text-gray-400 dark:text-gray-500"
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
            )}
          </div>
        </div>

        {/* Content */}
        <div className="flex-1 overflow-y-auto px-6 py-6">
          {error ? (
            <div className="flex flex-col items-center justify-center py-12 text-center">
              <svg
                className="mb-4 h-16 w-16 text-red-300 dark:text-red-600"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={1.5}
                  d="M12 8v4m0 4v.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
                />
              </svg>
              <p className="text-lg font-medium text-red-600 dark:text-red-400">
                {error}
              </p>
              <p className="mt-2 text-sm text-gray-500 dark:text-gray-500">
                잠시 후 다시 시도해주세요
              </p>
            </div>
          ) : isLoading ? (
            <div className="flex flex-col items-center justify-center py-12">
              <div className="mb-4 h-12 w-12 animate-spin rounded-full border-4 border-gray-300 border-t-blue-600 dark:border-gray-600 dark:border-t-blue-400" />
              <p className="text-lg font-medium text-gray-600 dark:text-gray-400">
                지역 정보를 불러오는 중입니다
              </p>
            </div>
          ) : currentRegions.length > 0 ? (
            <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 md:grid-cols-3">
              {currentRegions.map(region => (
                <RegionCard
                  key={region.id}
                  region={region}
                  isSelected={selectedRegion?.id === region.id}
                  onSelect={handleRegionSelect}
                  searchQuery={searchQuery}
                />
              ))}
            </div>
          ) : (
            <div className="flex flex-col items-center justify-center py-12 text-center">
              <svg
                className="mb-4 h-16 w-16 text-gray-300 dark:text-gray-600"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={1.5}
                  d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
                />
              </svg>
              <p className="text-lg font-medium text-gray-600 dark:text-gray-400">
                검색 결과가 없습니다
              </p>
              <p className="mt-2 text-sm text-gray-500 dark:text-gray-500">
                다른 키워드로 검색해보세요
              </p>
            </div>
          )}
        </div>

        {/* Selected Region Display */}
        {selectedRegion && (
          <div className="border-t border-gray-200 bg-gray-50 px-6 py-4 dark:border-gray-700 dark:bg-gray-800">
            <p className="text-sm text-gray-600 dark:text-gray-400">
              선택된 지역
            </p>
            <p className="mt-1 text-lg font-semibold text-gray-900 dark:text-gray-100">
              {selectedRegion.name}
            </p>
          </div>
        )}
      </div>
    </>
  );
}
