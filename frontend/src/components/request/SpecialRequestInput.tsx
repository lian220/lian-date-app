'use client';

import { useState, ChangeEvent } from 'react';

interface SpecialRequestInputProps {
  value: string;
  onChange: (value: string) => void;
  maxLength?: number;
  placeholder?: string;
}

export default function SpecialRequestInput({
  value,
  onChange,
  maxLength = 100,
  placeholder = '예: 사진 찍기 좋은 곳 위주로',
}: SpecialRequestInputProps) {
  const [isFocused, setIsFocused] = useState(false);

  const handleChange = (e: ChangeEvent<HTMLTextAreaElement>) => {
    const newValue = e.target.value;
    if (newValue.length <= maxLength) {
      onChange(newValue);
    }
  };

  const remainingChars = maxLength - value.length;
  const isNearLimit = remainingChars <= 20;

  return (
    <div className="w-full">
      <div className="mb-2 flex items-center justify-between">
        <label
          htmlFor="special-request"
          className="text-sm font-medium text-gray-700 dark:text-gray-300"
        >
          특별 요청사항 <span className="text-gray-500">(선택)</span>
        </label>
        <span
          className={`text-xs ${
            isNearLimit
              ? 'font-semibold text-orange-600 dark:text-orange-400'
              : 'text-gray-500 dark:text-gray-400'
          }`}
        >
          {value.length}/{maxLength}
        </span>
      </div>
      <div className="relative">
        <textarea
          id="special-request"
          value={value}
          onChange={handleChange}
          onFocus={() => setIsFocused(true)}
          onBlur={() => setIsFocused(false)}
          placeholder={placeholder}
          rows={3}
          className={`
            w-full resize-none rounded-lg border bg-white px-4 py-3 text-sm
            placeholder-gray-400 transition-all duration-200
            focus:outline-none focus:ring-2
            dark:bg-gray-800 dark:text-gray-100 dark:placeholder-gray-500
            ${
              isFocused
                ? 'border-blue-500 ring-blue-500/20 dark:border-blue-400'
                : 'border-gray-300 dark:border-gray-600'
            }
            ${
              isNearLimit && value.length > 0
                ? 'border-orange-400 dark:border-orange-500'
                : ''
            }
          `}
        />
        {value && (
          <button
            onClick={() => onChange('')}
            className="absolute right-3 top-3 rounded-full p-1 hover:bg-gray-100 dark:hover:bg-gray-700"
            aria-label="입력 내용 지우기"
            type="button"
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
      {value && (
        <p className="mt-2 text-xs text-gray-600 dark:text-gray-400">
          💡 입력하신 내용을 바탕으로 더 맞춤화된 데이트 코스를 추천해드릴게요
        </p>
      )}
    </div>
  );
}
