/**
 * 코스 생성 로딩 상태 컴포넌트
 * AI가 코스를 생성하는 동안 표시되는 로딩 UI
 */
export default function CourseLoadingState() {
  return (
    <div className="flex w-full max-w-md flex-col items-center justify-center rounded-xl border border-blue-200 bg-blue-50 p-12 dark:border-blue-800 dark:bg-blue-950">
      {/* 스피너 애니메이션 */}
      <div className="relative mb-6">
        <div className="h-16 w-16 animate-spin rounded-full border-4 border-blue-200 border-t-blue-600 dark:border-blue-800 dark:border-t-blue-400"></div>
        <div className="absolute inset-0 flex items-center justify-center">
          <span className="text-2xl">🤖</span>
        </div>
      </div>

      {/* 메시지 */}
      <div className="text-center">
        <h3 className="mb-2 text-xl font-bold text-gray-900 dark:text-gray-100">
          AI가 최적의 코스를 찾고 있어요
        </h3>
        <p className="text-sm text-gray-600 dark:text-gray-400">
          잠시만 기다려주세요. 최대 30초 정도 소요됩니다.
        </p>
      </div>

      {/* 로딩 도트 애니메이션 */}
      <div className="mt-6 flex gap-2">
        <div className="h-2 w-2 animate-bounce rounded-full bg-blue-600 dark:bg-blue-400 [animation-delay:-0.3s]"></div>
        <div className="h-2 w-2 animate-bounce rounded-full bg-blue-600 dark:bg-blue-400 [animation-delay:-0.15s]"></div>
        <div className="h-2 w-2 animate-bounce rounded-full bg-blue-600 dark:bg-blue-400"></div>
      </div>
    </div>
  );
}
