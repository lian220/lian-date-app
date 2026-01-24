'use client';

import { useState, useEffect } from 'react';
import { Region, RegionType } from '@/types/region';
import { REGION_DATA } from '@/constants/regions';
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
  const [activeTab, setActiveTab] = useState<RegionType>('seoul');
  const [selectedRegion, setSelectedRegion] = useState<Region | null>(
    initialRegion || null
  );

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

  const currentRegions =
    REGION_DATA.find(category => category.type === activeTab)?.regions || [];

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

        {/* Content */}
        <div className="flex-1 overflow-y-auto px-6 py-6">
          <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 md:grid-cols-3">
            {currentRegions.map(region => (
              <RegionCard
                key={region.id}
                region={region}
                isSelected={selectedRegion?.id === region.id}
                onSelect={handleRegionSelect}
              />
            ))}
          </div>
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
