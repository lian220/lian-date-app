import { CourseCreateError } from '@/types/course';

interface CourseErrorStateProps {
  error: CourseCreateError;
  onRetry: () => void;
  onEditCondition?: () => void;
}

/**
 * 에러 코드에 따른 메시지 반환
 */
function getErrorMessage(error: CourseCreateError): {
  title: string;
  message: string;
  showEditButton: boolean;
} {
  const code = error.code;

  if (error.isTimeout || code === 'TIMEOUT') {
    return {
      title: '요청 시간 초과',
      message: '시간이 초과했어요. 다시 시도해주세요.',
      showEditButton: false,
    };
  }

  switch (code) {
    case 'NO_PLACES_FOUND':
      return {
        title: '장소를 찾을 수 없어요',
        message:
          '조건에 맞는 장소를 찾을 수 없어요. 다른 지역이나 예산을 선택해주세요.',
        showEditButton: true,
      };
    case 'INVALID_REQUEST':
      return {
        title: '입력 정보 오류',
        message: '입력 정보를 확인해주세요.',
        showEditButton: true,
      };
    case 'SERVER_ERROR':
      return {
        title: '서버 오류',
        message: '일시적인 오류가 발생했어요. 잠시 후 다시 시도해주세요.',
        showEditButton: false,
      };
    default:
      return {
        title: '코스 생성 실패',
        message: error.message || '알 수 없는 오류가 발생했습니다.',
        showEditButton: false,
      };
  }
}

/**
 * 코스 생성 에러 상태 컴포넌트
 * API 오류 또는 타임아웃 발생 시 표시되는 에러 UI
 */
export default function CourseErrorState({
  error,
  onRetry,
  onEditCondition,
}: CourseErrorStateProps) {
  const errorInfo = getErrorMessage(error);

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
          {errorInfo.title}
        </h3>
        <p className="text-sm text-gray-600 dark:text-gray-400">
          {errorInfo.message}
        </p>
      </div>

      {/* 액션 버튼 */}
      <div className="flex w-full gap-3">
        {errorInfo.showEditButton && onEditCondition && (
          <button
            onClick={onEditCondition}
            className="flex flex-1 items-center justify-center gap-2 rounded-lg border border-gray-300 px-6 py-3 font-semibold text-gray-700 transition-colors hover:bg-gray-50 dark:border-gray-600 dark:text-gray-300 dark:hover:bg-gray-800"
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
                d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"
              />
            </svg>
            조건 수정
          </button>
        )}
        <button
          onClick={onRetry}
          className={`flex items-center justify-center gap-2 rounded-lg bg-blue-600 px-6 py-3 font-semibold text-white transition-colors hover:bg-blue-700 dark:bg-blue-500 dark:hover:bg-blue-600 ${
            errorInfo.showEditButton ? 'flex-1' : 'w-full'
          }`}
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
    </div>
  );
}
