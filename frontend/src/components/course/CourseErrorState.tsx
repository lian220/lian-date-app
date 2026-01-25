import { CourseCreateError } from '@/types/course';

interface CourseErrorStateProps {
  error: CourseCreateError;
  onRetry: () => void;
}

/**
 * 코스 생성 에러 상태 컴포넌트
 * API 오류 또는 타임아웃 발생 시 표시되는 에러 UI
 */
export default function CourseErrorState({
  error,
  onRetry,
}: CourseErrorStateProps) {
  const isTimeout = error.isTimeout;

  return (
    <div className="flex w-full max-w-md flex-col items-center justify-center rounded-xl border border-red-200 bg-red-50 p-12 dark:border-red-800 dark:bg-red-950">
      {/* 에러 아이콘 */}
      <div className="mb-6 flex h-16 w-16 items-center justify-center rounded-full bg-red-100 dark:bg-red-900">
        <svg
          className="h-8 w-8 text-red-600 dark:text-red-400"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth={2}
            d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
          />
        </svg>
      </div>

      {/* 에러 메시지 */}
      <div className="mb-6 text-center">
        <h3 className="mb-2 text-xl font-bold text-gray-900 dark:text-gray-100">
          {isTimeout ? '요청 시간 초과' : '코스 생성 실패'}
        </h3>
        <p className="text-sm text-gray-600 dark:text-gray-400">
          {error.message}
        </p>
        {isTimeout && (
          <p className="mt-2 text-xs text-gray-500 dark:text-gray-500">
            네트워크 상태를 확인하고 다시 시도해주세요.
          </p>
        )}
      </div>

      {/* 다시 시도 버튼 */}
      <button
        onClick={onRetry}
        className="flex items-center gap-2 rounded-lg bg-blue-600 px-6 py-3 font-semibold text-white transition-colors hover:bg-blue-700 dark:bg-blue-500 dark:hover:bg-blue-600"
      >
        <svg
          className="h-5 w-5"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth={2}
            d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"
          />
        </svg>
        다시 시도
      </button>
    </div>
  );
}
