'use client';

import { useState } from 'react';
import RegionBottomSheet from '@/components/region/RegionBottomSheet';
import DateTypeBottomSheet from '@/components/dateType/DateTypeBottomSheet';
import BudgetBottomSheet from '@/components/budget/BudgetBottomSheet';
import SpecialRequestInput from '@/components/request/SpecialRequestInput';
import ConditionSummary from '@/components/condition/ConditionSummary';
import { Region } from '@/types/region';
import { DateType } from '@/types/dateType';
import { BudgetRange } from '@/types/budget';
import { DateCondition } from '@/types/dateCondition';

export default function Home() {
  const [currentStep, setCurrentStep] = useState<
    'region' | 'dateType' | 'budget' | 'request' | 'summary'
  >('region');
  const [condition, setCondition] = useState<DateCondition>({
    region: null,
    dateType: null,
    budget: null,
    specialRequest: '',
  });

  const [isRegionSheetOpen, setIsRegionSheetOpen] = useState(false);
  const [isDateTypeSheetOpen, setIsDateTypeSheetOpen] = useState(false);
  const [isBudgetSheetOpen, setIsBudgetSheetOpen] = useState(false);

  const handleRegionSelect = (region: Region) => {
    setCondition(prev => ({ ...prev, region }));
    setIsRegionSheetOpen(false);
    setCurrentStep('dateType');
    setIsDateTypeSheetOpen(true);
  };

  const handleDateTypeSelect = (dateType: DateType) => {
    setCondition(prev => ({ ...prev, dateType }));
    setIsDateTypeSheetOpen(false);
    setCurrentStep('budget');
    setIsBudgetSheetOpen(true);
  };

  const handleBudgetSelect = (budget: BudgetRange) => {
    setCondition(prev => ({ ...prev, budget }));
    setIsBudgetSheetOpen(false);
    setCurrentStep('request');
  };

  const handleRequestChange = (request: string) => {
    setCondition(prev => ({ ...prev, specialRequest: request }));
  };

  const handleEditField = (field: keyof DateCondition) => {
    if (field === 'region') {
      setIsRegionSheetOpen(true);
    } else if (field === 'dateType') {
      setIsDateTypeSheetOpen(true);
    } else if (field === 'budget') {
      setIsBudgetSheetOpen(true);
    } else if (field === 'specialRequest') {
      setCurrentStep('request');
    }
  };

  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-zinc-50 p-8 font-sans dark:bg-black">
      <main className="flex w-full max-w-2xl flex-col items-center gap-8">
        <div className="text-center">
          <h1 className="mb-4 text-4xl font-bold text-gray-900 dark:text-gray-100">
            데이트 조건 입력
          </h1>
          <p className="text-lg text-gray-600 dark:text-gray-400">
            {currentStep === 'region' && '원하는 데이트 지역을 선택해주세요'}
            {currentStep === 'dateType' && '데이트 유형을 선택해주세요'}
            {currentStep === 'budget' && '예산 범위를 선택해주세요'}
            {currentStep === 'request' && '특별한 요청사항이 있으신가요?'}
            {currentStep === 'summary' && '입력하신 조건을 확인해주세요'}
          </p>
        </div>

        {currentStep === 'region' && (
          <button
            onClick={() => setIsRegionSheetOpen(true)}
            className="flex w-full max-w-md items-center justify-between rounded-xl border border-gray-300 bg-white px-6 py-4 text-left shadow-sm transition-all hover:border-blue-400 hover:shadow-md dark:border-gray-700 dark:bg-gray-800 dark:hover:border-blue-500"
          >
            <div className="flex flex-col gap-1">
              <span className="text-sm font-medium text-gray-500 dark:text-gray-400">
                지역
              </span>
              <span className="text-lg font-semibold text-gray-900 dark:text-gray-100">
                지역을 선택하세요
              </span>
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
        )}

        {currentStep === 'dateType' && (
          <button
            onClick={() => setIsDateTypeSheetOpen(true)}
            className="flex w-full max-w-md items-center justify-between rounded-xl border border-gray-300 bg-white px-6 py-4 text-left shadow-sm transition-all hover:border-blue-400 hover:shadow-md dark:border-gray-700 dark:bg-gray-800 dark:hover:border-blue-500"
          >
            <div className="flex flex-col gap-1">
              <span className="text-sm font-medium text-gray-500 dark:text-gray-400">
                데이트 유형
              </span>
              <span className="text-lg font-semibold text-gray-900 dark:text-gray-100">
                유형을 선택하세요
              </span>
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
        )}

        {currentStep === 'budget' && (
          <button
            onClick={() => setIsBudgetSheetOpen(true)}
            className="flex w-full max-w-md items-center justify-between rounded-xl border border-gray-300 bg-white px-6 py-4 text-left shadow-sm transition-all hover:border-blue-400 hover:shadow-md dark:border-gray-700 dark:bg-gray-800 dark:hover:border-blue-500"
          >
            <div className="flex flex-col gap-1">
              <span className="text-sm font-medium text-gray-500 dark:text-gray-400">
                예산 (1인 기준)
              </span>
              <span className="text-lg font-semibold text-gray-900 dark:text-gray-100">
                예산을 선택하세요
              </span>
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
        )}

        {currentStep === 'request' && (
          <div className="w-full max-w-md">
            <SpecialRequestInput
              value={condition.specialRequest}
              onChange={handleRequestChange}
            />
            <div className="mt-6 flex gap-3">
              <button
                onClick={() => setCurrentStep('budget')}
                className="flex-1 rounded-lg border border-gray-300 px-6 py-3 font-semibold text-gray-700 transition-colors hover:bg-gray-50 dark:border-gray-600 dark:text-gray-300 dark:hover:bg-gray-800"
              >
                이전
              </button>
              <button
                onClick={() => setCurrentStep('summary')}
                className="flex-1 rounded-lg bg-blue-600 px-6 py-3 font-semibold text-white transition-colors hover:bg-blue-700 dark:bg-blue-500 dark:hover:bg-blue-600"
              >
                다음
              </button>
            </div>
          </div>
        )}

        {currentStep === 'summary' && (
          <div className="w-full max-w-md">
            <ConditionSummary condition={condition} onEdit={handleEditField} />
            <div className="mt-6 flex gap-3">
              <button
                onClick={() => setCurrentStep('request')}
                className="flex-1 rounded-lg border border-gray-300 px-6 py-3 font-semibold text-gray-700 transition-colors hover:bg-gray-50 dark:border-gray-600 dark:text-gray-300 dark:hover:bg-gray-800"
              >
                이전
              </button>
              <button
                onClick={() => alert('데이트 코스 생성 기능은 추후 구현 예정입니다.')}
                disabled={
                  !condition.region || !condition.dateType || !condition.budget
                }
                className="flex-1 rounded-lg bg-blue-600 px-6 py-3 font-semibold text-white transition-colors hover:bg-blue-700 disabled:cursor-not-allowed disabled:bg-gray-400 dark:bg-blue-500 dark:hover:bg-blue-600 dark:disabled:bg-gray-600"
              >
                데이트 코스 생성
              </button>
            </div>
          </div>
        )}
      </main>

      {/* Bottom Sheets */}
      <RegionBottomSheet
        isOpen={isRegionSheetOpen}
        onClose={() => setIsRegionSheetOpen(false)}
        onSelect={handleRegionSelect}
        initialRegion={condition.region || undefined}
      />

      <DateTypeBottomSheet
        isOpen={isDateTypeSheetOpen}
        onClose={() => setIsDateTypeSheetOpen(false)}
        onSelect={handleDateTypeSelect}
        initialDateType={condition.dateType || undefined}
      />

      <BudgetBottomSheet
        isOpen={isBudgetSheetOpen}
        onClose={() => setIsBudgetSheetOpen(false)}
        onSelect={handleBudgetSelect}
        initialBudget={condition.budget || undefined}
      />
    </div>
  );
}
