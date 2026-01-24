'use client';

import { useState, useEffect } from 'react';
import { BudgetRange } from '@/types/budget';
import { MOCK_BUDGET_RANGES } from '@/constants/budgets';
import BudgetCard from './BudgetCard';

interface BudgetBottomSheetProps {
  isOpen: boolean;
  onClose: () => void;
  onSelect?: (budget: BudgetRange) => void;
  initialBudget?: BudgetRange;
}

export default function BudgetBottomSheet({
  isOpen,
  onClose,
  onSelect,
  initialBudget,
}: BudgetBottomSheetProps) {
  const [selectedBudget, setSelectedBudget] = useState<BudgetRange | null>(
    initialBudget || null
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

  const handleBudgetSelect = (budget: BudgetRange) => {
    setSelectedBudget(budget);
    onSelect?.(budget);
  };

  const handleBackdropClick = (e: React.MouseEvent) => {
    if (e.target === e.currentTarget) {
      onClose();
    }
  };

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
            <div>
              <h2 className="text-xl font-bold text-gray-900 dark:text-gray-100">
                예산 선택
              </h2>
              <p className="mt-1 text-sm text-gray-500 dark:text-gray-400">
                1인 기준 예산을 선택해주세요
              </p>
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
        <div className="flex-1 overflow-y-auto px-6 py-6">
          <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
            {MOCK_BUDGET_RANGES.map(budget => (
              <BudgetCard
                key={budget.id}
                budget={budget}
                isSelected={selectedBudget?.id === budget.id}
                onSelect={handleBudgetSelect}
              />
            ))}
          </div>
        </div>

        {/* Selected Budget Display */}
        {selectedBudget && (
          <div className="border-t border-gray-200 bg-gray-50 px-6 py-4 dark:border-gray-700 dark:bg-gray-800">
            <p className="text-sm text-gray-600 dark:text-gray-400">
              선택된 예산
            </p>
            <p className="mt-1 text-lg font-semibold text-gray-900 dark:text-gray-100">
              {selectedBudget.label} (1인 기준)
            </p>
          </div>
        )}
      </div>
    </>
  );
}
